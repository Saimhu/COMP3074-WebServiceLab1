
package com.example.webservicecontacts

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.webservicecontacts.ui.theme.ContactListScreen
import com.example.webservicecontacts.ui.theme.ContactDetailsScreen

@Composable
fun ContactApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "contacts"
    ) {
        composable("contacts") {
            ContactListScreen(
                onContactClick = { userId ->
                    navController.navigate("details/$userId")
                }
            )
        }

        composable("details/{userId}") { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toIntOrNull()

            ContactDetailsScreen(
                userId = userId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
