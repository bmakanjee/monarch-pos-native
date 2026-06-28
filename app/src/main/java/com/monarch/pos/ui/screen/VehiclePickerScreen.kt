package com.monarch.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monarch.pos.model.Service
import com.monarch.pos.model.VehicleType
import com.monarch.pos.ui.theme.MonarchGold
import com.monarch.pos.ui.theme.MonarchBlack

@Composable
fun VehiclePickerScreen(
    service: Service,
    onVehicleSelected: (String) -> Unit,
    onBack: () -> Unit
) {
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
                text = service.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        Text(
            text = "Select your vehicle type",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Sedan button
        VehicleButton(
            vehicleType = VehicleType.SEDAN,
            price = service.sedanPrice,
            onClick = { onVehicleSelected(VehicleType.SEDAN) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SUV button
        VehicleButton(
            vehicleType = VehicleType.SUV,
            price = service.suvPrice,
            onClick = { onVehicleSelected(VehicleType.SUV) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Back
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "← Back",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VehicleButton(
    vehicleType: VehicleType,
    price: Int,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color(0xFF1A1A1A),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = vehicleType.label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${"$%.0f".format(price / 100.0)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MonarchGold
            )
        }
    }
}
