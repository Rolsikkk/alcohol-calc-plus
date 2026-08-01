package com.homedistill.alcoholcalc.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.homedistill.alcoholcalc.core.update.isNewerVersion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionTag: String,
    val downloadUrl: String,
    val assetName: String,
    /** The release's description/changelog text, as written on GitHub. May be blank. */
    val releaseNotes: String,
)

/** Download progress: [bytesRead] so far, [totalBytes] from Content-Length or -1 if unknown. */
data class DownloadProgress(val bytesRead: Long, val totalBytes: Long) {
    val percent: Int? get() = if (totalBytes > 0) ((bytesRead * 100) / totalBytes).toInt() else null
}

private const val REPO_OWNER = "Rolsikkk"
private const val REPO_NAME = "alcohol-calc-plus"
private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest"
private const val USER_AGENT = "$REPO_NAME-app"
private const val CONNECT_TIMEOUT_MS = 10_000
private const val READ_TIMEOUT_MS = 15_000
private const val DOWNLOAD_OVERALL_TIMEOUT_MS = 300_000L
private const val PROGRESS_UPDATE_STEP_BYTES = 32 * 1024

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

            UpdateInfo(
                versionTag = tag,
                downloadUrl = downloadUrl,
                assetName = assetName,
                releaseNotes = json.optString("body").trim(),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Downloads the APK to the app's cache dir, reporting progress via [onProgress].
     * Gives up after [DOWNLOAD_OVERALL_TIMEOUT_MS] so a stalled connection fails instead of
     * spinning forever. Returns the file, or null on failure/timeout.
     */
    suspend fun downloadApk(info: UpdateInfo, onProgress: (DownloadProgress) -> Unit): File? =
        withContext(Dispatchers.IO) {
            withTimeoutOrNull(DOWNLOAD_OVERALL_TIMEOUT_MS) {
                try {
                    val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
                    val outFile = File(updatesDir, info.assetName)
                    val connection = (URL(info.downloadUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = CONNECT_TIMEOUT_MS
                        readTimeout = READ_TIMEOUT_MS
                        instanceFollowRedirects = true
                        setRequestProperty("User-Agent", USER_AGENT)
                    }
                    try {
                        if (connection.responseCode !in 200..299) return@withTimeoutOrNull null
                        val totalBytes = connection.contentLengthLong

                        connection.inputStream.use { input ->
                            outFile.outputStream().use { output ->
                                val buffer = ByteArray(8 * 1024)
                                var bytesRead = 0L
                                var bytesSinceLastUpdate = 0
                                onProgress(DownloadProgress(0L, totalBytes))
                                while (true) {
                                    val read = input.read(buffer)
                                    if (read == -1) break
                                    output.write(buffer, 0, read)
                                    bytesRead += read
                                    bytesSinceLastUpdate += read
                                    if (bytesSinceLastUpdate >= PROGRESS_UPDATE_STEP_BYTES) {
                                        bytesSinceLastUpdate = 0
                                        onProgress(DownloadProgress(bytesRead, totalBytes))
                                    }
                                }
                                onProgress(DownloadProgress(bytesRead, totalBytes))
                            }
                        }
                        outFile
                    } finally {
                        connection.disconnect()
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
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
            setRequestProperty("User-Agent", USER_AGENT)
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
