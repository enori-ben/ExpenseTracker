package com.example.expensetracker.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.Routes

@Composable
fun HomeScreen(navController: NavController,
               onProfileClick: () -> Unit = { navController.navigate(Routes.PROFILE_SCREEN) },
               onMenuClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(30.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Button
                Box(
                    modifier = Modifier
                        .clickable(onClick = onProfileClick)
                        .size(57.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.me),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Menu Button
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Menu",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}