package com.bymao.wearlooptimer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.rememberPickerState
import kotlinx.coroutines.delay

enum class TimePart { HOUR, MINUTE }
enum class ScreenMode { TIMER, PICKER_HOUR, PICKER_MINUTE }
// Vibration pattern: wait 0ms, vibrate 400ms, wait 300ms, vibrate 400ms, wait 300ms, vibrate 400ms
private val VIBRATION_PATTERN = longArrayOf(0, 400, 300, 400, 300, 400)

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
    val context = LocalContext.current
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(20) }
    var running by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableLongStateOf(hour * 3600L + minute * 60L) }
    var screenMode by remember { mutableStateOf(ScreenMode.TIMER) }

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
        if (running && remainingSeconds <= 0) {
            running = false
            vibrateThreeTimes(context)
        }
    }

    val displayHour = (remainingSeconds / 3600).toInt()
    val displayMinute = ((remainingSeconds % 3600) / 60).toInt()
    val displaySecond = (remainingSeconds % 60).toInt()

    when (screenMode) {
        ScreenMode.TIMER -> {
            TimerMainView(
                displayHour = displayHour,
                displayMinute = displayMinute,
                displaySecond = displaySecond,
                running = running,
                onHourClick = { if (!running) screenMode = ScreenMode.PICKER_HOUR },
                onMinuteClick = { if (!running) screenMode = ScreenMode.PICKER_MINUTE },
                onStart = { if (remainingSeconds > 0) running = true },
                onPause = { running = false },
                onStop = { running = false; remainingSeconds = 0 }
            )
        }
        ScreenMode.PICKER_HOUR -> {
            PickerView(
                initialValue = hour,
                maxValue = 24,
                suffix = "小时",
                onConfirm = { value ->
                    hour = value
                    screenMode = ScreenMode.TIMER
                }
            )
        }
        ScreenMode.PICKER_MINUTE -> {
            PickerView(
                initialValue = minute,
                maxValue = 60,
                suffix = "分钟",
                onConfirm = { value ->
                    minute = value
                    screenMode = ScreenMode.TIMER
                }
            )
        }
    }
}

@Composable
private fun TimerMainView(
    displayHour: Int,
    displayMinute: Int,
    displaySecond: Int,
    running: Boolean,
    onHourClick: () -> Unit,
    onMinuteClick: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Time display - fills horizontal screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "%02d".format(displayHour),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onHourClick)
            )
            Text(
                text = ":",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(
                text = "%02d".format(displayMinute),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onMinuteClick)
            )
            Text(
                text = ":",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(
                text = "%02d".format(displaySecond),
                color = Color.Gray,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Buttons row - large icons, no text labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(icon = Icons.Filled.PlayArrow, contentDesc = "Start", onClick = onStart)
            ActionButton(icon = Icons.Filled.Pause, contentDesc = "Pause", onClick = onPause)
            ActionButton(icon = Icons.Filled.Stop, contentDesc = "Stop", onClick = onStop)
        }
    }
}

@Composable
private fun PickerView(
    initialValue: Int,
    maxValue: Int,
    suffix: String,
    onConfirm: (Int) -> Unit
) {
    val pickerState = rememberPickerState(
        initialNumberOfOptions = maxValue,
        initiallySelectedOption = initialValue
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onConfirm(pickerState.selectedOption) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Picker(
                    state = pickerState,
                    contentDescription = "${suffix}_picker",
                    modifier = Modifier.width(80.dp)
                ) { option ->
                    Text(
                        text = "%02d".format(option),
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = suffix,
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 6.dp, end = 10.dp)
                )
            }
        }
    }
}

private fun vibrateThreeTimes(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_PATTERN, -1)
        }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, contentDesc: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF222222)),
        modifier = Modifier.size(52.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
