import kotlin.reflect.KMutableProperty1

open class Spell(var name: String, var cost: Int)

class AttackSpell(name: String, var damage: Int, var kind: String, cost: Int, var aoe: Boolean): Spell(name, cost)

class BoostSpell(name: String, var attribute_to_boost: KMutableProperty1<Entity, Int>, var boost_amount: Int, var duration: Int, var valueBuffer: Int, cost: Int): Spell(name, cost){
    fun checkDuration(turns: Int, lastBoostTurn: Int, boosted_entity: Entity): Boolean{
        if ((turns - lastBoostTurn) >= duration){
            resetBoostValue(boosted_entity)
            return true //returns true if entity's spell duration ended
        }
        println("$name is still active on ${boosted_entity.name}.")
        return false
    }
    fun resetBoostValue(target: Entity){
        attribute_to_boost.set(target, valueBuffer)
        println("$name's effect has ended. ${target.name} is back to normal.")
    }
}

//Boost spells
var accelerate = BoostSpell(name = "Accelerate I", attribute_to_boost = Entity::speed, boost_amount = 5, duration = 3, valueBuffer = 0, cost = 8)
var accelerate2 = BoostSpell(name = "Accelerate II", attribute_to_boost = Entity::speed, boost_amount = 10, duration = 3, valueBuffer = 0, cost = 12)
var accelerate3 = BoostSpell(name = "Accelerate III", attribute_to_boost = Entity::speed, boost_amount = 15, duration = 5, valueBuffer = 0, cost = 18)
var accelerate_spells = mutableListOf<BoostSpell>(accelerate, accelerate2, accelerate3)

var buff = BoostSpell(name = "Buff", attribute_to_boost = Entity::attack, boost_amount = 3, duration = 3, valueBuffer = 0, cost = 8)
var buff2 = BoostSpell(name = "Buff II", attribute_to_boost = Entity::attack, boost_amount = 6, duration = 5, valueBuffer = 0, cost = 16)
var buff_spells = mutableListOf<BoostSpell>(buff, buff2)

var heal1 = BoostSpell(name = "Heal I", attribute_to_boost = Entity::current_health, boost_amount = 10, duration = 0, valueBuffer = 0, cost = 5)
var heal2 = BoostSpell(name = "Heal II", attribute_to_boost = Entity::current_health, boost_amount = 15, duration = 0, valueBuffer = 0, cost = 8)
var heal3 = BoostSpell(name = "Heal III", attribute_to_boost = Entity::current_health, boost_amount = 20, duration = 0, valueBuffer = 0, cost = 12)
var heal4 = BoostSpell(name = "Heal IV", attribute_to_boost = Entity::current_health, boost_amount = 25, duration = 0, valueBuffer = 0, cost = 15)
var heal5 = BoostSpell(name = "Heal V", attribute_to_boost = Entity::current_health, boost_amount = 30, duration = 0, valueBuffer = 0, cost = 20)
var heal_spells = mutableListOf<BoostSpell>(heal1, heal2, heal3, heal4, heal5)

var shield = BoostSpell(name = "Shield I", attribute_to_boost = Entity::defense, boost_amount = 10, duration = 4, valueBuffer = 0, cost = 10)
var shield2 = BoostSpell(name = "Shield II", attribute_to_boost = Entity::defense, boost_amount = 20, duration = 5, valueBuffer = 0, cost = 20)
var shield3 = BoostSpell(name = "Shield III", attribute_to_boost = Entity::defense, boost_amount = 30, duration = 6, valueBuffer = 0, cost = 30)
var shield_spells = mutableListOf<BoostSpell>(shield, shield2, shield3)

var magic_guard = BoostSpell(name = "Magic Guard", attribute_to_boost = Entity::resistance, boost_amount = 10, duration = 4, valueBuffer = 0, cost = 10)
var magic_guard2 = BoostSpell(name = "Mega Magic Guard", attribute_to_boost = Entity::resistance, boost_amount = 20, duration = 5, valueBuffer = 0, cost = 20)
var magic_guard3 = BoostSpell(name = "Ultra Magic Guard", attribute_to_boost = Entity::resistance, boost_amount = 30, duration = 6, valueBuffer = 0, cost = 30)
var magic_guard_spells = mutableListOf<BoostSpell>(magic_guard, magic_guard2, magic_guard3)

//Player and Enemy attack spells
var fireball = AttackSpell(name = "Fireball", damage = 10, kind = "M", cost = 5, aoe = false)
var fireball2 = AttackSpell(name = "Fireball II", damage = 15, kind = "M", cost = 10, aoe = false)
var grand_fireball = AttackSpell(name = "Grand Fireball", damage = 30, kind = "M", cost = 15, aoe = false)
var fire_spells = mutableListOf<AttackSpell>(fireball, fireball2, grand_fireball)

