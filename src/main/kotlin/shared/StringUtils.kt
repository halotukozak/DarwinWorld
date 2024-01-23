package shared


fun String.truncated(n: Int) = if (n < length) take(n) + "..." else this
