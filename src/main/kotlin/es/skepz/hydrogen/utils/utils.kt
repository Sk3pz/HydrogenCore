package es.skepz.hydrogen.utils

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.wrappers.CFGFile
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

fun sendConfirmMsg(core: Hydrogen, player: Player, msg: String, callback: (confirm: Boolean) -> Unit) {
    playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 0.2f)
    IMessage("\n&6&lConfirmation Required&r\n")
        .add("&7$msg\n")
        .add("&7&oType or click on one of the following options:\n")
        .addHoverableClickCmd("  &a&oConfirm&r\n", "/core_confirm ${player.name}", "&7Confirm the action")
        .addHoverableClickCmd("  &c&oCancel&r", "/core_cancel ${player.name}", "&7Cancel the action")
        .add("\n")
        .send(player)
//    sendMessage(player,"&7&l$msg")
//    sendMessage(player, "&7Type &aConfirm &7or &ccancel&7.")
    core.confirmMap[player.uniqueId] = callback
}

fun getOfflineUserFileRaw(core: Hydrogen, uuid: UUID): UserFile? {
    // check if the user file exists in core.dataFolder.toString() + "/users/"
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        return null
    }

    val raw = CFGFile(core, uuid.toString(), "users")
    return UserFile(core, uuid, raw)
}

fun forceGetUserFile(core: Hydrogen, uuid: UUID): CFGFile {
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        file.createNewFile()
    }

    return CFGFile(core, uuid.toString(), "users")
}