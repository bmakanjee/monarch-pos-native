package com.monarch.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monarch.pos.ui.theme.MonarchGold
import com.monarch.pos.ui.theme.MonarchBlack

@Composable
fun DeclinedScreen(
    declineReason: String = "Card declined",
    amountAttempted: String = "",
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonarchBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Error icon placeholder
        Text(text = "✕", fontSize = 64.sp, color = Color(0xFFCF6679))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Payment Declined",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = declineReason,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        if (amountAttempted.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amountAttempted,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCF6679)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Retry
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonarchGold,
                contentColor = MonarchBlack
            )
        ) {
            Text(
                text = "Try Again",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(0.3f))
            )
        ) {
            Text("Cancel", fontSize = 16.sp)
        }
    }
}
