package net.nullsum.aufseher

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import net.nullsum.aufseher.api.POSTColorMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import net.nullsum.aufseher.api.LightsAPI

class LightsTileService: TileService() {
    private lateinit var service: LightsAPI.LightsService
    private val colorMode = POSTColorMode()

    override fun onCreate() {
        val api = LightsAPI("https://aufseher.nullsum.net")
        service = api.retrofit.create(LightsAPI.LightsService::class.java)
        super.onCreate()
    }
    override fun onClick() {
        super.onClick()

        if (qsTile.state == Tile.STATE_INACTIVE) {
            colorMode.mode = "color"
            colorMode.strips.add("bedroom")
            colorMode.strips.add("monitor")
            colorMode.strips.add("windowsill")
            colorMode.brightness = 255
            colorMode.white = 255
            colorMode.blue = 255
            qsTile.state = Tile.STATE_ACTIVE
        } else {
            colorMode.mode = "off"
            qsTile.state = Tile.STATE_INACTIVE
        }

        setColors(colorMode)
        qsTile.updateTile()
    }

    private fun setColors(colorMode: POSTColorMode) {
        val call2 = service.setColorMode(colorMode)
        call2.enqueue(object : Callback<POSTColorMode> {
            override fun onResponse(call: Call<POSTColorMode>, response: Response<POSTColorMode>) {
            }

            override fun onFailure(call: Call<POSTColorMode>, t: Throwable) {
            }
        })
    }
}
