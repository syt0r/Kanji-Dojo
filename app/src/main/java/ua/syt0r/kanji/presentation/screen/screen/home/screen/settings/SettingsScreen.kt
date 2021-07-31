package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.timepicker.MaterialTimePicker
import ua.syt0r.kanji.presentation.screen.MainContract
import java.time.LocalTime

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = viewModel<SettingsViewModel>(),
    navigation: MainContract.Navigation
) {

    Column {

        SettingSectionTitle(text = "Notification")

        NotificationSettings()

        SettingSectionTitle(text = "Other")

        Text(
            text = "About",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigation.navigateToAbout() }
                .padding(vertical = 12.dp, horizontal = 24.dp)
        )

    }

}

@Composable
fun SettingSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotificationSettings() {

    val shouldShowReminderNotifications = remember { mutableStateOf(false) }

    val toggleReminder = {
        shouldShowReminderNotifications.value = !shouldShowReminderNotifications.value
    }

    Row(
        modifier = Modifier
            .clickable { toggleReminder() }
            .padding(vertical = 12.dp, horizontal = 24.dp)
    ) {

        Text(text = "Show Reminder Notification", modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = shouldShowReminderNotifications.value,
            onCheckedChange = { toggleReminder() }
        )

    }

    AnimatedVisibility(
        visible = shouldShowReminderNotifications.value
    ) {

        val shouldShowPickerDialog = remember { mutableStateOf(false) }
        val notificationTime = remember { mutableStateOf(LocalTime.of(9, 0)) }

        if (shouldShowPickerDialog.value) {
            TimePickerDialog(
                selectedTime = notificationTime.value,
                onDismiss = { shouldShowPickerDialog.value = false },
                onTimePicked = {
                    shouldShowPickerDialog.value = false
                    notificationTime.value = it
                }
            )
        }

        Row(
            modifier = Modifier
                .clickable { shouldShowPickerDialog.value = true }
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {

            Text(text = "Notification time", modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(8.dp))

            Text("9 AM")

        }

    }

}

// TODO replace with compose
@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onDismiss: () -> Unit,
    onTimePicked: (LocalTime) -> Unit
) {

    val picker = MaterialTimePicker.Builder()
        .setHour(selectedTime.hour)
        .setMinute(selectedTime.minute)
        .setTitleText("Pick Time")
        .build()

    picker.addOnDismissListener { onDismiss() }
    picker.addOnPositiveButtonClickListener {
        onTimePicked(LocalTime.of(picker.hour, picker.minute))
    }

    picker.show(
        (LocalContext.current as AppCompatActivity).supportFragmentManager,
        "picker"
    )

}