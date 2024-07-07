package com.mioshek.theclock.views.clock

import android.graphics.Typeface
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.mioshek.theclock.ui.theme.bodyFontFamily
import com.mioshek.theclock.ui.theme.displayFontFamily

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
            itemsIndexed(alarms){index, value ->
                AlarmCard(index = index, alarm = value, alarmsListViewModel)
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
    index: Int,
    alarm: AlarmUiState,
    alarmsViewModel: AlarmsListViewModel,
    modifier: Modifier = Modifier
){
    var extended by remember{mutableStateOf(false)}

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
        modifier = modifier
            .padding(10.dp)
            .clickable {
                extended = !extended
            }
    ){
        Column(
            modifier = modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ){
                Box(
                    contentAlignment = Alignment.CenterStart,
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val text = TimeFormatter.format(alarm.initialTime, true)
                        Text(
                            text = text[0],
                            fontSize = 50.sp,
                            fontFamily = displayFontFamily,
                            fontWeight = FontWeight.W600,
                        )
                        Text(
                            text = text[1],
                            fontSize = 50.sp,
                            fontFamily = displayFontFamily,
                            fontWeight = FontWeight.W600,
                            modifier = modifier.padding(end = 5.dp)
                        )
                        if (text.size == 3){
                            Text(
                                text = text[2],
                                fontSize = 25.sp,
                                fontFamily = displayFontFamily,
                            )
                        }
                    }
                }

                Box(
                ) {
                    Switch(
                        checked = alarm.enabled,
                        onCheckedChange = {
                            alarmsViewModel.toggleAlarm(index)
                        },
                        modifier = modifier
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxWidth()
            ){
                Box(modifier = modifier.fillMaxWidth(0.8f)){
                    SelectedDaysView(
                        alarm.daysOfWeek,
                        onClick={
                            if (extended){
                                val startingSelectedDays = alarm.daysOfWeek.copyOf()
                                startingSelectedDays[it] = !startingSelectedDays[it]
                                alarmsViewModel.upsert(index, alarm.copy(daysOfWeek = startingSelectedDays))
                            }
                        }
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .padding(bottom = 5.dp, top = 5.dp)
                    .fillMaxWidth()
            ){
                Text(
                    text = TimeFormatter.calculateTimeLeft(alarm.initialTime, alarm.daysOfWeek),
                    fontSize = 12.sp,
                    fontFamily = bodyFontFamily,
                    fontWeight = FontWeight.Light
                )
            }

            if (extended){
                // Settings
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
                isFormView = true,
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
                                null,
                                createdAlarmUiState.copy(
                                    initialTime = time,
                                    ringTime = time
                                )
                            )
                            onClick()
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
    isFormView: Boolean = false,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val padding = if(isFormView) 10.dp else 0.dp
    val fontSize = if(isFormView) 18.sp else 14.sp

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
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
                    modifier = modifier.padding(padding),
                    textDecoration = if(selectedDays[index]) TextDecoration.Underline else TextDecoration.None,
                    fontFamily = bodyFontFamily,
                    fontSize = fontSize,
                    fontWeight = FontWeight.W900
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AlarmCardPreview(){
//    AlarmCard(alarm = AlarmUiState())
}