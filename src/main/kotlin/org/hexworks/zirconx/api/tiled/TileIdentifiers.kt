package org.hexworks.zirconx.api.tiled

inline class GlobalId internal constructor(val value: Int)
inline class LocalId internal constructor(val value: Int)

fun GlobalId.toLocalId(offset: Int) = LocalId(this.value - offset)
fun LocalId.toGlobalId(offset: Int) = GlobalId(this.value + offset)