package net.nullsum.aufseher

import android.graphics.Color
import android.support.annotation.ColorInt
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.ProgressBar
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.Snackbar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var service: LightsAPI.LightsService
    lateinit var request_bar: View

    val tag = "aufseher"

    private val colorMode = ColorMode()

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

        val seekbarBrightness = findViewById<View>(R.id.seekbar_brightness) as SeekBar
        seekbarBrightness.progress = seekbarBrightness.getMax()

        val seekbarBlue = findViewById<View>(R.id.seekbar_blue) as SeekBar
        seekbarBlue.progress = seekbarBlue.getMax()

        request_bar = findViewById<View>(R.id.request_bar) as ProgressBar
    }

    fun showRequestBar() {
        request_bar.setVisibility(View.VISIBLE)
    }

    fun removeRequestBar() {
        request_bar.setVisibility(View.GONE)
    }

    fun setMode(mode: String) {
        colorMode.mode = mode

        var allAttributes: List<Int> = listOf(
                R.id.values,
                R.id.value_red,
                R.id.value_green,
                R.id.value_blue,
                R.id.value_white,
                R.id.value_brightness,
                R.id.value_interval)

        for (attribute in allAttributes) {
            (findViewById<View>(attribute) as View).visibility = View.GONE
        }

        var visibleAttributes: List<Int> = listOf()

        when (colorMode.mode) {
            "color" -> visibleAttributes = mutableListOf(
                    R.id.values,
                    R.id.value_red,
                    R.id.value_green,
                    R.id.value_blue,
                    R.id.value_white,
                    R.id.value_brightness)
            "rainbow" -> visibleAttributes = mutableListOf(
                    R.id.values,
                    R.id.value_brightness,
                    R.id.value_interval)
            "flash" -> visibleAttributes = mutableListOf(
                    R.id.values,
                    R.id.value_red,
                    R.id.value_green,
                    R.id.value_blue,
                    R.id.value_white,
                    R.id.value_brightness,
                    R.id.value_interval)
        }

        for (attribute in visibleAttributes) {
            (findViewById<View>(attribute) as View).visibility = View.VISIBLE
        }
    }
    fun setColor() {
        colorMode.red = (findViewById<View>(R.id.seekbar_red) as SeekBar).progress
        colorMode.green = (findViewById<View>(R.id.seekbar_green) as SeekBar).progress
        colorMode.blue = (findViewById<View>(R.id.seekbar_blue) as SeekBar).progress
        colorMode.white = (findViewById<View>(R.id.seekbar_white) as SeekBar).progress
        colorMode.brightness = (findViewById<View>(R.id.seekbar_brightness) as SeekBar).progress
        colorMode.interval = (findViewById<View>(R.id.seekbar_interval) as SeekBar).progress

        colorMode.strips.clear()
        if ((findViewById<View>(R.id.checkbox_bedroom) as CheckBox).isChecked) {
            colorMode.strips.add("bedroom")
        }
        if ((findViewById<View>(R.id.checkbox_monitor) as CheckBox).isChecked) {
            colorMode.strips.add("monitor")
        }
        if ((findViewById<View>(R.id.checkbox_windowsill) as CheckBox).isChecked) {
            colorMode.strips.add("windowsill")
        }

        setColors(colorMode)
    }

    fun onRadioButtonClicked(view: View) {
        val radioID = (findViewById<View>(R.id.radiogroup) as RadioGroup).getCheckedRadioButtonId()
        val radioText = (findViewById<View>(radioID) as RadioButton).getText()

        setMode((radioText as String).toLowerCase())
    }

    fun showSnackbar(message: String) {
        Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    fun setColors(colorMode: ColorMode) {
        showRequestBar()

        val call2 = service.setColorMode(colorMode)
        call2.enqueue(object : Callback<ColorMode> {
            override fun onResponse(call: Call<ColorMode>, response: Response<ColorMode>) {
                request_bar.setVisibility(View.GONE)
                val status_code = response.code()
                if (status_code == 204) {
                    Log.d(tag, "setColors success")
                } else {
                    showSnackbar("😥 Error - HTTP " + status_code)
                    Log.e(tag, "setColors HTTP " + status_code)
                }
            }

            override fun onFailure(call: Call<ColorMode>, t: Throwable) {
                removeRequestBar()
                Log.e(tag, "setColors exception", t)
                showSnackbar("😥 Error")
            }
        })
    }

    fun onCheckboxClicked() {
    }

}