var icicle_storm = AttackSpell(name = "Icicle Storm", damage = 12, kind = "P", cost = 7, aoe = false)
var icicle_hurricane = AttackSpell(name = "Icicle Hurricane", damage = 19, kind = "P", cost = 16, aoe = false)
var glacial_torrent = AttackSpell(name = "Glacial Torrent", damage = 42, kind = "P", cost = 23, aoe = false)
var ice_spells = mutableListOf<AttackSpell>(icicle_storm, icicle_hurricane, glacial_torrent)

var zip = AttackSpell(name = "Zip", damage = 12, kind = "M", cost = 7, aoe = false)
var zap = AttackSpell(name = "Zap", damage = 16, kind = "M", cost = 12, aoe = false)
var zipzip = AttackSpell(name = "ZipZip", damage = 24, kind = "M", cost = 14, aoe = false)
var zapzap = AttackSpell(name = "ZapZap", damage = 32, kind = "M", cost = 24, aoe = false)
var megavolt_havoc = AttackSpell(name = "Megavolt Havoc", damage = 52, kind = "M", cost = 32, aoe = false)
var electric_spells = mutableListOf<AttackSpell>(zip, zap, zipzip, zapzap, megavolt_havoc)

//Boss and superboss spells

//Wyvern
var wyvern_fire = AttackSpell(name = "Wyvern Fire", damage = 20, kind = "M", cost = 10, aoe = true)

//Magus
var barrier = BoostSpell(name = "Barrier", attribute_to_boost = listOf(Entity::defense, Entity::resistance).random(), boost_amount = 20, duration = 4, valueBuffer = 0, cost = 10)
var dark_mist = AttackSpell(name = "Dark Mist", damage = 15, kind = "M", cost = 5, aoe = false)
var dark_matter = AttackSpell(name = "Dark Matter", damage = 30, kind = "M", cost = 8, aoe = true)

//Slash
var overclock = BoostSpell(name = "Overclock", attribute_to_boost = Entity::speed, boost_amount = 40, duration = 6, valueBuffer = 0, cost = 10)
var heavensword = AttackSpell(name = "Heavensword", damage = 15, kind = "P", cost = 5, aoe = false)
var shadowslash = AttackSpell(name = "Shadow Slash", damage = 20, kind = "P", cost = 8, aoe = true)

//High Priest
var searing_flame = AttackSpell(name = "Searing Flame", damage = 30, kind = "M", cost = 12, aoe = true)
var cold_front = AttackSpell(name = "Cold Front", damage = 15, kind = "M", cost = 8, aoe = true)
var exorcism = AttackSpell(name = "Exorcism", damage = 50, kind = "M", cost = 20, aoe = false)

//Zordo
var boulder = AttackSpell(name = "Boulder", damage = 40, kind = "M", cost = 12, aoe = true)
var focus = BoostSpell(name = "Focus", attribute_to_boost = Entity::defense, boost_amount = 30, duration = 6, valueBuffer = 0, cost = 10)
var fire_breath = AttackSpell(name = "Flame Breath", damage = 20, kind = "P", cost = 7, aoe = false)

//Magus II
var barrier2 = BoostSpell(name = "Barrier II", attribute_to_boost = listOf(Entity::defense, Entity::resistance).random(), boost_amount = 50, duration = 5, valueBuffer = 0, cost = 10)
var shadowstrike = AttackSpell(name = "Shadowstrike", damage = 40, kind = "P", cost = 12, aoe = false)
var eternal_darkness = AttackSpell(name = "Eternal Darkness", damage = 70, kind = "M", cost = 25, aoe = true)

//Grand Dragon
var upper_hand = BoostSpell(name = "Upper Hand", attribute_to_boost = Entity::attack, boost_amount = 25, duration = 5, valueBuffer = 0, cost = 10)
var exalt_inferno = AttackSpell(name = "Exalt Inferno", damage = 60, kind = "M", cost = 12, aoe = true)

//Queen Zeal
var starburst = AttackSpell(name = "Starburst", damage = 75, kind = "M", cost = 17, aoe = false)
var subjugation = AttackSpell(name = "Subjugation", damage = 50, kind = "P", cost = 10, aoe = false)

//Enemy-only spells
var ground_fault = AttackSpell(name = "Ground Fault", damage = 6, kind = "P", cost = 3, aoe = true)
var summoner_rift = AttackSpell(name = "Summoner's Rift", damage = 15, kind = "M", cost = 5, aoe = true)
var sacred_sigil = BoostSpell(name = "Sacred Sigil", attribute_to_boost = listOf(Entity::defense, Entity::resistance, Entity::speed, Entity::attack, Entity::magic).random(), boost_amount = 10, duration = 5, valueBuffer = 0, cost = 13)
var vicious_incantation = BoostSpell(name = "Vicious Incantation", attribute_to_boost = listOf(Entity::attack, Entity::magic).random(), boost_amount = 5, duration = 5, valueBuffer = 0, cost = 7)
var healx = BoostSpell(name = "Heal X", attribute_to_boost = Entity::current_health, boost_amount = 500, duration = 0, valueBuffer = 0, cost = 10)

