package com.gmail.guitaekm.scriptor_unique_grammar

import com.gmail.guitaekm.scriptor_unique_grammar.UniqueParser.ParseEntry
import com.ssblur.scriptor.api.word.Action
import com.ssblur.scriptor.api.word.Descriptor
import com.ssblur.scriptor.api.word.Subject
import com.ssblur.scriptor.api.word.Word
import com.ssblur.scriptor.data.saved_data.DictionarySavedData
import com.ssblur.scriptor.registry.words.WordRegistry.actionRegistry
import com.ssblur.scriptor.registry.words.WordRegistry.descriptorRegistry
import com.ssblur.scriptor.registry.words.WordRegistry.subjectRegistry
import com.ssblur.scriptor.word.PartialSpell
import com.ssblur.scriptor.word.Spell
import org.apache.commons.codec.Decoder

private fun List<ParseEntry>.split(splitCondition: (ParseEntry) -> Boolean): List<List<ParseEntry>> {
    val result: MutableList<List<ParseEntry>> = mutableListOf()
    val currList: MutableList<ParseEntry> = mutableListOf()
    for (parseEntry in this) {
        when {
            splitCondition(parseEntry) -> {
                // has to be copied
                result.add(currList.toList())
                currList.clear()
            }
            else -> currList.add(parseEntry)
        }
    }
    result.add(currList)
    return result
}

private fun List<ParseEntry>.split(splitParseEntry: ParseEntry): List<List<ParseEntry>> {
    return this.split {parseEntry -> parseEntry == splitParseEntry}
}

private fun List<ParseEntry>.split(wordType: String): List<List<ParseEntry>> {
    return this.split {parseEntry -> parseEntry.wordType == wordType}
}

class UniqueParser(val dictionary: DictionarySavedData, val positions: WordPositions) {
    private fun createParseEntryFromExternal(externalWord: String): ParseEntry? {
        val composedWord = dictionary.parseWord(externalWord) ?: return null
        val wordType = composedWord.split(":")[0]
        val internalWord = composedWord.split(":")[1]
        val position = positions.content[composedWord] ?: return null
        return ParseEntry(wordType, internalWord, externalWord, position)
    }

    private fun createParseEntryFromInternal(composedWord: String): ParseEntry? {
        val wordType = composedWord.split(":")[0]
        val internalWord = composedWord.split(":")[1]
        val externalWord = dictionary.getWord(composedWord) ?: return null
        val position = positions.content[composedWord] ?: return null
        return ParseEntry(wordType, internalWord, externalWord, position)
    }

    internal data class ParseEntry(
        val wordType: String,
        val internalWord: String,
        val externalWord: String,
        val position: WordPosition
    ) {
    }
    fun parse(text: String): Spell? {
        val tokens = text.split("[\\n\\r\\s]+".toRegex()).filter { it.isNotEmpty() } .toTypedArray()
        val parseEntries: List<ParseEntry> = tokens.map {
            token -> this.createParseEntryFromExternal(token) ?: return null
        }
        val subSpells = parseEntries.split(this.createParseEntryFromInternal("other:and")!!)
        if (subSpells.isEmpty()) {
            return null
        }
        val partialSpells: MutableList<PartialSpell> = ArrayList()
        val res = parseMainSpell(subSpells.first()) ?: return null
        partialSpells.add(res.second)
        val subject = res.first
        for (subSpell in subSpells.subList(1, subSpells.size)) {
            partialSpells.add(parsePartialSpell(subSpell) ?: return null)
        }
        return Spell(subject, *partialSpells.toTypedArray())
    }
    private fun parseMainSpell(entries: List<ParseEntry>): Pair<Subject, PartialSpell>? {
        if (entries.filter {entry -> entry.wordType == "subject"}.size != 1) {
            return null
        }
        if (entries.filter {entry -> entry.wordType == "action"}.size != 1) {
            return null
        }
        val actionEntry: ParseEntry = entries.find {entry -> entry.wordType == "action"} ?: return null
        val restEntries: List<ParseEntry>
        val subject = when {
            (entries.first().wordType == "subject") -> {
                if (actionEntry.position != WordPosition.AFTER) {
                    return null
                }
                restEntries = entries.subList(1, entries.size)
                subjectRegistry[entries.first().internalWord] ?: return null
            }

            (entries.last().wordType == "subject") -> {
                if (actionEntry.position != WordPosition.BEFORE) {
                    return null
                }
                restEntries = entries.subList(0, entries.size - 1)
                subjectRegistry[entries.last().internalWord] ?: return null
            }

            else -> return null
        }
        val partialSpell: PartialSpell = this.parsePartialSpell(restEntries) ?: return null
        return Pair(subject, partialSpell)
    }

    private fun parsePartialSpell(entries: List<ParseEntry>): PartialSpell? {
        val sublists = entries.split("action")
        val parseEntryAction: ParseEntry = entries.find { entry -> entry.wordType == "action"} ?: return null
        val action: Action = actionRegistry[parseEntryAction.internalWord] ?: return null
        when (sublists.size) {
            0 -> return PartialSpell(actionRegistry[entries.first().internalWord] ?: return null)
            1 -> {
                when {
                    (entries.first().wordType == "action") -> {
                        if (!sublists.first().all { entry -> entry.position == WordPosition.AFTER}) {
                            return null
                        }
                        val descriptors: List<Descriptor> = sublists.first().map {
                            entry -> descriptorRegistry[entry.internalWord] ?: return null
                        }
                        return PartialSpell(
                            action,
                            *(descriptors.toTypedArray())
                        )
                    }
                }
            }
            2 -> {
                for (entry in sublists.first()) {
                    if (entry.position != WordPosition.BEFORE) {
                        return null
                    }
                }
                for (entry in sublists.last()) {
                    if (entry.position != WordPosition.AFTER) {
                        return null
                    }
                }
                val descriptors = sublists.first().plus(sublists.last()).map {
                    entry -> descriptorRegistry[entry.internalWord] ?: return null
                }
                return PartialSpell(action, *descriptors.toTypedArray())
            }
            else -> {
                return null
            }
        }
        return null
    }
}