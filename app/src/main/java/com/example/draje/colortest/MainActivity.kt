package com.example.draje.colortest

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.pes.androidmaterialcolorpickerdialog.ColorPicker


class MainActivity : AppCompatActivity() {
    lateinit var service: LightsAPI.LightsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = LightsAPI("http://192.168.0.101:8000")
        service = api.retrofit.create(LightsAPI.LightsService::class.java)

        val call = service.colorMode
        call.enqueue(object : Callback<ColorMode> {
            override fun onResponse(call: Call<ColorMode>, response: Response<ColorMode>) {
                val mode = response.body()
                val cp = ColorPicker(this@MainActivity, mode!!.brightness,
                        mode.red, mode.green, mode.blue)
                cp.setCallback { color -> setColors(color) }

                val button = findViewById<View>(R.id.button1) as Button
                button.setOnClickListener { cp.show() }
                cp.show()
                Log.i("yay", "hi" + mode.mode)
            }

            override fun onFailure(call: Call<ColorMode>, t: Throwable) {
                Log.e("sad", "sad", t)
            }
        })

    }

    fun setColors(@ColorInt color: Int) {
        val c = ColorMode()

        c.red = Color.red(color)
        c.blue = Color.blue(color)
        c.green = Color.green(color)
        c.brightness = Color.alpha(color)

        val call2 = service.setColorMode(c)
        call2.enqueue(object : Callback<ColorMode> {
            override fun onResponse(call: Call<ColorMode>, response: Response<ColorMode>) {
                val mode = response.body()
            }

            override fun onFailure(call: Call<ColorMode>, t: Throwable) {
                Log.e("sad", "sad", t)
            }
        })
    }
}