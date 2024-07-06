package com.mioshek.theclock.views.timer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.db.AppViewModelProvider
import com.mioshek.theclock.ui.theme.displayFontFamily
import kotlin.reflect.KFunction3

@Composable
fun TimerView(
    modifier: Modifier = Modifier,
    timerViewModel: TimerListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var pickingTime by remember { mutableStateOf(false) }
    var pickedTime by remember { mutableStateOf(TimerUiState()) }
    var showInvalidArgumentAllert by remember { mutableStateOf(false) }
    var pickedPreset by remember{ mutableStateOf(arrayOf(0,0,0))}
    var blurValue by remember { mutableStateOf(0.dp)}

    fun changePreset(hours: Int, minutes: Int, seconds: Int){
        pickedPreset = arrayOf(hours, minutes, seconds)
    }

    val timers = timerViewModel.timers
    val seconds = (0..59).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    val minutes = seconds
    val hours = (0..24).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    blurValue = if (pickingTime) 8.dp else 0.dp

    Box(modifier = modifier.fillMaxSize()){
        Box(modifier = modifier
            .fillMaxSize()
            .blur(radius = blurValue)){
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    userScrollEnabled = !pickingTime,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    itemsIndexed(timers){index, timer ->
                        if (timer.visible)  SingleTimerView(index, timer, timerViewModel)
                    }
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
                .blur(radius = blurValue),
            contentAlignment = Alignment.BottomCenter
        ){
            if(!pickingTime){
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "NewTimer",
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                        .clickable(enabled = !pickingTime) {
                            pickingTime = true
                        }
                        .padding(10.dp)
                )
            }
        }
        if (pickingTime){
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(0.7f))
            ){
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    SliderWheelNumberPicker(
                        arrayOf(hours, minutes, seconds),
                        pickedPreset,
                        alignment = Alignment.Bottom,
                        onValueChange = {
                            pickedTime = TimerUiState(initialTime = ClockTime(seconds = it[2], minutes = it[1], hours = it[0]))
                        },
                        modifier = modifier.fillMaxHeight(0.7f)
                    )

                    ExampleTimerPresetsList(
                        ::changePreset,
                        modifier = modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth()
                    )

                    Row(modifier = modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier
                            .padding(10.dp)
                            .clickable{pickingTime = false},
                            contentAlignment = Alignment.CenterEnd
                        ){
                            Text(
                                text = "Cancel",
                                fontFamily = displayFontFamily,
                                modifier = modifier
                                    .border(
                                        width = 1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(0.2f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                            )
                        }

                        Spacer(modifier = modifier.padding(10.dp))

                        Box(modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    if (pickedTime.initialTime == ClockTime()) {
                                        showInvalidArgumentAllert = true
                                    } else {
                                        timerViewModel.createTimer(pickedTime)
                                        pickingTime = false
                                    }
                                },
                            contentAlignment = Alignment.CenterStart
                        ){
                            Text(
                                text = "Save",
                                fontFamily = displayFontFamily,
                                modifier = modifier
                                    .border(
                                        width = 1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(0.2f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showInvalidArgumentAllert) ZeroPickedDialog(onDismiss = {showInvalidArgumentAllert = false})
    }
}

@Composable
fun SingleTimerView(
    index: Int,
    timer:TimerUiState,
    timerViewModel: TimerListViewModel,
    modifier: Modifier = Modifier
){
    var showConfirmAlert by remember { mutableStateOf(false) }

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
            .clickable { }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 15.dp)
        ) {
            Text(text = getStringTime(timer.updatableTime, 0,3), fontSize = 40.sp, fontFamily = displayFontFamily)
            when(timer.timerState){

                TimingState.OFF -> {
                    TimerIcon(index, R.drawable.play, "Play", timerViewModel, timer, TimingState.RUNNING)
                }

                TimingState.RUNNING -> {
                    TimerIcon(index, R.drawable.stop, "Reset", timerViewModel, timer, TimingState.OFF)

                    TimerIcon(index, R.drawable.pause, "Pause", timerViewModel, timer, TimingState.PAUSED)
                }

                TimingState.PAUSED -> {
                    TimerIcon(index, R.drawable.stop, "Reset", timerViewModel, timer, TimingState.OFF)

                    TimerIcon(index, R.drawable.play, "Play", timerViewModel, timer, TimingState.RUNNING)
                }
            }

            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                if(timer.timerState == TimingState.OFF){
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "DeleteTimer",
                        modifier = modifier.clickable {
                            showConfirmAlert = true
                        }
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            Box(
                modifier = modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(timer.remainingProgress)
                    .size(3.dp)
                    .background(progressBarGradient),
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                contentAlignment = Alignment.TopEnd
            ){
                Text(text = StringFormatters.formatPercentage(timer.remainingProgress) + "%", fontFamily = displayFontFamily,)
            }
        }
        if (showConfirmAlert){
            ConfirmAlert(
                onDismiss = {showConfirmAlert = false},
                onConfirm = {
                    timerViewModel.deleteTimer(timer.id, index)
                    showConfirmAlert = false
                }
            )
        }
    }
}

@Composable
fun TimerIcon(
    index: Int,
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
                val previousTimerState = timer.timerState
                timerViewModel.updateTimer(
                    index,
                    timer.copy(timerState = timingState)
                )

                when (timingState) {
                    TimingState.OFF -> {
                        timerViewModel.updateTimer(
                            index,
                            TimerUiState(id = timer.id, initialTime = timer.initialTime)
                        )
                    }

                    TimingState.RUNNING -> {
                        if (previousTimerState == TimingState.PAUSED) timerViewModel.resumeTimer(
                            index
                        )
                        else timerViewModel.runTimer(index)
                    }

                    TimingState.PAUSED -> {}
                }
            }
    )
}

