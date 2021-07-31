package ua.syt0r.kanji.presentation.screen.screen.home.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R

@Composable
fun SearchScreen() {

    Row(
        Modifier.padding(12.dp)
    ) {

        val enteredText = remember { mutableStateOf("") }

        OutlinedTextField(
            value = enteredText.value,
            onValueChange = { enteredText.value = it },
            singleLine = true,
            label = { Text("Enter Kanji") },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_search_24),
            contentDescription = "",
            tint = MaterialTheme.colors.onSecondary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(CircleShape)
                .background(MaterialTheme.colors.secondary)
                .clickable {

                }
                .padding(12.dp)
                .size(24.dp)

        )

    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

    }


}