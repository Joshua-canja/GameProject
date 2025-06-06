package wow.gameproject;

import java.io.Serializable;

public abstract class Skill implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name, description;
    public boolean isPassive;

    public Skill(String name, String description, boolean isPassive) {
        this.name = name;
        this.description = description;
        this.isPassive = isPassive;
    }

    public void onBattleStart(Hero h) {}
    public boolean use(Hero h, Character enemy) { return false; }
}