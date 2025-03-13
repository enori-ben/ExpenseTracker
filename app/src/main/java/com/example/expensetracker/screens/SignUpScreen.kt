package com.example.expensetracker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.Routes

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassowrd by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // حالة إظهار/إخفاء الباسوورد




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.up),
            contentDescription = "Sign In Header",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp)
                .padding(top = 4.dp)
                .background(Color.White)
        ) {
            // حقل الإيميل (بقي كما هو)
            TextField(
                value = email,
                onValueChange = { email = it },

                label = {
                    Text(
                        "Enter Email",
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 31.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF040401),
                    unfocusedTextColor = Color(0xFF040401),
                    focusedBorderColor = Color(0xFF040401),
                    unfocusedBorderColor = Color(0xFF040401),
                    focusedLabelColor = Color(0xFF5E562A),
                    unfocusedLabelColor = Color(0xFF5E562A),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Enter Password",
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Fit
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF040401),
                    unfocusedTextColor = Color(0xFF040401),
                    focusedBorderColor = Color(0xFF040401),
                    unfocusedBorderColor = Color(0xFF040401),
                    focusedLabelColor = Color(0xFF5E562A),
                    unfocusedLabelColor = Color(0xFF5E562A),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                // حقل الباسوورد مع زر الإظهار/الإخفاء

                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            tint = Color.Black,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            )


            TextField(
                value = confirmPassowrd,
                onValueChange = { confirmPassowrd = it },
                label = {
                    Text(
                        "Confirm Password",
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Fit
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF040401),
                    unfocusedTextColor = Color(0xFF040401),
                    focusedBorderColor = Color(0xFF040401),
                    unfocusedBorderColor = Color(0xFF040401),
                    focusedLabelColor = Color(0xFF5E562A),
                    unfocusedLabelColor = Color(0xFF5E562A),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                // حقل الباسوورد مع زر الإظهار/الإخفاء

                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            tint = Color.Black,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            )

            // Forgot Password كزر
            Box(
                modifier = Modifier
                    .padding(start = 21.dp, top = 13.dp)
                    .clickable { /* Handle forgot password */ }

                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Forgot password?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF523804)

                )
            }

            Button(
                onClick = {
                    println("Email: $email")
                    println("Password: $password")
                    println("Confirm Password: $confirmPassowrd")

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 30.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(80.dp),
                        ambientColor = Color(0xFF605118),
                        spotColor = Color(0xFF605118),
                    )
                    .clip(RoundedCornerShape(40.dp))
            ) {
                Text("Sign Up")
            }

            // قسم "Don't have an account?" مع الخطوط
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Already have an account? ",
                        color = Color(0xB5513700)
                    )

                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.SIGN_IN_SCREEN) {
                                    launchSingleTop = true
                                    popUpTo(Routes.SIGN_UP_SCREEN) { inclusive = true }
                                }
                            }
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(60.dp),
                            )
                            .clip(RoundedCornerShape(40.dp))
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }


            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(navController = rememberNavController())
}