package com.example.expensetracker.view.home.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.repository.Routes
import com.google.firebase.auth.ktx.auth
import androidx.compose.material.icons.filled.Edit
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(navController: NavController) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    var userName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            Firebase.firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("name") ?: ""
                        userId = document.getString("userId") ?: "N/A"
                        profileImageUrl = document.getString("profileImage")
                    } else {
                        error = "User data not found"
                    }
                    loading = false
                }
                .addOnFailureListener { e ->
                    error = "Error fetching data: ${e.message}"
                    loading = false
                }
        } ?: run {
            error = "User not logged in"
            loading = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card with Back Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xEB16292F))

            ) {
                Box(
                    modifier = Modifier.padding(19.dp),
                    contentAlignment = Alignment.Center,

                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { navController.navigate(Routes.MAIN_SCREEN) },
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Profile",
                            fontSize = 30.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    offset = Offset(0f, 10f),
                                    blurRadius = 8f
                                )
                            ),
                            modifier = Modifier.padding(start = 96.dp, top = 24.dp),
                            color = Color.White
                        )
                    }
                }
            }

            when {
                loading -> FullScreenLoader()
                error != null -> ErrorSection(
                    errorMessage = error!!,
                    onRetry = { error = null; loading = true }
                )

                else -> ProfileContent(
                    userName = userName,
                    email = currentUser?.email ?: "",
                    userId = userId,
                    navController = navController,  // Fixed: Passing the actual navController
                    onLogout = {
                        auth.signOut()
                        navController.navigate(Routes.SIGN_IN_SCREEN) {
                            popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
                        }
                    },
                    onDeleteAccount = {
                        currentUser?.delete()?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                navController.navigate(Routes.SIGN_IN_SCREEN) {
                                    popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    userName: String,
    email: String,
    userId: String,
    navController: NavController,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(
            userName = userName,
            email = email,
            userId = userId,
            navController
        )

        Spacer(modifier = Modifier.height(40.dp))

        ProfileActions(
            onLogout = onLogout,
            onDeleteAccount = onDeleteAccount
        )
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    email: String,
    userId: String,
    navController: NavController
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(
                    8.dp,
                    CircleShape,
                    ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(Color(0xFF2E343B)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = userName.takeIf { it.isNotEmpty() }?.first()?.uppercase() ?: "?",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

        }


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,

            ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { navController.navigate(Routes.EDIT_PROFILE) }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Username",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ID: $userId",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


// باقي الدوال (ProfileActions, ErrorSection, FullScreenLoader) تبقى كما هي بدون تغيير

@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProfileActions(
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = { showLogoutDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        TextButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Delete Account",
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Exit Profile") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) { Text("EXIT") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) { Text("CANCEL") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile") },
            text = {
                Text("Your profile and all data will be permanently deleted. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("DELETE") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) { Text("CANCEL") }
            }
        )
    }
}

@Composable
private fun ErrorSection(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Try Again")
        }
    }
}