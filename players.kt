import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException
import kotlin.math.roundToInt
import kotlin.reflect.KFunction1

class Entity(var name: String, var weapon: Weapon, var current_health: Int, var max_health: Int, var attack: Int, var base_attack: Int, var defense: Int, var magic: Int, var basemagic: Int, var spells: MutableMap<Int, Spell>, var max_mana: Int, var current_mana: Int, var resistance: Int, var speed: Int, var level: Int, var xp: Int, var to_next_level: Int, var credits: Int){
    fun print_stats(){
        val str_to_print = "$name's stats are as follows: \n" +
                "Level: $level\n" +
                "XP: $xp\n" +
                "XP needed for level up: $to_next_level\n" +
                "Credits: $credits\n" +
                "Weapon: ${weapon.name}\n" +
                "Health: $current_health / $max_health\n" +
                "Attack: $base_attack + ${weapon.attack} from weapon. (Total ${attack})\n" +
                "Defense: $defense\n" +
                "Resistance: $resistance\n" +
                "Magic: $basemagic + ${weapon.magic} from weapon. (Total ${magic})\n" +
                "Mana: $current_mana / $max_mana\n" +
                "Spells: ${spells.values.map{it.name}}\n" +
                "Speed: $speed"
        println(str_to_print)
    }
    fun attack(target: Entity){
        var total_damage = attack - target.defense
        if (total_damage < 0){total_damage = 0}
        val resultant_health = target.current_health - total_damage
        if (resultant_health < 0){target.current_health = 0}else{target.current_health = resultant_health}
        println("$name has attacked ${target.name} for $total_damage damage!\n${target.name}: ${target.current_health} / ${target.max_health}")
        if ((speed - target.speed) >= 5){
            var second_damage = ((attack - target.defense) * .75).roundToInt()
            if (second_damage < 0){second_damage = 0}
            val second_health = target.current_health - second_damage
            if (second_health < 0){target.current_health = 0}else{target.current_health = second_health}
            println("Speed supremacy: $name has attacked ${target.name} again!\n${target.name}: ${target.current_health} / ${target.max_health}")
        }
    }
    fun equip_weapon(new_weapon: Weapon, stringflag: Boolean){
        if (weapon != fisticuffs){unequip_weapon()}
        attack = base_attack + new_weapon.attack
        magic = basemagic + new_weapon.magic
        weapon = new_weapon
        new_weapon.equipped = true
        if (stringflag){println("$name has equipped ${new_weapon.name}. Their attack is now $attack and their magic is now $magic.")}
    }
    fun unequip_weapon(){
        weapon.equipped = false
        weapon = fisticuffs
        fisticuffs.equipped = true
        attack = base_attack
        magic = basemagic
    }
    fun use_magic(spell_to_pass: Spell, target: Entity){
        if ((current_mana - spell_to_pass.cost) < 0){println("$name tried to use ${spell_to_pass.name}, but they don't have enough Mana!"); return}
        when (spell_to_pass.javaClass.simpleName.toString()){
            "AttackSpell" -> {
                val spell = spell_to_pass as AttackSpell
                if (spell.aoe) {
                    for (member in party.values){
                        val targetaoe = member
                        val spell_might = magic + spell.damage
                        val defense_affected = mutableMapOf("P" to targetaoe.defense, "M" to targetaoe.resistance)
                        val target_defense = defense_affected[spell.kind] ?: 0
                        val total_damage = if ((spell_might - target_defense) > 0){(spell_might - target_defense)}else{1}
                        if ((targetaoe.current_health - total_damage) < 0){targetaoe.current_health = 0}else{targetaoe.current_health -= total_damage}
                        println("${this.name} used ${spell.name} on ${targetaoe.name}! It did $total_damage damage.\n${targetaoe.name} HP (${targetaoe.current_health} / ${targetaoe.max_health})")
                    }
                        current_mana -= spell.cost
                }else{
                    val spell_might = magic + spell.damage
                    val defense_affected = mutableMapOf("P" to target.defense, "M" to target.resistance)
                    val target_defense = defense_affected[spell.kind] ?: 0
                    val total_damage = if ((spell_might - target_defense) > 0){(spell_might - target_defense)}else{1}
                    if ((target.current_health - total_damage) < 0){target.current_health = 0}else{target.current_health -= total_damage}
                    println("${this.name} used ${spell.name} on ${target.name}! It did $total_damage damage.\n${target.name} HP (${target.current_health} / ${target.max_health})")
                    current_mana -= spell.cost
                }
            }
            "BoostSpell" -> {
                val spell = spell_to_pass as BoostSpell
                val editing = spell.attribute_to_boost
                if (spell.attribute_to_boost == Entity::current_health){
                    editing.set(target, (spell.boost_amount + target.current_health))
                    if (target.current_health > target.max_health){target.current_health = target.max_health}
                    println("${this.name} used ${spell.name} on ${target.name}! HP + ${spell.boost_amount}!\n${target.name}: ${target.current_health} / ${target.max_health}")
                    current_mana -= spell.cost
                }else{
                    spell.valueBuffer = editing.get(target)
                    editing.set(target, (spell.boost_amount + spell.valueBuffer))
                    println("${this.name} used ${spell.name} on ${target.name}! Their ${spell.attribute_to_boost.name} is now ${spell.boost_amount + spell.valueBuffer} for ${spell.duration} turns.")
                    current_mana -= spell.cost
                }
            }
        }
    }
    fun xpcheck(){
        if (xp >= to_next_level){
            val start_level = level
            while((xp - to_next_level) >= 0) {
                current_health += 8
                max_health += 8
                attack += 3
                base_attack += 3
                defense += 3
                magic += 3
                basemagic += 3
                current_mana += 8
                max_mana += 8
                resistance += 3
                speed += 5
                level += 1
                xp = (xp - to_next_level)
                to_next_level += (to_next_level * .25).roundToInt()
            }
            val levels_gained = level - start_level
            println("$name gained ${levels_gained} levels! Their health and Mana went up by ${levels_gained * 8}, their speed went up by ${levels_gained * 5}, and all of their other stats each went up by ${levels_gained * 3}.")
            print_stats()
        }
    }
    fun learnSpell(){
        learnloop@while (true) {
            try{
                print("What category of spell should $name learn?\n1: Attack Spells\n2: Boost Spells\n>\t")
                val categoryChoice = readLine()?.toInt()
                val all_spells: List<Spell>
                val specificSpell: Int
                var spell_string = ""
                when (categoryChoice){
                    1 -> {
                        all_spells = fire_spells + ice_spells + electric_spells
                        for (spell in all_spells){spell_string += "${all_spells.indexOf(spell) + 1}: ${spell.name} (Damage: ${spell.damage}, Kind: ${spell.kind}, Cost: ${spell.cost} MP, AOE: ${spell.aoe})\n"}
                    }
                    2 -> {
                        all_spells = heal_spells + accelerate_spells + buff_spells + shield_spells + magic_guard_spells
                        for (spell in all_spells){spell_string += "${all_spells.indexOf(spell) + 1}: ${spell.name} (Attribute: ${spell.attribute_to_boost.name.capitalize()}, Boost Amount: ${spell.boost_amount}, Duration: ${spell.duration}, Cost: ${spell.cost} MP)\n"}
                    }
                    else -> {throw IllegalArgumentException()}
                }
                print("What spell should $name learn?\n${spell_string}>\t")
                specificSpell = readLine()?.toInt() ?: 1
                if (specificSpell !in 0..all_spells.size){throw IllegalArgumentException()}
                val chosenSpell = all_spells[specificSpell.minus(1)]
                val choiceCost = chosenSpell.cost * 325
                print("That'll cost $choiceCost CR. Continue? Y/N\n>\t")
                val continueLearning = readLine()?.capitalize()
                when (continueLearning){
                    "Y" -> {}
                    "N" -> continue@learnloop
                    else -> throw IllegalArgumentException()
                }
                if ((protag.credits - choiceCost) < 0){println("Insufficient credits."); continue@learnloop}
                if (chosenSpell in spells.values){println("$name already knows ${chosenSpell.name}!"); continue@learnloop}
                val maxidx = spells.keys.max()?.plus(1) ?: 0
                spells[maxidx] = chosenSpell
                println("$name learned ${chosenSpell.name}.")
                protag.credits -= choiceCost
                break
            }
            catch (exception: IllegalArgumentException){println("Not an option."); continue}
        }
    }
}

