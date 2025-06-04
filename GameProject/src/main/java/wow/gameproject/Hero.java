/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wow.gameproject;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class LinkedListInventory {
    private Node head;

    private static class Node {
        String item;
        Node next;
        Node(String item) { this.item = item; }
    }

    public void add(String item) {
        Node node = new Node(item);
        node.next = head;
        head = node;
    }



    public List<String> getItems() {
        List<String> items = new ArrayList<>();
        Node curr = head;
        while (curr != null) {
            items.add(curr.item);
            curr = curr.next;
        }
        Collections.reverse(items);
        return items;
    }
}

public class Hero extends Character {
    String heroClass;
    int exp;
    String hiddenClass;
    LinkedListInventory inventory = new LinkedListInventory();

    // HashMap for job upgrades
    static final HashMap<String, String[]> JOB_UPGRADES = new HashMap<>();
    static {
        JOB_UPGRADES.put("warrior", new String[]{"Knight", "Berserker"});
        JOB_UPGRADES.put("mage", new String[]{"Sorcerer", "Warlock"});
        JOB_UPGRADES.put("archer", new String[]{"Sniper", "Ranger"});
        JOB_UPGRADES.put("tank", new String[]{"Guardian", "Paladin"});
    }

    public Hero(String name, String heroClass) {
        super(name, 100, 10, 5, 1);
        this.heroClass = heroClass;
        this.exp = 0;
    }

    public void levelUp() {
        level++;
        hp += 10;
        atk += 2;
        def += 2;
        System.out.println(name + " leveled up to " + level + "!");
        if (level == 10 && hiddenClass == null) {
            Random rnd = new Random();
            if (rnd.nextBoolean()) {
                String[] upgrades = JOB_UPGRADES.get(heroClass);
                hiddenClass = upgrades[rnd.nextInt(upgrades.length)];
                System.out.println("Hidden class unlocked: " + hiddenClass);
            }
        }
    }

    public void attack(Monster m) {
        int damage = Math.max(10, atk - m.def);
        m.hp -= damage;
        System.out.println(name + " attacks " + m.name + " for " + damage + " damage!");
    }

    public void addItem(String item) {
        inventory.add(item);
        System.out.println(name + " picked up " + item);
    }

    public void showInventory() {
        List<String> items = inventory.getItems();
        System.out.println(name + "'s Inventory: " + (items.isEmpty() ? "Empty" : String.join(", ", items)));
    }
}