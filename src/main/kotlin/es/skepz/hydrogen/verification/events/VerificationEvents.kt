package es.skepz.hydrogen.verification.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.checkVerification
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class VerificationEvents(val core: Hydrogen) : CoreEvent(core) {

    private fun isEnabled(): Boolean {
        return core.files.config.cfg.getBoolean("verification.enabled")
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

    @EventHandler
    fun onInteractEntity(event: PlayerInteractEntityEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

    @EventHandler
    fun onPickup(event: PlayerPickupItemEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

    @EventHandler
    fun onDrop(event: PlayerPickupArrowEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            sendMessage(event.player, "&cYou must be verified to do that!")
        }
    }

}