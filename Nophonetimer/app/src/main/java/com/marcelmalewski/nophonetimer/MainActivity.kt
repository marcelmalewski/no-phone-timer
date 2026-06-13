package com.marcelmalewski.nophonetimer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcelmalewski.nophonetimer.ui.theme.Background
import com.marcelmalewski.nophonetimer.ui.theme.BackgroundSecondary
import com.marcelmalewski.nophonetimer.ui.theme.TextPrimary
import com.marcelmalewski.nophonetimer.ui.theme.TextSecondary
import com.marcelmalewski.nophonetimer.ui.theme.NoPhoneTimerTheme
import com.marcelmalewski.nophonetimer.ui.theme.Accent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(
            Intent(this, TrackingService::class.java)
        )

        enableEdgeToEdge()
        setContent {
            NoPhoneTimerTheme {
                NoPhoneTimerScreen()
            }
        }
    }
}

@Composable
fun NoPhoneTimerScreen() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        StatsRepository.initialize(
            context
        )
    }

    val appState by StatsRepository.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.32f)
                .background(Background)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(
                modifier = Modifier.height(60.dp)

            )
            Text(
                text = "No Phone", color = Accent, fontSize = 34.sp
            )
            Text(
                text = "Timer", color = Accent, fontSize = 34.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BackgroundSecondary
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                        text = "Today", color = Accent, fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = formatDuration(
                            appState.todayTotal
                        ), color = TextPrimary, fontSize = 56.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BackgroundSecondary
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                        text = "Last 7 Days", color = Accent, fontSize = 14.sp
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    appState.history.forEach { day ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = day.dayName, color = TextSecondary
                            )

                            Text(
                                text = formatShort(
                                    day.durationMs
                                ), color = TextPrimary
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {

        hours > 0 -> "${hours}h ${minutes}m ${seconds}s"

        minutes > 0 -> "${minutes}m ${seconds}s"

        else -> "${seconds}s"
    }
}

fun formatShort(ms: Long): String {
    val totalMinutes = ms / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"

        else -> "${minutes}m"
    }
}