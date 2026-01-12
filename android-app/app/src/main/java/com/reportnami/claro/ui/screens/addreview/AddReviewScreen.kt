package com.reportnami.claro.ui.screens.addreview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    productId: Long?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: AddReviewViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val name = remember { mutableStateOf("") }
    val comment = remember { mutableStateOf("") }
    val rating = remember { mutableIntStateOf(5) }

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.consumeSuccess()
            onSubmitted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (productId == null) {
                Text(text = "Invalid product selection", color = MaterialTheme.colorScheme.error)
                return@Column
            }

            if (state.error != null) {
                Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Your name (optional)") },
                singleLine = true,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = rating.intValue.toString(),
                onValueChange = {
                    val r = it.toIntOrNull()
                    if (r != null) rating.intValue = r
                },
                label = { Text("Rating (1-5)") },
                singleLine = true,
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                value = comment.value,
                onValueChange = { comment.value = it },
                label = { Text("Review (optional)") },
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    enabled = !state.isSubmitting,
                    onClick = {
                        viewModel.submit(
                            productId = productId,
                            reviewerName = name.value,
                            rating = rating.intValue,
                            comment = comment.value,
                        )
                    }
                ) {
                    Text(if (state.isSubmitting) "Submitting..." else "Submit")
                }

                Button(enabled = !state.isSubmitting, onClick = onBack) {
                    Text("Cancel")
                }
            }
        }
    }
}
