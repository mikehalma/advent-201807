package mhalma.advent201807

import kotlin.streams.toList

class Work(private val workers: List<Worker>, val incompleteSteps: Steps = Steps(), private val minDuration: Int = 0) {

    fun stepsInProgress(): List<Step> {
        return this.workers.filterNot {it.notWorking()}.map {it.currentStep}
    }

    fun hasIdleWorkers(): Boolean {
        return this.workers.count {it.notWorking()} > 0
    }

    fun startIdleWorkers(availableSteps: List<Step>, minDuration: Int): List<Step> {
        return availableSteps.stream()
                .peek {firstIdle()?.assignStep(it, minDuration)}
                .filter {stepsInProgress().contains(it)}
                .toList()
    }

    private fun firstIdle(): Worker? {
        return this.workers
                .filter {it.notWorking()}
                .firstOrNull()
    }

    fun performWork() {
        this.workers
                .filter {!it.notWorking()}
                .map {it.performWork()}
                .filter {it != null}
                .map {it as Step}
                .map {incompleteSteps.remove(it)}
    }

    fun assignWork() {
        val stepsBeingWorked = stepsInProgress()
        val availableStepsNotBeingWorked = incompleteSteps.getAvailableSteps().filterNot { stepsBeingWorked.contains(it) }.toMutableSet()

        while (availableStepsNotBeingWorked.isNotEmpty() && hasIdleWorkers()) {
            val assignedSteps = startIdleWorkers(availableStepsNotBeingWorked.toList(), minDuration)
            availableStepsNotBeingWorked.removeAll(assignedSteps)
        }

    }
}
