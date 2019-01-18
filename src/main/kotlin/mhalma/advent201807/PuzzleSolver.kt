package mhalma.advent201807

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
