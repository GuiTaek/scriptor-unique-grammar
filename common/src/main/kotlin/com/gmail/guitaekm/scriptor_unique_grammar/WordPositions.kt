package com.gmail.guitaekm.scriptor_unique_grammar

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.ssblur.scriptor.data.saved_data.DictionarySavedData
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData

open class WordPositions(val content: Map<String, WordPosition>) : SavedData() {

    init {
        this.setDirty()
    }

    constructor(dictionary: DictionarySavedData) : this(
        dictionary.words.keys.associateBy (
            { it!! },
            { WordPosition.entries.toTypedArray().random() }
        )
    )

    companion object {
        val codec = RecordCodecBuilder.create {
            instance -> instance.group(
                Codec
                    .unboundedMap(Codec.STRING, WordPosition.codec)
                    .fieldOf("positions")
                    .forGetter {
                        wordPositions: WordPositions -> wordPositions.content
                    }
        ).apply(instance) {
                wordPositions -> WordPositions(wordPositions)
            }
        }!!

        fun load(tag: CompoundTag): WordPositions? {
            val positionsTag = tag[ScriptorUniqueGrammarMod.MOD_ID + ":word_positions"]
            if (positionsTag != null) {
                val result = codec.decode(NbtOps.INSTANCE, positionsTag).result()
                if (result.isPresent && result.get().first != null) {
                    return result.get().first
                }
            }
            return null
        }

        fun computeIfAbsent(server: MinecraftServer): WordPositions {
            val overworld = server.getLevel(Level.OVERWORLD)
            return overworld!!.dataStorage.computeIfAbsent(
                Factory(
                    { WordPositions(DictionarySavedData.computeIfAbsent(overworld)) },
                    { tag: CompoundTag, _: Provider -> load(tag)},
                    DataFixTypes.SAVED_DATA_MAP_DATA
                ),
                "scriptor_unique_grammar_positions"
            )
        }
    }

    override fun save(compoundTag: CompoundTag, provider: Provider): CompoundTag {
        val toAdd: Tag = codec.encodeStart(NbtOps.INSTANCE, this).result().get()
        compoundTag.put(ScriptorUniqueGrammarMod.MOD_ID + ":word_positions", toAdd)
        return compoundTag
    }
}
