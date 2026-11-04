package com.aibasedtyping.prediction.langpack

import com.aibasedtyping.prediction.ngram.NgramModel
import com.aibasedtyping.prediction.trie.Trie

class EnglishLanguagePack : LanguagePack {
    override val languageCode: String = "en"
    override val displayName: String = "English"

    override fun populateTrie(trie: Trie) {
        val commonWords = listOf(
            "the" to 100.0, "be" to 95.0, "to" to 92.0, "of" to 90.0, "and" to 88.0,
            "a" to 85.0, "in" to 82.0, "that" to 80.0, "have" to 78.0, "i" to 75.0,
            "it" to 74.0, "for" to 72.0, "not" to 70.0, "on" to 68.0, "with" to 66.0,
            "he" to 65.0, "as" to 64.0, "you" to 62.0, "do" to 60.0, "at" to 58.0,
            "this" to 57.0, "but" to 55.0, "his" to 54.0, "by" to 52.0, "from" to 50.0,
            "they" to 49.0, "we" to 48.0, "say" to 46.0, "her" to 45.0, "she" to 44.0,
            "or" to 42.0, "an" to 40.0, "will" to 39.0, "my" to 38.0, "one" to 37.0,
            "all" to 36.0, "would" to 35.0, "there" to 34.0, "their" to 33.0, "what" to 32.0,
            "so" to 31.0, "up" to 30.0, "out" to 29.0, "if" to 28.0, "about" to 27.0,
            "who" to 26.0, "get" to 25.0, "which" to 24.0, "go" to 23.0, "me" to 22.0,
            "when" to 21.0, "make" to 20.0, "can" to 19.0, "like" to 18.0, "time" to 17.0,
            "no" to 16.0, "just" to 15.0, "him" to 14.0, "know" to 13.0, "take" to 12.0,
            "people" to 11.0, "into" to 10.0, "year" to 9.0, "your" to 8.5, "good" to 8.0,
            "some" to 7.5, "could" to 7.0, "them" to 6.5, "see" to 6.0, "other" to 5.5,
            "than" to 5.0, "then" to 4.8, "now" to 4.6, "look" to 4.4, "only" to 4.2,
            "come" to 4.0, "its" to 3.8, "over" to 3.6, "think" to 3.4, "also" to 3.2,
            "back" to 3.0, "after" to 2.8, "use" to 2.6, "two" to 2.4, "how" to 2.2,
            "our" to 2.0, "work" to 1.9, "first" to 1.8, "well" to 1.7, "way" to 1.6,
            "even" to 1.5, "new" to 1.4, "want" to 1.3, "because" to 1.2, "any" to 1.1,
            "these" to 1.0, "give" to 0.95, "day" to 0.90, "most" to 0.85, "us" to 0.80,
            "hello" to 2.5, "thanks" to 2.5, "please" to 2.5, "great" to 2.0, "awesome" to 1.5,
            "meeting" to 2.0, "tomorrow" to 2.0, "today" to 2.0, "tonight" to 1.8,
            "message" to 1.5, "email" to 1.5, "call" to 1.5, "phone" to 1.4, "happy" to 1.5
        )

        commonWords.forEach { (word, freq) ->
            trie.insert(word, freq)
        }
    }

    override fun populateNgramModel(ngramModel: NgramModel) {
        val bigrams = listOf(
            Triple("how", "are", 50.0),
            Triple("are", "you", 45.0),
            Triple("thank", "you", 40.0),
            Triple("see", "you", 35.0),
            Triple("let", "me", 30.0),
            Triple("going", "to", 28.0),
            Triple("want", "to", 25.0),
            Triple("i", "am", 24.0),
            Triple("i", "will", 22.0),
            Triple("i", "have", 20.0),
            Triple("at", "the", 18.0),
            Triple("in", "the", 17.0),
            Triple("on", "the", 16.0),
            Triple("good", "morning", 25.0),
            Triple("good", "night", 20.0),
            Triple("talk", "to", 15.0),
            Triple("looking", "forward", 18.0),
            Triple("sounds", "good", 16.0),
            Triple("please", "let", 14.0)
        )

        bigrams.forEach { (w1, w2, freq) ->
            ngramModel.addBigram(w1, w2, freq)
        }

        val trigrams = listOf(
            Pair(listOf("how", "are"), "you" to 40.0),
            Pair(listOf("thank", "you"), "so" to 30.0),
            Pair(listOf("see", "you"), "later" to 25.0),
            Pair(listOf("see", "you"), "tomorrow" to 20.0),
            Pair(listOf("let", "me"), "know" to 35.0),
            Pair(listOf("looking", "forward"), "to" to 30.0),
            Pair(listOf("i", "hope"), "you" to 25.0),
            Pair(listOf("have", "a"), "great" to 20.0),
            Pair(listOf("what", "do"), "you" to 20.0)
        )

        trigrams.forEach { (context, nextPair) ->
            ngramModel.addTrigram(context[0], context[1], nextPair.first, nextPair.second)
        }
    }
}

