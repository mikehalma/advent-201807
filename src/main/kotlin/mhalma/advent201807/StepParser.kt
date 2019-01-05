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

data class WorkerActivity(val id: Int, var currentStep: Step, var secondsLeft: Int)

fun calculateDuration(steps: MutableSet<Step>, minDuration: Int, workers: Int): Int {
    var workerActivities = (1..workers).map { worker -> WorkerActivity(worker,Step('0'), 0) }.toList()
    (0..Int.MAX_VALUE).forEach { second ->
        if (steps.size == 0) {
            return second
        }

        val stepsBeingWorked = workerActivities.map {it.currentStep}.filter {it != Step('0')}
        val nextStepsNotBeingWorked = getAllNextSteps(steps.toList()).filterNot {stepsBeingWorked.contains(it)}.toMutableSet()

        while (nextStepsNotBeingWorked.size > 0 && workerActivities.count {it.currentStep == Step('0')} > 0) {

            workerActivities = workerActivities.map { workerActivity ->
                if (workerActivity.secondsLeft > 0) {
                    workerActivity
                } else {
                    if (nextStepsNotBeingWorked.size > 0) {
                        workerActivity.currentStep = nextStepsNotBeingWorked.first()
                        workerActivity.secondsLeft = getStepTime(workerActivity.currentStep, minDuration)
                        nextStepsNotBeingWorked.remove(workerActivity.currentStep)
                    }
                   workerActivity
                }
            }.toList()
        }

        workerActivities = workerActivities.map { workerActivity ->
            if (workerActivity.secondsLeft > 0) {
                workerActivity.secondsLeft -= 1
                if (workerActivity.secondsLeft == 0) {
                    steps.remove(workerActivity.currentStep)
                    steps.forEach { it.dependencies.remove(workerActivity.currentStep) }
                    workerActivity.currentStep = Step('0')
                }

            }
            workerActivity
        }.toList()
    }
    return Int.MAX_VALUE
}


fun getStepTime(step: Step, minDuration: Int): Int {
    return 'A'.rangeTo('Z').indexOf(step.id) + minDuration + 1
}