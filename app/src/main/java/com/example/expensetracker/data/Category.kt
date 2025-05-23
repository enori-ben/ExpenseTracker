package com.example.expensetracker.data

import com.example.expensetracker.R
import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val iconRes: Int,
    val color: Color
)

val categories = listOf(
    Category("Health",    R.drawable.ic_health,    Color(0xFFAD1C1C)),
    Category("Food",      R.drawable.ic_food,      Color(0xFF51A3AD)),
    Category("Workout",   R.drawable.gym,          Color(0xFFD97706)),
    Category("Apparel",   R.drawable.ic_apparal,   Color(0xFF4B0093)),
    Category("Education", R.drawable.ic_edu,        Color(0xFF0E0D0D)),
    Category("Gifts",     R.drawable.ic_gifts,      Color(0xFFD91E75)),
    Category("Transport", R.drawable.ic_transport,  Color(0xFF006FAB)),
    Category("Other",     R.drawable.other,         Color(0xFF4B5563))
)
