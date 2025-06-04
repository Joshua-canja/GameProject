/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wow.gameproject;

public class Monster extends Character {
    int expReward;
    int goldReward;

    public Monster(String name, int hp, int atk, int def, int level) {
        super(name, hp, atk, def, level);
        this.expReward = expReward;
        this.goldReward = goldReward;
    }

    public void attack(Hero h) {
        int damage = Math.max(10, atk - h.def);
        h.hp -= damage;
        System.out.println(name + " attacks " + h.name + " for " + damage + " damage!");
    }
}