package com.example.gmtest.models

import android.graphics.Bitmap
import android.net.Uri


class ContactModel {
    var id: String? = null
    var lid: Long = -1
    var name: String? = null
    var mobileNumber: ArrayList<String> = ArrayList()
    var photoURI: String? = null
    var thumbUri: String? = null
    var email: ArrayList<String> = ArrayList()
}
