package com.aibasedtyping.prediction

import com.aibasedtyping.prediction.trie.Trie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TrieTest {
    private lateinit var trie: Trie

    @Before
    fun setUp() {
        trie = Trie()
        trie.insert("apple", 10.0)
        trie.insert("application", 20.0)
        trie.insert("apply", 15.0)
        trie.insert("banana", 5.0)
    }

    @Test
    fun testContains() {
        assertTrue(trie.contains("apple"))
        assertTrue(trie.contains("application"))
        assertFalse(trie.contains("appl"))
        assertFalse(trie.contains("orange"))
    }

    @Test
    fun testPrefixSearchRanking() {
        val results = trie.searchPrefix("app")
        assertEquals(3, results.size)
        // Highest frequency first
        assertEquals("application", results[0].first)
        assertEquals("apply", results[1].first)
        assertEquals("apple", results[2].first)
    }

    @Test
    fun testNonExistentPrefix() {
        val results = trie.searchPrefix("xyz")
        assertTrue(results.isEmpty())
    }
}

