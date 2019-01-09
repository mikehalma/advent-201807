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

data class Steps(val steps: MutableSet<Step> = mutableSetOf()) {
    fun isNotEmpty() = this.steps.size > 0

    fun isEmpty() = this.steps.size == 0

    fun getAvailableSteps(): List<Step> {
        return steps.filter { it.dependencies.isEmpty() }.sortedBy { it.id }
    }

    fun getNextAvailableStep(): Step {
        return getAvailableSteps().take(1)[0]
    }

    fun add(step : Step) = this.steps.add(step)

    fun remove(step: Step) {
        this.steps.remove(step)
        this.steps.forEach {it.dependencies.remove(step)}
    }

    fun removeAll(steps: List<Step>) {
        steps.forEach {remove(it)}
    }

    override fun toString() = steps.map { it.id }.joinToString("")
}

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

fun calculateDuration(steps: Steps, minDuration: Int, workers: Int): Int {
    val work = Work((1..workers).map {Worker(it)}.toList())
    val incompleteSteps = Steps(steps.steps)

    (0..Int.MAX_VALUE).forEach { second ->

        with (incompleteSteps) {
            if (isEmpty()) {
                return second
            }

            val stepsBeingWorked = work.stepsInProgress()
            val nextStepsNotBeingWorked = getAvailableSteps().filterNot { stepsBeingWorked.contains(it) }.toMutableSet()

            while (nextStepsNotBeingWorked.isNotEmpty() && work.hasIdleWorkers()) {
                val assignedSteps = work.startIdleWorkers(nextStepsNotBeingWorked.toList(), minDuration)
                nextStepsNotBeingWorked.removeAll(assignedSteps)
            }

            removeAll(work.performWork())
        }
    }
    throw IllegalStateException("The puzzle appears to be unsolvable.")
}
