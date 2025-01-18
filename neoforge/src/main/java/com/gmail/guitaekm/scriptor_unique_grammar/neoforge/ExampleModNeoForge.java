package com.gmail.guitaekm.scriptor_unique_grammar.neoforge;

import net.neoforged.fml.common.Mod;

import com.gmail.guitaekm.scriptor_unique_grammar.ScriptorUniqueGrammarMod;

@Mod(ScriptorUniqueGrammarMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ScriptorUniqueGrammarMod.INSTANCE.init();
    }
}
