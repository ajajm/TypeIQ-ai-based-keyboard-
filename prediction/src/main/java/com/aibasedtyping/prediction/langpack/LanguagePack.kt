package com.aibasedtyping.prediction.langpack

import com.aibasedtyping.prediction.ngram.NgramModel
import com.aibasedtyping.prediction.trie.Trie

interface LanguagePack {
    val languageCode: String
    val displayName: String

    fun populateTrie(trie: Trie)
    fun populateNgramModel(ngramModel: NgramModel)
}

