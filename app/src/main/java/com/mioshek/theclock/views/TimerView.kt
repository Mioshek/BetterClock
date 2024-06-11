package com.mioshek.theclock.views

import android.widget.ProgressBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.theclock.R
import com.mioshek.theclock.controllers.TimerListViewModel
import com.mioshek.theclock.controllers.TimerUiState
import com.mioshek.theclock.data.TimingState


@Composable
fun TimerView(
    modifier: Modifier = Modifier,
    timerViewModel: TimerListViewModel = viewModel()
) {
    val timers = timerViewModel.timers
    val seconds = (0..59).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    val minutes = seconds
    val hours = (0..99).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()

    Column {
//        SliderWheelNumberPicker(
//            arrayOf(hours, minutes, seconds),
//            0,
//            onValueChange = {
//
//            }
//        )

        LazyColumn(
            modifier = modifier.fillMaxSize().padding(10.dp)
        ) {
            itemsIndexed(timers){index, timer ->
                SingleTimerView(timer, timerViewModel)
            }
        }
    }
}

@Composable
fun SingleTimerView(timer:TimerUiState, timerViewModel: TimerListViewModel, modifier: Modifier = Modifier){
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.2f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
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
            Text(text = "00:00:00", fontSize = 30.sp)
            when(timer.timerState){

                TimingState.OFF -> {
                    Icon(
                        painter = painterResource(id = R.drawable.play),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Play",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .clickable {
                                timerViewModel.updateTimer(
                                    TimerUiState(
                                        timer.index,
                                        timer.time,
                                        TimingState.RUNNING,
                                        timer.percentRemain
                                    )
                                )
                            }
                    )
                }

                TimingState.RUNNING -> {
                    Icon(
                        painter = painterResource(id = R.drawable.stop),
                        contentDescription = "NewLoop",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .clickable {
                                timerViewModel.updateTimer(
                                    TimerUiState(
                                        timer.index,
                                        timer.time,
                                        TimingState.OFF,
                                        timer.percentRemain
                                    )
                                )
                            }
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.pause),
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .clickable {
                                timerViewModel.updateTimer(
                                    TimerUiState(
                                        timer.index,
                                        timer.time,
                                        TimingState.PAUSED,
                                        timer.percentRemain
                                    )
                                )
                            }
                    )
                }

                TimingState.PAUSED -> {
                    Icon(
                        painter = painterResource(id = R.drawable.stop),
                        contentDescription = "NewLoop",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .clickable {
                                timerViewModel.updateTimer(
                                    TimerUiState(
                                        timer.index,
                                        timer.time,
                                        TimingState.OFF,
                                        timer.percentRemain
                                    )
                                )
                            }
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.play),
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .clickable {
                                timerViewModel.updateTimer(
                                    TimerUiState(
                                        timer.index,
                                        timer.time,
                                        TimingState.RUNNING,
                                        timer.percentRemain
                                    )
                                )
                            }
                    )
                }
            }

            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "NewLoop",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp)
                        .clickable {
                            timerViewModel.updateTimer(
                                TimerUiState(
                                    timer.index,
                                    timer.time,
                                    TimingState.OFF,
                                    timer.percentRemain
                                )
                            )
                        }
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start,
            modifier = modifier.padding(10.dp)
        ) {
            Divider(
                modifier = modifier
                    .fillMaxWidth((timer.percentRemain - 25) / 100f),
                color = MaterialTheme.colorScheme.secondary, thickness = 3.dp
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SliderWheelNumberPickerPreview(){
    TimerView()
}