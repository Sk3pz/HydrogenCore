package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.getSpawn
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent

class EventPlayerDie(val core: Hydrogen) : CoreEvent(core) {

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.player.bedSpawnLocation == null) {
            event.player.teleport(getSpawn(core))
        }

        if (core.files.config.cfg.getBoolean("back-command-includes-death-location")) {
            core.backLocations[event.player.uniqueId] = event.player.location
        }
    }

}