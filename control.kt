import java.lang.IllegalArgumentException

fun main(){
    print("Welcome to KotlinTRPG! What's your name?\n>\t")
    protag.name = readLine() ?: "Protag"
    if (protag.name == "biff"){
        println("Dev mode active.")
        protag.xp = 99999
        protag.xpcheck()
        protag.credits = 99999
    }
    select_weapon()
    entity_equips()
    nav()
}

fun nav(){
    navloop@while (true) {
        try {
            val destinations = listOf("1: Arena", "2: Shop", "3: Home", "4: Exit game")
            println("\nWhere would you like to go?")
            for (key in destinations) {println(key)}
            print(">\t")
            val navChoice = readLine() ?: "3"
            when (navChoice.toInt()){
                1 -> chooseOpponentCategory()
                2 -> outerShopMenu()
                3 -> home()
                4 -> exitGame()
                else -> throw IllegalArgumentException()
            }
        }
        catch (exception: IllegalArgumentException){println("Not an option."); continue}
    }
}

