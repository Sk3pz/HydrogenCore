package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.info
import es.skepz.hydrogen.skepzlib.serverBroadcast
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.getSpawn
import es.skepz.hydrogen.utils.login
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

class EventPlayerJoin(private val core: Hydrogen) : CoreEvent(core) {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Component.text(colorize("&8(&a+&8) &7${event.player.name}")))

        if (!event.player.hasPlayedBefore()) {
            serverBroadcast("&7Please welcome &b${event.player.name} &7to the server!")
            event.player.teleport(getSpawn(core))
        }

        info(event.player, "Welcome to the server!", "Thanks for playing!", "&7Welcome to the server!\n" +
                "&7Be sure to do &3/rules&7 to read the rules!\n" +
                "&7Do &3/spawn &7to go to spawn!")
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        login(core, event.player, event)
    }

}