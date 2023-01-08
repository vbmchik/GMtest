package com.example.gmtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.gmtest.models.FlowModel
import com.example.gmtest.view_composables.Navigation
import com.example.gmtest.view_composables.SearchBar
import kotlinx.coroutines.*
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
/*
    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
           override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }


 */
   companion object{
    var mViewModel: FlowModel? = null
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[FlowModel::class.java]
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                   mViewModel!!.fectcher(applicationContext)
            }
        }
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

        //flowModel.fetchData(application.applicationContext)

        setContent {
            Scaffold(
                topBar = { SearchBar() },
                backgroundColor = Color.White
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Navigation(flowModel = mViewModel!!)
                }
            }

        }

    }

}