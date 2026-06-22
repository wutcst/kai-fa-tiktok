package cn.edu.whut.sept.zuul;

import java.util.Set;
import java.util.HashMap;

/**
 * 房间类.
 * 扩展支持了多物品HashMap容器以及LongDescription的信息自动提取展现.
 */
public class Room {
    private final String description;
    private final HashMap<String, Room> exits;
    private final HashMap<String, Item> items; // 新增：房间内的任意数量物品容器

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

    /**
     * 深度优化后的看（look）命令基础逻辑输出.
     */
    public String getLongDescription() {
        return "位置描述：你正在 " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    private String getExitString() {
        StringBuilder returnString = new StringBuilder("可见出口:");
        Set<String> keys = exits.keySet();
        for (String exit : keys) {
            returnString.append(" ").append(exit);
        }
        return returnString.toString();
    }

    /**
     * 获取当前房间内所有物件的信息集合（对应look/items命令要求）.
     */
    public String getItemString() {
        if (items.isEmpty()) {
            return "房间物品：空无一物。";
        }
        StringBuilder returnString = new StringBuilder("房间内可见物件：");
        int totalWeight = 0;
        for (String name : items.keySet()) {
            Item item = items.get(name);
            returnString.append(" ").append(name).append("(").append(item.getDescription()).append("-").append(item.getWeight()).append("kg)");
            totalWeight += item.getWeight();
        }
        return returnString + " | 房间内物品总重: " + totalWeight + "kg";
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * 获取房间内所有物品的容器Map
     * 用于Player类进行批量拾取(take all)操作
     */
    public HashMap<String, Item> getItems() {
        return this.items;
    }
}