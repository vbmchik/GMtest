package com.example.gmtest.Composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gmtest.models.ContactModel
import com.example.gmtest.models.FlowModel

@Composable
fun ContactsList(navController: NavController, state: MutableState<TextFieldValue>, flowModel: FlowModel = FlowModel(
    LocalContext.current)
) {
    val contacts = flowModel.getContacts(LocalContext.current)!!
    var reducedContacts: ArrayList<ContactModel>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text
        reducedContacts = if (searchedText.isEmpty()) (
                contacts
                )!! else {
            val resultList = ArrayList<ContactModel>()
            for (contact in contacts!!) {
                if (contact.name!!.lowercase()
                        .contains(searchedText.lowercase())
                ) {
                    resultList.add(contact)
                }
            }
            resultList
        }
        items(reducedContacts.size) { x ->
            var p : String?;
            if( reducedContacts[x].mobileNumber!!.size > 0)
                p = reducedContacts[x].mobileNumber!![0]
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
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsListPreview() {
    val navController = rememberNavController()
    var textState = remember {
        mutableStateOf(TextFieldValue(""))
    }
    ContactsList(navController = navController, state = textState)
}
