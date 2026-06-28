package com.monarch.pos.ui.screen

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stripe.stripeterminal.external.model.PaymentIntent
import androidx.navigation.compose.rememberNavController
import com.monarch.pos.model.Order
import com.monarch.pos.model.Service
import com.monarch.pos.model.VehicleType

// --- Route constants ---
object Routes {
    const val HOME = "home"
    const val VEHICLE_PICKER = "vehicle_picker"
    const val CUSTOM_AMOUNT = "custom_amount"
    const val PAYMENT = "payment"       // TODO: Stripe SDK payment screen (ANVIL task)
    const val APPROVED = "approved"     // TODO: Approved screen (ANVIL task)
    const val DECLINED = "declined"
}

@Composable
fun MonarchNavGraph() {
    val navController = rememberNavController()

    // Shared order state - built across screens then handed to Stripe
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
            val order = pendingOrder ?: return@composable
            val scope = rememberCoroutineScope()
 
            LaunchedEffect(order) {
                scope.launch {
                    // 1. Get payment intent from backend
                    val response = BackendClient.createPaymentIntent(
                        amountCents = order.amountCents,
                        currency = "aud",
                        label = order.label
                    )
                    // 2. Collect payment using terminal SDK
                    Terminal.getInstance().retrievePaymentIntent(response.clientSecret) { pi, err ->
                        if (err != null) {
                            navController.popBackStack()
                            return@retrievePaymentIntent
        }
        Terminal.getInstance().collectPaymentMethod(pi!!, object : PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                Terminal.getInstance().confirmPaymentIntent(pi, object : PaymentIntentCallback {
                    override fun onSuccess(pi: PaymentIntent) {
                        // Store last4 from the charge for receipt
                        pendingOrder = order.copy()
        navController.navigate(Routes.APPROVED)
                    }
                    override fun onFailure(e: TerminalException) {
                        navController.navigate(Routes.DECLINED)
                    }
                })
            }
            override fun onFailure(e: TerminalException) {
                navController.navigate(Routes.DECLINED)
            }
        })
    }
}
    }

    // Show loading spinner while collecting
    Box(Modifier.fillMaxSize().background(MonarchBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MonarchGold)
            Text("Tap card on reader...", color = Color.White)
        }
    }
    }

    // 3. Add imports at top of file
    // import com.stripe.stripeterminal.external.models.PaymentIntent
    // import com.stripe.stripeterminal.external.model.PaymentIntentCallback
            // Backend URL: https://monarch-pinpad.monarchdetailing.workers.dev/create-payment-intent
            // Stripe publishable key: inject via BuildConfig.STRIPE_PUBLISHABLE_KEY
        }

        composable(Routes.APPROVED) {
            val order = pendingOrder ?: return@composable
            ApprovedScreen(
                order = order,
                onDone = {
                    pendingOrder = null
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                }
            )
        }
            // On any choice -> navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
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
