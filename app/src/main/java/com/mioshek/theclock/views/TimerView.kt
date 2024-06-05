package com.mioshek.theclock.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mioshek.mioshekassets.SliderWheelNumberPicker
import com.mioshek.mioshekassets.rememberPickerState

@Composable
fun TimerView(modifier: Modifier = Modifier) {
    val seconds = (0..59).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    val minutes = seconds
    val hours = (0..99).map { i -> if (i < 10) "0$i" else "$i"  }.toTypedArray()
    Column(

    ) {
        val valuesPickerState = rememberPickerState()

        SliderWheelNumberPicker(
            arrayOf(hours, minutes, seconds),
            valuesPickerState,
            1
        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SliderWheelNumberPickerPreview(){
    TimerView()
}