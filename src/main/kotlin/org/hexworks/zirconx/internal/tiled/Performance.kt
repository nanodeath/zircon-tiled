package org.hexworks.zirconx.internal.tiled

import java.util.concurrent.atomic.AtomicInteger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val checkingPerformance = System.getenv("MEASURE_PERFORMANCE").toBoolean()

private val nesting = AtomicInteger(0)

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

private fun generateNesting(size: Int) = String(CharArray(size) { '-' })