@Composable
fun ExampleTimerPresetsList(onClick: KFunction3<Int, Int, Int, Unit>, modifier: Modifier = Modifier){
    val presets = mutableListOf(
        TimerUiState(name = "Make a Cup Of Tea", initialTime = ClockTime(minutes = 5)),
        TimerUiState(name = "Quick Shower", initialTime = ClockTime(minutes = 10)),
        TimerUiState(name = "Meditating", initialTime = ClockTime(minutes = 15)),
        TimerUiState(name = "Go For a Walk", initialTime = ClockTime(minutes = 20)),
        TimerUiState(name = "Power Nap", initialTime = ClockTime(minutes = 30)),
        TimerUiState(name = "Gym Session", initialTime = ClockTime(hours = 1))
    )

    LazyRow(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom
    ){
        itemsIndexed(presets){ _, item ->
            TimerPresetCard(onClick,item)
        }
    }
}

@Composable
fun TimerPresetCard(onClick: KFunction3<Int, Int, Int, Unit>, preset: TimerUiState, modifier: Modifier = Modifier){
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick(
                    preset.initialTime.hours.toInt(),
                    preset.initialTime.minutes.toInt(),
                    preset.initialTime.seconds.toInt()
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.padding(8.dp)
        ) {
            Text(text = preset.name, fontSize = 12.sp, fontFamily = displayFontFamily)
            Text(text = getStringTime(preset.initialTime, 0, 3), fontSize = 30.sp, fontFamily = displayFontFamily)
        }
    }
}

@Composable
fun ZeroPickedDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Invalid Value Of Picked Argument!", fontFamily = displayFontFamily) },
        text = { Text("Time Picked Cannot Be Zero!", fontFamily = displayFontFamily) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK", fontFamily = displayFontFamily)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", fontFamily = displayFontFamily)
            }
        }
    )
}

@Composable
fun ConfirmAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete", fontFamily = displayFontFamily) },
        text = { Text("Are you sure you want to delete timer?", fontFamily = displayFontFamily) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK", fontFamily = displayFontFamily)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", fontFamily = displayFontFamily)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SliderWheelNumberPickerPreview(){
    TimerView()
}