fun recruit_check(){
    val iter_rp = mutableListOf<Entity>()
    if (recruitable_party.size == 0){println("No one to recruit.")}
    else{for (character in recruitable_party.values){if (protag.level >= character.level){iter_rp.add(character)}}}
    for (character in iter_rp){
        recruit(character = character)
    }
}

fun learn_spell_target() {
    var learner: Entity
    while (true) {
        try {
            var partystr = ""
            for (playerEntry in party.entries){partystr += "${playerEntry.key}: ${playerEntry.value.name}\n"}
            print("Who should learn a spell?\n${partystr}>\t")
            val learn_target = readLine()?.toInt() ?: 1
            if (learn_target !in party.keys){throw IllegalArgumentException()}
            learner = party[learn_target] ?: protag
            break
        } catch (exception: IllegalArgumentException) {println("Not an option."); continue}
    }
    learner.learnSpell()
}

fun recruit(character: Entity){
    loop@while (true){
        print("${character.name} would like to join your party.\nY: Accept\nN: Deny\nS: View ${character.name}'s stats\n>\t")
        val recruit_confirm = readLine() ?: "nullrestart"
        if (recruit_confirm.capitalize() !in mutableListOf("Y", "N", "S", "Nullrestart")){println("That isn't an option."); continue}
        when (recruit_confirm.capitalize()){
            "Y" -> {
                val partymaxkey = party.keys.max() ?: 1
                party[partymaxkey + 1] = character
                println("${character.name} has joined your party.")
                val char_removeidx = recruitable_party.filterValues { it == boogsley }
                val charidx = char_removeidx.keys
                recruitable_party.remove(1, character)
                break@loop
            }
            "N" -> {println("${character.name} has not joined your party."); break@loop}
            "S" -> {character.print_stats(); println(); continue@loop}
            "Nullrestart" -> {println("recruitconfirm is null"); continue@loop}
        }
    }
}

