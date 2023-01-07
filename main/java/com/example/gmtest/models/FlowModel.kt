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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

@Suppress("DEPRECATION")
class FlowModel(context: Context) : ViewModel() {

  companion object {
      var contactList = ArrayList<ContactModel>()

      var savableText: String = ""
  }

    @SuppressLint("StaticFieldLeak")
    private var ctx: Context?


    init {
        ctx = context
        viewModelScope.launch {
            contactList = async{fetchData()}.await()
            ctx = null
        }

    }

    suspend fun fetchData() : ArrayList<ContactModel>{
        return coroutineScope { getContacts(ctx!!) }
    }


    @SuppressLint("Range", "NewApi")
    fun getContacts(ctx: Context): ArrayList<ContactModel> {
        if (contactList.size > 0) return contactList
        val list: ArrayList<ContactModel> = ArrayList()
        val contentResolver: ContentResolver = ctx.contentResolver
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

                    list.add(info)
                    cursorInfo.close()
                    mailcursor.close()
                }

            }
            cursor.close()
        }
        list.sortBy { x -> x.name }
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