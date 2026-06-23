package cn.edu.whut.sept.zuul.model;

import java.util.Set;
import java.util.HashMap;

/**
 * 房间实体类。
 * 代表游戏中的一个独立场景。扩展支持了多物品HashMap容器，
 * 以及长描述（LongDescription）信息的自动提取与展现。
 *
 * @author Zuul开发组
 * @version 1.1
 */
public class Room {
    /** 房间描述。去掉了 final 修饰符，允许任务完成后动态改变房间外观特征 */
    private String description;

    /** 房间的各个出口集合，Key为方向（如east），Value为对应的邻接房间 */
    private final HashMap<String, Room> exits;

    /** 房间内放置的物品容器，支持存放任意数量的物品，Key为物品名，Value为物品对象 */
    private final HashMap<String, Item> items;

    /**
     * 构造一个新的房间。
     *
     * @param description 该房间的初始文字描述
     */
    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
    }

    /**
     * 设置房间的出口。
     *
     * @param direction 出口的方向（如 "north", "east" 等）
     * @param neighbor  该方向上连接的相邻房间对象
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * 向房间内添加一件物品。
     *
     * @param item 被添加到房间的物品对象
     */
    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    /**
     * 从房间内移除一件特定物品。
     *
     * @param name 要移除的物品名称
     * @return Item 被成功移除的物品对象；若房间内无此物品则返回 null
     */
    public Item removeItem(String name) {
        return items.remove(name);
    }

    /**
     * 获取房间的简短描述。
     *
     * @return String 房间的简短描述字符串
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * 动态修改当前房间的文字描述。
     * 主要用于剧情驱动（如解锁新通道后外观发生改变）。
     *
     * @param description 新的房间描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取房间的详细描述信息。
     * 深度优化后的看（look）命令基础逻辑输出。
     * 包含位置描述、可见出口以及房间内的物品一览。
     *
     * @return String 拼接好的房间完整信息字符串
     */
    public String getLongDescription() {
        return "位置描述：你正在 " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    /**
     * 内部辅助方法：提取所有的有效出口拼接成字符串。
     *
     * @return String 格式化的出口信息（例如 "可见出口: north east"）
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
     * 获取当前房间内所有可见物件的信息集合。
     * 对应 look / items 命令的要求，计算并展示物件总重量。
     *
     * @return String 格式化的物品信息以及总重展示
     */
    public String getItemString() {
        if (items.isEmpty()) {
            return "房间物品：这里空无一物。";
        }
        StringBuilder returnString = new StringBuilder("房间内可见物件：\n");
        int totalWeight = 0;
        for (String name : items.keySet()) {
            Item item = items.get(name);
            returnString.append("  - ")
                    .append(name)
                    .append(" (说明: ").append(item.getDescription())
                    .append(", 重量: ").append(item.getWeight()).append("kg)\n");
            totalWeight += item.getWeight();
        }
        return returnString.append(">> 房间内物品总重: ").append(totalWeight).append("kg").toString();
    }

    /**
     * 获取指定方向上的相邻房间。
     *
     * @param direction 指定方向（如 "east"）
     * @return Room 相邻的房间；若该方向无出口则返回 null
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * 获取房间内所有物品的容器 Map。
     * 暴露给 Player 类以进行批量操作（如 take all 批量拾取）。
     *
     * @return HashMap<String, Item> 当前房间内所有物品的哈希表
     */
    private String imageName;

    public HashMap<String, Item> getItems() {
        return this.items;
    }

    /**
     * 获取房间绑定的图片名称
     *
     * @return String 图片文件名
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * 设置房间绑定的图片名称
     *
     * @param imageName 图片文件名
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}