fun select_weapon(){
    while (true){
        try {
            val starting_weapons = mutableListOf<Weapon>(baseSword, simpleSpear, hammer, studentWand)
            var weapon_string = ""
            for (weapon in starting_weapons) {
                val elementidx = (starting_weapons.indexOf(weapon) + 1)
                weapon_string += "$elementidx: ${weapon.name} (ATK: ${weapon.attack}, MAG: ${weapon.magic})\n"
            }
            print("What weapon would you like to start out with, ${protag.name}?\n${weapon_string}>\t")
            val weapchoice = readLine()?.toInt()?.minus(other = 1) ?: 0
            protag.equip_weapon(new_weapon = starting_weapons[weapchoice], stringflag = true)
            break
        }
        catch (exception: IllegalArgumentException){println("Not an option."); continue}
        catch (exception: IndexOutOfBoundsException){println("Not a valid selection."); continue}
    }
}

fun entity_equips(){
    //Party
    boogsley.equip_weapon(new_weapon = hammer, stringflag = false)
    juno.equip_weapon(new_weapon = studentWand, stringflag = false)
    minerva.equip_weapon(new_weapon = simpleSpear, stringflag = false)
    athena.equip_weapon(new_weapon = simpleSpear, stringflag = false)
    //Enemy
    skeleton.equip_weapon(new_weapon = big_stick, stringflag = false)
    imp.equip_weapon(new_weapon = imp_staff, stringflag = false)
    cyclops.equip_weapon(new_weapon = mega_hammer, stringflag = false)
    summoner.equip_weapon(new_weapon = golden_staff, stringflag = false)
    dark_lieutenant.equip_weapon(new_weapon = katana, stringflag = false)
    //Boss
    wyvern.equip_weapon(new_weapon = wyvern_claws, stringflag = false)
    magus.equip_weapon(new_weapon = moonfall_scythe, stringflag = false)
    slash.equip_weapon(new_weapon = slasher, stringflag = false)
    high_priest.equip_weapon(new_weapon = holy_staff, stringflag = false)
    zordo.equip_weapon(new_weapon = razorclaw, stringflag = false)
    magus_2.equip_weapon(new_weapon = dreamreaper, stringflag = false)
    grand_dragon.equip_weapon(new_weapon = dragon_claws, stringflag = false)
    queen_zeal.equip_weapon(new_weapon = razorwind, stringflag = false)
}

