package wow.gameproject;

import java.io.Serializable;
import java.util.*;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() { return items; }
    public void addItem(Item item) { items.add(item); }
    public void removeItem(int idx) { items.remove(idx); }
}