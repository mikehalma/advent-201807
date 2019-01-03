package mhalma.advent201807

import java.lang.IllegalArgumentException
import java.util.*

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
    val steps = parseSteps(descriptions).toList()
    Collections.sort(steps) {
        p0, p1 -> when {
            p0 == null || p1 == null -> throw IllegalArgumentException("cannot compare null steps")
            p1.dependencies.contains(p0) -> -1
            p0.dependencies.contains(p1) -> 1
            else -> p0.id.compareTo(p1.id)
        }
    }
    return steps.map { it.id }.joinToString("")
}