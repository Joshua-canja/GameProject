package wow.gameproject;

import java.io.Serializable;
import java.util.Random;

public class Monster extends Character implements Serializable {
    private static final long serialVersionUID = 1L;
    public int expReward;
    public int goldReward;
    public int mana;

    // Store base damage for recalculation/flexibility if desired
    public int baseMinAtk;
    public int baseMaxAtk;

    public Monster(String name, int hp, int minAtk, int maxAtk, int def, int level, int mana, int expReward, int goldReward) {
        super(name, hp, minAtk, maxAtk, def, level);
        this.mana = mana;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.baseMinAtk = minAtk;
        this.baseMaxAtk = maxAtk;
    }

    public void attack(Hero h) {
        Random random = new Random();
        int lower = minAtk;
        int upper = maxAtk;
        if (upper < lower) {
            int temp = lower;
            lower = upper;
            upper = temp;
        }
        int dmg = random.nextInt(upper - lower + 1) + lower;
        int dealt = Math.max(1, dmg - h.getTotalDef());
        h.hp -= dealt;
        System.out.println(name + " attacks " + h.name + " for " + dealt + " damage! (rolled " + dmg + ", hero DEF " + h.getTotalDef() + ")");
    }
}