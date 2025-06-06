package wow.gameproject;

import java.io.*;
import java.util.*;
import wow.gameproject.Item.Rarity;

class Dungeon {
    String rank;
    int reqLevel;
    String name;
    int baseMinionHp, baseMinionMinAtk, baseMinionMaxAtk, baseMinionDef, baseMinionMana, expReward, goldReward;

    public Dungeon(String rank, int reqLevel, String name, int baseMinionHp, int baseMinionMinAtk, int baseMinionMaxAtk, int baseMinionDef, int baseMinionMana, int expReward, int goldReward) {
        this.rank = rank;
        this.reqLevel = reqLevel;
        this.name = name;
        this.baseMinionHp = baseMinionHp;
        this.baseMinionMinAtk = baseMinionMinAtk;
        this.baseMinionMaxAtk = baseMinionMaxAtk;
        this.baseMinionDef = baseMinionDef;
        this.baseMinionMana = baseMinionMana;
        this.expReward = expReward;
        this.goldReward = goldReward;
    }
}

public class GameProject {
    static final Dungeon[] DUNGEONS = {
        new Dungeon("E", 1, "Goblin", 380, 12, 18, 7, 70, 20, 10),
        new Dungeon("D", 3, "Wolf", 400, 16, 22, 10, 80, 30, 16),
        new Dungeon("C", 5, "Orc", 450, 22, 26, 14, 90, 45, 26),
        new Dungeon("B", 7, "Troll", 500, 28, 34, 18, 100, 65, 40),
        new Dungeon("A", 10, "Ogre", 600, 36, 44, 24, 120, 90, 60),
        new Dungeon("S", 15, "Dragon", 800, 50, 68, 38, 160, 180, 120)
    };

    static final HashMap<String, Double> DIFFICULTY = new HashMap<>();
    static {
        DIFFICULTY.put("easy", 0.8);
        DIFFICULTY.put("normal", 1.0);
        DIFFICULTY.put("hard", 1.2);
    }
    static final Map<String, Double> DIFFICULTY_EXP_MULTIPLIER = Map.of(
        "easy", 0.7,
        "normal", 1.0,
        "hard", 1.5
    );

    static final Item[] ITEM_DROPS = {
        new Item("HP Potion", Item.Type.HP_POTION, 35, Rarity.COMMON),
        new Item("Mana Potion", Item.Type.MANA_POTION, 25, Rarity.COMMON),
        new Item("Sword", Item.Type.SWORD, 2, Rarity.UNCOMMON),
        new Item("Staff", Item.Type.STAFF, 2, Rarity.UNCOMMON),
        new Item("Shield", Item.Type.SHIELD, 1, Rarity.UNCOMMON),
        new Item("Helmet", Item.Type.HELMET, 1, Rarity.RARE),
        new Item("Armor", Item.Type.ARMOR, 2, Rarity.RARE),
        new Item("Ring", Item.Type.RING, 1, Rarity.EPIC),
        new Item("Excalibur", Item.Type.SWORD, 5, Rarity.LEGENDARY),
        new Item("Archmage Staff", Item.Type.STAFF, 5, Rarity.LEGENDARY)
    };

    static final Map<Item.Rarity, Double> RARITY_DROP_CHANCE = Map.of(
        Rarity.COMMON, 0.30,
        Rarity.UNCOMMON, 0.15,
        Rarity.RARE, 0.07,
        Rarity.EPIC, 0.025,
        Rarity.LEGENDARY, 0.01
    );

    Hero hero;
    Stack<String> actionStack = new Stack<>();
    Queue<String> messageQueue = new LinkedList<>();
    Scanner sc = new Scanner(System.in);
    Random random = new Random();

