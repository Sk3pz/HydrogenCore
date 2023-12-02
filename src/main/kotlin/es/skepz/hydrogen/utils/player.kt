package es.skepz.hydrogen.utils

import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.colorize
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent

fun refreshPermissions(plugin: Hydrogen, player: Player) {
    // remove current permissions
    player.recalculatePermissions()
    for (attachment in player.effectivePermissions) {
        val attch = attachment.attachment ?: continue
        player.removeAttachment(attch)
    }

    // get the user file
    val file = UserFile(plugin, player)

    // get the permissions from the rank and the user file
    val rank = file.getRank()
    val rankPerms = plugin.files.ranks.cfg.getStringList("ranks.$rank.permissions")

    val ufPerms = file.getPerms()
    val op = plugin.files.ranks.cfg.getBoolean("ranks.$rank.isOp") || file.isOp()

    // get the verification permissions
    val verifPerms = plugin.files.config.cfg.getStringList("verification.verified-perms")

    // give user the permissions
    for (perm in rankPerms) {
        player.addAttachment(plugin, perm, true)
    }
    for (perm in ufPerms) {
        player.addAttachment(plugin, perm, true)
    }
    if (checkVerification(plugin, player)) {
        for (perm in verifPerms) {
            player.addAttachment(plugin, perm, true)
        }
    }
    player.recalculatePermissions()

    player.isOp = op
}

fun login(plugin: Hydrogen, player: Player, event: PlayerLoginEvent): Boolean {
    // create user file
    val file = UserFile(plugin, player)
    file.setLastLogin()
    // ban check
    if (file.isBanned()) {
        event.kickMessage(Component.text(colorize("&cYou are banned from this server!\n" +
                "&cReason: &f${file.banReason()}\n" +
                (if (file.banSender() == "none") "" else "&cBanned by: &f${file.banSender()}\n") +
                (if (file.banTime() == -1L) "&cThis ban is permanent." else "&cBanned until: &f${file.bannedUntil()}"))))
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.kickMessage())
        return false
    }

    refreshPermissions(plugin, player)

    return true
}

// this is used for handling reloads and should not call the setLastLogin() method
fun reloadLogin(plugin: Hydrogen, player: Player) {
    // create user file
    val file = UserFile(plugin, player)
    // ban check
    if (file.isBanned()) {
        player.kick(Component.text(colorize("&cYou are banned from this server!\n" +
                "&cReason: &f${file.banReason()}\n" +
                (if (file.banSender() == "none") "" else "&cBanned by: &f${file.banSender()}\n") +
                (if (file.banTime() == -1L) "&cThis ban is permanent." else "&cBanned until: &4${file.bannedUntil()}"))))
        return
    }

    // give players the permissions that they should have
    refreshPermissions(plugin, player)
}

fun logout(plugin: Hydrogen, player: Player) {
    val file = UserFile(plugin, player)
    file.setLastLogoff()
    plugin.tpaRequests.remove(player.uniqueId)
    plugin.tpahereRequests.remove(player.uniqueId)
    plugin.confirmMap.remove(player.uniqueId)
}

fun reloadLogout(plugin: Hydrogen, player: Player) {
    plugin.tpaRequests.remove(player.uniqueId)
    plugin.tpahereRequests.remove(player.uniqueId)
    plugin.confirmMap.remove(player.uniqueId)
}

fun checkVerification(core: Hydrogen, player: Player): Boolean {
    val file = UserFile(core, player)
    return file.isVerified() || checkPermission(player, "hydrogen.verification.bypass")
}