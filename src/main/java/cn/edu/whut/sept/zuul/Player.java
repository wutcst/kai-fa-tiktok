package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Stack;

/**
 * 独立玩家实体类.
 * 负责托管背包逻辑、重量计算、以及多层 back 回退历史记录栈.
 *
 * @author 组员B
 * @version 1.0
 */
public class Player {
    private final String name;
    private Room currentRoom;
    private int maxWeight;
    private int currentWeight;
    private final HashMap<String, Item> inventory;
    private final Stack<Room> roomHistory;

    /**
     * 玩家类的构造函数.
     *
     * @param name      玩家名字
     * @param maxWeight 玩家最大负重
     */
    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.inventory = new HashMap<>();
        this.roomHistory = new Stack<>();
    }

    /**
     * 获取玩家当前所在的房间.
     *
     * @return 当前房间对象
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * 设置玩家当前所在的房间.
     *
     * @param room 目标房间对象
     */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    /**
     * 获取玩家最大负重上限.
     *
     * @return 最大负重
     */
    public int getMaxWeight() {
        return maxWeight;
    }

    /**
     * 设置玩家最大负重上限.
     *
     * @param maxWeight 负重上限值
     */
    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * 获取玩家当前已经背负的物品总重量.
     *
     * @return 当前负重值
     */
    public int getCurrentWeight() {
        return currentWeight;
    }

    /**
     * 获取玩家移动的历史路径栈.
     *
     * @return 历史房间栈
     */
    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    /**
     * 将当前房间推入历史记录栈中，以便回退.
     *
     * @param room 历史房间对象
     */
    public void pushRoomToHistory(Room room) {
        roomHistory.push(room);
    }

    /**
     * 玩家拾取物品的核心业务逻辑.
     *
     * @param itemName 想要拾取的物品名称
     * @param room     当前所在的房间对象
     * @return 是否成功拾取
     */
    public boolean takeItem(String itemName, Room room) {
        Item item = room.removeItem(itemName);
        if (item == null) {
            System.out.println("这里没有这个物品！");
            return false;
        }
        if (this.currentWeight + item.getWeight() > this.maxWeight) {
            System.out.println("【重量拦截】物品太重了！超出你的负重能力极限。");
            room.addItem(item);
            return false;
        }
        inventory.put(item.getName(), item);
        this.currentWeight += item.getWeight();
        System.out.println("你成功将 [" + itemName + "] 放入背包。");
        return true;
    }

    /**
     * 玩家丢弃物品的核心业务逻辑.
     *
     * @param itemName 想要丢弃的物品名称
     * @param room     当前所在的房间对象
     */
    public void dropItem(String itemName, Room room) {
        Item item = inventory.remove(itemName);
        if (item == null) {
            System.out.println("你的背包里没有这个物品！");
            return;
        }
        this.currentWeight -= item.getWeight();
        room.addItem(item);
        System.out.println("你从背包丢弃了: [" + itemName + "]");
    }

    /**
     * 拼接背包与当前玩家负重详情.
     *
     * @return 背包详情描述字符串
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "随身背包：当前没有任何物件。";
        }
        StringBuilder returnString = new StringBuilder("随身背包物件：");
        for (String itemName : inventory.keySet()) {
            returnString.append(" ").append(itemName)
                    .append("(").append(inventory.get(itemName).getWeight()).append("kg)");
        }
        return returnString + " | 负重状态: " + currentWeight + "kg/" + maxWeight + "kg";
    }
}