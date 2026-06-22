package cn.edu.whut.sept.zuul.model;

/**
 * 物品实体类.
 * 代表游戏房间内或背包中的具体物件，包含名称、描述与重量属性.
 *
 * @author 组员A
 * @version 1.0
 */
public class Item {
    private final String name;
    private final String description;
    private final int weight;

    /**
     * 物品类的构造函数.
     *
     * @param name        物品名称
     * @param description 物品描述
     * @param weight      物品重量
     */
    public Item(String name, String description, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    /**
     * 获取物品名称.
     *
     * @return 物品名称字符串
     */
    public String getName() {
        return name;
    }

    /**
     * 获取物品描述.
     *
     * @return 物品描述字符串
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取物品重量.
     *
     * @return 物品重量克数或公斤数
     */
    public int getWeight() {
        return weight;
    }
}