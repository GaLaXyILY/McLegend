package kr.toxicity.mclegend

import kr.toxicity.mclegend.api.McLegend
import kr.toxicity.mclegend.util.info

class McLegendImpl: McLegend() {
    override fun onEnable() {
        info("Plugin enabled.")
    }

    override fun onDisable() {
        info("Plugin disabled.")
    }
}