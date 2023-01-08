package com.example.gmtest.view_composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.navigation.NavController
import com.example.gmtest.models.FlowModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainScreen(navController: NavController, flowModel: FlowModel = FlowModel() ) {

    val state = remember{ mutableStateOf(TextFieldValue(FlowModel.savableText)) }
    Column {
        SearchView(textState = state)
        ContactsList(navController = navController, state = state, flowModel )
    }


}
