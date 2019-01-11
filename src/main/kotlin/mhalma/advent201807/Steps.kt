package mhalma.advent201807

class Steps(private val set: MutableSet<Step> = mutableSetOf()) : MutableSet<Step> by set {

    fun getAvailableSteps(): List<Step> {
        return filter { it.dependencies.isEmpty() }.sortedBy { it.id }
    }

    fun getNextAvailableStep(): Step {
        return getAvailableSteps().take(1)[0]
    }

    override fun remove(step: Step): Boolean {
        forEach {it.dependencies.remove(step)}
        return set.remove(step)
    }

    override fun removeAll(steps: Collection<Step>): Boolean {
        return steps.map {remove(it)}.all {true}
    }

    override fun toString() = map { it.id }.joinToString("")
}

