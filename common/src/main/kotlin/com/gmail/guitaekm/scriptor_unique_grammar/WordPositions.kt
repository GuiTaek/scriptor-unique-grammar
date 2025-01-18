package com.gmail.guitaekm.scriptor_unique_grammar

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.ssblur.scriptor.data.saved_data.DictionarySavedData

open class WordPositions(val content: Map<String, WordPosition>) {
    constructor(dictionary: DictionarySavedData) : this (
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
    }
}