package com.informatique.electronicmeetingsplatform.ui.components.popup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Example screen demonstrating all dialog types and features
 * This file serves as a reference for how to use the dialog system
 */
@Composable
fun DialogExampleScreen() {
    val dialogState = rememberDialogState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Dialog Examples",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Tap buttons to see different dialog types",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Alert dialogs
            SectionTitle("Alert Dialogs")

            Button(
                onClick = {
                    dialogState.showAlert(
                        message = "تم حفظ التغييرات بنجاح!",
                        type = DialogType.SUCCESS,
                        title = "نجح"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Success Alert")
            }

            Button(
                onClick = {
                    dialogState.showAlert(
                        message = "فشل الاتصال بالخادم. يرجى التحقق من اتصال الإنترنت.",
                        type = DialogType.ERROR,
                        title = "خطأ"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Error Alert")
            }

            Button(
                onClick = {
                    dialogState.showAlert(
                        message = "لديك تغييرات غير محفوظة ستفقد عند الخروج.",
                        type = DialogType.WARNING,
                        title = "تحذير"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Warning Alert")
            }

            Button(
                onClick = {
                    dialogState.showAlert(
                        message = "لديك 3 إشعارات جديدة في انتظارك.",
                        type = DialogType.INFO,
                        title = "معلومة"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Info Alert")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Confirmation dialogs
            SectionTitle("Confirmation Dialogs")

            Button(
                onClick = {
                    dialogState.showConfirmation(
                        message = "هل أنت متأكد أنك تريد المتابعة؟ لا يمكن التراجع عن هذا الإجراء.",
                        title = "تأكيد الإجراء",
                        onConfirm = {
                            // Perform action
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simple Confirmation")
            }

            Button(
                onClick = {
                    dialogState.showConfirmation(
                        message = "سيتم إرسال البريد الإلكتروني إلى جميع المشاركين. هل تريد المتابعة؟",
                        title = "إرسال بريد إلكتروني",
                        type = DialogType.INFO,
                        confirmText = "إرسال",
                        cancelText = "إلغاء",
                        onConfirm = {
                            // Send email
                        },
                        onCancel = {
                            // Cancel action
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmation with Callback")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Destructive dialogs
            SectionTitle("Destructive Dialogs (Delete)")

            Button(
                onClick = {
                    dialogState.showDestructive(
                        message = "هل تريد حذف هذا الملف؟ لا يمكن التراجع عن هذا الإجراء.",
                        title = "تأكيد الحذف",
                        onConfirm = {
                            // Delete file
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Simple Delete")
            }

            Button(
                onClick = {
                    dialogState.showDestructive(
                        message = "سيتم حذف هذا العنصر نهائياً. هل أنت متأكد؟",
                        title = "تأكيد الحذف",
                        itemName = "اجتماع_2024.pdf",
                        onConfirm = {
                            // Delete with item name
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete with Item Name")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Loading dialogs
            SectionTitle("Loading Dialogs")

            Button(
                onClick = {
                    dialogState.showLoading("جاري التحميل...")
                    scope.launch {
                        delay(3000)
                        dialogState.dismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Loading (3s)")
            }

            Button(
                onClick = {
                    dialogState.showLoading(
                        message = "جاري الرفع...",
                        progress = 0.6f
                    )
                    scope.launch {
                        delay(3000)
                        dialogState.dismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Loading with Progress")
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Dialog host
        dialogState.DialogHost()
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
 * Example of using DialogManager and AlertPopupManager with Dependency Injection
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
                // Show loading
                alertPopupManager.getDialogManager().showLoading("جاري الحفظ...")

                // Simulate saving
                delay(1000)

                // Dismiss loading
                alertPopupManager.getDialogManager().dismissDialog()

                // Show success toast
                alertPopupManager.showSuccess(
                    message = "تم حفظ البيانات بنجاح!",
                    title = "نجح"
                )
            } catch (e: Exception) {
                // Dismiss loading
                alertPopupManager.getDialogManager().dismissDialog()

                // Show error dialog
                alertPopupManager.getDialogManager().showError(
                    message = e.message ?: "فشل في حفظ البيانات",
                    title = "خطأ"
                )
            }
        }
    }

    fun onDeleteItem(itemName: String) {
        // Show delete confirmation dialog (BLOCKING)
        alertPopupManager.showDeleteConfirmation(
            message = "هل تريد حذف هذا الملف؟ لا يمكن التراجع عن هذا الإجراء.",
            title = "تأكيد الحذف",
            itemName = itemName,
            onConfirm = {
                // User confirmed, perform delete
                performDelete(itemName)
            },
            onCancel = {
                // User cancelled (optional callback)
            }
        )
    }

    private fun performDelete(itemName: String) {
        viewModelScope.launch {
            try {
                // Delete logic
                alertPopupManager.showSuccess("تم حذف الملف")
            } catch (e: Exception) {
                alertPopupManager.showError("فشل في حذف الملف")
            }
        }
    }

    fun onPublishMeeting() {
        // Show warning confirmation
        alertPopupManager.showWarningDialog(
            message = "سيتم نشر الاجتماع لجميع المشاركين. هل تريد المتابعة؟",
            title = "نشر الاجتماع",
            confirmText = "نشر",
            cancelText = "إلغاء",
            onConfirm = {
                // Publish meeting
                publishMeeting()
            }
        )
    }

    private fun publishMeeting() {
        viewModelScope.launch {
            alertPopupManager.getDialogManager().showLoading("جاري النشر...")
            // Publish logic...
            alertPopupManager.getDialogManager().dismissDialog()
            alertPopupManager.showSuccess("تم نشر الاجتماع بنجاح")
        }
    }
}
*/

/**
 * Example of using DialogHost in a screen with DI
 */
/*
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    alertPopupManager: AlertPopupManager // Injected
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Your screen content
        Column {
            Button(onClick = { viewModel.onSaveData() }) {
                Text("Save Data")
            }

            Button(onClick = { viewModel.onDeleteItem("example.pdf") }) {
                Text("Delete Item")
            }

            Button(onClick = { viewModel.onPublishMeeting() }) {
                Text("Publish Meeting")
            }
        }

        // Alert popup host (for toast notifications)
        AlertPopupHost(alertPopupManager = alertPopupManager)

        // Dialog host (for blocking dialogs)
        DialogHost(dialogManager = alertPopupManager.getDialogManager())
    }
}
*/
