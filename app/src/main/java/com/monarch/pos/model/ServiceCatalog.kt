package com.monarch.pos.model

// Monarch Detailing official service catalog (verified pricing)
data class Service(
    val id: String,
    val name: String,
    val emoji: String,
    val sedanPrice: Int,   // cents
    val suvPrice: Int      // cents
)

object ServiceCatalog {
    val services = listOf(
        Service("signature",  "✨ Signature",  "✨", 19900, 21900),
        Service("executive",  "⭐ Executive",  "⭐", 49900, 54900),
        Service("presale",    "💎 Pre-Sale",   "💎", 89900, 99900)
    )

    fun findById(id: String) = services.find { it.id == id }
}

enum class VehicleType(val label: String) {
    SEDAN("Sedan / Hatch / Coupe"),
    SUV("SUV / Ute")
}

data class Order(
    val service: Service?,     // null = custom amount
    val vehicleType: VehicleType?,
    val customAmountCents: Int = 0
) {
    val amountCents: Int get() = when {
        service != null && vehicleType == VehicleType.SEDAN -> service.sedanPrice
        service != null && vehicleType == VehicleType.SUV   -> service.suvPrice
        else -> customAmountCents
    }

    val displayAmount: String get() = "$%.2f".format(amountCents / 100.0)
    val label: String get() = service?.name ?: "Custom"
}
