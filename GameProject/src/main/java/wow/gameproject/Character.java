package wow.gameproject;

import java.io.Serializable;

public class Character implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public int hp, minAtk, maxAtk, def, level;

    public Character(String name, int hp, int minAtk, int maxAtk, int def, int level) {
        this.name = name;
        this.hp = hp;
        this.minAtk = minAtk;
        this.maxAtk = maxAtk;
        this.def = def;
        this.level = level;
    }
}