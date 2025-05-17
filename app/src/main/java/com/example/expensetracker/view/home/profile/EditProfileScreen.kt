package com.example.expensetracker.view.home.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.Routes
import com.example.expensetracker.screens.ResponsiveTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEB16292F))
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Change name",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 46.dp)
        )
        Spacer(modifier = Modifier.height(46.dp))

        TextField(value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center, fontSize = 24.sp, color = Color.Black
            ),


            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF040401),
                unfocusedTextColor = Color(0xFF040401),
                focusedBorderColor = Color(0xFF040401),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF040401),
                unfocusedLabelColor = Color(0xFF040401),
                cursorColor = Color(0xFF040401)
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
        )
//
//        ResponsiveTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = "New Name",
//            iconRes = R.drawable.profile,
//            keyboardType = KeyboardType.Text,
//
//        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFFFFBB0C)),
            onClick = {
                isLoading = true
                val user = auth.currentUser
                val updates = hashMapOf<String, Any>(
                    "name" to name
                )

                // تحديث البيانات في Firestore
                db.collection("users").document(user!!.uid)
                    .update(updates)
                navController.navigate(Routes.PROFILE_SCREEN)

            },
            modifier = Modifier.fillMaxWidth()
                .padding( start = 120.dp, end = 120.dp)
            ,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.save_changes),color = Color.Black)
            }
        }

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel),color = Color(0xFF477496))
        }
    }
}