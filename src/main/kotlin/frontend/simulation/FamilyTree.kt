package frontend.simulation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class FamilyTree private constructor(
  private val id: UUID? = null,
  private val children: MutableStateFlow<Set<FamilyTree>> = MutableStateFlow(setOf()),
) {
  constructor(children: List<UUID>) : this(children = MutableStateFlow(children.map(::FamilyTree).toSet()))

  fun add(childId: UUID, vararg parentIds: UUID) = FamilyTree(id = childId).let { child ->
    parentIds.forEach { id ->
      find(id)?.children?.update { it + child } //shit, it can be null
    }
  }

  fun find(id: UUID): FamilyTree? = if (this.id == id) this else children.value.firstNotNullOfOrNull { it.find(id) }

  val descendants: Int
    get() {
      tailrec fun loop(visited: Set<FamilyTree>, acc: Set<FamilyTree>): Int =
        if (acc.isEmpty()) visited.size
        else {
          val current = acc.first()
          loop(visited + current, acc - current + current.children.value)
        }
      return loop(setOf(), children.value)
    }

}
