package backend

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*

sealed interface Family {
  val children: StateFlow<Set<FamilyNode>>
}

class FamilyRoot(
  children: List<UUID>,
) : Family {

  private val descendantsMap = MutableStateFlow(children.associateWith { FamilyNode() })
  override val children: StateFlow<Set<FamilyNode>> = MutableStateFlow(descendantsMap.value.values.toSet())

  fun find(id: UUID): FamilyNode? = descendantsMap.value[id]

  fun add(childId: UUID, vararg parentIds: UUID) = FamilyNode().let { child ->
    descendantsMap.update { it + (childId to child) }
    parentIds
      .mapNotNull(::find)
      .forEach { parent -> parent.children.update { it + child } }
  }
}


class FamilyNode : Family {

  override val children: MutableStateFlow<Set<FamilyNode>> = MutableStateFlow(setOf())

  val descendants: Int
    get() {
      tailrec fun loop(visited: Set<FamilyNode>, acc: Set<FamilyNode>): Int =
        if (acc.isEmpty()) visited.size
        else {
          val current = acc.first()
          loop(visited + current, acc - current + current.children.value)
        }
      return loop(setOf(), children.value)
    }
}
