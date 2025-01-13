package com.gmail.guitaekm;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ssblur.scriptor.data.DictionarySavedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueDictionarySavedData extends DictionarySavedData {
    public enum WordPosition {
        BEFORE, AFTER
    }
    Codec<WordPosition> wordPositionCodec = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec
                            .STRING
                            .fieldOf("name")
                            .forGetter(WordPosition::name)
            ).apply(instance, WordPosition::valueOf));

    public Map<String, WordPosition> wordPositions;
    public UniqueDictionarySavedData(
            List<String> spellStructure,
            List<Pair<String, String>> words,
            Map<String, WordPosition> wordPositions
    ) {
        super(spellStructure, words);
        this.wordPositions = new HashMap<>(wordPositions);
    }
    public Codec<UniqueDictionarySavedData> additionalWorldCodec = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("spellStructure").forGetter(
                            worldData -> worldData.spellStructure.stream().map(WORD::toString).toList()
                    ),
                    Codec.compoundList(Codec.STRING, Codec.STRING)
                                    .fieldOf("words")
                                            .forGetter(worldData ->
                                                    worldData.words.keySet().stream().map(key -> new Pair<>(
                                                            key, worldData.words.get(key))).toList()
                                                    ),
                    Codec.unboundedMap(Codec.STRING, this.wordPositionCodec).fieldOf("wordPositions").forGetter(
                            uniqueDict -> uniqueDict.wordPositions
                    )
            ).apply(instance, UniqueDictionarySavedData::new)
    );
}
