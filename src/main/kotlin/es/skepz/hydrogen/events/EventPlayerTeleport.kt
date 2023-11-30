package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerTeleportEvent

class EventPlayerTeleport(val core: Hydrogen) : CoreEvent(core) {

        @EventHandler
        fun onTeleport(event: PlayerTeleportEvent) {
            val player = event.player
            core.backLocations[player.uniqueId] = event.from
        }

}