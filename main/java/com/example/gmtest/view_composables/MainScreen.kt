package com.example.gmtest.view_composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.example.gmtest.models.FlowModel

@Composable
fun MainScreen(navController: NavController ) {

    val state = remember{ mutableStateOf(TextFieldValue(FlowModel.savableText)) }

    Column {
        SearchView(textState = state)
        ContactsList(navController = navController, state = state )
    }
}
