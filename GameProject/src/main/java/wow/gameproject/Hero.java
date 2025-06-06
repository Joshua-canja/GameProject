package wow.gameproject;

import java.io.Serializable;
import java.util.*;

public class Hero extends Character implements Serializable {
    private static final long serialVersionUID = 1L;

    public String heroClass;
    public int exp;
    public int expToLevel;
    public int mana;
    public int gold = 0;
    public List<Skill> skills = new ArrayList<>();
    public Inventory inventory = new Inventory();

    // Equipped item slots
    public Item weapon = null;
    public Item armor = null;
    // Add more slots as needed

    // Store base stats to allow recalculation for equip/unequip flexibility
    public int baseMinAtk;
    public int baseMaxAtk;
    public int baseDef;

    public static final Map<String, String[]> JOB_UPGRADES = Map.of(
        "warrior", new String[]{"knight", "berserker"},
        "mage", new String[]{"archmage", "warlock"},
        "archer", new String[]{"sniper", "ranger"},
        "tank", new String[]{"guardian", "paladin"}
    );
    public String hiddenClass = null;
    public boolean fledLastBattle = false;

    public Hero(String name, String heroClass) {
        super(name, 500, 30, 45, 10, 1);
        this.heroClass = heroClass;
        this.exp = 0;
        this.expToLevel = calcExpToLevel();
        this.mana = maxMana();
        this.baseMinAtk = minAtk;
        this.baseMaxAtk = maxAtk;
        this.baseDef = def;
        assignSkills(heroClass);
    }

    public int calcExpToLevel() {
        return 100 + (level - 1) * 50;
    }

    public int maxHp() {
        return 500 + (level - 1) * 30;
    }

    public int maxMana() {
        return 100 + (level - 1) * 10;
    }

    // Always use these getters for current stats so they always include equipment
    public int getTotalMinAtk() {
        return baseMinAtk + (weapon != null ? weapon.value : 0);
    }
    public int getTotalMaxAtk() {
        return baseMaxAtk + (weapon != null ? weapon.value : 0);
    }
    public int getTotalDef() {
        return baseDef + (armor != null ? armor.value : 0);
    }

    public void attack(Character target) {
        Random random = new Random();
        int lower = getTotalMinAtk();
        int upper = getTotalMaxAtk();
        if (upper < lower) {
            int temp = lower;
            lower = upper;
            upper = temp;
        }
        int dmg = random.nextInt(upper - lower + 1) + lower;
        int dealt = Math.max(1, dmg - target.def);
        target.hp -= dealt;
        System.out.println(name + " attacks " + target.name + " for " + dealt + " damage! (rolled " + dmg + ", target DEF " + target.def + ")");
    }

