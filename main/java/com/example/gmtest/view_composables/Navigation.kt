package com.example.gmtest.view_composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gmtest.models.FlowModel
import kotlinx.coroutines.flow.flow

@Composable
fun Navigation(flowModel: FlowModel = FlowModel() ){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController, flowModel = flowModel)
        }
        composable(
            "details/{contactName}",
            arguments = listOf(navArgument("contactName") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("contactName")?.let { name ->
                DetailsScreen(person = flowModel.contacts.value!!.find { it.id == name }!!)
            }
        }
    }
}