package mhalma.advent201807

data class Worker(val id: Int, var currentStep: Step = Step('0'), var secondsLeft: Int = 0) {

    fun assignStep(step: Step, minDuration: Int) {
        this.currentStep = step
        this.secondsLeft = step.getStepTime(minDuration)
    }

    fun performWork(): Step? {
        this.secondsLeft -= 1
        if (this.secondsLeft == 0) {
            return removeCurrentStep()
        }
        return null
    }

    private fun removeCurrentStep(): Step {
        val step = this.currentStep
        this.currentStep = Step.EMPTY_STEP
        return step
    }

    fun notWorking(): Boolean {
        return this.secondsLeft == 0 && this.currentStep == Step.EMPTY_STEP
    }
}
