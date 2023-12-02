package es.skepz.hydrogen.commands.admin.punish

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.ban.ProfileBanList
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.ArrayList

class UnbanCommand(val core: Hydrogen) : CoreCMD(core, "unban", "&c/unban <&7name&c>", 1,
    "hydrogen.command.unban", false, false) {

    override fun run() {
        // get the player or offline player from the first argument
        val target = args[0]
        val senderName = if (sender is Player)
            PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName())
        else "Console"

        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's file
        val file = UserFile(core, targetPlayer.uniqueId)

        // set the player's ban status to false
        file.setUnbanned()
        Bukkit.getBanList<ProfileBanList>(BanList.Type.PROFILE).pardon(targetPlayer.playerProfile)

        sendMessage(sender, "&7You have unbanned &b$target.")
        Bukkit.getLogger().severe("$senderName has unbanned $target")
    }
}