    void saveProgress() {
        try (FileOutputStream fos = new FileOutputStream("savegame.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(hero);
            System.out.println("Progress saved!");
        } catch (IOException e) {
            System.out.println("Failed to save progress: " + e.getMessage());
        }
    }

    boolean loadProgress() {
        try (FileInputStream fis = new FileInputStream("savegame.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            hero = (Hero) ois.readObject();
            System.out.println("Progress loaded! Welcome back, " + hero.name + " the " + hero.heroClass + "!");
            return true;
        } catch (Exception e) {
            System.out.println("No savegame found or failed to load.");
            return false;
        }
    }

    void chooseClass() {
        System.out.println("Choose your class: warrior, mage, archer, tank");
        String heroClass = sc.nextLine().trim().toLowerCase();
        while (!Hero.JOB_UPGRADES.containsKey(heroClass)) {
            System.out.println("Invalid input. Choose your class: warrior, mage, archer, tank");
            heroClass = sc.nextLine().trim().toLowerCase();
        }
        System.out.print("Enter your hero's name: ");
        String name = sc.nextLine().trim();
        hero = new Hero(name, heroClass);
        System.out.println("Welcome " + hero.name + " the " + hero.heroClass + "!");
    }

    void showDungeons() {
        System.out.println("Available dungeons:");
        for (int i = 0; i < DUNGEONS.length; i++) {
            Dungeon d = DUNGEONS[i];
            System.out.printf("%d. Rank %s Dungeon (Level %d+)\n", i+1, d.rank, d.reqLevel);
        }
    }

    void enterDungeon() {
        showDungeons();
        int idx = -1;
        while (true) {
            System.out.print("Choose dungeon number: ");
            String input = sc.nextLine().trim();
            try {
                idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= DUNGEONS.length) {
                    System.out.println("Invalid input.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        Dungeon d = DUNGEONS[idx];
        if (hero.level < d.reqLevel) {
            System.out.println("Required level: " + d.reqLevel);
            return;
        }
        System.out.println("Choose difficulty: easy, normal, hard");
        String diff = sc.nextLine().trim().toLowerCase();
        while (!DIFFICULTY.containsKey(diff)) {
            System.out.println("Invalid input. Choose difficulty: easy, normal, hard");
            diff = sc.nextLine().trim().toLowerCase();
        }
        System.out.printf("Entering Rank %s Dungeon (%s mode)...\n", d.rank, diff);
        fightDungeon(d, diff);
    }

    void fightDungeon(Dungeon d, String diff) {
        double multi = DIFFICULTY.get(diff);
        double expMulti = DIFFICULTY_EXP_MULTIPLIER.get(diff);

        for (int i = 1; i <= 5; i++) {
            System.out.println("\nMinion " + i + " appears!");
            Monster minion = new Monster(
    d.name + " Minion",
    (int)(d.baseMinionHp * multi),
    (int)(20 * multi), // base minAtk is now 20, scaled by difficulty
    (int)(30 * multi), // base maxAtk is now 30, scaled by difficulty
    (int)(d.baseMinionDef * multi),
    d.reqLevel,
    (int)(d.baseMinionMana * multi),
    (int)(d.expReward * 0.3),
    (int)(d.goldReward * 0.2)
);
            boolean survived = fightMonster(minion, expMulti, true);
            if (!survived) {
                System.out.println("You were defeated by a minion! Returning to town...");
                return;
            }
            if (hero.fledLastBattle) {
                hero.fledLastBattle = false;
                System.out.println("You fled the dungeon and returned to town safely!");
                return;
            }
        }

        System.out.println("\nThe Boss appears: " + d.name + "!");
        Monster boss = new Monster(
            d.name,
            (int)(d.baseMinionHp * multi * 2),
            (int)(50 * multi * 2),
            (int)(70 * multi * 2),
            (int)(d.baseMinionDef * multi * 2),
            d.reqLevel,
            (int)(d.baseMinionMana * multi * 2),
            d.expReward * 2,
            d.goldReward * 2
        );
        boolean bossSurvived = fightMonster(boss, expMulti, true);
        if (hero.fledLastBattle) {
            hero.fledLastBattle = false;
            System.out.println("You fled the dungeon and returned to town safely!");
        } else if (bossSurvived) {
            System.out.println("Dungeon cleared! Congratulations!");
        } else {
            System.out.println("You were defeated by the boss! Returning to town...");
        }
    }

    public Item getRandomDrop() {
        double roll = random.nextDouble();
        Item.Rarity[] rarities = { Item.Rarity.LEGENDARY, Item.Rarity.EPIC, Item.Rarity.RARE, Item.Rarity.UNCOMMON, Item.Rarity.COMMON };
        for (Item.Rarity rarity : rarities) {
            if (roll < RARITY_DROP_CHANCE.get(rarity)) {
                List<Item> candidates = new ArrayList<>();
                for (Item item : ITEM_DROPS) {
                    if (item.rarity == rarity) candidates.add(item);
                }
                if (!candidates.isEmpty()) {
                    return candidates.get(random.nextInt(candidates.size()));
                }
            }
        }
        return null;
    }

    boolean fightMonster(Monster m, double expMulti, boolean fleeAllowed) {
        for (Skill skill : hero.skills) if (skill.isPassive) skill.onBattleStart(hero);

        int expDrop = (int)(m.expReward * expMulti); // Calculate actual EXP drop ONCE and use for both display and reward

        while (hero.hp > 0 && m.hp > 0) {
            System.out.printf("%s (Lv.%d %s) HP: %d/%d | Mana: %d/%d | EXP: %d/%d | Gold: %d\n",
                hero.name, hero.level, hero.heroClass,
                hero.hp, hero.maxHp(), hero.mana, hero.maxMana(),
                hero.exp, hero.expToLevel, hero.gold);
            System.out.printf("%s HP: %d | Mana: %d | EXP drop: %d\n",
                m.name, m.hp, m.mana, expDrop); // Shows actual exp drop
            System.out.print("Choose action: 1.attack, 2.inventory, 3.pass, 4.skills");
            if (fleeAllowed) System.out.print(", 5.flee");
            System.out.println();
            String action = sc.nextLine().trim().toLowerCase();
            boolean playerActionUsed = false;

            if (action.equals("1")) {
                hero.attack(m);
                actionStack.push("1");
                playerActionUsed = true;
            } else if (action.equals("2")) {
                hero.showInventory();
                System.out.println("Choose item number to use or 0 to cancel:");
                String itemChoice = sc.nextLine().trim();
                try {
                    int itemIdx = Integer.parseInt(itemChoice) - 1;
                    if (itemIdx >= 0) {
                        hero.useItem(itemIdx);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
                continue;
            } else if (action.equals("3")) {
                System.out.println(hero.name + " passes the turn.");
                actionStack.push("pass");
                playerActionUsed = true;
            } else if (action.equals("4")) {
                ArrayList<Skill> activeSkills = new ArrayList<>();
                for (Skill skill : hero.skills) if (!skill.isPassive) activeSkills.add(skill);

                if (activeSkills.isEmpty()) {
                    System.out.println("No active skills available!");
                    continue;
                }
                System.out.println("Available skills:");
                for (int i = 0; i < activeSkills.size(); i++) {
                    System.out.printf("%d. %s - %s\n", i + 1, activeSkills.get(i).name, activeSkills.get(i).description);
                }
                System.out.println("Choose skill number or 0 to cancel:");
                String skillChoice = sc.nextLine().trim();
                int chosen = -1;
                try {
                    chosen = Integer.parseInt(skillChoice);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                    continue;
                }
                if (chosen == 0) continue;
                if (chosen > 0 && chosen <= activeSkills.size()) {
                    Skill skill = activeSkills.get(chosen - 1);
                    playerActionUsed = skill.use(hero, m);
                } else {
                    System.out.println("Invalid skill choice.");
                    continue;
                }
                if (!playerActionUsed) continue;
            } else if (fleeAllowed && action.equals("5")) {
                playerActionUsed = true;
                double fleeChance = 1.0 * hero.hp / hero.maxHp();
                fleeChance = Math.max(fleeChance, 0.1);
                if (random.nextDouble() < fleeChance) {
                    System.out.println("You successfully fled the dungeon!");
                    hero.fledLastBattle = true;
                    return true;
                } else {
                    System.out.printf("Flee attempt failed! (Chance: %.0f%%)\n", fleeChance * 100);
                }
            } else {
                System.out.println("Invalid input.");
                continue;
            }

            if (playerActionUsed && m.hp > 0) {
                m.attack(hero);
            }
            messageQueue.add("Round: " + hero.name + " HP " + hero.hp + ", " + m.name + " HP " + m.hp);
        }

        if (hero.hp > 0) {
            System.out.println(hero.name + " defeated the " + m.name + "!");
            System.out.println("You gained " + expDrop + " EXP from defeating the " + m.name + "!");
            hero.gainExp(expDrop); // Use the calculated drop
            hero.gold += m.goldReward;
            System.out.println(hero.name + " picked up " + m.goldReward + " gold!");
            Item item = getRandomDrop();
            if (item != null) {
                hero.addItem(item);
                System.out.println("You found a " + item + "!");
            } else {
                System.out.println("No item dropped this time.");
            }
            return true;
        } else {
            return false;
        }
    }

    void showMessages() {
        System.out.println("Game Messages:");
        while (!messageQueue.isEmpty()) {
            System.out.println(messageQueue.poll());
        }
    }

    void run() {
        System.out.println("1. New Game\n2. Load Game");
        String startChoice = sc.nextLine().trim();
        if (startChoice.equals("2")) {
            if (!loadProgress()) chooseClass();
        } else {
            chooseClass();
        }

        while (true) {
            hero.hp = hero.maxHp();
            hero.mana = hero.maxMana();
            System.out.printf("%s (Lv.%d %s) HP: %d/%d | Mana: %d/%d | EXP: %d/%d | Gold: %d\n",
                hero.name, hero.level, hero.heroClass,
                hero.hp, hero.maxHp(), hero.mana, hero.maxMana(),
                hero.exp, hero.expToLevel, hero.gold);
            System.out.println("\nMain Menu: 1.dungeon, 2.inventory, 3.messages, 4.save, 5.quit");
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("1")) {
                enterDungeon();
            } else if (cmd.equals("2")) {
                hero.showInventory();
                System.out.println("Choose item number to use or 0 to cancel:");
                String itemChoice = sc.nextLine().trim();
                try {
                    int itemIdx = Integer.parseInt(itemChoice) - 1;
                    if (itemIdx >= 0) {
                        hero.useItem(itemIdx);
                    }
                } catch (NumberFormatException e) {}
            } else if (cmd.equals("3")) {
                showMessages();
            } else if (cmd.equals("4")) {
                saveProgress();
            } else if (cmd.equals("5")) {
                System.out.println("Thanks for playing!");
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    public static void main(String[] args) {
        new GameProject().run();
    }
}