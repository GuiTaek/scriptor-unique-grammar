package com.gmail.guitaekm.scriptor_unique_grammar

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.Codec

enum class WordPosition(val isAfter: Boolean) {
    BEFORE(false),
    AFTER(true);
    companion object {
        val codec: Codec<WordPosition> = RecordCodecBuilder.create {
            instance -> instance.group(
                Codec
                    .STRING
                    .fieldOf("position")
                    .forGetter { position -> position.toString() }
            ).apply(instance) {
                position: String ->
                WordPosition.valueOf(position)
            }
        }
    }
}