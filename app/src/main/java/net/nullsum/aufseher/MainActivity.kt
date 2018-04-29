package net.nullsum.aufseher

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatRadioButton
import android.util.Log
import android.view.View
import android.widget.*
import net.nullsum.aufseher.api.GETColorMode
import net.nullsum.aufseher.api.LightsAPI
import net.nullsum.aufseher.api.POSTColorMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var service: LightsAPI.LightsService
    lateinit var requestBar: View

    val tag = "aufseher"

    private val colorMode = POSTColorMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = LightsAPI("https://sol.nullsum.net")
        service = api.retrofit.create(LightsAPI.LightsService::class.java)

        (findViewById<View>(R.id.button_request) as Button).setOnClickListener { setColor() }

        (findViewById<View>(R.id.checkbox_monitor) as CheckBox).isChecked = true
        (findViewById<View>(R.id.checkbox_bedroom) as CheckBox).isChecked = true
        (findViewById<View>(R.id.checkbox_windowsill) as CheckBox).isChecked = true

        val seekbarBrightness = findViewById<View>(R.id.seekbar_brightness) as SeekBar
        seekbarBrightness.progress = seekbarBrightness.max

        val seekbarBlue = findViewById<View>(R.id.seekbar_blue) as SeekBar
        seekbarBlue.progress = seekbarBlue.max

        requestBar = findViewById<View>(R.id.request_bar) as ProgressBar
    }

    override fun onResume() {
        super.onResume()
        setControls()
    }

    private fun setControls() {
        val call1 = service.getColorMode()
        call1.enqueue(object : Callback<GETColorMode> {
            override fun onResponse(call: Call<GETColorMode>, response: Response<GETColorMode>) {
                val statusCode = response.code()
                val body = response.body()
                if (body != null) {
                    for ((k, v) in body.strips) {
                        if (k == "monitor") {
                            (findViewById<View>(R.id.seekbar_red) as SeekBar).progress = v.red
                            (findViewById<View>(R.id.seekbar_blue) as SeekBar).progress = v.blue
                            (findViewById<View>(R.id.seekbar_green) as SeekBar).progress = v.green
                            (findViewById<View>(R.id.seekbar_white) as SeekBar).progress = v.white
                            (findViewById<View>(R.id.seekbar_brightness) as SeekBar).progress = v.brightness
                            (findViewById<View>(R.id.seekbar_interval) as SeekBar).progress = v.interval
                            setMode(v.mode)
                            when(v.mode) {
                                "color" -> (findViewById<View>(R.id.radio_color) as RadioButton).isChecked = true
                                "off" -> (findViewById<View>(R.id.radio_off) as RadioButton).isChecked = true
                                "flash" -> (findViewById<View>(R.id.radio_flash) as RadioButton).isChecked = true
                                "rainbow" -> (findViewById<View>(R.id.radio_rainbow) as RadioButton).isChecked = true
                            }
                        }
                    }
                    findViewById<View>(R.id.main_layout).visibility = View.VISIBLE
                }
                if (statusCode == 200) {
                    Log.d(tag, "getColors success")
                } else {
                    showSnackbar("😥 Error - HTTP $statusCode")
                    Log.e(tag, "getColors HTTP $statusCode")
                }
            }

            override fun onFailure(call: Call<GETColorMode>, t: Throwable) {
                removeRequestBar()
                Log.e(tag, "getColors exception", t)
                showSnackbar("😥 Error")
            }
        })

    }

    private fun showRequestBar() {
        requestBar.visibility = View.VISIBLE
    }

    fun removeRequestBar() {
        requestBar.visibility = View.GONE
    }

    private fun setMode(mode: String) {
        colorMode.mode = mode

        val allAttributes: List<Int> = listOf(
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

    private fun setColor() {
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
        val radioText = (view as AppCompatRadioButton).text
        setMode((radioText as String).toLowerCase())
    }

    fun showSnackbar(message: String) {
        Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun setColors(colorMode: POSTColorMode) {
        showRequestBar()

        val call2 = service.setColorMode(colorMode)
        call2.enqueue(object : Callback<POSTColorMode> {
            override fun onResponse(call: Call<POSTColorMode>, response: Response<POSTColorMode>) {
                requestBar.visibility = View.GONE
                val statusCode = response.code()
                if (statusCode == 204) {
                    Log.d(tag, "setColors success")
                } else {
                    showSnackbar("😥 Error - HTTP $statusCode")
                    Log.e(tag, "setColors HTTP $statusCode")
                }
            }

            override fun onFailure(call: Call<POSTColorMode>, t: Throwable) {
                removeRequestBar()
                Log.e(tag, "setColors exception", t)
                showSnackbar("😥 Error")
            }
        })
    }

}
