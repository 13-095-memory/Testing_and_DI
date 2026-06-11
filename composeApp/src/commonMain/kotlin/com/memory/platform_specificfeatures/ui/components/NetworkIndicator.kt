package com.memory.platform_specificfeatures.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NetworkStatusIndicator(isConnected: Boolean) {
    AnimatedVisibility(
        visible = true,
        enter = expandVertically(),
        exit = shrinkVertically()

    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 150.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Connected" else "No Internet Connection",
                    color = if (isConnected) Color(0xFF388E3C) else Color(0xFFC62828),
                    fontSize = 13.sp
                )
            }
        }
    }
}
