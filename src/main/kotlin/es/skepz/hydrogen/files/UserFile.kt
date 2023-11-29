package es.skepz.hydrogen.files

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.wrappers.CFGFile
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

class UserFile(plugin: Hydrogen, player: UUID) : CFGFile(plugin, player.toString(), "users") {

    constructor(plugin: Hydrogen, player: Player) : this(plugin, player.uniqueId)

    init {
        default("name", Bukkit.getOfflinePlayer(player).name)
        default("rank", "default")
        default("balance", 100L)
        default("op", false)

        default("punishments.amount-of.mutes", 0)
        default("punishments.amount-of.kicks", 0)
        default("punishments.amount-of.bans", 0)

        default("punishments.muted.enabled", false)
        default("punishments.muted.reason", "none")
        default("punishments.muted.sender", "none")
        default("punishments.muted.time", -1L)
        default("punishments.muted.start", -1L)

        default("punishments.banned.enabled", false)
        default("punishments.banned.reason", "none")
        default("punishments.banned.sender", "none")
        default("punishments.banned.time", -1L)
        default("punishments.banned.start", -1L)

        default("time.first-joined", Date().time)
        setLastLogin()
        default("last-logoff", -1L)
    }

    fun getRank(): String {
        reload()
        return cfg.getString("rank") ?: "default"
    }

    fun setRank(rank: String) {
        set("rank", rank)
    }

    fun getPerms(): List<String> {
        reload()
        return cfg.getStringList("permissions")
    }

    fun addPerm(perm: String) {
        val list = getPerms().toMutableList()
        list.add(perm)
        set("permissions", list)
    }

    fun removePerm(perm: String) {
        val list = getPerms().toMutableList()
        list.remove(perm)
        set("permissions", list)
    }

    fun isOp(): Boolean {
        reload()
        return cfg.getBoolean("op")
    }

    fun getHomes(): List<String> {
        reload()
        val list = mutableListOf<String>()
        for (key in cfg.getConfigurationSection("homes")?.getKeys(false) ?: listOf()) {
            list.add(key)
        }
        return list
    }

    fun homeExists(name: String): Boolean {
        reload()
        return cfg.getConfigurationSection("homes")?.getKeys(false)?.contains(name) ?: false
    }

    fun setHome(name: String, loc: Location) {
        set("homes.$name.world", loc.world?.name ?: "world")
        set("homes.$name.x", loc.x)
        set("homes.$name.y", loc.y)
        set("homes.$name.z", loc.z)
        set("homes.$name.pitch", loc.pitch)
        set("homes.$name.yaw", loc.yaw)
    }

    fun delHome(name: String) {
        set("homes.$name", null)
    }

