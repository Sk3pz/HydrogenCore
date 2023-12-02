package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getWarp
import es.skepz.hydrogen.utils.getWarps
import es.skepz.hydrogen.utils.warpExists
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.ArrayList

class WarpCommand(val core: Hydrogen) : CoreCMD(core, "warp", "&c/warp <&7name&c>",
    0, "none", true, true) {

    override fun init() {
        // not used for this function
    }

    override fun run() {
        var player = getPlayer()!!

        if (args.size == 0) {
            // list warps
            val warps = getWarps(core)
            if (warps.isEmpty()) {
                return sendMessage(player, "&cThere are no warps!")
            }
            sendMessage(player, "&7Warps: &b${warps.joinToString("&7, &b")}&7.")
            return
        }

        if (args.size > 1) {
            if (!checkPermission(player, "hydrogen.command.warp.other")) {
                return sendMessage(player, "&cYou don't have permission to warp other players!")
            }

            player = core.server.getPlayer(args[1]) ?: return sendMessage(player, "&cPlayer not found!")
        }

        val warp = args[0]

        val loc = getWarp(core, warp) ?: return sendMessage(player, "&cWarp not found!")

        val permission = core.files.warps.cfg.getString("warps.$warp.permission")
        if (permission != null && !checkPermission(player, permission)) {
            return sendMessage(player, "&cYou don't have permission to warp there!")
        }

        player.teleport(loc)
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
        sendMessage(player, "&7You have been teleported to &b$warp&7!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = ArrayList<String>()

        val warps = getWarps(core)
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], warps, completions)
        }
        return completions
    }

}