    public void gainExp(int amount) {
        exp += amount;
        System.out.println(name + " gained " + amount + " EXP.");
        while (exp >= expToLevel) {
            exp -= expToLevel;
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        baseMinAtk += 2;
        baseMaxAtk += 2;
        baseDef += 2;
        expToLevel = calcExpToLevel();
        hp = maxHp();
        mana = maxMana();
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

    // Show equipped items first, then inventory
    public void showInventory() {
        System.out.println(name + "'s Equipped Items:");
        System.out.printf("  Weapon: %s\n", (weapon != null ? weapon.toString() : "None"));
        System.out.printf("  Armor: %s\n", (armor != null ? armor.toString() : "None"));
        System.out.println("\n" + name + "'s Inventory:");
        List<Item> items = inventory.getItems();
        if (items.isEmpty()) {
            System.out.println("  (Empty)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("  %d. %s\n", i + 1, items.get(i));
            }
        }
        System.out.println("Gold: " + gold);
    }

    // Equip/unequip logic uses base stats, and always recalculates total stat via getter
    public void useItem(int idx) {
        List<Item> items = inventory.getItems();
        if (idx < 0 || idx >= items.size()) {
            System.out.println("Invalid item.");
            return;
        }
        Item item = items.get(idx);
        switch (item.type) {
            case HP_POTION:
                int heal = item.value;
                hp = Math.min(hp + heal, maxHp());
                System.out.println("Healed for " + heal + " HP. Current HP: " + hp + "/" + maxHp());
                inventory.removeItem(idx);
                break;
            case MANA_POTION:
                int manaHeal = item.value;
                mana = Math.min(mana + manaHeal, maxMana());
                System.out.println("Restored " + manaHeal + " Mana. Current Mana: " + mana + "/" + maxMana());
                inventory.removeItem(idx);
                break;
            case SWORD:
            case STAFF:
            case RING:
                if (weapon != null) {
                    System.out.println("Unequipped " + weapon.name + ".");
                }
                weapon = item;
                System.out.println("Equipped " + item.name + "! ATK increased by " + item.value);
                inventory.removeItem(idx);
                break;
            case SHIELD:
            case HELMET:
            case ARMOR:
                if (armor != null) {
                    System.out.println("Unequipped " + armor.name + ".");
                }
                armor = item;
                System.out.println("Equipped " + item.name + "! DEF increased by " + item.value);
                inventory.removeItem(idx);
                break;
            default:
                System.out.println("Cannot use this item.");
        }
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    private void assignSkills(String heroClass) {
        skills.clear();
        if (heroClass.equals("warrior")) {
            skills.add(new Skill("Endurance", "Passive: +5 DEF on battle start", true) {
                @Override public void onBattleStart(Hero h) { h.baseDef += 5; }
            });
            skills.add(new Skill("Power Strike", "Active: Deal 150% ATK damage (costs 20 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 20) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 20;
                    int dmg = (int) ((h.getTotalMaxAtk() * 1.5) - m.def);
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " uses Power Strike for " + dmg + " damage!");
                    return true;
                }
            });
            skills.add(new Skill("Whirlwind", "Active: Deal 80% ATK damage to all enemies (costs 30 Mana) [Single enemy here]", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 30) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 30;
                    int dmg = (int) ((h.getTotalMaxAtk() * 0.8) - m.def);
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " uses Whirlwind for " + dmg + " damage!");
                    return true;
                }
            });
        } else if (heroClass.equals("mage")) {
            skills.add(new Skill("Arcane Mind", "Passive: +10 Mana on battle start", true) {
                @Override public void onBattleStart(Hero h) {
                    h.mana = Math.min(h.mana + 10, h.maxMana());
                }
            });
            skills.add(new Skill("Fireball", "Active: Deal 130% ATK magic damage (costs 25 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 25) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 25;
                    int dmg = (int) ((h.getTotalMaxAtk() * 1.3) - m.def / 2);
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " casts Fireball for " + dmg + " magic damage!");
                    return true;
                }
            });
            skills.add(new Skill("Ice Lance", "Active: Deal 110% ATK and slow (costs 20 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 20) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 20;
                    int dmg = (int) ((h.getTotalMaxAtk() * 1.1) - m.def);
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " casts Ice Lance for " + dmg + " magic damage!");
                    return true;
                }
            });
        } else if (heroClass.equals("archer")) {
            skills.add(new Skill("Agility", "Passive: +5 minAtk on battle start", true) {
                @Override public void onBattleStart(Hero h) { h.baseMinAtk += 5; }
            });
            skills.add(new Skill("Piercing Shot", "Active: Ignores 50% of target's defense (costs 15 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 15) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 15;
                    int dmg = h.getTotalMaxAtk() - (m.def / 2);
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " uses Piercing Shot for " + dmg + " damage!");
                    return true;
                }
            });
            skills.add(new Skill("Double Shot", "Active: Hit twice for 70% ATK each (costs 20 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 20) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 20;
                    int dmg = (int)(h.getTotalMaxAtk() * 0.7) - m.def;
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " uses Double Shot for " + dmg + " damage!");
                    m.hp -= dmg;
                    System.out.println("Second arrow hits for " + dmg + " damage!");
                    return true;
                }
            });
        } else if (heroClass.equals("tank")) {
            skills.add(new Skill("Fortify", "Passive: +10% HP on battle start", true) {
                @Override public void onBattleStart(Hero h) {
                    h.hp = Math.min((int)(h.hp * 1.1), h.maxHp());
                }
            });
            skills.add(new Skill("Shield Bash", "Active: Deal damage and stun (costs 20 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 20) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 20;
                    int dmg = h.getTotalMinAtk() + 10 - m.def;
                    dmg = Math.max(1, dmg);
                    m.hp -= dmg;
                    System.out.println(h.name + " uses Shield Bash for " + dmg + " damage and stuns the enemy!");
                    return true;
                }
            });
            skills.add(new Skill("Taunt", "Active: Reduces enemy attack (costs 15 Mana)", false) {
                @Override public boolean use(Hero h, Character m) {
                    if (h.mana < 15) { System.out.println("Not enough mana!"); return false; }
                    h.mana -= 15;
                    if (m.minAtk > 1) m.minAtk--;
                    if (m.maxAtk > 1) m.maxAtk--;
                    System.out.println(h.name + " uses Taunt! Enemy attacks weakened.");
                    return true;
                }
            });
        }
    }
}