package com.monarch.pos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monarch.pos.model.Order
import com.monarch.pos.ui.theme.MonarchGold

@Composable
fun ApprovedScreen(
    order: Order,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = "Approved",
            tint = MonarchGold,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        Spacer(Modifier.height(20.dp))
    
        Text(
            text = "Payment Approved",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = MonarchGold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(Modifier.height(16.dp))
    
        Text(
            text = order.displayAmount,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = MonarchGold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(Modifier.height(20.dp))
    
        Text(
            text = "Service: ${order.label}",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = { onDone() },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Done")
        }
        
        Spacer(Modifier.height(20.dp))
        
        Text(
            text = "Send receipt?",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
        
        Spacer(Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { /* TODO: Email receipt */ },
                modifier = Modifier
                    .size(60.dp)
            ) {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = "Email receipt",
                    tint = MonarchGold
                )
            }
            
            IconButton(
                onClick = { /* TODO: SMS receipt */ },
                modifier = Modifier
                    .size(60.dp)
            ) {
                Icon(
                    Icons.Filled.Sms,
                    contentDescription = "SMS receipt",
    tint = MonarchGold
                )
            }
        }
        
        Spacer(Modifier.height(20.dp))
    }
}