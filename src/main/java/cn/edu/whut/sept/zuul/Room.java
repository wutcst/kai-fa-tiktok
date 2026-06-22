package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Set;

/**
 * 房间类.
 * 扩展支持了多物品HashMap容器以及LongDescription的信息自动提取展现.
 *
 * @author 组员A
 * @version 1.0
 */
public class Room {
    private final String description;
    private final HashMap<String, Room> exits;
    private final HashMap<String, Item> items;

    /**
     * 房间类的构造函数.
     *
     * @param description 房间的文本描述描述
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
     * @param neighbor  连接的邻居房间
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * 向房间内放置一个物品.
     *
     * @param item 物品对象
     */
    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    /**
     * 从房间内拿走或移除一个物品.
     *
     * @param name 物品的名称
     * @return 被移除的物品对象，若不存在则返回 null
     */
    public Item removeItem(String name) {
        return items.remove(name);
    }

    /**
     * 获取房间的简短描述.
     *
     * @return 简短描述字符串
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * 深度优化后的看（look）命令基础逻辑输出.
     *
     * @return 包含出口和物品的完整房间长描述
     */
    public String getLongDescription() {
        return "位置描述：你正在 " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    /**
     * 内部方法：获取房间的所有出口拼接字符串.
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
     * @return 物品信息拼接字符串
     */
    public String getItemString() {
        if (items.isEmpty()) {
            return "房间物品：空无一物。";
        }
        StringBuilder returnString = new StringBuilder("房间内可见物件：");
        int totalWeight = 0;
        for (String name : items.keySet()) {
            Item item = items.get(name);
            returnString.append(" ").append(name).append("(")
                    .append(item.getDescription()).append("-").append(item.getWeight()).append("kg)");
            totalWeight += item.getWeight();
        }
        return returnString + " | 房间内物品总重: " + totalWeight + "kg";
    }

    /**
     * 获取指定方向上的出口房间.
     *
     * @param direction 方向字符串
     * @return 对应的房间对象，不存在则返回 null
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }
}