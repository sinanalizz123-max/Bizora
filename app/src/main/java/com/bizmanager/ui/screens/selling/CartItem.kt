package com.bizmanager.ui.screens.selling

data class CartItem(
    val productId: Long,
    val productName: String,
    val variantId: Long? = null,
    val variantName: String? = null,
    val unitPrice: Double,
    val quantity: Double = 1.0,
    val taxRate: Double = 0.0,
    val isWeightBased: Boolean = false
) {
    val lineSubtotal: Double get() = unitPrice * quantity
}
