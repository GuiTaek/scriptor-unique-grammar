package com.gmail.guitaekm.scriptor_unique_grammar

import com.ssblur.scriptor.data.saved_data.DictionarySavedData
import net.minecraft.server.MinecraftServer
import com.ssblur.unfocused.event.common.ServerStartEvent

class ParserStore(server: MinecraftServer) {

    private val parser: UniqueParser = UniqueParser(
        DictionarySavedData.computeIfAbsent(server.overworld()),
        WordPositions.computeIfAbsent(server)
    )

    private val dictionary: DictionarySavedData = DictionarySavedData
        .computeIfAbsent(server.overworld())

    companion object {
        fun initializeEvents() {
            ServerStartEvent.register {
                server -> makeInstance(server)
            }
        }
        private var INSTANCE: ParserStore? = null
        fun makeInstance(server: MinecraftServer) {
            INSTANCE = ParserStore(server)
        }
        fun getParser(): UniqueParser {
            return INSTANCE!!.parser
        }
        fun getDictionary(): DictionarySavedData {
            return INSTANCE!!.dictionary
        }
    }

}