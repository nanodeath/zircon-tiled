package org.hexworks.zirconx.internal.tiled

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val checkingPerformance = System.getenv("MEASURE_PERFORMANCE").toBoolean()

private val nesting = AtomicInteger(0)

internal data class PerfData(var counter: Int, var runningTotal: Long) {
    fun reset() {
        counter = 0
        runningTotal = 0
    }
}

private val counters: ConcurrentMap<String, PerfData> = ConcurrentHashMap()

@OptIn(ExperimentalContracts::class)
internal inline fun <T> logDuration(name: String, callback: () -> T): T {
    contract {
        callsInPlace(callback, InvocationKind.EXACTLY_ONCE)
    }
    if (!checkingPerformance) {
        return callback()
    }
    val start = System.currentTimeMillis()
    val currentNesting = nesting.getAndIncrement()
    val result = callback()
    nesting.decrementAndGet()
    val durationMs = System.currentTimeMillis() - start
    val slow = if (durationMs >= 100) "\uD83D\uDD25" else "" // 🔥
    System.err.println("PERF: ${generateNesting(currentNesting)}$slow$name took ${durationMs}ms")
    return result
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> logDurationNs(name: String, logEvery: Int = 0, callback: () -> T): T {
    contract {
        callsInPlace(callback, InvocationKind.EXACTLY_ONCE)
    }
    if (!checkingPerformance) {
        return callback()
    }
    val start = System.nanoTime()
    val currentNesting = nesting.getAndIncrement()
    val result = callback()
    nesting.decrementAndGet()
    val durationNs = System.nanoTime() - start
    if (logEvery <= 0) {
        System.err.println("PERF: ${generateNesting(currentNesting)}$name took ${durationNs}ns")
    } else {
        val myData = counters.computeIfAbsent(name) { PerfData(0, 0L) }
        synchronized(myData) {
            myData.runningTotal += durationNs
            if (++myData.counter == logEvery) {
                val invocations = myData.counter
                val totalTime = myData.runningTotal
                myData.reset()
                val formattedTime = (totalTime.toDouble() / invocations).toInt()
                System.err.println("PERF: ${generateNesting(currentNesting)}$name took ${formattedTime}ns on average ($invocations hits)")
            }
        }
    }
    return result
}

private fun generateNesting(size: Int) = String(CharArray(size) { '-' })