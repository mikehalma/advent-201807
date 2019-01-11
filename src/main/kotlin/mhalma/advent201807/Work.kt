package mhalma.advent201807

import mhalma.advent201807.Step.Companion.EMPTY_STEP
import kotlin.streams.toList

class Work(private val workers: List<Worker>) {

    fun stepsInProgress(): List<Step> {
        return this.workers.map {it.currentStep}.filter {it != EMPTY_STEP}
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

    fun performWork(): List<Step> {
        return this.workers
                .filter {!it.notWorking()}
                .map {it.performWork()}
                .filter {it != null}
                .map {it as Step}
                .toList()
    }
}