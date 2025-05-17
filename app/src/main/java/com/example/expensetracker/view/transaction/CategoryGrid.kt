package com.example.expensetracker.view.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.expensetracker.data.Category


@Composable
fun CategoryGrid (
    categories: List<Category>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxWidth()
            .heightIn(max = 400.dp)

    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = (selectedCategory == category.name),
                onClick = { onCategorySelected(category.name) },

                )




        }
    }
}
