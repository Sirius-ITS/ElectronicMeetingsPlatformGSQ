package com.informatique.electronicmeetingsplatform.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileViewModel

@Composable
fun AttendanceSection(viewModel: ProfileViewModel){

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    Column {

    }
}