package es.skepz.hydrogen.verification.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.checkVerification
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*

class VerificationEvents(val core: Hydrogen) : CoreEvent(core) {

    private fun isEnabled(): Boolean {
        return core.files.config.cfg.getBoolean("verification.enabled")
    }

    private fun dong(player: Player) {
        sendMessage(player, "<red>You must be verified to do that!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 5, 0.0f)
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

    @EventHandler
    fun onInteractEntity(event: PlayerInteractEntityEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        if (!isEnabled()) return
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (!checkVerification(core, player)) {
            event.isCancelled = true
            dong(player)
        }
    }

    @EventHandler
    fun onDrop(event: PlayerPickupArrowEvent) {
        if (!isEnabled()) return
        if (!checkVerification(core, event.player)) {
            event.isCancelled = true
            dong(event.player)
        }
    }

}