package com.bizmanager.util

import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {

    private const val DB_FILE = "business_manager.db"
    private const val SETTINGS_FILE = "settings.preferences_pb"

    private fun dbFile(context: Context): File =
        context.getDatabasePath(DB_FILE)

    private fun settingsFile(context: Context): File =
        File(context.filesDir, "datastore").resolve(SETTINGS_FILE)

    fun writeBackup(context: Context, output: OutputStream) {
        val db = dbFile(context)
        val settings = settingsFile(context)
        ZipOutputStream(output.buffered()).use { zip ->
            if (db.exists()) {
                zip.putNextEntry(ZipEntry(DB_FILE))
                db.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
            if (settings.exists()) {
                zip.putNextEntry(ZipEntry(SETTINGS_FILE))
                settings.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    fun readBackup(context: Context, input: InputStream) {
        val db = dbFile(context)
        val settings = settingsFile(context)
        val tempDb = File(context.cacheDir, "restore_$DB_FILE")
        ZipInputStream(input.buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                when (entry.name) {
                    DB_FILE -> {
                        tempDb.outputStream().use { zip.copyTo(it) }
                    }
                    SETTINGS_FILE -> {
                        settings.parentFile?.mkdirs()
                        settings.outputStream().use { zip.copyTo(it) }
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        settings.parentFile?.mkdirs()
        if (tempDb.exists()) {
            db.parentFile?.mkdirs()
            // SQLite journal/WAL files would corrupt a live copy; have the app closed first.
            File(db.parentFile, DB_FILE + "-journal").delete()
            File(db.parentFile, DB_FILE + "-wal").delete()
            File(db.parentFile, DB_FILE + "-shm").delete()
            tempDb.copyTo(db, overwrite = true)
            tempDb.delete()
        }
    }
}
