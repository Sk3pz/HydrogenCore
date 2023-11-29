package es.skepz.hydrogen.skepzlib.wrappers

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

open class CoreEvent(private val plugin: JavaPlugin) : Listener {

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

}