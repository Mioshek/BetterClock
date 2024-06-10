package com.mioshek.theclock.controllers

class TimerController {
}
//fun runTimer() {
//    // assuming the initial timer state is set from the UI
//    // we calculate the future - the millis timer will finish
//    val future = calculateFutureMillis()
//    val cycleTimeMs = 33
//    while (System.currentTimeMillis() < future - cycleTimeMs && uiState.stopwatchState == TimingState.RUNNING){
//        val currentTime = System.currentTimeMillis()
//        val remainingTime = future - currentTime
//
//        val time = getStopwatchTime(remainingTime)
//        uiState.update {currentState ->
//            currentState.copy(
//                time = time
//            )
//        }
//        delay(cycleTimeMs) // 30FPS
//    }
//    uiState.stopwatchState = TimingState.OFF
//    // TIMER FINISHED IDFK reset it to 0, send notifications?, blast ears
//}
//
//private fun calculateFutureMillis(): Long {
//    val time = uiState.time
//    return time.hours * 60 * 60 * 1000 + time.minutes * 60 * 1000 + time.seconds * 1000
//}