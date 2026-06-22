package cn.edu.whut.sept.zuul;

import java.util.Set;
import java.util.HashMap;

/**
 * 房间类.
 * 扩展支持了多物品HashMap容器以及LongDescription的信息自动提取展现.
 *
 * @author 组员B
 * @version 1.0
 */
public class Room {
    private final String description;
    private final HashMap<String, Room> exits;
    private final HashMap<String, Item> items; // 新增：房间内的任意数量物品容器

    /**
     * 构造函数，创建一个房间对象.
     *
     * @param description 房间的简短描述信息
     */
    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
    }

    /**
     * 设置房间的出口.
     *
     * @param direction 出口的方向
     * @param neighbor 相邻的房间
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * 向房间内添加物品.
     *
     * @param item 要添加到房间的物品对象
     */
    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    /**
     * 从房间内移除指定名称的物品.
     *
     * @param name 待移除物品的名称
     * @return Item 被移除的物品对象，如果房间内不存在该物品则返回null
     */
    public Item removeItem(String name) {
        return items.remove(name);
    }

    /**
     * 获取房间的简短描述.
     *
     * @return String 房间的描述字符串
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * 获取房间的详细描述，包含出口和可视物品信息.
     * 深度优化后的看（look）命令基础逻辑输出.
     *
     * @return String 房间的详细描述多行字符串
     */
    public String getLongDescription() {
        return "位置描述：你正在 " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    /**
     * 获取房间所有可见出口的字符串表示.
     *
     * @return String 包含所有出口方向的字符串
     */
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
     *
     * @return String 包含所有可视物品详细信息和总重量的字符串
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

    /**
     * 获取指定方向的出口房间.
     *
     * @param direction 出口的方向 (如 "north", "south")
     * @return Room 指定方向上的相邻房间，如果没有则返回null
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * 获取房间内所有物品的容器Map.
     * 用于Player类进行批量拾取(take all)操作.
     *
     * @return HashMap 包含当前房间所有物品的映射表
     */
    public HashMap<String, Item> getItems() {
        return this.items;
    }
}