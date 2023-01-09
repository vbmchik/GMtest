package com.example.gmtest.models

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gmtest.models.FlowModel.Companion.contactList
import com.example.gmtest.models.FlowModel.Companion.used
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

@Suppress("DEPRECATION")
class FlowModel() : ViewModel() {

    companion object {
        var contactList = ArrayList<ContactModel>()
        var used = false
        var savableText: String = ""
    }
    val uiState: MutableLiveData<ArrayList<ContactModel>> by lazy {
        MutableLiveData<ArrayList<ContactModel>>()
    }
    //val contacts = mutableStateListOf<ContactModel>( )
    private val _contacts = MutableStateFlow<SnapshotStateList<ContactModel>>(mutableStateListOf())
    val contacts: StateFlow<SnapshotStateList<ContactModel>> = _contacts
    init{
        uiState.value = ArrayList<ContactModel>()
        //contacts = uiState.value!!
    }

    fun fectcher(ctx: Context){
        viewModelScope.launch {
            async{fetchData(ctx)}
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun fetchData(ctx: Context): ArrayList<ContactModel> ? = withContext(Dispatchers.IO) {
        var c: ArrayList<ContactModel>? = null
        if (!used)
            c =  getContacts(ctx!!)
        viewModelScope.launch {
            if (c!=null)
                //uiState.value =c
            else
                uiState.value = contactList
        }
      uiState.value
   }

    @SuppressLint("Range", "NewApi")
    private fun getContacts(ctx: Context): ArrayList<ContactModel> {
        if (contactList.size > 0 || used ) return contactList
        used=true
        val list: ArrayList<ContactModel> = ArrayList()
        val contentResolver: ContentResolver = ctx.contentResolver
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val lid: Long = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val cursorInfo: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, "DISPLAY_NAME ASC"
                    )
                    val person: Uri =
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, lid)
                    val tUri =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                    val pURI =
                        //cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                        Uri.withAppendedPath(
                            person,
                            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
                        )
                            .toString()
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
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
                    viewModelScope.launch {
                        contacts.value.add(info)
                        //contacts.value[0] = contacts.value[0].copy(selected = true)
                    }
                    list.add(info)
                    cursorInfo!!.close()
                    mailcursor.close()
                }

            }
            cursor.close()
        }
        list.sortBy { x -> x.name }
        if(contactList.size == 0)
            contactList = list
        return list
    }

    fun getBitmap(tUri: String, ctx: Context): ImageBitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(ctx.contentResolver, Uri.parse(tUri))
        } catch (e: IOException) {
            // Do nothing
            e.printStackTrace()
        }
        if (bitmap != null) {
            return bitmap.asImageBitmap()
        } else
            return null
    }
}