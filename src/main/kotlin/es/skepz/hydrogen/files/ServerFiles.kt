package es.skepz.hydrogen.files

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.wrappers.CFGFile
import java.io.File

class ServerFiles(private val plugin: Hydrogen) {

    var config: CFGFile = CFGFile(plugin, "config", "")
    var rules: CFGFile = CFGFile(plugin, "rules", "")
    var filter: CFGFile = CFGFile(plugin, "filter", "")

    var ranks: CFGFile

    var data: CFGFile = CFGFile(plugin, "data", "ds")
    var warps: CFGFile = CFGFile(plugin, "warps", "ds")
    var worlds = CFGFile(plugin, "worlds", "ds")

    fun reload() {
        config.reload()
        if (!ranks.exists())
            plugin.saveResource("ranks.yml", false)
        ranks.reload()
        rules.reload()
        filter.reload()
        warps.reload()
        data.reload()
        worlds.reload()
    }

    fun restore() {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()

        reload()

        config.default("custom-chat", true)

        // verification
        config.default("verification.enabled", false)
        config.default("verification.prefix", "&a&l✔")
        config.default("verification.unverified-prefix", "&c&l✘")
        config.default("verification.verified-perms", listOf(""))

        val file = File(plugin.dataFolder.toString(), "ranks.yml")
        if (!file.exists())
            plugin.saveResource("ranks.yml", false)
        ranks = CFGFile(plugin,    "ranks",    "")

        rules.default("rules", listOf("Be polite and respectful", "No cheating or using unfair game advantages",
            "No leaking personal information", "No arguing with the staff team", "No begging for a role"))

        filter.default("enabled", true)
        filter.default("op-override-filter", true)
        filter.default("words", listOf("nigger", "nig", "bitch", "fuck", "ass", "nigga", "shit",
            "damn", "cunt", "clit", "dick", "penis", "cock", "vagina", "hell", "faggot", "boob"))
    }

    init {
        val file = File(plugin.dataFolder.toString(), "ranks.yml")
        if (!file.exists())
            plugin.saveResource("ranks.yml", false)
        ranks = CFGFile(plugin,    "ranks",    "")
        restore()
    }
}