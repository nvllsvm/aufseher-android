package net.nullsum.aufseher

import android.graphics.Color
import android.support.annotation.ColorInt
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var service: LightsAPI.LightsService

    val tag = "aufseher"

    val colorMode = ColorMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = LightsAPI("https://sol.nullsum.net")
        service = api.retrofit.create(LightsAPI.LightsService::class.java)

        (findViewById<View>(R.id.button_request) as Button).setOnClickListener { setColor() }

        (findViewById<View>(R.id.checkbox_monitor) as CheckBox).setChecked(true)
        (findViewById<View>(R.id.checkbox_bedroom) as CheckBox).setChecked(true)
        (findViewById<View>(R.id.checkbox_windowsill) as CheckBox).setChecked(true)

        (findViewById<View>(R.id.radio_color) as RadioButton).setChecked(true)
        setMode("color")

        val seekbar_brightness = findViewById<View>(R.id.seekbar_brightness) as SeekBar
        seekbar_brightness.setProgress(seekbar_brightness.getMax())

        val seekbar_blue = findViewById<View>(R.id.seekbar_blue) as SeekBar
        seekbar_blue.setProgress(seekbar_blue.getMax())
    }

    fun setMode(mode: String) {
        colorMode.mode = mode

        (findViewById<View>(R.id.values) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_red) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_green) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_blue) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_white) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_brightness) as View).setVisibility(View.GONE)
        (findViewById<View>(R.id.value_interval) as View).setVisibility(View.GONE)

        if (mode == "color") {
            (findViewById<View>(R.id.values) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_red) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_green) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_blue) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_white) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_brightness) as View).setVisibility(View.VISIBLE)
        } else if (mode == "rainbow") {
            (findViewById<View>(R.id.values) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_brightness) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_interval) as View).setVisibility(View.VISIBLE)
        } else if (mode == "flash") {
            (findViewById<View>(R.id.values) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_red) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_green) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_blue) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_white) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_brightness) as View).setVisibility(View.VISIBLE)
            (findViewById<View>(R.id.value_interval) as View).setVisibility(View.VISIBLE)
        } else if (mode == "off") {
        }
    }
    fun setColor() {
        colorMode.red = (findViewById<View>(R.id.seekbar_red) as SeekBar).getProgress()
        colorMode.green = (findViewById<View>(R.id.seekbar_green) as SeekBar).getProgress()
        colorMode.blue = (findViewById<View>(R.id.seekbar_blue) as SeekBar).getProgress()
        colorMode.white = (findViewById<View>(R.id.seekbar_white) as SeekBar).getProgress()
        colorMode.brightness = (findViewById<View>(R.id.seekbar_brightness) as SeekBar).getProgress()

        setColors(colorMode)
    }

    fun onRadioButtonClicked(view: View) {
        val radioID = (findViewById<View>(R.id.radiogroup) as RadioGroup).getCheckedRadioButtonId()
        val radioText = (findViewById<View>(radioID) as RadioButton).getText()

        setMode((radioText as String).toLowerCase())
    }

    fun setColors(colorMode: ColorMode) {
        val call2 = service.setColorMode(colorMode)
        call2.enqueue(object : Callback<ColorMode> {
            override fun onResponse(call: Call<ColorMode>, response: Response<ColorMode>) {
                Log.i(tag, "success")
            }

            override fun onFailure(call: Call<ColorMode>, t: Throwable) {
                Log.e(tag, "sad", t)
            }
        })
    }

    fun onCheckboxClicked() {
    }

}
