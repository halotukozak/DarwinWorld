@file:Suppress("unused")

package shared

fun <T> Boolean.ifTake(f: () -> T): T? = if (this) f() else null
