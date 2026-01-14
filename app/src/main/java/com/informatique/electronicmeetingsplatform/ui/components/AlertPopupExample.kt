package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Example screen demonstrating all alert popup types and features
 * This file serves as a reference for how to use the alert popup system
 */
@Composable
fun AlertPopupExampleScreen() {
    // Method 1: Using local state (no DI required)
    val alertState = rememberAlertPopupState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Alert Popup Examples",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Tap buttons to see different alert types",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Success alerts
            SectionTitle("Success Alerts")

            Button(
                onClick = {
                    alertState.showSuccess(
                        message = "Your changes have been saved successfully!",
                        title = "Success"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simple Success")
            }

            Button(
                onClick = {
                    alertState.showSuccess(
                        message = "Document uploaded to the cloud",
                        title = "Upload Complete",
                        actionLabel = "View",
                        onAction = {
                            // Navigate to document or perform action
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Success with Action")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Error alerts
            SectionTitle("Error Alerts")

            Button(
                onClick = {
                    alertState.showError(
                        message = "Failed to connect to the server. Please check your internet connection.",
                        title = "Connection Error"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Simple Error")
            }

            Button(
                onClick = {
                    alertState.showError(
                        message = "Network request failed. Would you like to try again?",
                        title = "Error",
                        actionLabel = "Retry",
                        onAction = {
                            // Retry logic here
                        },
                        duration = 6000L // Show longer for errors
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Error with Retry")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Warning alerts
            SectionTitle("Warning Alerts")

            Button(
                onClick = {
                    alertState.showWarning(
                        message = "You have unsaved changes that will be lost.",
                        title = "Warning"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Simple Warning")
            }

            Button(
                onClick = {
                    alertState.showWarning(
                        message = "This action cannot be undone. Are you sure you want to continue?",
                        title = "Confirm Action",
                        actionLabel = "Proceed",
                        onAction = {
                            // Proceed with action
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Warning with Action")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Info alerts
            SectionTitle("Info Alerts")

            Button(
                onClick = {
                    alertState.showInfo(
                        message = "You have 3 new notifications waiting for you.",
                        title = "Info"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Simple Info")
            }

            Button(
                onClick = {
                    alertState.showInfo(
                        message = "A new version of the app is available for download.",
                        title = "Update Available",
                        actionLabel = "Update",
                        onAction = {
                            // Navigate to update
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Info with Action")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Advanced examples
            SectionTitle("Advanced Examples")

            Button(
                onClick = {
                    scope.launch {
                        // Show multiple alerts in sequence (they will queue)
                        alertState.showInfo("Step 1: Preparing data...")
                        delay(1500)
                        alertState.showInfo("Step 2: Processing...")
                        delay(1500)
                        alertState.showSuccess("Step 3: Complete!")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Multiple Sequential Alerts")
            }

            Button(
                onClick = {
                    alertState.showAlert(
                        AlertPopupData(
                            message = "This alert has a custom duration of 8 seconds",
                            type = AlertType.INFO,
                            title = "Custom Duration",
                            duration = 8000L,
                            dismissible = true
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Custom Duration (8s)")
            }

            Button(
                onClick = {
                    alertState.showAlert(
                        AlertPopupData(
                            message = "You cannot dismiss this alert manually. Wait for auto-dismiss.",
                            type = AlertType.WARNING,
                            title = "Non-dismissible",
                            duration = 5000L,
                            dismissible = false
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Non-dismissible Alert")
            }

            // Add spacing at bottom for alerts
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Alert popup host - place at bottom of Box
        alertState.AlertHost()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * Example of using AlertPopupManager with Dependency Injection
 * Use this pattern in your ViewModels
 */
/*
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val alertPopupManager: AlertPopupManager
) : ViewModel() {

    fun onSaveData() {
        viewModelScope.launch {
            try {
                // Simulate saving
                delay(1000)
                alertPopupManager.showSuccess(
                    message = "Data saved successfully!",
                    title = "Success"
                )
            } catch (e: Exception) {
                alertPopupManager.showError(
                    message = e.message ?: "Failed to save data",
                    title = "Error",
                    actionLabel = "Retry",
                    onAction = { onSaveData() }
                )
            }
        }
    }

    fun onDeleteItem() {
        viewModelScope.launch {
            alertPopupManager.showWarning(
                message = "This action cannot be undone",
                title = "Confirm Delete",
                actionLabel = "Delete",
                onAction = {
                    // Perform delete
                    performDelete()
                }
            )
        }
    }

    private fun performDelete() {
        viewModelScope.launch {
            try {
                // Delete logic
                alertPopupManager.showSuccess("Item deleted")
            } catch (e: Exception) {
                alertPopupManager.showError("Failed to delete item")
            }
        }
    }

    fun showNotificationCount(count: Int) {
        viewModelScope.launch {
            alertPopupManager.showInfo(
                message = "You have $count new notification${if (count != 1) "s" else ""}",
                title = "Notifications",
                actionLabel = "View",
                onAction = {
                    // Navigate to notifications
                }
            )
        }
    }
}
*/

/**
 * Example of using AlertPopupHost with DI in a screen
 */
/*
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    alertPopupManager: AlertPopupManager = hiltViewModel<ExampleViewModel>().alertPopupManager
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Your screen content
        Column {
            Button(onClick = { viewModel.onSaveData() }) {
                Text("Save Data")
            }

            Button(onClick = { viewModel.onDeleteItem() }) {
                Text("Delete Item")
            }
        }

        // Alert popup host
        AlertPopupHost(alertPopupManager = alertPopupManager)
    }
}
*/

