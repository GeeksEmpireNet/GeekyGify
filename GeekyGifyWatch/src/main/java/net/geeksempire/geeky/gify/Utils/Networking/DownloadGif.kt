/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/12/20 1:13 PM
 * Last modified 2/12/20 11:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.Networking

import android.content.Context
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadGif (var context: Context) {

    @Throws(Exception::class)
    fun downloadGifFile(linkToGif: String) : Deferred<File> = CoroutineScope(SupervisorJob() + Dispatchers.Default).async {

        val downloadGifByte = URL(linkToGif).readBytes()

        val filePath = context.externalMediaDirs[0].path + "/GeekyGify" + ".GIF"
        val gifFile = File(filePath)
        val fileOutputStream = FileOutputStream(gifFile)

        fileOutputStream.write(downloadGifByte)

        fileOutputStream.flush()
        fileOutputStream.close()

        gifFile
    }
}