package cn.edu.whut.sept.zuul;

/**
 * 物品实体类.
 * 代表游戏房间内或背包中的具体物件，包含名称、描述与重量属性.
 * * @author 组员A
 * @version 1.0
 */
public class Item {
    private final String name;
    private final String description;
    private final int weight;

    public Item(String name, String description, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getWeight() {
        return weight;
    }
}