// Copyright 2011 Google Inc. All Rights Reserved.

package karrel.kr.co.wifidirectsample

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import karrel.kr.co.wifidirectsample.util.copyFile

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

class FileTransferService() : IntentService("FileTransferService") {

    override fun onHandleIntent(intent: Intent?) {
        println("FileTransferService > onHandleIntent")

        val context = applicationContext
        if (intent!!.action == ACTION_SEND_FILE) {
            val fileUri = intent.extras!!.getString(EXTRAS_FILE_PATH)

            val host = intent.extras!!.getString(EXTRAS_GROUP_OWNER_ADDRESS)
            val socket = Socket()
            val port = intent.extras!!.getInt(EXTRAS_GROUP_OWNER_PORT)

            try {
                socket.bind(null)
                socket.connect(InetSocketAddress(host, port), SOCKET_TIMEOUT)

                val stream = socket.getOutputStream()
                val cr = context.contentResolver
                var inputStream: InputStream? = null
                try {
                    inputStream = cr.openInputStream(Uri.parse(fileUri))
                } catch (e: FileNotFoundException) {
                }

                copyFile(inputStream!!, stream)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (socket != null) {
                    if (socket.isConnected) {
                        try {
                            socket.close()
                        } catch (e: IOException) {
                            // Give up
                            e.printStackTrace()
                        }

                    }
                }
            }

        }
    }

    companion object {

        private val SOCKET_TIMEOUT = 5000
        val ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE"
        val EXTRAS_FILE_PATH = "file_url"
        val EXTRAS_GROUP_OWNER_ADDRESS = "go_host"
        val EXTRAS_GROUP_OWNER_PORT = "go_port"
    }
}
