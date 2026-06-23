package cn.edu.whut.sept.zuul.model;

import java.util.Set;
import java.util.HashMap;

public class Room {
    private String description;
    private final HashMap<String, Room> exits;
    private final HashMap<String, Item> items;
    private String imageName;
    private boolean isDark = false; // 是否为黑暗房间

    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    public Item removeItem(String name) {
        return items.remove(name);
    }

    public String getShortDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDark(boolean dark) { this.isDark = dark; }
    public boolean isDark() { return isDark; }

    public String getLongDescription(boolean hasLight) {
        if (isDark && !hasLight) {
            return "【环境：极度黑暗】\n 这里漆黑一片，你什么也看不清。也许你需要一个手电筒？\n"
                    + " 出口：" + getExitString(); // 黑暗中只能摸索到出口，看不到物品
        }
        return " 抵达：" + description + "\n"
                + " 出口：" + getExitString() + "\n"
                + getItemString(hasLight);
    }

    public String getLongDescription() {
        return getLongDescription(true);
    }

    private String getExitString() {
        StringBuilder returnString = new StringBuilder();
        for (String exit : exits.keySet()) {
            returnString.append("[").append(exit).append("] ");
        }
        return returnString.toString();
    }

    /**
     * 增强逻辑：如果房间是黑暗的且没有手电筒，隐藏物品列表
     */
    public String getItemString(boolean hasLight) {
        if (isDark && !hasLight) {
            return " 这里太黑了，看不清有什么。";
        }
        if (items.isEmpty()) {
            return " 房间物品：空。";
        }
        StringBuilder returnString = new StringBuilder(" 房间物品：\n");
        for (String name : items.keySet()) {
            Item item = items.get(name);
            returnString.append("  • ").append(name)
                    .append(" (").append(item.getWeight()).append("kg)")
                    .append(" - ").append(item.getDescription()).append("\n");
        }
        return returnString.toString();
    }

    public String getItemString() { return getItemString(true); }

    public Room getExit(String direction) { return exits.get(direction); }

    public HashMap<String, Item> getItems() { return this.items; }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }
}