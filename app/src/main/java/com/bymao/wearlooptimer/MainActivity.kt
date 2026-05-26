package com.bymao.wearlooptimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import kotlinx.coroutines.delay

enum class TimePart { HOUR, MINUTE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TimerScreen()
            }
        }
    }
}

@Composable
private fun TimerScreen() {
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(20) }
    var selectedPart by remember { mutableStateOf(TimePart.MINUTE) }
    var running by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableLongStateOf(hour * 3600L + minute * 60L) }

    LaunchedEffect(hour, minute, running) {
        if (!running) {
            remainingSeconds = hour * 3600L + minute * 60L
        }
    }

    LaunchedEffect(running) {
        while (running && remainingSeconds > 0) {
            delay(1_000)
            remainingSeconds -= 1
        }
        if (remainingSeconds <= 0) {
            running = false
        }
    }

    val displayHour = (remainingSeconds / 3600).toInt()
    val displayMinute = ((remainingSeconds % 3600) / 60).toInt()

    val hourPickerState = rememberPickerState(initialNumberOfOptions = 24, initiallySelectedOption = hour)
    val minutePickerState = rememberPickerState(initialNumberOfOptions = 60, initiallySelectedOption = minute)

    LaunchedEffect(hourPickerState.selectedOption) {
        hour = hourPickerState.selectedOption
    }
    LaunchedEffect(minutePickerState.selectedOption) {
        minute = minutePickerState.selectedOption
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayHour.toString(),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = if (selectedPart == TimePart.HOUR) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedPart = TimePart.HOUR }
                )
                Text(text = ":", color = Color.White, fontSize = 40.sp, modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "%02d".format(displayMinute),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = if (selectedPart == TimePart.MINUTE) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedPart = TimePart.MINUTE }
                )
            }

            Box(modifier = Modifier.height(90.dp), contentAlignment = Alignment.Center) {
                if (selectedPart == TimePart.HOUR) {
                    Picker(
                        state = hourPickerState,
                        contentDescription = "hour_picker",
                        modifier = Modifier.size(64.dp)
                    ) { option ->
                        Text(text = option.toString(), color = Color.White)
                    }
                } else {
                    Picker(
                        state = minutePickerState,
                        contentDescription = "minute_picker",
                        modifier = Modifier.size(64.dp)
                    ) { option ->
                        Text(text = "%02d".format(option), color = Color.White)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(label = "开始", symbol = "▶") {
                if (remainingSeconds > 0) running = true
            }
            ActionButton(label = "暂停", symbol = "⏸") {
                running = false
            }
            ActionButton(label = "结束", symbol = "⏹") {
                running = false
                remainingSeconds = 0
            }
        }
    }
}

@Composable
private fun ActionButton(label: String, symbol: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF222222)),
            modifier = Modifier.size(44.dp)
        ) {
            Text(text = symbol, color = Color.White)
        }
        Text(text = label, color = Color.White, fontSize = 10.sp)
    }
}
