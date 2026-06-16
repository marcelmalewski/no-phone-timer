package com.marcelmalewski.nophonetimer

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import com.marcelmalewski.nophonetimer.ui.theme.Accent
import com.marcelmalewski.nophonetimer.ui.theme.Background
import com.marcelmalewski.nophonetimer.ui.theme.BackgroundSecondary
import com.marcelmalewski.nophonetimer.ui.theme.NoPhoneTimerTheme
import com.marcelmalewski.nophonetimer.ui.theme.TextPrimary
import com.marcelmalewski.nophonetimer.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var usageGranted by remember {
        mutableStateOf(false)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {

                    usageGranted = hasUsageAccess(context)

                    StatisticsRepository.refresh(context)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5.minutes)
            StatisticsRepository.refresh(context)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val nextMidnight = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            delay((nextMidnight - System.currentTimeMillis()).milliseconds)
            StatisticsRepository.refresh(context)
        }
    }

    val appState by StatisticsRepository.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Text(text = "No Phone", color = Accent, fontSize = 34.sp)

            Text(text = "Timer", color = Accent, fontSize = 34.sp)

            Spacer(modifier = Modifier.height(40.dp))

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

                    Text(text = "Today", color = Accent, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = appState.todayTotal.formatDuration(),
                        color = TextPrimary,
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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

                    Text(text = "Last 7 Days", color = Accent, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    appState.history.forEach { day ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(text = day.dayOfWeek, color = TextSecondary)

                            Text(
                                text = day.noPhoneDuration.formatDuration(false),
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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

                    Text(text = "Usage Access", color = Accent, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (usageGranted) {
                            "Granted ✓"
                        } else {
                            "Required ⚠"
                        }, color = TextPrimary, fontSize = 24.sp
                    )

                    if (!usageGranted) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Settings.ACTION_USAGE_ACCESS_SETTINGS
                                    )
                                )
                            }) {
                            Text("Grant Usage Access")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun Long.formatDuration(showSeconds: Boolean = true): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) {
            append("${hours}h ")
        }
        if (minutes > 0 || hours > 0) {
            append("${minutes}m")
        }
        if (showSeconds) {
            if (isNotEmpty()) {
                append(" ")
            }

            append("${seconds}s")
        }
    }
}