    fun getHome(name: String): Location? {
        reload()
        val world = cfg.getString("homes.$name.world") ?: return null
        val x = cfg.getDouble("homes.$name.x")
        val y = cfg.getDouble("homes.$name.y")
        val z = cfg.getDouble("homes.$name.z")
        val pitch = cfg.getDouble("homes.$name.pitch").toFloat()
        val yaw = cfg.getDouble("homes.$name.yaw").toFloat()
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

    fun firstJoin(): Date {
        reload()
        return Date(cfg.getLong("time.first-joined"))
    }
    fun lastLogin(): Date {
        reload()
        return Date(cfg.getLong("time.last-login"))
    }
    fun setLastLogin() {
        set("time.last-login", Date().time)
    }
    fun lastLogoff(): Date {
        reload()
        return Date(cfg.getLong("last-logoff"))
    }
    fun setLastLogoff() {
        set("time.last-logoff", Date().time)
    }

    fun getBal(): Long {
        reload()
        return cfg.getLong("balance")
    }
    fun setBal(bal: Long) {
        set("balance", bal)
    }
    fun addToBal(amount: Long) {
        set("balance", getBal() + amount)
    }
    fun rmFromBal(amount: Long) {
        set("balance", getBal() - amount)
    }

    fun getMutes(): Int {
        reload()
        return cfg.getInt("punishments.amount-of.mutes")
    }
    fun setMutes(amount: Int) {
        set("punishments.amount-of.mutes", amount)
    }
    fun addMute() {
        setMutes(getMutes() + 1)
    }
    fun getKicks(): Int {
        reload()
        return cfg.getInt("punishments.amount-of.kicks")
    }
    fun setKicks(amount: Int) {
        set("punishments.amount-of.kicks", amount)
    }
    fun addKick() {
        setKicks(getKicks() + 1)
    }
    fun getBans(): Int {
        reload()
        return cfg.getInt("punishments.amount-of.bans")
    }
    fun setBans(amount: Int) {
        set("punishments.amount-of.bans", amount)
    }
    fun addBan() {
        setBans(getBans() + 1)
    }

    fun isBanned(): Boolean {
        reload()
        if (shouldUnban()) {
            setUnbanned()
            return false
        }
        return cfg.getBoolean("punishments.banned.enabled")
    }
    fun banReason(): String {
        reload()
        return cfg.getString("punishments.banned.reason") ?: "none"
    }
    fun banSender(): String {
        reload()
        return cfg.getString("punishments.banned.sender") ?: "none"
    }
    fun banTime(): Long {
        reload()
        return cfg.getLong("punishments.banned.time")
    }
    fun banStart(): Long {
        reload()
        return cfg.getLong("punishments.banned.start")
    }
    fun banTimeRemaining(): Long {
        reload()
        if (!cfg.getBoolean("punishments.banned.enabled")) return -1L
        return banStart() + banTime()
    }
    fun shouldUnban(): Boolean {
        reload()
        if (banTime() == -1L) return false
        return banTimeRemaining() <= 0
    }
    fun bannedUntil(): String {
        reload()
        return SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(banTimeRemaining()))
    }
    fun setBanned(reason: String, sender: String = "none", time: Long = -1L) {
        set("punishments.banned.enabled", true)
        set("punishments.banned.reason", reason)
        set("punishments.banned.sender", sender)
        set("punishments.banned.time", time)
        set("punishments.banned.start", System.currentTimeMillis())
    }
    fun setUnbanned() {
        set("punishments.banned.enabled", false)
        set("punishments.banned.reason", "none")
        set("punishments.banned.sender", "none")
        set("punishments.banned.time", -1L)
        set("punishments.banned.start", -1L)
    }

    fun isMuted(): Boolean {
        reload()
        if (shouldUnmute()) {
            setUnmuted()
            return false
        }
        return cfg.getBoolean("punishments.muted.enabled")
    }
    fun muteReason(): String {
        reload()
        return cfg.getString("punishments.muted.reason") ?: "none"
    }
    fun muteSender(): String {
        reload()
        return cfg.getString("punishments.muted.sender") ?: "none"
    }
    fun muteTime(): Long {
        reload()
        return cfg.getLong("punishments.muted.time")
    }
    fun muteStart(): Long {
        reload()
        return cfg.getLong("punishments.muted.start")
    }
    fun muteTimeRemaining(): Long {
        reload()
        if (!cfg.getBoolean("punishments.muted.enabled")) return -1L
        return muteStart() + muteTime()
    }
    fun mutedUntil(): String {
        reload()
        return SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(muteTimeRemaining()))
    }
    fun shouldUnmute(): Boolean {
        reload()
        if (muteTime() == -1L) return false
        return muteTimeRemaining() <= 0
    }
    fun setMuted(reason: String, sender: String = "none", time: Long = -1L) {
        set("punishments.muted.enabled", true)
        set("punishments.muted.reason", reason)
        set("punishments.muted.sender", sender)
        set("punishments.muted.time", time)
        set("punishments.muted.start", System.currentTimeMillis())
    }
    fun setUnmuted() {
        set("punishments.muted.enabled", false)
        set("punishments.muted.reason", "none")
        set("punishments.muted.sender", "none")
        set("punishments.muted.time", -1L)
        set("punishments.muted.start", -1L)
    }

}