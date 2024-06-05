package com.mioshek.theclock.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mioshek.theclock.R
import com.mioshek.theclock.ui.theme.TheClockTheme

enum class TimingState{
    RUNNING,
    OFF,
    PAUSED,
}

@Composable
fun StopwatchView(modifier: Modifier = Modifier) {
    var stopwatchState by remember { mutableStateOf(TimingState.OFF) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){
            Text(
                text = "00:00:00",
                fontSize = 70.sp,
                fontFamily = FontFamily.Serif,
            )
        }

        Row(
            modifier = modifier
                .weight(1f)
                .padding(25.dp),
            verticalAlignment = Alignment.Bottom
            ) {
            when(stopwatchState){
                TimingState.OFF -> {
                    Button(
                        onClick = {
                            stopwatchState = TimingState.RUNNING
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }

                TimingState.RUNNING -> {
                    Button(
                        onClick = { /*TODO START */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.flag), contentDescription = "NewLoop")
                    }

                    Button(
                        onClick = { stopwatchState = TimingState.PAUSED },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.pause), contentDescription = "Pause")
                    }
                }

                TimingState.PAUSED -> {
                    Button(
                        onClick = { stopwatchState = TimingState.OFF },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Clear")
                    }

                    Button(
                        onClick = { stopwatchState = TimingState.RUNNING },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StopwatchPreview(){
    TheClockTheme {
        StopwatchView()
    }
}