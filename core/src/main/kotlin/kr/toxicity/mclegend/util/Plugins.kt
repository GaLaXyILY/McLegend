package kr.toxicity.mclegend.util

import kr.toxicity.mclegend.api.McLegend

val PLUGIN
    get() = McLegend.inst()

fun info(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach {
            logger.info(it)
        }
    }
}
fun warn(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach {
            logger.warning(it)
        }
    }
}