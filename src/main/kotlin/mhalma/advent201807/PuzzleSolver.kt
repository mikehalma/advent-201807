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
    val work = Work((1..workers).map {Worker(it)}.toList(), Steps(steps), minDuration)
    (0..Int.MAX_VALUE).forEach { second ->
        with (work) {
            if (incompleteSteps.isEmpty()) {
                return second
            }
            assignWork()
            performWork()
        }
    }
    throw IllegalStateException("The puzzle appears to be unsolvable.")
}
