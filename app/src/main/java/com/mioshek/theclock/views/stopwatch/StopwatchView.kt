package com.mioshek.theclock.views.stopwatch

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.theclock.R
import com.mioshek.theclock.assets.StringFormatters.Companion.getStringTime
import com.mioshek.theclock.controllers.StopwatchUiState
import com.mioshek.theclock.controllers.StopwatchViewModel
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.ui.theme.TheClockTheme
import kotlinx.coroutines.delay

@Composable
fun StopwatchView(
    modifier: Modifier = Modifier,
    stopwatchViewModel: StopwatchViewModel = viewModel()
) {
    val stopwatchUiState by stopwatchViewModel.stopwatchUiState.collectAsState()
    var elementsSize by remember { mutableStateOf(arrayOf(0.88f, 0f, 0.12f)) }
    var stopwatchAlignment by remember { mutableStateOf(Alignment.Center)}

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = modifier
            .weight(elementsSize[0])
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
            contentAlignment = stopwatchAlignment){
            Text(
                text = getStringTime(clock = stopwatchUiState.time, 0, 4),
                fontSize = 50.sp,
                fontFamily = FontFamily.Serif,
            )
        }

        if (stopwatchUiState.stages.isNotEmpty()) {
            stopwatchAlignment = Alignment.BottomCenter
            elementsSize = arrayOf(0.2f, 0.68f, 0.12f)

            Box(modifier = modifier
                .weight(elementsSize[1])
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp),
                contentAlignment = Alignment.TopCenter){

                StagesView(
                    stopwatchUiState = stopwatchUiState,
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
            }
        }
        else{
            elementsSize = arrayOf(0.88f, 0f, 0.12f)
            stopwatchAlignment = Alignment.Center
        }

        Box(modifier = modifier
            .weight(elementsSize[2])
            .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            StopwatchButtons(stopwatchViewModel = stopwatchViewModel)
        }
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
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Play",
                    modifier = modifier
                        .size(50.dp)
                        .clickable {
                            stopwatchViewModel.changeStopwatchState(TimingState.RUNNING)
                            stopwatchViewModel.runStopwatch()
                        },
                )
            }

            TimingState.RUNNING -> {
                Icon(
                    painter = painterResource(id = R.drawable.flag), contentDescription = "NewLoop",
                    modifier = modifier
                        .size(50.dp)
                        .clickable { stopwatchViewModel.addStage() },
                )

                Icon(
                    painter = painterResource(id = R.drawable.pause),
                    contentDescription = "Pause",
                    modifier = modifier
                        .size(50.dp)
                        .clickable {
                            val timestamp = System.currentTimeMillis()
                            Storage.put("EndTime", timestamp)
                            stopwatchViewModel.changeStopwatchState(TimingState.PAUSED)
                        },
                )
            }

            TimingState.PAUSED -> {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Clear",
                    modifier = modifier
                        .size(50.dp)
                        .clickable {
                            stopwatchViewModel.changeStopwatchState(TimingState.OFF)
                            stopwatchViewModel.resetStopwatch()
                        },
                )

                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Play",
                    modifier = modifier
                        .size(50.dp)
                        .clickable {
                            stopwatchViewModel.changeStopwatchState(TimingState.RUNNING)
                            stopwatchViewModel.resumeStopwatch()
                        },
                )
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
    ) {
        val listState = rememberLazyListState()

        // This will scroll to the last item whenever the stages list is updated
        LaunchedEffect(stopwatchUiState.stages.size) {
            delay(250)
            if (stopwatchUiState.stages.isNotEmpty()) {
                listState.animateScrollToItem(stopwatchUiState.stages.size - 1)
            }
        }

        Row (
            modifier
                .padding(start = 0.dp, end = 10.dp, top = 5.dp, bottom = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Stage")
            Text(text = "Stage Time")
            Text(text = "Overall time")
        }

        Divider(color = MaterialTheme.colorScheme.onBackground.copy(0.6f), thickness = 1.dp)

        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.Top,
            reverseLayout = true
        ) {
            itemsIndexed(stopwatchUiState.stages) { i, stage ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp, start = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (i < 9) "0${i+1}" else "${i+1}",
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = "Stage Time",
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = getStringTime(stage, 0, 4),
                        fontFamily = FontFamily.Default
                    )
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