package mhalma.advent201807

data class Step(val id: Char, val dependencies: MutableSet<Step> = mutableSetOf()) {
    fun addDependency(dependency: Step) {
        dependencies.add(dependency)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Step) {
            return false
        }
        return id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun toString(): String {
        return "Step ${this.id}, dependencies ${this.dependencies.map {it.id}.joinToString("")}"
    }
}

fun parseStep(description: String): Pair<Char, Char> {
    val regex = """.*([A-Z]).*([A-Z])""".toRegex()
    val result = regex.find(description)
    val (dependency, id) = result!!.destructured
    return Pair(id.toCharArray()[0], dependency.toCharArray()[0])
}

fun parseSteps(descriptions: List<String>): Set<Step> {

    val steps = mutableSetOf<Step>()

    descriptions.forEach {
        val (stepId, dependencyId) = parseStep(it)
        val step = steps.find { it.id == stepId}?:Step(stepId, mutableSetOf())
        val dependency = steps.find { it.id == dependencyId}?:Step(dependencyId, mutableSetOf())
        step.addDependency(dependency)
        if (!steps.contains(step)) steps.add(step)
        if (!steps.contains(dependency)) steps.add(dependency)
    }

    return steps
}

fun calculateStepOrder(descriptions: List<String>): String {
    val steps = parseSteps(descriptions).toMutableSet()
    val orderedSteps = mutableListOf<Step>()
    while (steps.size > 0) {
        val next = steps.filter { it.dependencies.size == 0 }.sortedBy { it.id }.take(1)[0]
        orderedSteps.add(next)
        steps.remove(next)
        steps.forEach { it.dependencies.remove(next) }
    }
    return orderedSteps.map { it.id }.joinToString("")
}