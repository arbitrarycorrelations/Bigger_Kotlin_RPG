import java.lang.Exception

class InvalidKeyException(message: String): Exception(message)

fun outerShopMenu(){ //base shop func
    while (true){
        try{
            val weapon_categories = mutableMapOf<Int, MutableList<Weapon>>(1 to swords, 2 to spears, 3 to hammers, 4 to magic_weapons)
            print("Welcome to the shop. Which weapon category would you like to browse?\n1: Swords\n2: Spears\n3: Hammers\n4: Magic Weapons\n5: Exit shop\n>\t")
            val browse_category = readLine()?.toInt()
            if (browse_category == 5){break}
            val chosen_category = weapon_categories[browse_category] ?: mutableListOf()
            val category_map = mutableMapOf<Int, Weapon>()
            var category_string = ""
            for (item in chosen_category){
                val idx = (chosen_category.indexOf(item)) + 1
                category_map[idx] = item
                category_string += "$idx: ${item.name} (ATK: ${item.attack}, MAG: ${item.magic}, Cost: ${item.cost})\n"
            }
            innerShopMenu(category_map = category_map, category_string = category_string)
        }
        catch (exception: IllegalArgumentException){println("That isn't an option."); continue}
    }
}

fun innerShopMenu(category_map: MutableMap<Int, Weapon>, category_string: String){
    while (true){
        try {
            print("Purchase which item?\n${category_string}\nD: Don't purchase an item\n>\t")
            val item_purchase = readLine()
            if (item_purchase in mutableListOf<String>("d", "D")){println("Cancelled purchase."); break}
            val item_to_purchase = category_map[item_purchase?.toInt()]
            if (item_to_purchase == null){throw InvalidKeyException(message = "That isn't an option.")}
            else{purchase(item = item_to_purchase as Weapon)}
        }
        catch(exception: IllegalArgumentException){println("That isn't an option."); continue}
        catch(exception: InvalidKeyException){println(exception.message); continue}
    }
}
fun purchase(item: Weapon){
    purchaseloop@while (true){
        if (item.cost > protag.credits){println("You can't buy that."); break}
        print("Buy ${item.name} (ATK: ${item.attack}, MAG: ${item.magic}, Cost: ${item.cost})?\n(Y/N)\n>\t")
        val will_purchase = readLine()
        if (will_purchase?.capitalize() !in mutableListOf<String>("Y", "N")){println("Invalid choice."); continue}
        when (will_purchase){
            "Y" -> {
                var equip_str = ""
                for (entry in party.entries){equip_str += "${entry.key}: ${entry.value.name} (Base ATK: ${entry.value.base_attack}, Base MAG: ${entry.value.basemagic})\n"}
                print("Equip the ${item.name} on whom?\n$equip_str>\t")
                val equip_choice = readLine()?.toInt() ?: 0
                if (equip_choice == 0){println("Not an option."); continue@purchaseloop}
                else if (equip_choice !in party.keys){println("Not an option."); continue@purchaseloop}
                party[equip_choice]?.equip_weapon(new_weapon = item, stringflag = true)
                protag.credits -= item.cost
                println("${protag.name} now has ${protag.credits} credits.")
                break@purchaseloop
            }
            "N" -> {println("Cancelled purchase of ${item.name}"); break@purchaseloop}
        }
    }
}