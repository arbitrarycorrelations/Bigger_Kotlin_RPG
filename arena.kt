import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

class MenuBackOut(message: String, var culprit: Any): Exception(message)
class BattleEnd(message: String): Exception(message)
class LoopBreak(message: String): Exception(message)

class turncounter(var value: Int = 1)
val turns = turncounter()

class active_spell(var spell: BoostSpell = heal1, var active_on: Entity = skeleton, var turn_used: Int = 99, var valuebuffer: Int = 0)
var active_opponent_spell = active_spell() // default init values
var party_active_spells = mutableMapOf<Entity, active_spell>()

class DeadPartyMap(var map: MutableMap<Int, Entity> = mutableMapOf())
val dead_party_obj = DeadPartyMap()

fun chooseOpponentCategory(){ //base func for arena
    while (true){
        try{
            print("Welcome to the arena! Do you want to fight an enemy or a boss?\n1: Enemy\n2: Boss\nX: Exit arena\n>\t")
            val enemy_category = readLine() ?: "Nullvalue"
            val choices = mutableMapOf<Int, MutableMap<Int, Entity>>(1 to enemies, 2 to bosses)
            if (enemy_category == "Nullvalue"){println("NULL"); continue}
            if (enemy_category in listOf<String>("x", "X")){println("Exiting arena."); break}
            if (enemy_category.toInt() !in listOf<Int>(1, 2)){throw InvalidKeyException(message = "InvalidKeyException.")}
            var chosen_category = choices[enemy_category.toInt()]
            if (bosses_defeated.size == 5){println("Party will fight superbosses."); chosen_category = superbosses}
            chooseOpponent(opponent_category = chosen_category ?: enemies)
        }
        catch (exception: IllegalArgumentException){println("Not an option"); continue}
        catch (exception: InvalidKeyException){println(exception.message); continue}
    }
}

fun chooseOpponent(opponent_category: MutableMap<Int, Entity>){
    while (true){
        try{
            var category_items = ""
            for (item in opponent_category.entries){
                category_items += "${item.key}: ${item.value.name}\n"
            }
            print("Choose your opponent:\n${category_items}\nS: Print entity stats\n>\t")
            val opponent_choice = readLine()
            if (opponent_choice in listOf("s", "S")){
                print("View whose stats?\n${category_items}>\t")
                val stat_detail = readLine()?.toInt()
                opponent_category[stat_detail]?.print_stats()
                continue
            }
            val opponent = opponent_category[opponent_choice?.toInt()] ?: skeleton
            println("Player will fight ${opponent.name}")
            battleLoop(opponent = opponent)
        }
        catch (exception: IllegalArgumentException){println("Not an option"); continue}
        catch (exception: InvalidKeyException){println(exception.message); continue}
    }
}

