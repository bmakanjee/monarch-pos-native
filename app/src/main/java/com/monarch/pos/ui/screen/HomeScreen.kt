package com.monarch.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monarch.pos.model.Service
import com.monarch.pos.model.ServiceCatalog
import com.monarch.pos.ui.theme.MonarchGold
import com.monarch.pos.ui.theme.MonarchBlack

@Composable
fun HomeScreen(
    onServiceSelected: (Service) -> Unit,
    onCustomAmount: () -> Unit,
    dailySalesCount: Int = 0,
    dailySalesTotal: String = "$0.00"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonarchBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Text(
            text = "MONARCH",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MonarchGold,
            letterSpacing = 6.sp
        )
        Text(
            text = "DETAILING",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Service buttons grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ServiceCatalog.services) { service ->
                ServiceCard(
                    service = service,
                    onClick = { onServiceSelected(service) }
                )
            }
            // Custom amount card (full width via span - TODO: GridItemSpan when needed)
            item {
                ServiceCard(
                    label = "Custom Amount",
                    emoji = "▸",
                    subtitle = "Enter amount",
                    onClick = onCustomAmount
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily summary footer
        Text(
            text = "Today: $dailySalesCount sales · $dailySalesTotal",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    onClick: () -> Unit
) {
    ServiceCard(
        label = service.name,
        emoji = service.emoji,
        subtitle = "from ${"$%.0f".format(service.sedanPrice / 100.0)}",
        onClick = onClick
    )
}

@Composable
private fun ServiceCard(
    label: String,
    emoji: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MonarchGold
            )
        }
    }
}
