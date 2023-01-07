package com.example.gmtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.gmtest.view_composables.Navigation
import com.example.gmtest.view_composables.SearchBar
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {


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

    @Deprecated("Deprecated in Java")
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