fun battleLoop(opponent: Entity){ //control for arena
    println("Battle start!")
    for (character in party.values){party_active_spells[character] = active_spell()}
    var current_fighter = party[1] ?: protag
    val dead_party = dead_party_obj
    val partySizeAtStart = party.size
    val death_check_map = mutableMapOf<Int, Entity>()
    for (entry in party){death_check_map[entry.key] = entry.value}
    battleloop@while (true){
        try{
            val fmap = party.filterValues {it == current_fighter}.keys
            val idx = fmap.single()
            turns.value += 1
            if (active_opponent_spell.spell != heal1){
                active_opponent_spell.spell.valueBuffer = active_opponent_spell.valuebuffer
                val duration_ended = active_opponent_spell.spell.checkDuration(turns = turns.value, lastBoostTurn = active_opponent_spell.turn_used, boosted_entity = active_opponent_spell.active_on)
                if (duration_ended){active_opponent_spell = active_spell()}
            }
            for (member in party_active_spells.entries){
                val active_member_spell = member.value
                val active_member = member.key
                if (active_member_spell.active_on != skeleton) {
                    active_member_spell.spell.valueBuffer = active_member_spell.valuebuffer
                    val duration_ended = active_member_spell.spell.checkDuration(
                        turns = turns.value,
                        lastBoostTurn = active_member_spell.turn_used,
                        boosted_entity = active_member_spell.active_on
                    )
                    if (duration_ended) {
                        party_active_spells[active_member] = active_spell()
                    }
                }
            }
            if (opponent.current_health <= 0){
                println("Party has defeated ${opponent.name}! They got ${turns.value * 15} CR!")
                protag.credits += (turns.value * 15)
                throw BattleEnd(message = "Battle over")
            }
            if (dead_party.map.size >= partySizeAtStart){
                println("All party members have fallen! You lose!")
                throw BattleEnd(message = "Battle over")
            }
            else {
                for (member in death_check_map.entries) {
                    if (member.value.current_health <= 0) {
                        println("${member.value.name} died!")
                        party.remove(key = member.key, value = member.value)
                        dead_party.map[member.key] = member.value
                    }
                    if (dead_party.map.size >= partySizeAtStart){
                        println("All party members have fallen! You lose!")
                        println("Lost 100 CR for losing.")
                        protag.credits = if ((protag.credits - 100) >= 0){protag.credits - 100}else{0}
                        throw BattleEnd(message = "Battle over")
                    }
                }
                for (member in dead_party.map.entries){
                    death_check_map.remove(key = member.key)
                }
            }
            partyAction(current_fighter, opponent)
            val max_fighter_idx = party.keys.max()
            val min_fighter_idx = party.keys.min()
            val previous_fighter = current_fighter
            val fallback_fighter = if (party[max_fighter_idx] == current_fighter){party[min_fighter_idx]!!}else{party[max_fighter_idx]!!}
            current_fighter = party[idx + 1] ?: fallback_fighter
        }
        catch(exception: IllegalArgumentException){println("Not an option."); continue}
        catch(exception: BattleEnd){break@battleloop}
    }
    battleOver(current_fighter, dead_party.map, opponent)
}

fun battleOver(current_fighter: Entity, dead_chars: MutableMap<Int, Entity>, opponent: Entity){
    for (char in dead_chars.entries){party[char.key] = char.value}
    resetValues(party)
    for (active_spell in party_active_spells.entries){
        if (active_spell.value.active_on != skeleton){
            active_spell.value.spell.valueBuffer = active_spell.value.valuebuffer
            active_spell.value.spell.checkDuration(turns = 99, lastBoostTurn = 0, boosted_entity = active_spell.value.active_on)
        }
    }
    resetValues(party = mutableMapOf(1 to opponent))
    if (active_opponent_spell.spell != heal1) {
        active_opponent_spell.spell.checkDuration(turns = 99, lastBoostTurn = 0, boosted_entity = opponent)
    }
    for (char in party.values){char.xp += (turns.value * 50); char.xpcheck()}
    turns.value = 0
    nav()
}

