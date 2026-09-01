package com.bizmanager.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PrinterLine(
    val text: String = "",
    val bold: Boolean = false,
    val center: Boolean = false,
    val doubleHeight: Boolean = false
)

class BluetoothPrinter(private val context: Context) {

    companion object {
        private const val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb"
    }

    @SuppressLint("MissingPermission")
    private fun adapter(): BluetoothAdapter? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) return null
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return manager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    @SuppressLint("MissingPermission")
    fun pairedPrinters(): List<String> {
        val adapter = adapter() ?: return emptyList()
        return adapter.bondedDevices.mapNotNull { it.name ?: "Printer" }
    }

    @SuppressLint("MissingPermission")
    suspend fun print(lines: List<PrinterLine>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val adapter = adapter() ?: throw IllegalStateException("Bluetooth unavailable or permission missing")
            if (!adapter.isEnabled) throw IllegalStateException("Bluetooth is off")
            val device: BluetoothDevice = adapter.bondedDevices
                .firstOrNull { it.type == BluetoothDevice.DEVICE_TYPE_CLASSIC && it.name != null }
                ?: throw IllegalStateException("No paired classic Bluetooth printer found")
            val socket: BluetoothSocket =
                device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID)) as? BluetoothSocket
                    ?: throw IllegalStateException("Could not create socket")
            socket.connect()
            val output: OutputStream = socket.outputStream
            output.write(buildEscPos(lines))
            output.flush()
            socket.close()
        }
    }

    private fun buildEscPos(lines: List<PrinterLine>): ByteArray {
        val bytes = java.io.ByteArrayOutputStream()
        bytes.write(byteArrayOf(0x1B, 0x40)) // initialize printer
        bytes.write(byteArrayOf(0x1B, 0x74, 0x00)) // codepage 0 (cp437)
        lines.forEach { line ->
            if (line.doubleHeight) {
                bytes.write(byteArrayOf(0x1D, 0x21, 0x11))
            } else {
                bytes.write(byteArrayOf(0x1D, 0x21, 0x00))
            }
            if (line.bold) {
                bytes.write(byteArrayOf(0x1B, 0x45, 0x01))
            }
            bytes.write(
                if (line.center) byteArrayOf(0x1B, 0x61, 0x01)
                else byteArrayOf(0x1B, 0x61, 0x00)
            )
            val body = line.text.trimEnd()
            val encoded = (if (line.center) body else body.take(42)).toByteArray(Charsets.US_ASCII)
            bytes.write(encoded)
            bytes.write(byteArrayOf(0x0A)) // newline
            if (line.bold) {
                bytes.write(byteArrayOf(0x1B, 0x45, 0x00))
            }
        }
        bytes.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00)) // partial cut
        return bytes.toByteArray()
    }
}
