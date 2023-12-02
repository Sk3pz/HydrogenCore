package es.skepz.hydrogen.events

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.invalid
import es.skepz.hydrogen.skepzlib.serverBroadcast
import es.skepz.hydrogen.skepzlib.wrappers.CoreEvent
import es.skepz.hydrogen.utils.checkVerification
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler

class EventPlayerChat(private val core: Hydrogen): CoreEvent(core) {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        if (!core.files.config.cfg.getBoolean("custom-chat")) return

        event.isCancelled = true
        var msgPlain = PlainTextComponentSerializer.plainText().serialize(event.message())

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
            invalid(player, "&c&lCannot send message!", "&cYou are muted in this chat.",
                "&cReason: &f${file.muteReason()}\n" +
                        (if (file.muteSender() == "none") "" else "&cMuted by: &f${file.muteSender()}\n") +
                        (if (file.muteTime() == -1L) "&cThis mute is permanent." else "&cMuted until: &f${file.mutedUntil()}"))
            return
        }

        val verificationPrefix = if (core.files.config.cfg.getBoolean("verification.enabled")) {
            if (checkVerification(core, player)) {
                core.files.config.cfg.getString("verification.prefix") ?: "&a&l✔"
            } else {
                core.files.config.cfg.getString("verification.unverified-prefix") ?: ""
            }
        } else {
            ""
        }

        val rank = file.getRank()
        val rankPrefix = core.files.ranks.cfg.getString("ranks.$rank.prefix") ?: ""
        val nameColor = core.files.ranks.cfg.getString("ranks.$rank.nameColor") ?: "&8"
        val chatColor = core.files.ranks.cfg.getString("ranks.$rank.chatColor") ?: "&f"

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
            "$rankPrefix $nameColor"
        }

        serverBroadcast("$verificationPrefix $prefix${player.name} &7> $chatColor${colorize(msgPlain)}")
    }

}