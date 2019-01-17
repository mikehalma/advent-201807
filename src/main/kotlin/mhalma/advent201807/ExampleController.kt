package mhalma.advent201807

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ExampleController() {

    @GetMapping("/calculateStepOrder/{input}")
    fun calculateStepOrder(@PathVariable input: String) =
        ResponseEntity( StepOrder(calculateStepOrder(getStepsFromFile("static/$input.txt"))), HttpStatus.OK)

    @GetMapping("/calculateDuration/{input}/{minDuration}/{workers}")
    fun calculateDuration(@PathVariable input: String, @PathVariable minDuration: Int, @PathVariable workers: Int) =
            ResponseEntity(Duration(calculateDuration(parseSteps(getStepsFromFile("static/$input.txt")), minDuration, workers)), HttpStatus.OK)
}

class StepOrder(val order: String)
class Duration(val duration: Int)