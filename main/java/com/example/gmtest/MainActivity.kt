package com.example.gmtest

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeStream
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gmtest.MainActivity.Companion.contactList
import com.example.gmtest.models.ContactModel
import java.io.IOException
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    companion object {
        var contactList = ArrayList<ContactModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                setContent()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS),
                    111
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    setContent()
                } else {
                    exitProcess(-1)
                }
                return
            }
            else -> {

            }
        }
    }

    private fun setContent() {
        setContent {
            Scaffold(
                topBar = { SearchBar() },
                backgroundColor = Color.White
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Navigation()
                }
            }

        }
    }

}


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable(
            "details/{contactName}",
            arguments = listOf(navArgument("contactName") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("contactName")?.let { name ->
                DetailsScreen(person = contactList.find { it.id == name }!!)
            }
        }
    }
}


@Composable
fun SearchBar() {
    TopAppBar(
        title = { Text(text = "Search", fontSize = 18.sp, fontFamily = FontFamily.SansSerif) },
        backgroundColor = Color.LightGray,
        contentColor = Color.Blue
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar()
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        singleLine = true,
        shape = RectangleShape,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = colorResource(id = R.color.primary),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    SearchView(textState)
}

fun getBitmap(tUri: String, id: Long, ctx: Context): ImageBitmap? {
    var bitmap: Bitmap? = null
    if (tUri != null) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(ctx.contentResolver, Uri.parse(tUri))
        } catch (e: IOException) {
            // Do nothing
            e.printStackTrace()
        }
    }
    if (bitmap != null) {
        return bitmap.asImageBitmap()
    } else
        return null
}

fun Modifier.badgeLayout() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        // based on the expectation of only one line of text
        val minPadding = placeable.height / 4
        val width = maxOf(placeable.width + minPadding, placeable.height)
        layout(width, placeable.height) {
            placeable.place((width - placeable.width) / 2, 0)
        }
    }

@Composable
fun ContactListItem(
    id: Long,
    name: String,
    thumbnail: String?,
    phone: String,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .heightIn(min = 70.dp)
            .clickable(onClick = { onItemClick(id.toString()) })
            .background(color = Color.White, shape = RoundedCornerShape(2.dp))
            .padding(all = 2.dp)
    ) {
        Row {
            if (thumbnail == null) {
                Text(
                    text = name[0].toString(),
                    fontSize = 25.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(Color.Green, CircleShape)
                        .badgeLayout()
                )

            } else {
                Image(
                    bitmap = getBitmap(thumbnail, id, LocalContext.current)!!,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(start = 20.dp, end = 40.dp)
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = phone, color = Color.Black, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContactListItemPreview() {
    ContactListItem(1, name = "Boris", thumbnail = null, phone = "0532583678", onItemClick = {})
}

@Composable
fun ContactsList(navController: NavController, state: MutableState<TextFieldValue>) {
    val contacts = getContacts(LocalContext.current)
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
            ContactListItem(
                id = reducedContacts[x].lid,
                name = reducedContacts[x].name!!,
                thumbnail = reducedContacts[x].thumbUri,
                phone = reducedContacts[x].mobileNumber!![0],
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
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    ContactsList(navController = navController, state = textState)
}

@Composable
fun MainScreen(navController: NavController) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    Column {
        SearchView(textState)
        ContactsList(navController = navController, state = textState)
    }
}


@SuppressLint("Range", "NewApi")
fun getContacts(ctx: Context): ArrayList<ContactModel>? {
    if (contactList.size > 0) return contactList
    val list: ArrayList<ContactModel> = ArrayList()
    val contentResolver: ContentResolver = ctx.getContentResolver()
    val cursor: Cursor? =
        contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
    if (cursor!!.count > 0) {
        while (cursor.moveToNext()) {
            val id: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val lid: Long = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                val cursorInfo: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null
                )
                val person: Uri =
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, lid)
                val tUri =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                val pURI =
                    //cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                    Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
                        .toString()
                val name =
                    cursor!!.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val info = ContactModel()
                info.id = id
                info.lid = lid
                info.name =
                    name
                info.photoURI = pURI
                info.thumbUri = tUri
                while (cursorInfo!!.moveToNext()) {
                    val p =
                        cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (p != null)
                        info.mobileNumber.add(p)
                }

                val mailcursor: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null
                )
                while (mailcursor!!.moveToNext()) {
                    val e =
                        mailcursor.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    if (e != null)
                        info.email.add(e)
                }

                list.add(info)
                cursorInfo.close()
                mailcursor.close()
            }

        }
        cursor!!.close()
    }
    list.sortBy { x -> x.name }
    contactList = list
    return list
}

fun getPhotoFromUri(uri: String?, ctx: Context): ImageBitmap? {
    if (uri == null) return null
    var im: ImageBitmap? = null
    try {
        /* im = decodeStream(
             ContactsContract.Contacts.openContactPhotoInputStream(
                 ctx.contentResolver,
                 Uri.parse(uri)
             )
         ).asImageBitmap() */
        im = decodeStream(
            ctx.contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")!!.createInputStream()
        ).asImageBitmap()
    } catch (exc: Exception) {
        im = null
    }
    return im
}

@Composable
fun DetailsScreen(person: ContactModel) {
    var valueStateList = remember{ mutableStateListOf<String>().apply{
         add(person.name.toString())
         addAll(person.mobileNumber)
         addAll(person.email)
    } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .wrapContentSize(Alignment.TopCenter)
            .verticalScroll(rememberScrollState())
    ) {
        val image = getPhotoFromUri(person.photoURI, LocalContext.current)
        if (image == null) {
            Text(
                text = person.name!![0].toString(),
                fontSize = 150.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color.Green, CircleShape)
                    .badgeLayout()
                //.align(Alignment.CenterHorizontally)
            )

        } else {
            Image(
                bitmap = image,
                contentDescription = "",
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(400.dp)
            )
        }


        TextField(
            value =  remember{person.name!!},

            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )
        for (phone in person.mobileNumber)
            TextField(
                value = phone,
                color = Color.Magenta,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
        for (email in person.email)
            TextField(
                value = email,
                color = Color.Green,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
    }


}


@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(ContactModel().also { it.name = "Phil" })
}

