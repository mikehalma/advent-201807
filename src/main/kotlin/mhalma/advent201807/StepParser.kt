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

data class Worker(val id: Int, var currentStep: Step = Step('0'), var secondsLeft: Int = 0)

class Work(val workers: List<Worker>) {
    fun stepsInProgress(): List<Step> {
        return this.workers.map {it.currentStep}.filter {it != Step('0')}
    }

    fun hasIdleWorkers(): Boolean {
        return this.workers.count {it.currentStep == Step('0')} > 0
    }

    fun startIdleWorkers(availableSteps: List<Step>, minDuration: Int): List<Step> {
        val assignedSteps = mutableListOf<Step>()
        val availableStepsRemaining = availableSteps.toMutableSet()
        this.workers.filter {it.secondsLeft == 0}.forEach { worker ->
            if (availableStepsRemaining.size > 0) {
                val nextStep = availableStepsRemaining.first()
                availableStepsRemaining.remove(nextStep)
                worker.currentStep = nextStep
                worker.secondsLeft = getStepTime(nextStep, minDuration)
                assignedSteps.add(nextStep)
            }
        }
        return assignedSteps.toList()
    }

    fun performWork(): List<Step> {
        val finishedSteps = mutableListOf<Step>()
        this.workers.filter {it.secondsLeft > 0}.forEach {worker ->
            worker.secondsLeft -= 1
            if (worker.secondsLeft == 0) {
                finishedSteps.add(worker.currentStep)
                worker.currentStep = Step('0')
            }
        }
        return finishedSteps.toList()
    }
}

fun calculateDuration(steps: MutableSet<Step>, minDuration: Int, workers: Int): Int {
    val work = Work((1..workers).map { worker -> Worker(worker) }.toList())
    (0..Int.MAX_VALUE).forEach { second ->
        if (steps.size == 0) {
            return second
        }

        val stepsBeingWorked = work.stepsInProgress()
        val nextStepsNotBeingWorked = getAllNextSteps(steps.toList()).filterNot {stepsBeingWorked.contains(it)}.toMutableSet()

        while (nextStepsNotBeingWorked.size > 0 && work.hasIdleWorkers()) {
            nextStepsNotBeingWorked.removeAll(work.startIdleWorkers(nextStepsNotBeingWorked.toList(), minDuration))
        }

        val finishedSteps = work.performWork()
        finishedSteps.forEach {step ->
            steps.remove(step)
            steps.forEach { it.dependencies.remove(step) }
        }
    }
    return Int.MAX_VALUE
}


fun getStepTime(step: Step, minDuration: Int): Int {
    return 'A'.rangeTo('Z').indexOf(step.id) + minDuration + 1
}