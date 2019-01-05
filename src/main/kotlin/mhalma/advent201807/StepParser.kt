package mhalma.advent201807

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

class Work(val workers: List<Worker>) {
    fun stepsInProgress(): List<Step> {
        return this.workers.map {it.currentStep}.filter {it != Step('0')}
    }

    fun hasIdleWorkers(): Boolean {
        return this.workers.count {it.currentStep == Step('0')} > 0
    }

    fun startIdleWorkers(availableSteps: List<Step>, minDuration: Int): List<Step> {
        val availableStepsRemaining = availableSteps.toMutableSet()
        val assignedSteps = mutableListOf<Step>()

        this.workers.filter {it.secondsLeft == 0}.forEach { worker ->
            if (availableStepsRemaining.size > 0) {
                val nextStep = availableStepsRemaining.first()
                availableStepsRemaining.remove(nextStep)
                worker.assignStep(nextStep, minDuration)
                assignedSteps.add(nextStep)
            }
        }
        return assignedSteps.toList()
    }

    fun performWork(): List<Step> {
        val finishedSteps = mutableListOf<Step>()
        this.workers.filter {it.secondsLeft > 0}.forEach {worker ->
            val finishedStep = worker.performWork()
            if (finishedStep != null) {
                finishedSteps.add(finishedStep)
            }
        }
        return finishedSteps.toList()
    }
}

fun calculateDuration(steps: Set<Step>, minDuration: Int, workers: Int): Int {
    val work = Work((1..workers).map { worker -> Worker(worker) }.toList())
    val incompleteSteps = steps.toMutableSet()

    (0..Int.MAX_VALUE).forEach { second ->
        if (incompleteSteps.size == 0) {
            return second
        }

        val stepsBeingWorked = work.stepsInProgress()
        val nextStepsNotBeingWorked = getAllNextSteps(incompleteSteps.toList()).filterNot {stepsBeingWorked.contains(it)}.toMutableSet()

        while (nextStepsNotBeingWorked.isNotEmpty() && work.hasIdleWorkers()) {
            nextStepsNotBeingWorked.removeAll(work.startIdleWorkers(nextStepsNotBeingWorked.toList(), minDuration))
        }

        val finishedSteps = work.performWork()
        finishedSteps.forEach {step ->
            incompleteSteps.remove(step)
            incompleteSteps.forEach { it.dependencies.remove(step) }
        }
    }
    return Int.MAX_VALUE
}