fun resetValues(party: MutableMap<Int, Entity>){
    for (character in party.values){
        character.current_health = character.max_health
        character.current_mana = character.max_mana
    }
}

fun selectMethodCaller(methodToCall: KFunction1<Entity, Unit>) {
    var caller_string = ""
    for (entry in party.entries){caller_string += "${entry.key}: ${entry.value.name}\n"}
    var selected_caller: Entity
    while (true){
        try {
            print("Call method on whom?\n${caller_string}>\t")
            val selected_caller_idx = readLine() ?: "1"
            selected_caller = party[selected_caller_idx.toInt()] ?: throw IllegalArgumentException()
            break
        }
        catch (exception: IllegalArgumentException){println("Not a choice."); continue}
    }
    methodToCall.call(selected_caller)
}

fun home() {
    var selectedDestination: String
    while (true) {
        try {
            val homeFunctions = listOf("1: View Entity Stats", "2: Learn a Spell", "3: Recruit a Character", "4: Leave Home")
            println("Party has returned home. What would you like to do?")
            for (function in homeFunctions) {println(function)}
            print(">\t")
            selectedDestination = readLine() ?: "4"
            if (selectedDestination.toInt() !in 1..4){throw IllegalArgumentException()}else{break}
        }
        catch (exception: IllegalArgumentException){}
    }
    when (selectedDestination.toInt()){
        1 -> selectMethodCaller(methodToCall = (Entity::print_stats))
        2 -> selectMethodCaller(methodToCall = (Entity::learnSpell))
        3 -> recruit_check()
        4 -> exitGame()
    }
}
fun exitGame(){}

