package com.gmail.guitaekm.scriptor_unique_grammar.mixin;

import com.gmail.guitaekm.scriptor_unique_grammar.ParserStore;
import com.gmail.guitaekm.scriptor_unique_grammar.UniqueParser;
import com.gmail.guitaekm.scriptor_unique_grammar.WordPositions;
import com.ssblur.scriptor.data.saved_data.DictionarySavedData;
import com.ssblur.scriptor.word.Spell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(DictionarySavedData.class)
abstract public class DictionarySavedDataMixin {

    @Unique
    private final DictionarySavedData scriptor_unique_grammar$self = (DictionarySavedData) (Object) this;

    @Inject(method="parse", at = @At("HEAD"), remap = false)
    public void parseInjectHead(
            String text, CallbackInfoReturnable<Spell> cir
    ) {
        System.out.println("hello world");
        System.out.println(
                ParserStore
                        .Companion
                        .getInstance(scriptor_unique_grammar$self)
                        .getParser()
                        .parse(text)
                        != null
        );
    }
}
