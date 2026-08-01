package com.homedistill.alcoholcalc.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.homedistill.alcoholcalc.core.update.isNewerVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionTag: String,
    val downloadUrl: String,
    val assetName: String,
)

private const val REPO_OWNER = "Rolsikkk"
private const val REPO_NAME = "alcohol-calc-plus"
private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest"
private const val CONNECT_TIMEOUT_MS = 10_000
private const val READ_TIMEOUT_MS = 10_000

class UpdateRepository(private val context: Context) {

    /** Returns update info if a newer GitHub release exists, or null if up to date / check failed. */
    suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val json = fetchJson(LATEST_RELEASE_URL) ?: return@withContext null
            val tag = json.optString("tag_name").takeIf { it.isNotBlank() } ?: return@withContext null
            if (!isNewerVersion(currentVersionName, tag)) return@withContext null

            val assets = json.optJSONArray("assets") ?: return@withContext null
            var downloadUrl: String? = null
            var assetName: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.optString("name")
                if (name.endsWith(".apk")) {
                    downloadUrl = asset.optString("browser_download_url")
                    assetName = name
                    break
                }
            }
            if (downloadUrl.isNullOrBlank() || assetName == null) return@withContext null

            UpdateInfo(versionTag = tag, downloadUrl = downloadUrl, assetName = assetName)
        } catch (_: Exception) {
            null
        }
    }

    /** Downloads the APK to the app's cache dir. Returns the file, or null on failure. */
    suspend fun downloadApk(info: UpdateInfo): File? = withContext(Dispatchers.IO) {
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val outFile = File(updatesDir, info.assetName)
            val connection = (URL(info.downloadUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                instanceFollowRedirects = true
            }
            connection.inputStream.use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
            connection.disconnect()
            outFile
        } catch (_: Exception) {
            null
        }
    }

    /** Launches the system package installer for the downloaded APK. */
    fun installApk(file: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    private fun fetchJson(url: String): JSONObject? {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            requestMethod = "GET"
            setRequestProperty("Accept", "application/vnd.github+json")
        }
        return try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(body)
        } finally {
            connection.disconnect()
        }
    }
}
