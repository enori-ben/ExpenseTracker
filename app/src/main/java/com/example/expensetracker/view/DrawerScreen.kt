package com.example.expensetracker.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.Routes

@Composable
fun DrawerScreen(
    onClose: () -> Unit,
    navController: NavController
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.me),
                        contentDescription = "Close"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "بن عتوس نورالدين", style = MaterialTheme.typography.titleLarge)
            }

            // Menu Items
            DrawerItem(
                text = "الإعدادات",
                iconRes = R.drawable.me,
                onClick = { navController.navigate(Routes.SETTINGS_SCREEN) }
            )
            DrawerItem(
                text = "المساعدة",
                iconRes = R.drawable.me,
                onClick = { navController.navigate(Routes.HELP_SCREEN) }
            )
            DrawerItem(
                text = "عن التطبيق",
                iconRes = R.drawable.me,
                onClick = { navController.navigate(Routes.ABOUT_SCREEN) }
            )
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text) },
        icon = { Icon(painterResource(iconRes), contentDescription = null) },
        selected = false,
        onClick = {
            onClick()
        },
        modifier = Modifier.padding(vertical = 4.dp))
}