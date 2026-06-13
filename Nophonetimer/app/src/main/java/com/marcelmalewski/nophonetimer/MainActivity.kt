package com.marcelmalewski.nophonetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.marcelmalewski.nophonetimer.ui.theme.NoPhoneTimerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NoPhoneTimerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NoPhoneTimerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NoPhoneTimerScreen(modifier: Modifier = Modifier) {
    var seconds by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds++
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Phone Timer",
            fontSize = 24.sp
        )

        Text(
            text = formatTime(seconds),
            fontSize = 48.sp
        )

        Button(
            onClick = {
                isRunning = !isRunning
            }
        ) {
            Text(
                if (isRunning) "Stop" else "Start"
            )
        }
    }
}

fun formatTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format(
        "%02d:%02d:%02d",
        hours,
        minutes,
        seconds
    )
}

@Preview(showBackground = true)
@Composable
fun NoPhoneTimerPreview() {
    NoPhoneTimerTheme {
        NoPhoneTimerScreen()
    }
}