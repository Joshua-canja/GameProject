/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wow.gameproject;

public abstract class Character {
    String name;
    int hp, atk, def, level;

    public Character(String name, int hp, int atk, int def, int level) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.level = level;
    }
}
