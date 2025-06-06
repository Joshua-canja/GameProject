package wow.gameproject;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { HP_POTION, MANA_POTION, SWORD, STAFF, SHIELD, HELMET, ARMOR, RING }
    public enum Rarity { COMMON, UNCOMMON, RARE, EPIC, LEGENDARY }

    public final String name;
    public final Type type;
    public final int baseValue;
    public final Rarity rarity;
    public final int value;

    public Item(String name, Type type, int baseValue, Rarity rarity) {
        this.name = name;
        this.type = type;
        this.baseValue = baseValue;
        this.rarity = rarity;
        // For equipment, value should be the actual stat bonus, e.g. Excalibur = 50
        this.value = baseValue * getRarityMultiplier(rarity);
    }

    public static int getRarityMultiplier(Rarity rarity) {
        switch (rarity) {
            case COMMON: return 1;
            case UNCOMMON: return 2;
            case RARE: return 3;
            case EPIC: return 5;
            case LEGENDARY: return 10;
            default: return 1;
        }
    }

    @Override
    public String toString() {
        return "[" + rarity + "] " + name + " (+" + value + " " + getStatType() + ")";
    }

    private String getStatType() {
        switch (type) {
            case HP_POTION: return "HP";
            case MANA_POTION: return "Mana";
            case SWORD: case STAFF: case RING: return "ATK";
            case SHIELD: case HELMET: case ARMOR: return "DEF";
            default: return "";
        }
    }
}