package com.aibasedtyping.prediction

import com.aibasedtyping.prediction.ngram.NgramModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NgramModelTest {
    private lateinit var ngramModel: NgramModel

    @Before
    fun setUp() {
        ngramModel = NgramModel()
        ngramModel.addBigram("how", "are", 10.0)
        ngramModel.addBigram("how", "is", 5.0)
        ngramModel.addTrigram("how", "are", "you", 20.0)
    }

    @Test
    fun testBigramPrediction() {
        val predictions = ngramModel.predictNextWords(listOf("how"))
        assertTrue(predictions.isNotEmpty())
        assertEquals("are", predictions[0].first)
    }

    @Test
    fun testTrigramPrediction() {
        val predictions = ngramModel.predictNextWords(listOf("how", "are"))
        assertTrue(predictions.isNotEmpty())
        assertEquals("you", predictions[0].first)
    }
}

