package com.example.gmtest.models

import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

class UpdateModel {

    fun editContact(
        context: Context,
        ctx: ContentResolver,
        contact: ContactModel,
        state: SnapshotStateList<String>
    ) {
        val nameParams = arrayOf(
            contact.id!!,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )

        var res = true
        if (contact.name != state[0]) {
            res = updateInContact(
                ctx,
                contact.id.toString(),
                state[0],
                nameParams,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.Data.CONTENT_URI
            )
            if (!res) {
                Toast.makeText(context, "Cannot update contact name!", Toast.LENGTH_SHORT).show()
                return
            }
            contact.name = state[0]
        }
        var t = 1
        contact.mobileNumber.forEach {
            if (it != state[t]) {
                var numberParams = arrayOf(
                    contact.id!!,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, it
                )
                res = updateInContact(
                    ctx,
                    contact.id.toString(),
                    state[t],
                    numberParams,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Data.CONTENT_URI,
                    " AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?"
                )
                if (!res) {
                    Toast.makeText(context, "Cannot update contact phone!" + it, Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                contact.mobileNumber[t-1] = state[t]
            }
            ++t;
        }
        contact.email.forEach {
            if (it != state[t]) {
                var emailParams = arrayOf(
                    contact.id!!,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, it
                )
                res = updateInContact(
                    ctx,
                    contact.id.toString(),
                    state[t],
                    emailParams,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.Data.CONTENT_URI,
                    " AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?"
                )
                if (!res) {
                    Toast.makeText(context, "Cannot update contact phone!" + it, Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                contact.email[t-1-contact.mobileNumber.size] = state[t]
            }
            ++t;
        }
    }

    fun updateInContact(
        ctx: ContentResolver,
        id: String,
        field: String,
        params: Array<String>,
        key: String,
        uris: Uri,
        addWhere: String = ""
    ): Boolean {
        var where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? "
        var ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where + addWhere, params)
                .withValue(key, field)
                .build()
        );

        try {
            val results: Array<ContentProviderResult> =
                ctx.applyBatch(ContactsContract.AUTHORITY, ops)
            var t = results;
        } catch (ex: Exception) {
            return false
        }
        return true
    }



    fun getPhotoFromUri(uri: String?, ctx: Context): ImageBitmap? {
        if (uri == null) return null
        var im: ImageBitmap? = null
        try {
            im = BitmapFactory.decodeStream(
                ctx.contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")!!
                    .createInputStream()
            ).asImageBitmap()
        } catch (exc: Exception) {
            im = null
        }
        return im
    }

}