package com.monarch.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monarch.pos.ui.theme.MonarchGold
import com.monarch.pos.ui.theme.MonarchBlack

@Composable
fun CustomAmountScreen(
    onAmountConfirmed: (Int) -> Unit,  // amount in cents
    onBack: () -> Unit
) {
    // State: raw digit string e.g. "19900" = $199.00
    var digits by remember { mutableStateOf("") }

    val amountCents: Int = digits.toIntOrNull() ?: 0
    val displayAmount: String = "${"$%.2f".format(amountCents / 100.0)}"

    val presets = listOf(5000, 10000, 25000)  // $50, $100, $250

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonarchBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "MONARCH",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MonarchGold,
            letterSpacing = 5.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Custom Amount",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Amount display
        Text(
            text = displayAmount,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = if (amountCents > 0) MonarchGold else Color.White.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preset buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            presets.forEach { cents ->
                OutlinedButton(
                    onClick = { digits = cents.toString() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MonarchGold),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MonarchGold)
                    )
                ) {
                    Text("${"$%.0f".format(cents / 100.0)}")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Numeric entry field - system keyboard (S710 has no physical keypad)
        OutlinedTextField(
            value = digits,
            onValueChange = { raw ->
                // Only digits, max 7 (max $99,999.99)
                val cleaned = raw.filter { it.isDigit() }.take(7)
                digits = cleaned
            },
            label = { Text("Amount in cents (e.g. 19900 = \$199)", color = Color.White.copy(0.5f)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MonarchGold,
                unfocusedBorderColor = Color.White.copy(0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = MonarchGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Confirm button
        Button(
            onClick = { if (amountCents >= 100) onAmountConfirmed(amountCents) },
            enabled = amountCents >= 100,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonarchGold,
                contentColor = MonarchBlack,
                disabledContainerColor = Color.White.copy(0.1f)
            )
        ) {
            Text(
                text = if (amountCents >= 100) "Charge $displayAmount" else "Enter amount",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Back", color = Color.White.copy(0.5f))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
