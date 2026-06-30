package com.monarch.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monarch.pos.model.Order
import com.monarch.pos.model.Service
import com.monarch.pos.model.VehicleType
import com.monarch.pos.network.BackendClient
import com.monarch.pos.ui.theme.MonarchBlack
import com.monarch.pos.ui.theme.MonarchGold
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.launch

// --- Route constants ---
object Routes {
    const val HOME = "home"
    const val VEHICLE_PICKER = "vehicle_picker"
    const val CUSTOM_AMOUNT = "custom_amount"
    const val PAYMENT = "payment"
    const val APPROVED = "approved"
    const val DECLINED = "declined"
}

@Composable
fun MonarchNavGraph() {
    val navController = rememberNavController()
    var pendingOrder by remember { mutableStateOf<Order?>(null) }
    var pendingService by remember { mutableStateOf<Service?>(null) }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onServiceSelected = { service ->
                    pendingService = service
                    navController.navigate(Routes.VEHICLE_PICKER)
                },
                onCustomAmount = {
                    pendingService = null
                    navController.navigate(Routes.CUSTOM_AMOUNT)
                }
            )
        }

        composable(Routes.VEHICLE_PICKER) {
            val service = pendingService
            if (service == null) {
                navController.popBackStack()
            } else {
                VehiclePickerScreen(
                    service = service,
                    onVehicleSelected = { vehicleType ->
                        pendingOrder = Order(service = service, vehicleType = vehicleType)
                        navController.navigate(Routes.PAYMENT)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.CUSTOM_AMOUNT) {
            CustomAmountScreen(
                onAmountConfirmed = { amountCents ->
                    pendingOrder = Order(service = null, vehicleType = null, customAmountCents = amountCents)
                    navController.navigate(Routes.PAYMENT)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PAYMENT) {
            val order = pendingOrder ?: run {
                navController.popBackStack()
                return@composable
            }
            val scope = rememberCoroutineScope()

            // Show loading spinner while Stripe processes
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MonarchBlack),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MonarchGold)
                    Text("Tap card on reader...", color = Color.White)
                }
            }

            LaunchedEffect(order) {
                scope.launch {
                    try {
                        // 1. Create payment intent via Cloudflare Worker backend
                        val response = BackendClient.createPaymentIntent(
                            amountCents = order.amountCents,
                            currency = "aud",
                            label = order.label ?: "Monarch Detailing"
                        )

                        // 2. Retrieve payment intent from Stripe
                        Terminal.getInstance().retrievePaymentIntent(
                            response.clientSecret,
                            object : PaymentIntentCallback {
                                override fun onSuccess(paymentIntent: PaymentIntent) {
                                    // 3. Collect payment method on S710 reader
                                    Terminal.getInstance().collectPaymentMethod(
                                        paymentIntent,
                                        object : PaymentIntentCallback {
                                            override fun onSuccess(collected: PaymentIntent) {
                                                // 4. Confirm payment
                                                Terminal.getInstance().confirmPaymentIntent(
                                                    collected,
                                                    object : PaymentIntentCallback {
                                                        override fun onSuccess(confirmed: PaymentIntent) {
                                                            pendingOrder = order
                                                            navController.navigate(Routes.APPROVED)
                                                        }
                                                        override fun onFailure(e: TerminalException) {
                                                            navController.navigate(Routes.DECLINED)
                                                        }
                                                    }
                                                )
                                            }
                                            override fun onFailure(e: TerminalException) {
                                                navController.navigate(Routes.DECLINED)
                                            }
                                        }
                                    )
                                }
                                override fun onFailure(e: TerminalException) {
                                    navController.navigate(Routes.DECLINED)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        navController.navigate(Routes.DECLINED)
                    }
                }
            }
        }

        composable(Routes.APPROVED) {
            val order = pendingOrder ?: run {
                navController.popBackStack()
                return@composable
            }
            ApprovedScreen(
                order = order,
                onDone = {
                    pendingOrder = null
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DECLINED) {
            val order = pendingOrder
            DeclinedScreen(
                amountAttempted = order?.displayAmount ?: "",
                onRetry = { navController.popBackStack(Routes.PAYMENT, inclusive = false) },
                onCancel = {
                    pendingOrder = null
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
