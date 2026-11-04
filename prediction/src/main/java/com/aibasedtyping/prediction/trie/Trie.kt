package com.aibasedtyping.prediction.trie

class TrieNode {
    val children = HashMap<Char, TrieNode>()
    var frequency: Double = 0.0
    var isWord: Boolean = false
}

class Trie {
    private val root = TrieNode()

    fun insert(word: String, frequency: Double = 1.0) {
        if (word.isBlank()) return
        var current = root
        for (ch in word.lowercase()) {
            current = current.children.getOrPut(ch) { TrieNode() }
        }
        current.isWord = true
        current.frequency = maxOf(current.frequency, frequency)
    }

    fun contains(word: String): Boolean {
        var current = root
        for (ch in word.lowercase()) {
            current = current.children[ch] ?: return false
        }
        return current.isWord
    }

    fun searchPrefix(prefix: String, limit: Int = 20): List<Pair<String, Double>> {
        if (prefix.isBlank()) return emptyList()
        val lowerPrefix = prefix.lowercase()
        var current = root

        for (ch in lowerPrefix) {
            current = current.children[ch] ?: return emptyList()
        }

        val results = mutableListOf<Pair<String, Double>>()
        collectWords(current, StringBuilder(lowerPrefix), results)
        return results.sortedByDescending { it.second }.take(limit)
    }

    private fun collectWords(node: TrieNode, currentWord: StringBuilder, results: MutableList<Pair<String, Double>>) {
        if (node.isWord) {
            results.add(Pair(currentWord.toString(), node.frequency))
        }

        for ((ch, child) in node.children) {
            currentWord.append(ch)
            collectWords(child, currentWord, results)
            currentWord.deleteCharAt(currentWord.length - 1)
        }
    }

    fun clear() {
        root.children.clear()
        root.frequency = 0.0
        root.isWord = false
    }
}

