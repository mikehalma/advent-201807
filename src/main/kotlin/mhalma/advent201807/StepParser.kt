package mhalma.advent201807

import kotlin.streams.toList

data class Step(val id: Char, val dependencies: MutableSet<Step> = mutableSetOf()) {
    private val STEP_VALUES = 'A'.rangeTo('Z')

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

    fun getStepTime(minDuration: Int): Int {
        return STEP_VALUES.indexOf(this.id) + minDuration + 1
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
        val step = steps.find { it.id == stepId}?:Step(stepId)
        val dependency = steps.find { it.id == dependencyId}?:Step(dependencyId)
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
        val next = getNextStep(steps.toList())
        orderedSteps.add(next)
        steps.remove(next)
        steps.forEach { it.dependencies.remove(next) }
    }
    return orderedSteps.map { it.id }.joinToString("")
}

fun getNextStep(steps: List<Step>): Step {
    return getAllNextSteps(steps).take(1)[0]
}

fun getAllNextSteps(steps: List<Step>): List<Step> {
    return steps.filter { it.dependencies.size == 0 }.sortedBy { it.id }
}

data class Worker(val id: Int, var currentStep: Step = Step('0'), var secondsLeft: Int = 0) {
    fun assignStep(step: Step, minDuration: Int) {
        this.currentStep = step
        this.secondsLeft = step.getStepTime(minDuration)
    }

    fun performWork(): Step? {
        this.secondsLeft -= 1
        if (this.secondsLeft == 0) {
            val step = this.currentStep
            this.currentStep = Step('0')
            return step
        }
        return null
    }
}

class Work(private val workers: List<Worker>) {
    fun stepsInProgress(): List<Step> {
        return this.workers.map {it.currentStep}.filter {it != Step('0')}
    }

    fun hasIdleWorkers(): Boolean {
        return this.workers.count {it.currentStep == Step('0')} > 0
    }

    fun startIdleWorkers(availableSteps: List<Step>, minDuration: Int): List<Step> {
        return availableSteps.stream()
                .peek {firstIdle()?.assignStep(it, minDuration)}
                .filter {stepsInProgress().contains(it)}
                .toList()
    }

    private fun firstIdle(): Worker? {
        return this.workers.filter {it.secondsLeft == 0}.firstOrNull()
    }

    fun performWork(): List<Step> {
        return this.workers
                .filter {it.secondsLeft > 0}
                .map {it.performWork()}
                .filter {it != null}
                .map {it as Step}
                .toList()
    }
}

fun calculateDuration(steps: Set<Step>, minDuration: Int, workers: Int): Int {
    val work = Work((1..workers).map {Worker(it)}.toList())
    val incompleteSteps = steps.toMutableSet()

    (0..Int.MAX_VALUE).forEach { second ->
        if (incompleteSteps.size == 0) {
            return second
        }

        val stepsBeingWorked = work.stepsInProgress()
        val nextStepsNotBeingWorked = getAllNextSteps(incompleteSteps.toList()).filterNot {stepsBeingWorked.contains(it)}.toMutableSet()

        while (nextStepsNotBeingWorked.isNotEmpty() && work.hasIdleWorkers()) {
            val assignedSteps = work.startIdleWorkers(nextStepsNotBeingWorked.toList(), minDuration)
            nextStepsNotBeingWorked.removeAll(assignedSteps)
        }

        work.performWork().forEach {step ->
            incompleteSteps.remove(step)
            incompleteSteps.forEach { it.dependencies.remove(step) }
        }
    }
    return Int.MAX_VALUE
}
