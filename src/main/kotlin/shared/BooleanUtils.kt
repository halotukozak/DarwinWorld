@file:Suppress("unused")

package shared

fun <T> Boolean.ifTrue(f: () -> T): T? = if (this) f() else null
