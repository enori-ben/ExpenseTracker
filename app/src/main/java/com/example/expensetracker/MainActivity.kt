package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.repository.Routes
import com.example.expensetracker.screens.SignInScreen
import com.example.expensetracker.screens.SignUpScreen
import com.example.expensetracker.screens.Splashscreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.view.AddScreen
import com.example.expensetracker.view.MainScreen
import com.example.expensetracker.view.ProfileScreen
import com.example.expensetracker.view.StatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH_SCREEN
                ) {
                    composable(Routes.SPLASH_SCREEN) { Splashscreen(navController) }
                    composable(Routes.SIGN_IN_SCREEN) { SignInScreen(navController) }
                    composable(Routes.SIGN_UP_SCREEN) { SignUpScreen(navController) }
                    composable(Routes.MAIN_SCREEN) { MainScreen(navController) }
                    composable(Routes.PROFILE_SCREEN) { ProfileScreen(navController) }
                    }

                    // ... داخل NavHost الثاني



                    }

            }
        }
    }



@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ExpenseTrackerTheme {
        val navController = rememberNavController()
        Splashscreen(navController)
    }
}