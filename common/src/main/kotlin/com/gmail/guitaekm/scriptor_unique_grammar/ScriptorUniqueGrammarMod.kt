package com.gmail.guitaekm.scriptor_unique_grammar

import com.ssblur.scriptor.ScriptorMod
import org.apache.logging.log4j.LogManager
import com.ssblur.unfocused.ModInitializer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import com.ssblur.scriptor.data.saved_data.DictionarySavedData

@Suppress("RedundantVisibilityModifier", "SpellCheckingInspection", "unused")
public object ScriptorUniqueGrammarMod: ModInitializer("scriptor_unique_grammar") {
    public const val MOD_ID = "scriptor_unique_grammar"
    public val LOGGER = LogManager.getLogger()!!
    fun init() {
        ParserStore.initializeEvents()
    }
}