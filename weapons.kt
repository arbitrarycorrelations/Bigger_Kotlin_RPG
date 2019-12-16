
class Weapon(var name: String, var attack: Int, var magic: Int, var equipped: Boolean, var cost: Int, var description: String){}

var fisticuffs = Weapon(name = "Fisticuffs", attack = 0, magic = 0, equipped = true, cost = 0, description = "Good ol' fisticuffs. Can't go wrong.")

var baseSword = Weapon(name = "Weathered Sword", attack = 7, magic = 3, equipped = false, cost = 30, description = "A sword worn from years of heavy use.")
var steelSword = Weapon(name = "Steel Sword", attack = 12, magic = 6, equipped = false, cost = 500, description = "A sword used by infantry troops.")
var knightSword = Weapon(name = "Knight's Sword", attack = 24, magic = 8, equipped = false, cost = 1000, description = "A sophisticated sword issued only to knights.")
var orizaki = Weapon(name = "Orizaki", attack = 36, magic = 12, equipped = false, cost = 3000, description = "A legendary sword thought lost to time.")
var swords = mutableListOf<Weapon>(baseSword, steelSword, knightSword, orizaki)

var simpleSpear = Weapon(name = "Simple Spear", attack = 5, magic = 5, equipped = false, cost = 30, description = "A simple spear made from a sharpened rock affixed to a stick.")
var steelSpear = Weapon(name = "Steel Spear", attack = 9, magic = 9, equipped = false, cost = 500, description = "A spear used by fleet-footed rogues.")
var superSpear = Weapon(name = "Royal Spear", attack = 16, magic = 16, equipped = false, cost = 1000, description = "A spear used by the King's Royal Guard.")
var lightpiercer = Weapon(name = "Lightpiercer", attack = 24, magic = 24, equipped = false, cost = 3000, description = "A goddess's spear able to pierce light itself.")
var spears = mutableListOf<Weapon>(simpleSpear, steelSpear, superSpear, lightpiercer)

var hammer = Weapon(name = "Hammer", attack = 10, magic = 0, equipped = false, cost = 30, description = "A boulder tied to a pole.")
var battleHammer = Weapon(name = "Battle Hammer", attack = 18, magic = 0, equipped = false, cost = 500, description = "A double-sided hammer favored by military muscle-bounds.")
var skullCrusher = Weapon(name = "Skull Crusher", attack = 32, magic = 0, equipped = false, cost = 1000, description = "A skull-crushing hammer only the strongest can swing.")
var hephaestusHammer = Weapon(name = "Hephaestus Hammer", attack = 48, magic = 0, equipped = false, cost = 3000, description = "A hammer crafted by the legendary Hephaestus.")
var hammers = mutableListOf<Weapon>(hammer, battleHammer, skullCrusher, hephaestusHammer)

var studentWand = Weapon(name = "Student's Wand", attack = 0, magic = 10, equipped = false, cost = 30, description = "A simple wand used by students.")
var summonerWand = Weapon(name = "Summoner's Wand", attack = 0, magic = 18, equipped = false, cost = 500, description = "A wand issued to a summoner in the army.")
var sacrosanctSummoner = Weapon(name = "Sacrosanct Summoner", attack = 0, magic = 32, equipped = false, cost = 750, description = "A wand capable of casting precious spells. Only issued to the most accomplished mages.")
var solStaff = Weapon(name = "Sol's Staff", attack = 0, magic = 48, equipped = false, cost = 2300, description = "A magical staff burning with the fire of the sun itself. Once used by the sun god Sol.")
var magic_weapons = mutableListOf<Weapon>(studentWand, summonerWand, sacrosanctSummoner, solStaff)

var all_weapons = mutableListOf<MutableList<Weapon>>(swords, spears, hammers, magic_weapons)

//boss / enemy weapons here

//Enemy
var big_stick = Weapon(name = "Big Stick", attack = 5, magic = 0, equipped = false, cost = 0, description = "A big ol' stick that is a surprisingly decent weapon.")
var imp_staff = Weapon(name = "Imp Staff", attack = 5, magic = 7, equipped = false, cost = 0, description = "A staff hand crafted by an Imp. It's really just a stick.")
var mega_hammer = Weapon(name = "Mega Hammer", attack = 15, magic = 0, equipped = false, cost = 0, description = "A gargantuan hammer that demands unimaginable strength from its wielder.")
var golden_staff = Weapon(name = "Golden Staff", attack = 7, magic = 13, equipped = false, cost = 0, description = "A finely forged staff made from a golden alloy.")
var katana = Weapon(name = "Katana", attack = 17, magic = 10, equipped = false, cost = 0, description = "A lightning-fast sword that is unstoppable in the right hands.")

//Boss
var wyvern_claws = Weapon(name = "Wyvern Claws", attack = 30, magic = 20, equipped = false, cost = 0, description = "Razor-sharp claws said to be capable of slicing through any armor.")
var moonfall_scythe = Weapon(name = "Moonfall Scythe", attack = 20, magic = 40, equipped = false, cost = 0, description = "Magus's scythe imbued with the power of a moon rock.")
var slasher = Weapon(name = "Slasher", attack = 20, magic = 20, equipped = false, cost = 0, description = "Slash's trusted longsword.")
var holy_staff = Weapon(name = "Holy Staff", attack = 0, magic = 35, equipped = false, cost = 0, description = "The High Priest's fabled staff.")
var razorclaw = Weapon(name = "Razorclaw", attack = 40, magic = 20, equipped = false, cost = 0, description = "A sword made from a Wyvern's claw.")
var dreamreaper = Weapon(name = "Dreamreaper", attack = 40, magic = 45, equipped = false, cost = 0, description = "Magus's scythe that can steal dreams.")
var dragon_claws = Weapon(name = "Grand Dragon Claws", attack = 20, magic = 20, equipped = false, cost = 0, description = "The Grand Dragon's massively destructive claws.")
var razorwind = Weapon(name = "Razorwind", attack = 30, magic = 30, equipped = false, cost = 0, description = "A swift wand-sword combo.")