package com.example.expensetracker.data

import com.example.expensetracker.R
import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val iconRes: Int,
    val color: Color
)

val categories = listOf(
    Category("Health",    R.drawable.ic_health,    Color(0xFFDC2626)),
    Category("Food",      R.drawable.ic_food,      Color(0xFF4A74D5)),
    Category("Workout",   R.drawable.gym,          Color(0xFFD97706)),
    Category("Apparel",   R.drawable.ic_apparal,   Color(0xFF7F0EEE)),
    Category("Education", R.drawable.ic_edu,        Color(0xFF000000)),
    Category("Gifts",     R.drawable.ic_gifts,      Color(0xFFDB2777)),
    Category("Transport", R.drawable.ic_transport,  Color(0xFF0025E1)),
    Category("Other",     R.drawable.other,         Color(0xFF4B5563))
)
