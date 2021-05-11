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
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.commonsware.ggweb.databinding.ActivityMainBinding
import services.C2MediaPublisher
import services.TumblerServiceApi

private const val PERM_AUDIO = 1337

class MainActivity : AppCompatActivity() {
    private val motor: MainMotor by viewModels()
    private lateinit var gg: GGWeb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        motor.setPublisher(C2MediaPublisher(filesDir.absolutePath, TumblerServiceApi()))
        if (hasAudioPermission()) {
            loadContent()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.RECORD_AUDIO), PERM_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERM_AUDIO) {
            if (hasAudioPermission()) {
                loadContent()
            } else {
                Toast.makeText(this, "WTF?", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadContent() {
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        gg = GGWeb(this, onTxEnded = ::onTxEnded) {
            with(binding) {
                listOf(recording).forEach { it.isChecked = true }
            }
        }

        binding.recording.setOnCheckedChangeListener { _, isChecked ->
            gg.startRecording {
                motor.handleMessage(it)
                true
            }
        }

        val adapter = MainAdapter(layoutInflater)

        binding.transcript.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
    }

    private fun onTxEnded(ggWeb: GGWeb) {
        Log.d("GGWeb", "onTxEnded")
    }

    private fun hasAudioPermission() =
        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
}
