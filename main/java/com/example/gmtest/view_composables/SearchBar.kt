package com.example.gmtest.view_composables

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar() {
    TopAppBar(
        title = { Text(text = "Contacts", fontSize = 18.sp, fontFamily = FontFamily.SansSerif) },
        backgroundColor = Color.LightGray,
        contentColor = Color.Blue
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar()
}