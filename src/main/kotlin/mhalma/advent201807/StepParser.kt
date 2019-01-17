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

fun calculateStepOrder(descriptions: List<String>): String {
    val steps = parseSteps(descriptions)
    val orderedSteps = Steps()
    while (steps.isNotEmpty()) {
        val next = steps.getNextAvailableStep()
        orderedSteps.add(next)
        steps.remove(next)
    }
    return orderedSteps.toString()
}


fun calculateDuration(steps: Steps, minDuration: Int, workers: Int): Int {
    val work = Work((1..workers).map {Worker(it)}.toList())
    val incompleteSteps = Steps(steps)

    (0..Int.MAX_VALUE).forEach { second ->

        with (incompleteSteps) {
            if (isEmpty()) {
                return second
            }

            val stepsBeingWorked = work.stepsInProgress()
            val availableStepsNotBeingWorked = getAvailableSteps().filterNot { stepsBeingWorked.contains(it) }.toMutableSet()

            while (availableStepsNotBeingWorked.isNotEmpty() && work.hasIdleWorkers()) {
                val assignedSteps = work.startIdleWorkers(availableStepsNotBeingWorked.toList(), minDuration)
                availableStepsNotBeingWorked.removeAll(assignedSteps)
            }

            removeAll(work.performWork())
        }
    }
    throw IllegalStateException("The puzzle appears to be unsolvable.")
}
