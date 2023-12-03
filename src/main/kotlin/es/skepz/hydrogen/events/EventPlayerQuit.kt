package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.logout
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class EventPlayerQuit(val core: Hydrogen) : CoreEvent(core) {

    @EventHandler
    fun onJoin(event: PlayerQuitEvent) {
        event.quitMessage(colorize("<dark_gray>(<red>-<dark_gray>) <gray>${event.player.name}"))

        logout(core, event.player)
    }

}