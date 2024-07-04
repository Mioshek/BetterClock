package com.mioshek.theclock.views.clock

import android.text.BoringLayout
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mioshek.mioshekassets.SliderWheelNumberPicker
import com.mioshek.theclock.controllers.AlarmsListViewModel
import com.mioshek.theclock.controllers.AlarmsUiState
import com.mioshek.theclock.db.AppViewModelProvider

@Composable
fun AlarmsListView(
    alarmsListViewModel: AlarmsListViewModel = viewModel(factory= AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val alarms = alarmsListViewModel.alarms

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        for (alarm in alarms){
            AlarmCard(alarm)
        }

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

@Composable
fun AlarmCard(
    alarm: AlarmsUiState,
    modifier: Modifier = Modifier
){
    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
            modifier = modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(text = "8:00", fontSize = 50.sp)
                    Text(text = "AM", fontSize = 20.sp)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SelectedDaysView(alarm.daysOfWeek)
                        Switch(checked = true, onCheckedChange = {}, modifier = modifier.padding(start = 10.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(text = "10 hours and 9 minutes from now", fontSize = 12.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = modifier.padding(10.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
            modifier = modifier.padding(start = 10.dp, end = 10.dp)
        ){
            Column {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
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
                            Text(text = "8:00", fontSize = 30.sp)
                            Text(text = "AM")
                        }
                    }

                    Box(
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = modifier
                        ) {
                            SelectedDaysView(alarm.daysOfWeek)
                            Switch(checked = true, onCheckedChange = {}, modifier = modifier.padding(start = 10.dp))
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .padding(start = 10.dp, bottom = 10.dp)
                        .fillMaxWidth()
                ){
                    Text(text = "Starts in 10 hours and 9 minutes")
                }
            }
        }
    }
}

@Composable
fun AlarmCreatorView(){
    Column {
//        SliderWheelNumberPicker(wheelValues = , startIndex = arrayOf(0,0,0))
    }
}

@Composable
fun SelectedDaysView(
    selectedDays: Array<Boolean>,
    modifier: Modifier = Modifier
){
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row{
        days.forEachIndexed{ index, value ->
            Text(
                text = value,
                color = if(selectedDays[index]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f),
                modifier = modifier.padding(2.dp),
                textDecoration = if(selectedDays[index]) TextDecoration.Underline else TextDecoration.None
            )
        }
    }
}




@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AlarmsListPreview(){
    AlarmsListView()
}