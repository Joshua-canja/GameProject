/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package wow.gameproject;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

class Dungeon {
    String rank;
    int reqLevel;
    String name;
    int baseHp, baseAtk, baseDef;

    public Dungeon(String rank, int reqLevel, String name, int hp, int atk, int def) {
        this.rank = rank;
        this.reqLevel = reqLevel;
        this.name = name;
        this.baseHp = hp;
        this.baseAtk = atk;
        this.baseDef = def;
    }
}

public class GameProject {
    // Array for dungeons
    static final Dungeon[] DUNGEONS = {
        new Dungeon("E", 1, "Goblin", 30, 5, 2),
        new Dungeon("D", 3, "Wolf", 50, 8, 4),
        new Dungeon("C", 5, "Orc", 80, 12, 6),
        new Dungeon("B", 7, "Troll", 120, 18, 8),
        new Dungeon("A", 10, "Ogre", 160, 22, 12),
        new Dungeon("S", 15, "Dragon", 250, 40, 20)
    };

    // Difficulty settings: HashMap
    static final HashMap<String, Double> DIFFICULTY = new HashMap<>();
    static {
        DIFFICULTY.put("easy", 0.8);
        DIFFICULTY.put("normal", 1.0);
        DIFFICULTY.put("hard", 1.2);
    }

    Hero hero;
    Stack<String> actionStack = new Stack<>();       // Stack for actions
    Queue<String> messageQueue = new LinkedList<>(); // Queue for game messages
    Scanner sc = new Scanner(System.in);

    void chooseClass() {
        System.out.println("Choose your class: warrior, mage, archer, tank");
        String heroClass = sc.nextLine().trim().toLowerCase();
        while (!Hero.JOB_UPGRADES.containsKey(heroClass)) {
            System.out.println("Invalid class. Choose again:");
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
        System.out.print("Choose dungeon number: ");
        int idx = sc.nextInt() - 1; sc.nextLine();
        if (idx < 0 || idx >= DUNGEONS.length) {
            System.out.println("Invalid dungeon.");
            return;
        }
        Dungeon d = DUNGEONS[idx];
        if (hero.level < d.reqLevel) {
            System.out.println("Required level: " + d.reqLevel);
            return;
        }
        System.out.println("Choose difficulty: easy, normal, hard");
        String diff = sc.nextLine().trim().toLowerCase();
        while (!DIFFICULTY.containsKey(diff)) {
            System.out.println("Invalid difficulty. Choose again:");
            diff = sc.nextLine().trim().toLowerCase();
        }
        System.out.printf("Entering Rank %s Dungeon (%s mode)...\n", d.rank, diff);
        fightMonster(d, diff);
    }

    void fightMonster(Dungeon d, String diff) {
        double multi = DIFFICULTY.get(diff);
        Monster m = new Monster(d.name, (int)(d.baseHp * multi), (int)(d.baseAtk * multi), (int)(d.baseDef * multi), d.reqLevel);

        while (hero.hp > 0 && m.hp > 0) {
            System.out.printf("%s HP: %d | %s HP: %d\n", hero.name, hero.hp, m.name, m.hp);
            System.out.println("Choose action: 1.attack, 2.inventory, 3.pass");
            String action = sc.nextLine().trim().toLowerCase();
            if (action.equals("1")) {
                hero.attack(m);
                actionStack.push("1");
            } else if (action.equals("2")) {
                hero.showInventory();
                continue;
            } else if (action.equals("3")) {
                System.out.println(hero.name + " passes the turn.");
                actionStack.push("pass");
            } else {
                System.out.println("Invalid action.");
                continue;
            }
            if (m.hp > 0) {
                m.attack(hero);
            }
            messageQueue.add("Round: " + hero.name + " HP " + hero.hp + ", " + m.name + " HP " + m.hp);
        }

        if (hero.hp > 0) {
            System.out.println(hero.name + " defeated the " + m.name + "!");
            hero.exp += 10;
            if (hero.exp >= 10) {
                hero.levelUp();
                hero.exp = 0;
            }
            hero.addItem(m.name + " loot");
        } else {
            System.out.println("You were defeated! Returning to town...");
        }
    }

    void showMessages() {
        System.out.println("Game Messages:");
        while (!messageQueue.isEmpty()) {
            System.out.println(messageQueue.poll());
        }
    }

    void run() {
        chooseClass();
        while (true) {
            System.out.println("\nMain Menu: 1.dungeon, 2.inventory, 3.messages, 4.quit");
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("1")) {
                enterDungeon();
            } else if (cmd.equals("2")) {
                hero.showInventory();
            } else if (cmd.equals("3")) {
                showMessages();
            } else if (cmd.equals("4")) {
                System.out.println("Thanks for playing!");
                break;
            } else {
                System.out.println("Unknown command.");
            }
        }
    }

    public static void main(String[] args) {
        new GameProject().run();
    }
}