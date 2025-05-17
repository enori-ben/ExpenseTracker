package com.example.expensetracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.repository.Routes
import com.example.expensetracker.screens.SignInScreen
import com.example.expensetracker.screens.SignUpScreen
import com.example.expensetracker.screens.Splashscreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.view.*
import com.example.expensetracker.view.mainscreen.MainScreen
import com.example.expensetracker.view.home.drawer.AboutScreen
import com.example.expensetracker.view.home.profile.EditProfileScreen
import com.example.expensetracker.view.home.profile.ProfileScreen
import com.example.expensetracker.view.scan.Scanning
import com.example.expensetracker.view.stats.StatsScreen
import com.example.expensetracker.view.transaction.AddTran
import com.example.expensetracker.view.TransactionViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val transactionViewModel: TransactionViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val navController = rememberNavController()
                val startDestination = remember {
                    if (isUserLoggedIn()) Routes.MAIN_SCREEN else Routes.SPLASH_SCREEN
                }

                NavHost(navController, startDestination = Routes.SPLASH_SCREEN) {
                    composable(Routes.SPLASH_SCREEN) { Splashscreen(navController) }
                    composable(Routes.SIGN_IN_SCREEN) { SignInScreen(navController) }
                    composable(Routes.SIGN_UP_SCREEN) { SignUpScreen(navController) }
                    composable(Routes.MAIN_SCREEN) { MainScreen(navController, transactionViewModel) }
                    composable(Routes.PROFILE_SCREEN) { ProfileScreen(navController) }
                    composable(
                        route = "${Routes.ADD_TRANSACTION}?editId={editId}",
                        arguments = listOf(
                            navArgument("editId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val editId = backStackEntry.arguments?.getString("editId")
                        AddTran(
                            navController = navController,
                            viewModel     = transactionViewModel,
                            editId        = editId
                        )
                    }
                    composable(Routes.SCANNING_SCREEN) {
                        Scanning(
                            onClose   = { navController.popBackStack() },
                            onConfirm = { navController.navigate(Routes.MAIN_SCREEN) },
                            viewModel = transactionViewModel
                        )
                    }
                    composable(Routes.EDIT_PROFILE) { EditProfileScreen(navController) }

                    composable(Routes.STATS_SCREEN) { StatsScreen(navController, transactionViewModel) }
                    composable(Routes.ABOUT_SCREEN)    { AboutScreen(navController) }
                    composable(
                        route = "${Routes.TRANSACTION_DETAIL}/{transactionId}",
                        arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        TransactionDetailScreen(
                            transactionId = backStackEntry.arguments!!.getString("transactionId")!!,
                            viewModel     = transactionViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainPreview() {
        ExpenseTrackerTheme {
            Splashscreen(rememberNavController())
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val auth = Firebase.auth
        return auth.currentUser != null
    }
}
