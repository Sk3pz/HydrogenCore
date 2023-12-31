package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.*
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.checkVerification
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

class EventPlayerChat(private val core: Hydrogen): CoreEvent(core) {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        if (!core.files.config.cfg.getBoolean("custom-chat")) return

        event.isCancelled = true
        var msgPlain = deserializeComponent(event.message())

        val player = event.player

        // check if confirming, if so run the confirm function
        if (core.confirmMap.containsKey(player.uniqueId)) {
            val confirm = core.confirmMap[player.uniqueId]!!
            core.confirmMap.remove(player.uniqueId)
            val lowermsg = msgPlain.lowercase()
            val confirmed = lowermsg == "confirm" || lowermsg == "c"
                    || lowermsg == "yes" || lowermsg == "y"
            confirm(confirmed)
            return
        }

        // mute check
        val file = UserFile(core, player)
        if (file.isMuted()) {
            invalid(player, "<red><bold>Cannot send message!", "<red>You are muted in this chat.",
                "<red>Reason: <gray>${file.muteReason()}\n" +
                        (if (file.muteSender() == "none") "" else "<red>Muted by: <gray>${file.muteSender()}\n") +
                        (if (file.muteTime() == -1L) "<red>This mute is permanent." else "<red>Muted until: <gray>${file.mutedUntil()}"))
            return
        }

        val verificationPrefix = if (core.files.config.cfg.getBoolean("verification.enabled")) {
            if (checkVerification(core, player)) {
                val fromFile = core.files.config.cfg.getString("verification.prefix") ?: "<green><bold>✔"
                "$fromFile "
            } else {
                val fromFile = core.files.config.cfg.getString("verification.unverified-prefix") ?: "<red><bold>✘"
                "$fromFile "
            }
        } else {
            ""
        }

        val rank = file.getRank()
        val rankPrefix = core.files.ranks.cfg.getString("ranks.$rank.prefix") ?: ""
        val nameColor = core.files.ranks.cfg.getString("ranks.$rank.nameColor") ?: "<dark_gray>"
        val chatColor = core.files.ranks.cfg.getString("ranks.$rank.chatColor") ?: "<gray>"

        // filter chat
        if (core.files.filter.cfg.getBoolean("enabled")) {
            if (!(player.isOp && core.files.config.cfg.getBoolean("op-override-filter"))) {
                val filteredWords = core.files.filter.cfg.getStringList("words")
                for (word in filteredWords) {
                    val regex =
                        Regex("(?i)\\b${Regex.escape(word)}\\b")  // Case-insensitive regex for whole word matching
                    msgPlain = regex.replace(msgPlain) { match ->
                        "*".repeat(match.value.length)
                    }
                }
            }
        }

        val prefix = if (rankPrefix.isEmpty()) {
            nameColor
        } else {
            "$rankPrefix<reset> $nameColor"
        }

        if (checkPermission(player, "hydrogen.chat.format")) {
            core.server.broadcast(colorize("$verificationPrefix<reset>$prefix${player.name}<reset> <gray>» $chatColor$msgPlain"))
        } else {
            core.server.broadcast(colorize("$verificationPrefix<reset>$prefix${player.name}<reset> <gray>» $chatColor").append(Component.text(msgPlain)))
        }
    }

}