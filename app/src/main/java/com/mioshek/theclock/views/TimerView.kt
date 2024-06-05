package com.mioshek.theclock.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mioshek.mioshekassets.SliderWheelNumberPicker
import com.mioshek.mioshekassets.rememberPickerState
import com.mioshek.theclock.R

enum class TimerState{

}

@Composable
fun TimerView(modifier: Modifier = Modifier) {
    var timerState by remember { mutableStateOf(TimingState.OFF) }
    val seconds = (0..59).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    val minutes = seconds
    val hours = (0..99).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    Box(

    ) {
        SliderWheelNumberPicker(
            arrayOf(hours, minutes, seconds),
            1
        )

        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            when(timerState){
                TimingState.OFF -> {
                    Button(
                        onClick = {
                            timerState = TimingState.RUNNING
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }

                TimingState.RUNNING -> {
                    Button(
                        onClick = { timerState = TimingState.OFF },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "NewLoop")
                    }

                    Button(
                        onClick = { timerState = TimingState.PAUSED },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.pause), contentDescription = "Pause")
                    }
                }

                TimingState.PAUSED -> {
                    Button(
                        onClick = { timerState = TimingState.OFF },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Clear")
                    }

                    Button(
                        onClick = { timerState = TimingState.RUNNING },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SliderWheelNumberPickerPreview(){
    TimerView()
}