//Party members
var protag = Entity(name = "protag", weapon = fisticuffs, current_health = 70, max_health = 70, attack = 25, base_attack = 25, defense = 20, magic = 15, basemagic = 15, spells = mutableMapOf(1 to accelerate, 2 to buff, 3 to heal2, 4 to fireball, 5 to icicle_storm), max_mana = 30, current_mana = 30, resistance = 15, speed = 15, level = 1, xp = 0, to_next_level = 50, credits = 0)
var boogsley = Entity(name = "Boogsley", weapon = fisticuffs, current_health = 100, max_health = 100, attack = 30, base_attack = 30, defense = 15, magic = 30, basemagic = 30, spells = mutableMapOf(1 to heal3, 2 to buff2, 3 to accelerate2, 4 to fireball), max_mana = 100, current_mana = 100, resistance = 35, speed = 20, level = 3, xp = 0, to_next_level = 150, credits = 0)
var juno = Entity(name = "Juno", weapon = fisticuffs, current_health = 135, max_health = 135, attack = 5, base_attack = 5, defense = 20, magic = 60, basemagic = 60, spells = mutableMapOf(1 to accelerate3, 2 to heal4, 3 to fireball2, 4 to zipzip, 5 to zapzap), max_mana = 350, current_mana = 350, resistance = 60, speed = 35, level = 6, xp = 0, to_next_level = 395, credits = 0)
var minerva = Entity(name = "Minerva", weapon = fisticuffs, current_health = 150, max_health = 150, attack = 50, base_attack = 50, defense = 50, magic = 40, basemagic = 40, spells = mutableMapOf(1 to accelerate2, 2 to heal3, 3 to fireball2), max_mana = 250, current_mana = 250, resistance = 50, speed = 90, level = 10, xp = 0, to_next_level = 1230, credits = 0)
var athena = Entity(name = "Athena", weapon = fisticuffs, current_health = 200, max_health = 200, attack = 50, base_attack = 50, defense = 55, magic = 50, basemagic = 50, spells = mutableMapOf(1 to heal5, 2 to accelerate3, 3 to megavolt_havoc, 4 to glacial_torrent, 5 to grand_fireball), max_mana = 350, current_mana = 350, resistance = 55, speed = 100, level = 15, xp = 0, to_next_level = 1830, credits = 0)
val party = mutableMapOf<Int, Entity>(1 to protag)
var recruitable_party = mutableMapOf<Int, Entity>(1 to boogsley, 2 to juno, 3 to minerva, 4 to athena)
val party_order = mutableMapOf<Int, Entity>(1 to protag, 2 to boogsley, 3 to juno, 4 to minerva, 5 to athena)

//Enemies
var skeleton = Entity(name = "Skeleton", weapon = fisticuffs, current_health = 55, max_health = 55, attack = 30, base_attack = 30, defense = 25, magic = 5, basemagic = 5, spells = mutableMapOf(1 to buff, 2 to ground_fault), max_mana = 30, current_mana = 30, resistance = 30, speed = 20, level = 1, xp = 0, to_next_level = 0, credits = 0)
var imp = Entity(name = "Imp", weapon = fisticuffs, current_health = 60, max_health = 60, attack = 20, base_attack = 20, defense = 15, magic = 30, basemagic = 30, spells = mutableMapOf(1 to heal2, 2 to fireball2), max_mana = 100, current_mana = 100, resistance = 40, speed = 30, level = 3, xp = 0, to_next_level = 0, credits = 0)
var cyclops = Entity(name = "Clyclops", weapon = fisticuffs, current_health = 150, max_health = 150, attack = 40, base_attack = 40, defense = 45, magic = 10, basemagic = 10, spells = mutableMapOf(1 to buff2), max_mana = 40, current_mana = 40, resistance = 35, speed = 25, level = 5, xp = 0, to_next_level = 0, credits = 0)
var summoner = Entity(name = "Summoner", weapon = fisticuffs, current_health = 100, max_health = 100, attack = 20, base_attack = 20, defense = 35, magic = 50, basemagic = 50, spells = mutableMapOf(1 to heal3, 2 to shield, 3 to icicle_storm, 3 to zipzip, 4 to summoner_rift, 5 to sacred_sigil), max_mana = 175, current_mana = 175, resistance = 45, speed = 35, level = 7, xp = 0, to_next_level = 0, credits = 0)
var dark_lieutenant = Entity(name = "Dark Lieutenant", weapon = fisticuffs, current_health = 200, max_health = 200, attack = 40, base_attack = 40, defense = 40, magic = 40, basemagic = 40, spells = mutableMapOf(1 to heal5, 2 to magic_guard2, 3 to shield2, 4 to vicious_incantation, 5 to grand_fireball, 6 to zipzip), max_mana = 200, current_mana = 200, resistance = 40, speed = 40, level = 10, xp = 0, to_next_level = 0, credits = 0)
val enemies = mutableMapOf<Int, Entity>(1 to skeleton, 2 to imp, 3 to cyclops, 4 to summoner, 5 to dark_lieutenant)

