package com.mioshek.theclock.views.clock

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.mioshekassets.SliderWheelNumberPicker
import com.mioshek.theclock.controllers.AlarmsListViewModel
import com.mioshek.theclock.controllers.AlarmUiState
import com.mioshek.theclock.data.TimeFormatter
import com.mioshek.theclock.db.AppViewModelProvider

@Composable
fun AlarmsListView(
    alarmsListViewModel: AlarmsListViewModel = viewModel(factory= AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val alarms = alarmsListViewModel.alarms
    var showCreatingWindow by remember {mutableStateOf(false)}
    val borderRadius = if (showCreatingWindow) 8.dp else 0.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .blur(borderRadius),
    ){
        LazyColumn(
            modifier = modifier.padding(top = 40.dp)
        ) {
            items(alarms.size){
                AlarmCard(alarm = alarms[it])
            }
        }

        if (!showCreatingWindow){
            Box(modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ){
                Box(modifier = modifier
                    .padding(16.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onBackground.copy(0.6f),
                        RoundedCornerShape(50.dp)
                    )
                    .clickable { showCreatingWindow = true }
                ){
                    Icon(
                        painter = rememberVectorPainter(image= Icons.Default.Add),
                        contentDescription = "Add",
                        modifier.size(40.dp)
                    )
                }
            }
        }
    }

    if (showCreatingWindow){
        Box(modifier = modifier
            .background(MaterialTheme.colorScheme.background.copy(0.7f))
            .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
            AlarmCreatorView(alarmsViewModel = alarmsListViewModel, {showCreatingWindow = false})
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmUiState,
    modifier: Modifier = Modifier
){
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
        modifier = modifier.padding(10.dp)
    ){
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = modifier.padding(4.dp)
//        ) {
//            Box(){
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    val text = TimeFormatter.format(alarm.initialTime,false)
//                    Text(
//                        text = text[0],
//                        fontSize = 40.sp,
//                        fontFamily = FontFamily(Typeface.MONOSPACE),
//                    )
//                    if (text.size == 2){
//                        Text(
//                            text = text[1],
//                            fontSize = 20.sp,
//                            fontFamily = FontFamily(Typeface.MONOSPACE),
//                        )
//                    }
//                }
//            }
//
//
//            Column {
//                Box(
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = modifier
//                    ) {
//                        SelectedDaysView(alarm.daysOfWeek, onClick={}, modifier = modifier.weight(0.8f))
//                        Switch(checked = true, onCheckedChange = {}, modifier = modifier
//                            .padding(start = 10.dp)
//                            .weight(0.4f))
//                    }
//                }
//
//                Box(
//                contentAlignment = Alignment.CenterEnd,
//                modifier = modifier
//                    .padding(bottom = 5.dp)
//                    .fillMaxWidth()
//                ){
//                    Text(text = "Starts in 10 hours and 9 minutes", fontSize = 12.sp)
//                }
//            }
//        }
        Column {
            Row (
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ){
                Box(
                    contentAlignment = Alignment.CenterStart
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val text = TimeFormatter.format(alarm.initialTime,false)
                        Text(
                            text = text[0],
                            fontSize = 40.sp,
                            fontFamily = FontFamily(Typeface.MONOSPACE),
                        )
                        if (text.size == 2){
                            Text(
                                text = text[1],
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Typeface.MONOSPACE),
                            )
                        }
                    }
                }

                Box(
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                    ) {
                        SelectedDaysView(alarm.daysOfWeek, onClick={}, modifier = modifier.weight(0.8f))
                        Switch(checked = true, onCheckedChange = {}, modifier = modifier
                            .padding(start = 10.dp)
                            .weight(0.4f))
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .padding(bottom = 5.dp)
                    .fillMaxWidth()
            ){
                Text(text = "Starts in 10 hours and 9 minutes", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AlarmCreatorView(
    alarmsViewModel: AlarmsListViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val createdAlarmUiState by alarmsViewModel.alarm.collectAsState()
    var time by remember{ mutableIntStateOf(0) }
    var startPickerIndex by remember{ mutableStateOf(arrayOf(0,0))}
    var tempIndexes = startPickerIndex

    val hours = (0..23).map { "$it" }.toTypedArray()
    val minutes = (0..59).map { i -> if (i < 10) "0$i" else "$i" }.toTypedArray()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .weight(0.4f)
                .fillMaxSize()
        ) {
            SliderWheelNumberPicker(
                wheelValues = arrayOf(hours, minutes),
                startIndex = startPickerIndex,
                showSeparator = true,
                onValueChange = {
                    time = (it[0].toInt() * 60 + it[1].toInt())
                    tempIndexes = arrayOf(hours.size * 100 + it[0].toInt(), minutes.size * 100 + it[1].toInt())
                }
            )
        }

        Box(
            modifier = modifier
                .weight(0.1f)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            SelectedDaysView(
                selectedDays = createdAlarmUiState.daysOfWeek,
                textPadding = 10.dp,
                onClick = {
                    startPickerIndex = tempIndexes
                    val startingSelectedDays = createdAlarmUiState.daysOfWeek.copyOf()
                    startingSelectedDays[it] = !startingSelectedDays[it]
                    alarmsViewModel.changeUiState(createdAlarmUiState.copy(daysOfWeek = startingSelectedDays))
                }
            )
        }

        Box(
            modifier = modifier
                .weight(0.1f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            TextField(
                value = createdAlarmUiState.name ?: "",
                onValueChange = {
                    alarmsViewModel.changeUiState(createdAlarmUiState.copy(name = it))
                    startPickerIndex = tempIndexes
                },
                placeholder = { Text(text = "Name") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                )
            )
        }

        Box(
            modifier = modifier
                .weight(0.3f)
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {

                        }
                ) {
                    Text(text = "Alarm sound")
                    Text(text = "Chosen Alarm Sound")
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(text = "Vibration")
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }

        Box(modifier = modifier.weight(0.1f)) {
            var boxWidth by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        boxWidth = with(density) { it.size.width.toDp() }
                    }
            ) {
                Box(modifier = modifier.clickable { onClick() }) {
                    Text(text = "Cancel")
                }

                Box(
                    modifier = modifier
                        .clickable {
                            alarmsViewModel.upsert(
                                createdAlarmUiState.copy(
                                    initialTime = time,
                                    ringTime = time
                                )
                            )
                            onClick()
                            Log.d(
                                "Created:",
                                alarmsViewModel.alarms
                                    .last()
                                    .toString()
                            )
                        }
                        .padding(start = boxWidth / 3)
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun SelectedDaysView(
    selectedDays: Array<Boolean>,
    textPadding: Dp = 0.dp,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ){
        days.forEachIndexed{ index, value ->
            Box(
                modifier = modifier
                    .clickable {
                        onClick(index)
                    }
            ){
                val color = if(selectedDays[index])
                    MaterialTheme.colorScheme.primary
                else if (index == selectedDays.size -1) Color.Red.copy(0.8f)
                else MaterialTheme.colorScheme.onBackground.copy(0.4f)
                Text(
                    text = value,
                    color = color,
                    modifier = modifier.padding(textPadding),
                    textDecoration = if(selectedDays[index]) TextDecoration.Underline else TextDecoration.None,
                    fontFamily = FontFamily(Typeface.MONOSPACE),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AlarmCardPreview(){
    AlarmCard(alarm = AlarmUiState())
}