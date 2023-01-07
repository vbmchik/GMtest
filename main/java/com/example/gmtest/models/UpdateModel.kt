package com.example.gmtest.models

import android.content.ContentProviderOperation
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
        var res : Boolean
        if (contact.name != state[0]) {
            res = updateInContact(
                ctx,
                state[0],
                nameParams,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
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
                val numberParams = arrayOf(
                    contact.id!!,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, it
                )
                res = updateInContact(
                    ctx,
                    state[t],
                    numberParams,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    " AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?"
                )
                if (!res) {
                    Toast.makeText(context, "Cannot update contact phone!" + it, Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                contact.mobileNumber[t-1] = state[t]
            }
            ++t
        }
        contact.email.forEach {
            if (it != state[t]) {
                val emailParams = arrayOf(
                    contact.id!!,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, it
                )
                res = updateInContact(
                    ctx,
                    state[t],
                    emailParams,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    " AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?"
                )
                if (!res) {
                    Toast.makeText(context, "Cannot update contact phone!" + it, Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                contact.email[t-1-contact.mobileNumber.size] = state[t]
            }
            ++t
        }
    }

    fun updateInContact(
        ctx: ContentResolver,
        field: String,
        params: Array<String>,
        key: String,
        addWhere: String = ""
    ): Boolean {
        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? "
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where + addWhere, params)
                .withValue(key, field)
                .build()
        )

        try {
                ctx.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (ex: Exception) {
            return false
        }
        return true
    }



    fun getPhotoFromUri(uri: String?, ctx: Context): ImageBitmap? {
        if (uri == null) return null
        var im: ImageBitmap?
        try {
            val d = ctx.contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")!!
            val s = d.createInputStream()
            BitmapFactory.decodeStream(
                s
            ).asImageBitmap().also { im = it }
            s.close()
            d.close()
        } catch (exc: Exception) {
            im = null
        }
        return im
    }

}