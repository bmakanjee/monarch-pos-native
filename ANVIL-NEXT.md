# ANVIL - Your Job (Chunk 3 Stripe Integration)

Friday has scaffolded everything. All screens compile-ready. Your ONLY job = 2 TODOs in Navigation.kt.

## Files done by Friday - DO NOT rewrite:
- model/ServiceCatalog.kt — verified Monarch pricing
- ui/theme/MonarchTheme.kt — gold/black brand
- ui/screen/HomeScreen.kt — service buttons
- ui/screen/VehiclePickerScreen.kt — sedan/SUV picker
- ui/screen/CustomAmountScreen.kt — custom amount entry
- ui/screen/DeclinedScreen.kt — retry/cancel
- ui/screen/Navigation.kt — nav graph (has 2 TODOs for you)
- ui/screen/MainActivity.kt — wired up

## TODO 1: Routes.PAYMENT composable in Navigation.kt

```kotlin
composable(Routes.PAYMENT) {
    val order = pendingOrder ?: return@composable
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(order) {
        scope.launch {
            // 1. Get payment intent from Worker
            val response = BackendClient.createPaymentIntent(
                amountCents = order.amountCents,
                currency = "aud",
                label = order.label
            )
            // BackendClient at: com/stripe/aod/sampleapp/network/BackendService.kt (existing)
            // OR write simple okhttp POST to:
            // POST https://monarch-pinpad.monarchdetailing.workers.dev/create-payment-intent
            // body: {"amount": 19900, "currency": "aud"}
            // returns: {"clientSecret": "pi_xxx_secret_xxx"}

            // 2. Collect payment
            Terminal.getInstance().retrievePaymentIntent(response.clientSecret) { pi, err ->
                if (err != null) { navController.popBackStack(); return@retrievePaymentIntent }
                Terminal.getInstance().collectPaymentMethod(pi!!, object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Terminal.getInstance().confirmPaymentIntent(paymentIntent, object : PaymentIntentCallback {
                            override fun onSuccess(pi: PaymentIntent) {
                                pendingOrder = pendingOrder?.copy(/* store last4 from pi.charges */ )
                                navController.navigate(Routes.APPROVED)
                            }
                            override fun onFailure(e: TerminalException) {
                                navController.navigate(Routes.DECLINED)
                            }
                        })
                    }
                    override fun onFailure(e: TerminalException) { navController.navigate(Routes.DECLINED) }
                })
            }
        }
    }

    // Show loading spinner while collecting
    Box(Modifier.fillMaxSize().background(MonarchBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MonarchGold)
            Spacer(Modifier.height(16.dp))
            Text("Tap card on reader...", color = Color.White)
            Text(order.displayAmount, fontSize = 32.sp, color = MonarchGold, fontWeight = FontWeight.Bold)
        }
    }
}
```

## TODO 2: Routes.APPROVED composable in Navigation.kt

```kotlin
composable(Routes.APPROVED) {
    val order = pendingOrder
    ApprovedScreen(
        amount = order?.displayAmount ?: "",
        onDone = {
            pendingOrder = null
            navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
        }
    )
}
```

Then create ApprovedScreen.kt:
- Big green checkmark (✓)
- Amount in MonarchGold, large
- Label: order.label  
- 3 buttons: Email receipt / SMS receipt / Skip
- All navigate HOME

## Stripe SDK setup (if not already done):
1. In build.gradle: `implementation 'com.stripe:stripeterminal-android:3.+'`
2. In MyApp.kt: `TerminalApplicationDelegate.onCreate(this)`
3. Implement ConnectionTokenProvider to call GET /connection-token

## Backend (already deployed, just call it):
- Base URL: https://monarch-pinpad.monarchdetailing.workers.dev
- POST /create-payment-intent -> {clientSecret, paymentIntentId}
- GET /connection-token -> {secret}
- POST /send-receipt {paymentIntentId, method: email/sms, contact}

## START CHECKLIST:
1. curl localhost:1234/v1/models - if down, kanban_block
2. ls ~/Code/monarch-pos-native/app/src/main/java/com/monarch/pos/
3. Read Navigation.kt - understand the 2 TODO locations
4. Implement Payment screen first (fills the loading UI + Stripe calls)
5. Implement Approved screen
6. git add -A && git commit -m "feat: Stripe payment + approved screens" && git push
7. DELIVER: bash ~/.hermes/scripts/agent-dm-braj.sh "ANVIL POS: Payment + Approved done. PR: github.com/bmakanjee/monarch-pos-native"
8. kanban_complete
