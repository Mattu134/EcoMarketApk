package com.example.ecomarketapk.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ecomarketapk.R

@Composable
fun EcoLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "EcoMarket Logo",
        modifier = modifier
            .size(120.dp)
            .padding(top = 10.dp)
    )
}
