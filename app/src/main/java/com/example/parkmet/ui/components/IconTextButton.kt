package com.example.parkmet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.parkmet.ui.theme.Background
import com.example.parkmet.ui.theme.PrimaryDark

// Button with text and Icon

@Composable
fun IconTextButton(text: String, icon: ImageVector, isTakingFullWith: Boolean=true, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = if(isTakingFullWith) Modifier.clickable { onClick() }.fillMaxWidth() else Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PrimaryDark)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = text, tint= Background)
            Text(text = text, style = MaterialTheme.typography.titleMedium, color= Background)
        }
    }
}
