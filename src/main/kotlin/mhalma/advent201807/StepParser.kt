package mhalma.advent201807

import org.springframework.util.ResourceUtils

fun parseStep(description: String): Pair<Char, Char> {
    val regex = """.*([A-Z]).*([A-Z])""".toRegex()
    val result = regex.find(description)
    val (dependency, id) = result!!.destructured
    return Pair(id.toCharArray()[0], dependency.toCharArray()[0])
}

fun parseSteps(descriptions: List<String>): Steps {

    val steps = mutableSetOf<Step>()

    descriptions.forEach {
        val (stepId, dependencyId) = parseStep(it)
        val step = steps.find { it.id == stepId}?:Step(stepId)
        val dependency = steps.find { it.id == dependencyId}?:Step(dependencyId)
        step.addDependency(dependency)
        if (!steps.contains(step)) steps.add(step)
        if (!steps.contains(dependency)) steps.add(dependency)
    }

    return Steps(steps)
}

fun getStepsFromFile(fileName: String) = ResourceUtils.getFile("classpath:$fileName").readLines()