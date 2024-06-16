package com.mioshek.theclock.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.theclock.R
import com.mioshek.theclock.controllers.StopwatchUiState
import com.mioshek.theclock.controllers.StopwatchViewModel
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.data.getStringTime
import com.mioshek.theclock.ui.theme.TheClockTheme

@Composable
fun StopwatchView(
    modifier: Modifier = Modifier,
    stopwatchViewModel: StopwatchViewModel = viewModel()
) {
    val stopwatchUiState by stopwatchViewModel.stopwatchUiState.collectAsState()

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
                text = getStringTime(clock = stopwatchUiState.time, 0,4),
                fontSize = 50.sp,
                fontFamily = FontFamily.Serif,
            )
        }

        if(stopwatchUiState.stages.size > 0){
            Box(modifier = modifier
                .weight(4f)
                .fillMaxSize()){
                StagesView(
                    stopwatchUiState = stopwatchUiState,
                    modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                )
            }
        }

        StopwatchButtons(
            stopwatchViewModel,
            modifier
                .weight(0.8f)
                .fillMaxSize()
        )
    }
}

@Composable
fun StopwatchButtons(stopwatchViewModel: StopwatchViewModel, modifier: Modifier = Modifier){
    val stopwatchUiState by stopwatchViewModel.stopwatchUiState.collectAsState()
    Row(
        modifier = modifier
            .padding(25.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        when(stopwatchUiState.stopwatchState){
            TimingState.OFF -> {
                Button(
                    onClick = {
                        stopwatchViewModel.changeStopwatchState(TimingState.RUNNING)
                        stopwatchViewModel.runStopwatch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                }
            }

            TimingState.RUNNING -> {
                Button(
                    onClick = { stopwatchViewModel.addStage()},
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(painter = painterResource(id = R.drawable.flag), contentDescription = "NewLoop")
                }

                Button(
                    onClick = {
                        val timestamp = System.currentTimeMillis()
                        Storage.put("EndTime", timestamp)
                        stopwatchViewModel.changeStopwatchState(TimingState.PAUSED)
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(painter = painterResource(id = R.drawable.pause), contentDescription = "Pause")
                }
            }

            TimingState.PAUSED -> {
                Button(
                    onClick = {
                        stopwatchViewModel.changeStopwatchState(TimingState.OFF)
                        stopwatchViewModel.resetStopwatch()
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Clear")
                }

                Button(
                    onClick = {
                        stopwatchViewModel.changeStopwatchState(TimingState.RUNNING)
                        stopwatchViewModel.resumeStopwatch()
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                }
            }
        }
    }
}

@Composable
fun StagesView(
    stopwatchUiState: StopwatchUiState,
    modifier: Modifier = Modifier
){
    Column(
        modifier.fillMaxSize()
    ) {
        Row (
            modifier
                .padding(10.dp)
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Stage", modifier = modifier
                .weight(1f)
                .align(Alignment.CenterVertically))
            Text(text = "Stage Time", modifier = modifier
                .weight(2f)
                .align(Alignment.CenterVertically))
            Text(text = "Overall time", modifier = modifier
                .weight(2f)
                .align(Alignment.CenterVertically))
        }

        Divider(color = MaterialTheme.colorScheme.onBackground.copy(0.6f), thickness = 1.dp)

        LazyColumn(
            modifier
                .weight(5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            itemsIndexed(stopwatchUiState.stages){i, stage ->
                Row(
                    modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (i < 10) "0$i" else "$i", modifier = modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically))
                    Text(text = "Stage Time", modifier = modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically))
                    Text(text = getStringTime(stage, 0,4), modifier = modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically))
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