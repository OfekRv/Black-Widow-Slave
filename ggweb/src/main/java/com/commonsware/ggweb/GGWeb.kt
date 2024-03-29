/*
 * Copyright (c) 2021 CommonsWare, LLC
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.commonsware.ggweb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebMessage
import android.webkit.WebMessagePort
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject

private val DEFAULT_MESSAGE_HANDLER: (String) -> Boolean = { false }
private const val AUDIO_STREAM = AudioManager.STREAM_MUSIC

/**
 * API for sending and receiving data over sound, by using GGWave's JavaScript API and HTML bindings in a
 * dynamically-constructed WebView.
 *
 * You need to request and hold the `RECORD_AUDIO` permission before creating an instance of this class. An
 * IllegalStateException will be thrown if you do not hold that permission.
 *
 * @param[context] a `Context` suitable for creating a `WebView`
 * @param[htmlLocation] Uri-style location to ggwave.html file (default = "file:///android_asset/ggwave.html")
 * @param[onReady] will be invoked when it is safe to start calling the other [GGWeb] functions
 */
@SuppressLint("SetJavaScriptEnabled")
class GGWeb(
    private val context: Context,
    private val htmlLocation: String = "file:///android_asset/ggwave.html",
    private val autoAdjustVolume: Boolean = false,
    private val onTxEnded: (GGWeb) -> Unit = {},
    onReady: (GGWeb) -> Unit = {}
) {
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var channel: Array<WebMessagePort> = emptyArray()
    private val web: WebView = WebView(context)
    private var onMessage: (String) -> Boolean = DEFAULT_MESSAGE_HANDLER
    private var lastVolume = audioManager.getStreamVolume(AUDIO_STREAM)

    init {
        if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
            throw IllegalStateException("You need to request RECORD_AUDIO permission before creating a GGWeb instance!")
        }

        web.apply {
            settings.apply {
                javaScriptEnabled = true
            }

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    request.resources?.forEach {
                        when (it) {
                            PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                                request.grant(arrayOf(it))
                            }
                        }
                    }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    initPort(view)
                    onReady(this@GGWeb)
                }
            }

            loadUrl(htmlLocation)
        }
    }

    /**
     * Sends a message via data over sound. Where the sound is emitted will depend on the device configuration
     * (speaker, Bluetooth headset, etc.). This function is asynchronous, and the audio will not have been
     * played by the time this call returns.
     *
     * @param[message] the text of the message to send
     * @param[useUltrasound] if `true`, audio will be played in near-ultrasound; if `false`, audio will be in hearing range
     * @param[fastMode] if `true`, the audio will be played faster (with higher risk of error)
     */
    fun send(message: String, useUltrasound: Boolean = true, fastMode: Boolean = true) {
        val json = encodeMessage(message, useUltrasound, fastMode)

        lastVolume = audioManager.getStreamVolume(AUDIO_STREAM)

        if (autoAdjustVolume) audioManager.setStreamVolume(AUDIO_STREAM, audioManager.getStreamMaxVolume(AUDIO_STREAM), 0)

        web.postWebMessage(WebMessage("send:$json"), Uri.EMPTY)
    }

    /**
     * Call this to enable receipt of inbound data over sound messages from GGWave-compatible sources. This function
     * is asynchronous, and the recording will not have begun by the time it returns, though it should shortly
     * thereafter.
     *
     * @param[onMessage] will be invoked with received messages while recording is enabled; return true to keep recording or false to stop
     */
    fun startRecording(onMessage: (String) -> Boolean) {
        this.onMessage = onMessage

        web.postWebMessage(WebMessage("startRecording"), Uri.EMPTY)
    }

    /**
     * Call this to turn off the recording enabled by [startRecording()]. No further messages will be delivered to
     * the `onMessage` function type that you passed to [startRecording()]. This function call is asynchronous, and
     * the recording will not have stopped by the time it returns, though it should shortly thereafter.
     */
    fun stopRecording() {
        this.onMessage = DEFAULT_MESSAGE_HANDLER

        web.postWebMessage(WebMessage("stopRecording"), Uri.EMPTY)
    }

    private fun initPort(web: WebView) {
        channel = web.createWebMessageChannel()

        channel[0].setWebMessageCallback(object : WebMessagePort.WebMessageCallback() {
            override fun onMessage(port: WebMessagePort, message: WebMessage) {
                val data = message.data

                when {
                    data.startsWith("message:") -> {
                        if (!onMessage(data.substring(8))) {
                            stopRecording()
                        }
                    }
                    data == "onTxEnded" -> {
                        if (autoAdjustVolume) audioManager.setStreamVolume(AUDIO_STREAM, lastVolume, 0)
                        onTxEnded(this@GGWeb)
                    }
                }
            }
        })

        web.postWebMessage(WebMessage("port", arrayOf(channel[1])), Uri.EMPTY)
    }

    private fun encodeMessage(message: String, useUltrasound: Boolean, fastMode: Boolean) = JSONObject().apply {
        put("message", message)
        put("useUltrasound", useUltrasound)
        put("fastMode", fastMode)
    }.toString()

    private fun hasPermission(perm: String) =
        context.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED
}