//Bosses
var wyvern = Entity(name = "Wyvern", weapon = fisticuffs, current_health = 250, max_health = 250, attack = 35, base_attack = 35, defense = 50, magic = 35, basemagic = 35, spells = mutableMapOf(1 to healx, 2 to sacred_sigil, 3 to vicious_incantation, 4 to wyvern_fire, 5 to grand_fireball), max_mana = 250, current_mana = 250, resistance = 50, speed = 65, level = 10, xp = 0, to_next_level = 0, credits = 0)
var magus = Entity(name = "Magus", weapon = fisticuffs, current_health = 350, max_health = 350, attack = 40, base_attack = 40, defense = 50, magic = 50, basemagic = 50, spells = mutableMapOf(1 to healx, 2 to barrier, 3 to dark_mist, 4 to dark_matter), max_mana = 360, current_mana = 360, resistance = 55, speed = 65, level = 0, xp = 0, to_next_level = 0, credits = 0)
var slash = Entity(name = "Slash", weapon = fisticuffs, current_health = 270, max_health = 270, attack = 70, base_attack = 90, defense = 70, magic = 40, basemagic = 40, spells = mutableMapOf(1 to overclock, 2 to heavensword, 3 to shadowslash, 4 to heal4), max_mana = 200, current_mana = 200, resistance = 30, speed = 90, level = 0, xp = 0, to_next_level = 0, credits = 0)
var high_priest = Entity(name = "High Priest", weapon = fisticuffs, current_health = 150, max_health = 150, attack = 30, base_attack = 30, defense = 55, magic = 65, basemagic = 65, spells = mutableMapOf(1 to healx, 2 to searing_flame, 3 to cold_front, 4 to exorcism), max_mana = 430, current_mana = 430, resistance = 70, speed = 50, level = 0, xp = 0, to_next_level = 0, credits = 0)
var zordo = Entity(name = "Zordo", weapon = fisticuffs, current_health = 290, max_health = 290, attack = 50, base_attack = 50, defense = 55, magic = 55, basemagic = 55, spells = mutableMapOf(1 to boulder, 2 to focus, 3 to fire_breath, 4 to healx), max_mana = 180, current_mana = 180, resistance = 55, speed = 75, level = 0, xp = 0, to_next_level = 0, credits = 0)
var magus_2 = Entity(name = "Magus II", weapon = fisticuffs, current_health = 410, max_health = 410, attack = 55, base_attack = 55, defense = 65, magic = 65, basemagic = 65, spells = mutableMapOf(1 to healx, 2 to barrier2, 3 to shadowstrike, 4 to eternal_darkness), max_mana = 450, current_mana = 450, resistance = 65, speed = 70, level = 5, xp = 0, to_next_level = 0, credits = 0)
var grand_dragon = Entity(name = "Grand Dragon", weapon = fisticuffs, current_health = 500, max_health = 500, attack = 60, base_attack = 60, defense = 75, magic = 60, basemagic = 60, spells = mutableMapOf(1 to upper_hand, 2 to healx, 3 to exalt_inferno), max_mana = 300, current_mana = 300, resistance = 65, speed = 55, level = 0, xp = 0, to_next_level = 0, credits = 0)
var queen_zeal = Entity(name = "Queen Zeal", weapon = fisticuffs, current_health = 750, max_health = 750, attack = 70, base_attack = 70, defense = 75, magic = 70, basemagic = 70, spells = mutableMapOf(1 to healx, 2 to starburst, 3 to subjugation), max_mana = 350, current_mana = 350, resistance = 75, speed = 75, level = 0, xp = 0, to_next_level = 0, credits = 0)
val bosses = mutableMapOf<Int, Entity>(1 to wyvern, 2 to magus, 3 to slash, 4  to high_priest, 5 to zordo)
val bosses_defeated = mutableListOf<Entity>()
val superbosses = mutableMapOf<Int, Entity>(1 to magus_2, 2 to grand_dragon, 3 to queen_zeal)
val superbosses_defeated = mutableListOf<Entity>()


