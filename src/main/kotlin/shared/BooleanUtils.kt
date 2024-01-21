package shared


/**
 * Similar to [takeIf] from the standard library, but lazy.
 */
fun <T> Boolean.ifTake(f: () -> T): T? = if (this) f() else null
