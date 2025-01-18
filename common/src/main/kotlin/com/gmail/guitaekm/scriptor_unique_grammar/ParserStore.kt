package com.gmail.guitaekm.scriptor_unique_grammar

import com.gmail.guitaekm.scriptor_unique_grammar.mixin.DictionarySavedDataMixin
import com.ssblur.scriptor.data.saved_data.DictionarySavedData
import org.spongepowered.asm.mixin.Unique

class ParserStore(dict: DictionarySavedData) {

    val positions: WordPositions = WordPositions(dict)
    val parser: UniqueParser = UniqueParser(
        dict,
        this.positions
    )

    companion object {
        private var INSTANCE: ParserStore? = null
        fun getInstance(dict: DictionarySavedData): ParserStore {
            if (INSTANCE == null) {
                INSTANCE = ParserStore(dict)
            }
            return INSTANCE!!
        }
    }

}