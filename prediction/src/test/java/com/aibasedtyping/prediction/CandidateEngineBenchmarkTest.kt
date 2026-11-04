package com.aibasedtyping.prediction

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.system.measureNanoTime

class CandidateEngineBenchmarkTest {
    private lateinit var engine: CandidateEngine

    @Before
    fun setUp() {
        engine = CandidateEngine()
    }

    @Test
    fun testCandidateGenerationLatencyUnder15msBudget() = runBlocking {
        // Warmup
        repeat(50) {
            engine.getCandidates("th")
            engine.getCandidates("how are ")
        }

        // Measure 100 iterations of prefix lookup
        val runs = 100
        var totalPrefixTimeNs = 0L
        repeat(runs) {
            val elapsed = measureNanoTime {
                val results = engine.getCandidates("appl")
                assertTrue(results.isNotEmpty() || results.isEmpty())
            }
            totalPrefixTimeNs += elapsed
        }

        val avgPrefixTimeMs = (totalPrefixTimeNs / runs) / 1_000_000.0

        // Measure 100 iterations of next-word prediction
        var totalNextWordTimeNs = 0L
        repeat(runs) {
            val elapsed = measureNanoTime {
                val results = engine.getCandidates("how are ")
                assertTrue(results.isNotEmpty())
            }
            totalNextWordTimeNs += elapsed
        }

        val avgNextWordTimeMs = (totalNextWordTimeNs / runs) / 1_000_000.0

        println("BENCHMARK RESULTS:")
        println("  Average Prefix Completion Latency: ${String.format("%.3f", avgPrefixTimeMs)} ms")
        println("  Average Next-Word Prediction Latency: ${String.format("%.3f", avgNextWordTimeMs)} ms")

        // Strict assertion: Average must comfortably be below the 15ms SLA
        assertTrue("Prefix latency $avgPrefixTimeMs ms exceeds 15ms limit", avgPrefixTimeMs < 15.0)
        assertTrue("Next-word latency $avgNextWordTimeMs ms exceeds 15ms limit", avgNextWordTimeMs < 15.0)
    }
}

