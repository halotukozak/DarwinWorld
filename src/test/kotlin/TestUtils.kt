@Suppress("UNCHECKED_CAST")
fun <T> Any.getPrivateField(name: String): T = try {
  this::class.java.getDeclaredField(name).apply { isAccessible = true }.get(this) as T
} catch (e: NoSuchFieldException, ) {
  this::class.java.superclass.getDeclaredField(name).apply { isAccessible = true }.get(this) as T
}