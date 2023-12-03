package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.info
import es.skepz.hydrogen.skepzlib.serverBroadcast
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.getSpawn
import es.skepz.hydrogen.utils.login
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

class EventPlayerJoin(private val core: Hydrogen) : CoreEvent(core) {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(colorize("<dark_gray>(<green>+<dark_gray>) <gray>${event.player.name}"))

        if (!event.player.hasPlayedBefore()) {
            serverBroadcast("<gray>Please welcome <aqua>${event.player.name} <gray>to the server!")
            event.player.teleport(getSpawn(core))
        }

        info(event.player, "Welcome to the server!", "Thanks for playing!", "<gray>Welcome to the server!\n" +
                "<gray>Be sure to do <dark_aqua>/rules<gray> to read the rules!\n" +
                "<gray>Do <dark_aqua>/spawn <gray>to go to spawn!")
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        login(core, event.player, event)
    }

}