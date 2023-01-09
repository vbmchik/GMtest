package com.example.gmtest.view_composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gmtest.models.ContactModel
import com.example.gmtest.models.FlowModel
import kotlinx.coroutines.*


@Composable
fun ContactsList(navController: NavController, state: MutableState<TextFieldValue>, flowModel: FlowModel = FlowModel()
) {

    //var contacts =  flowModel.uiState.observeAsState()
    val contacts = flowModel.contacts
    var reducedContacts: SnapshotStateList<ContactModel>

    //flowModel.fectcher(LocalContext.current)
    //var reducedContacts: ArrayList<ContactModel>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text

        reducedContacts = if (searchedText.isEmpty()) (
                contacts.value
                ) else {
            val resultList = SnapshotStateList<ContactModel>()

            for (contact in contacts.value) {
                if (contact.name!!.lowercase()
                        .contains(searchedText.lowercase())
                ) {
                    resultList.add(contact)
                }
            }
            resultList
        }
        items(reducedContacts.size) { x ->
            val p : String?
            if( reducedContacts[x].mobileNumber.size > 0)
                p = reducedContacts[x].mobileNumber[0]
            else
                p = null
            ContactListItem(
                id = reducedContacts[x].lid,
                name = reducedContacts[x].name!!,
                thumbnail = reducedContacts[x].thumbUri,
                p,
                onItemClick =
                { contact ->
                    navController.navigate("details/$contact") {
//stack to start
                        popUpTo("main") {
                            saveState = true
                        }
// like singleton
                        launchSingleTop = true
// use cache
                        restoreState = true
                    }
                },
                flowModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsListPreview() {
    val navController = rememberNavController()
    val textState = remember {
        mutableStateOf(TextFieldValue(""))
    }
    ContactsList(navController = navController, state = textState)
}
