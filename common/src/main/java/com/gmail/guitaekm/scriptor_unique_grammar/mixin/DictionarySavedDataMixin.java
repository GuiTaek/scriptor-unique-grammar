package com.gmail.guitaekm.scriptor_unique_grammar.mixin;

import com.gmail.guitaekm.scriptor_unique_grammar.ParserStore;
import com.ssblur.scriptor.data.saved_data.DictionarySavedData;
import com.ssblur.scriptor.word.Spell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DictionarySavedData.class)
abstract public class DictionarySavedDataMixin {

    @Inject(method = "parse", at = @At("HEAD"), remap = false, cancellable = true)
    public void parseInjectHead(
            String text, CallbackInfoReturnable<Spell> cir
    ) {
        Spell spell = ParserStore
                .Companion
                .getParser()
                .parse(text);
        cir.setReturnValue(spell);
    }

    @Inject(method = "generate", at = @At("HEAD"), remap = false, cancellable = true)
    public void generateInjectHead(Spell spell, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(
                ParserStore
                    .Companion
                    .getParser()
                    .generate(spell)
        );
    }
}