fun partyAction(current_fighter: Entity, opponent: Entity){
    actionLoop@while(true){
        print("What will ${current_fighter.name} do?\n1: Attack\n2: Use Magic (MP ${current_fighter.current_mana}/${current_fighter.max_mana})\n3: Concede\n>\t")
        val fighter_choice = readLine()?.toInt()
        if (fighter_choice !in listOf<Int>(1, 2, 3)){println("Not an option"); continue}
        when (fighter_choice){
            1 -> {
                current_fighter.attack(target = opponent)
                if (opponent.current_health > 0){opponentAction(self = opponent, turns = turns.value)}else{println("${opponent.name} is dead!")}
                break@actionLoop
            }
            2 -> {
                when(useMagicMenu(fighter = current_fighter, opponent = opponent)){
                    "complete" -> {
                        if (opponent.current_health > 0){opponentAction(self = opponent, turns = turns.value)}else{println("${opponent.name} is dead!")}
                        break@actionLoop
                    }
                    "incomplete" -> {continue@actionLoop}}
            }
            3 -> {
                if ((protag.credits - 100) < 0){println("You don't have enough money to concede!"); continue@actionLoop}
                println("The party laid down their swords! CR - 100"); protag.credits -= 100; battleOver(current_fighter, dead_party_obj.map, opponent)}
        }
    }
}
fun opponentAction(self: Entity, turns: Int) {
    val magicSeed = Random.nextInt(0, 15)
    val useMagic = magicSeed in (0..7)
    when (useMagic) {
        true -> {
            var last_heal_turn = 99
            val eligible_spells = mutableListOf<Spell>()
            if (self.current_health <= (self.max_health * .40) && (turns - last_heal_turn).absoluteValue > 3) { //heal spell use logic
                println("HEAL SP")
                for (spell in self.spells.values){if (spell.name.contains("Heal")) eligible_spells.add(spell)}
                last_heal_turn = turns
                self.use_magic(spell_to_pass = eligible_spells.random(), target = self)
            }
            else{
                val attack_spell_use = (0..10).random() //boost / attack spell use logic
                var boost_used = false
                if ((active_opponent_spell.active_on) != self && (turns - active_opponent_spell.turn_used).absoluteValue > 3 && attack_spell_use > 7) {
                    for (spell in self.spells.values) {
                        if (spell is BoostSpell && !spell.name.contains("Heal")) {
                            eligible_spells.add(spell)
                        }
                    }
                    println("BOOST SP")
                    if (eligible_spells.size > 0) {
                        val spell_to_use = eligible_spells.random() as BoostSpell
                        active_opponent_spell.spell = spell_to_use
                        active_opponent_spell.active_on = self
                        active_opponent_spell.turn_used = turns
                        active_opponent_spell.valuebuffer = spell_to_use.attribute_to_boost.get(self)
                        boost_used = true
                        self.use_magic(spell_to_pass = spell_to_use, target = self)
                    }
                    else{println("${self.name} tried to use a boost spell, but they don't know any!")}
                }
                else if (attack_spell_use <= 7 && !boost_used){
                    println("ATTACK SP")
                    for (spell in self.spells.values){
                        if (spell is AttackSpell){eligible_spells.add(spell)}
                    }
                    if (eligible_spells.size > 0){self.use_magic(spell_to_pass = eligible_spells.random(), target = party.values.random())}
                    else{println("${self.name} tried to use an attack spell, but they don't know any!"); self.attack(target = party.values.random())}
                }
                else{self.attack(target = party.values.random())}
            }
        }
        false -> {self.attack(target = party.values.random())}
    }
}

