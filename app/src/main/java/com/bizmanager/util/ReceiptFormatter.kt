package com.bizmanager.util

import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.entity.SaleItemEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReceiptData(
    val businessName: String,
    val businessPhone: String,
    val businessAddress: String,
    val sale: SaleEntity,
    val items: List<SaleItemEntity>,
    val receiptFooter: String = "Thank you for your visit!"
)

object ReceiptFormatter {

    fun toPrinterLines(data: ReceiptData): List<PrinterLine> {
        val lines = mutableListOf<PrinterLine>()
        lines += PrinterLine(data.businessName.ifBlank { "Business" }, bold = true, center = true, doubleHeight = true)
        if (data.businessPhone.isNotBlank()) lines += PrinterLine(data.businessPhone, center = true)
        if (data.businessAddress.isNotBlank()) lines += PrinterLine(data.businessAddress, center = true)
        lines += PrinterLine("")
        lines += PrinterLine("Inv: ${data.sale.transactionNumber}", bold = true)
        lines += PrinterLine("Date: ${formatTime(data.sale.timestamp)}")
        lines += PrinterLine("Payment: ${data.sale.paymentMethod}")
        lines += PrinterLine("")
        lines += PrinterLine("ITEM".padEnd(22) + "AMOUNT")
        data.items.forEach { item ->
            val name = item.productSnapshot.take(20)
            lines += PrinterLine(name)
            lines += PrinterLine("${qty(item.quantity)} x $%.2f".format(item.unitPrice).padEnd(22) + "$%.2f".format(item.lineTotal))
        }
        lines += PrinterLine("")
        lines += PrinterLine("Subtotal".padEnd(22) + "$%.2f".format(data.sale.subtotal))
        if (data.sale.discount > 0) {
            lines += PrinterLine("Discount".padEnd(22) + "-$%.2f".format(data.sale.discount))
        }
        if (data.sale.taxTotal > 0) {
            lines += PrinterLine("Tax".padEnd(22) + "$%.2f".format(data.sale.taxTotal))
        }
        lines += PrinterLine("TOTAL".padEnd(22) + "$%.2f".format(data.sale.total), bold = true)
        if (data.sale.amountReceived > 0) {
            lines += PrinterLine("Paid".padEnd(22) + "$%.2f".format(data.sale.amountReceived))
            lines += PrinterLine("Change".padEnd(22) + "$%.2f".format(data.sale.changeDue))
        }
        lines += PrinterLine("")
        lines += PrinterLine(data.receiptFooter, center = true)
        return lines
    }

    fun toText(data: ReceiptData): String = toPrinterLines(data).joinToString("\n") { it.text }

    private fun formatTime(ts: Long): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(ts))

    private fun qty(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else "%.2f".format(value)
}
