package com.mioshek.theclock.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.mioshekassets.SliderWheelNumberPicker
import com.mioshek.theclock.R
import com.mioshek.theclock.assets.StringFormatters
import com.mioshek.theclock.assets.StringFormatters.Companion.getStringTime
import com.mioshek.theclock.controllers.TimerListViewModel
import com.mioshek.theclock.controllers.TimerUiState
import com.mioshek.theclock.data.TimingState

@Composable
fun TimerView(
    modifier: Modifier = Modifier,
    timerViewModel: TimerListViewModel = viewModel()
) {
    var pickingTime by remember { mutableStateOf(false) }
    val timers = timerViewModel.timers
    val seconds = (0..59).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    val minutes = seconds
    val hours = (0..99).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()

    Box(modifier = modifier.fillMaxSize()){
        Box(modifier = modifier.fillMaxSize()){
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    userScrollEnabled = !pickingTime,
                    modifier = modifier
                        .fillMaxHeight(0.9f)
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    itemsIndexed(timers){index, timer ->
                        SingleTimerView(timer, timerViewModel)
                    }
                }

                if(!pickingTime){
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "NewTimer",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp)
                            .clickable(enabled = !pickingTime) {
                                pickingTime = true
                            }
                    )
                }
            }
        }

        if (pickingTime){
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.6f))
            ){
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    SliderWheelNumberPicker(
                        arrayOf(hours, minutes, seconds),
                        0,
                        alignment = Alignment.Bottom,
                        onValueChange = {

                        },
                        modifier = modifier.weight(3f),
                    )

                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .weight(1f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Cancel",
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                                .clickable {
                                    pickingTime = false
                                }
                        )
                        
                        Spacer(modifier = modifier.padding(10.dp))
                        
                        Text(
                            text = "Save",
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                                .clickable {

                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleTimerView(timer:TimerUiState, timerViewModel: TimerListViewModel, modifier: Modifier = Modifier){
    val progressBarGradient = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0f to Color.Red.copy(0.5f),
            0.5f to Color.Yellow.copy(0.5f),
            1.0f to Color.Green.copy(0.5f)
        )
    )
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, 10.dp)
        ) {
            Text(text = getStringTime(timer.updatableTime, 0,3), fontSize = 30.sp)
            when(timer.timerState){

                TimingState.OFF -> {
                    TimerIcon(R.drawable.play, "Play", timerViewModel, timer, TimingState.RUNNING)
                }

                TimingState.RUNNING -> {
                    TimerIcon(R.drawable.stop, "Reset", timerViewModel, timer, TimingState.OFF)

                    TimerIcon(R.drawable.pause, "Pause", timerViewModel, timer, TimingState.PAUSED)
                }

                TimingState.PAUSED -> {
                    TimerIcon(R.drawable.stop, "Reset", timerViewModel, timer, TimingState.OFF)

                    TimerIcon(R.drawable.play, "Play", timerViewModel, timer, TimingState.RUNNING)
                }
            }

            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                TimerIcon(R.drawable.delete, "NewLoop", timerViewModel, timer, TimingState.OFF)
            }
        }

        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = modifier.padding(10.dp)
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth(timer.remainingProgress)
                    .size(2.dp)
                    .background(progressBarGradient),
            )
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ){
                Text(text = StringFormatters.formatPercentage(timer.remainingProgress) + "%")
            }
        }
    }
}

@Composable
fun TimerIcon(
    icon: Int,
    description: String,
    timerViewModel: TimerListViewModel,
    timer: TimerUiState,
    timingState: TimingState,
){
    Icon(
        painter = painterResource(icon),
        contentDescription = description,
        modifier = Modifier
            .size(40.dp)
            .padding(5.dp)
            .clickable {
                timerViewModel.updateTimer(
                    TimerUiState(
                        timer.id,
                        timer.initialTime,
                        timer.updatableTime,
                        timingState,
                        timer.remainingProgress
                    )
                )
                when (timingState) {
                    TimingState.OFF -> {
                        timerViewModel.updateTimer(TimerUiState(id = timer.id))
                    }

                    TimingState.RUNNING -> {
                        timerViewModel.runTimer(timer.id)
                    }

                    TimingState.PAUSED -> {}
                }
            }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SliderWheelNumberPickerPreview(){
    TimerView()
}