fun useMagicMenu(fighter: Entity, opponent: Entity): String{
    superloop@while (true) {
        try{
            var fighter_spells = ""
            print("Use attack spells or boost spells? (MP ${fighter.current_mana}/${fighter.max_mana})\n1: Attack Spells\n2: Boost Spells\n3: Don't use a spell\n>\t")
            val category = readLine()?.toInt()
            if (category !in listOf<Int>(1, 2, 3)){println("Invalid choice."); continue@superloop}
            if (category == 3){throw MenuBackOut(message = "", culprit = category as Int)}
            val selectableSpells = mutableMapOf<Int, Spell>()
            when (category){
                1 -> {
                    for(entry in fighter.spells.entries){
                    if (entry.value is AttackSpell){
                        val entryAttackSpell = entry.value as AttackSpell
                        fighter_spells += "${selectableSpells.size + 1}: ${entryAttackSpell.name} (Damage: ${entryAttackSpell.damage}, Kind: ${entryAttackSpell.kind}, AOE: ${entryAttackSpell.aoe}, Cost: ${entryAttackSpell.cost})\n"
                        selectableSpells[selectableSpells.size + 1] = entry.value
                        }
                    }
                }
                2 -> {
                    for(entry in fighter.spells.entries){
                    if (entry.value is BoostSpell){
                        val entryBoostSpell = entry.value as BoostSpell
                        fighter_spells += "${selectableSpells.size + 1}: ${entryBoostSpell.name} (Attribute: ${entryBoostSpell.attribute_to_boost.name.capitalize()}, Boost Amount: ${entryBoostSpell.boost_amount}, Duration: ${entryBoostSpell.duration}, Cost: ${entryBoostSpell.cost})\n"
                        selectableSpells[selectableSpells.size + 1] = entry.value
                        }
                    }
                }
            }
            while (true){
                var partystr = ""
                print("Use which spell?\n${fighter_spells}B: Go back\n>\t")
                val spell_choice_any: Any = readLine() ?: "b"
                if (spell_choice_any in listOf("b", "B")){throw LoopBreak(message = "LoopBreak")}
                val spell_choice = spell_choice_any.toString().toInt()
                if (spell_choice !in selectableSpells.keys){println("Not an option."); continue}
                val choice_cost = selectableSpells[spell_choice]?.cost ?: 0
                if ((fighter.current_mana - choice_cost) < 0){throw MenuBackOut(message = "${fighter.name} does not have enough mana to use ${selectableSpells[spell_choice]?.name}", culprit = 3)}
                if (selectableSpells[spell_choice] is BoostSpell){
                    for (entry in party.entries){partystr += "${entry.key}: ${entry.value.name} (HP: ${entry.value.current_health} / ${entry.value.max_health})\n"}
                    print("Use ${selectableSpells[spell_choice]?.name} on whom?\n${partystr}>\t")
                    val membertarg = readLine()?.toInt()
                    if (membertarg !in party.keys){println("Not an option."); continue}
                    val rand = selectableSpells.values.random()
                    val spellpass = selectableSpells[spell_choice] ?: rand
                    val boost_targ = party[membertarg] ?: protag
                    val active_party_spell = party_active_spells[boost_targ] ?: active_spell()
                    if (active_party_spell.active_on == skeleton){
                        val spellpass_as_boost = spellpass as BoostSpell
                        active_party_spell.spell = spellpass_as_boost
                        active_party_spell.turn_used = turns.value
                        active_party_spell.active_on = boost_targ
                        active_party_spell.valuebuffer = spellpass_as_boost.attribute_to_boost.get(receiver = boost_targ)
                        fighter.use_magic(spell_to_pass = spellpass, target = boost_targ)
                        return "complete"
                    }
                    else{
                        active_party_spell.spell.valueBuffer = active_party_spell.valuebuffer
                        val effectOver = active_party_spell.spell.checkDuration(turns = turns.value, lastBoostTurn = active_party_spell.turn_used, boosted_entity = active_party_spell.active_on)
                        if (effectOver){
                            party_active_spells[boost_targ] = active_spell()
                            val spellpass_as_boost = spellpass as BoostSpell
                            active_party_spell.spell = spellpass_as_boost
                            active_party_spell.turn_used = turns.value
                            active_party_spell.active_on = boost_targ
                            active_party_spell.valuebuffer = spellpass_as_boost.attribute_to_boost.get(receiver = boost_targ)
                            fighter.use_magic(spell_to_pass = spellpass, target = boost_targ)
                            return "complete"
                        }
                        else{continue@superloop}
                    }
                }
                else if (selectableSpells[spell_choice] is AttackSpell){
                    val rand = selectableSpells.values.random()
                    val spellpass = selectableSpells[spell_choice] ?: rand
                    fighter.use_magic(spell_to_pass = spellpass, target = opponent)
                    return "complete"
                }
            }
        }
        catch(exception: IllegalArgumentException){println("Not an option."); continue}
        catch(exception: LoopBreak){continue}
        catch(exception: MenuBackOut){println("${exception.message}"); if (exception.culprit == 3){return "incomplete"}}
    }
}