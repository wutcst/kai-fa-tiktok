package cn.edu.whut.sept.zuul.model;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * 独立玩家实体类。
 * 负责托管玩家背包逻辑、负重量计算、以及利用 Stack 实现多层后退（back）的历史记录功能。
 *
 * @author 组员B
 * @version 1.0
 */
public class Player {
    /** 玩家名称 */
    private final String name;
    /** 玩家当前所处的房间 */
    private Room currentRoom;
    /** 玩家的极限负重能力 */
    private int maxWeight;
    /** 玩家背包当前的总重量 */
    private int currentWeight;
    /** 随身背包物品容器，Key为物品名，Value为物品对象 */
    private final HashMap<String, Item> inventory;
    /** 玩家走过的房间历史记录栈，用于实现多层级无限回退 */
    private final Stack<Room> roomHistory;

    /**
     * 创建一个玩家对象。
     *
     * @param name 玩家的姓名
     * @param maxWeight 玩家的最大负重量（单位：kg）
     */
    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.inventory = new HashMap<>();
        this.roomHistory = new Stack<>();
    }

    /**
     * 获取玩家当前所处的房间。
     *
     * @return Room 当前房间对象
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * 设置玩家当前所处的房间。
     *
     * @param room 目标房间对象
     */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    /**
     * 获取玩家的最大负重能力。
     *
     * @return int 最大负重值
     */
    public int getMaxWeight() {
        return maxWeight;
    }

    /**
     * 调整玩家的最大负重能力（可供后期喝药水升级等功能使用）。
     *
     * @param maxWeight 新的最大负重值
     */
    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * 获取当前背包的总负重。
     *
     * @return int 当前负重值
     */
    public int getCurrentWeight() {
        return currentWeight;
    }

    /**
     * 获取玩家的路径历史记录栈。
     *
     * @return Stack<Room> 历史路径房间栈
     */
    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    /**
     * 将当前离开的房间压入历史记录栈中（为后续的 back 命令做准备）。
     *
     * @param room 刚才经过的房间对象
     */
    public void pushRoomToHistory(Room room) {
        roomHistory.push(room);
    }

    /**
     * 判断玩家背包中是否拥有特定名称的物品。
     * 此功能为剧情任务的状态机检测提供了支撑。
     *
     * @param itemName 需要检查的物品名
     * @return boolean 若背包中有此物品返回 true，否则返回 false
     */
    public boolean hasItem(String itemName) {
        return inventory.containsKey(itemName);
    }

    /**
     * 玩家拾取单一物品的核心业务逻辑。
     * 包含对【超重行为】的强制拦截判断。
     *
     * @param itemName 要拾取的物品名称
     * @param room 当前所在的房间（物品拾取来源）
     * @return boolean 拾取成功返回 true，因过重拦截或物品不存在返回 false
     */
    public boolean takeItem(String itemName, Room room) {
        Item item = room.removeItem(itemName);
        if (item == null) {
            System.out.println("这里没有这个物品！");
            return false;
        }

        // 【重量限制拦截逻辑】判断拾取此物品后是否会超出最大负重
        if (this.currentWeight + item.getWeight() > this.maxWeight) {
            System.out.println("【重量拦截】物品 [" + itemName + "] 太重了！超出你的负重能力极限。");
            room.addItem(item); // 拦截触发，将物品退回至房间原位
            return false;
        }

        // 拾取成功处理
        inventory.put(item.getName(), item);
        this.currentWeight += item.getWeight();
        System.out.println("你成功将 [" + itemName + "] 放入背包。");
        return true;
    }

    /**
     * 玩家拾取房间内全部可见物品的逻辑实现（take all）。
     * 自动遍历并复用单体拾取的超重检测机制。
     *
     * @param room 当前进行扫荡拾取的房间
     */
    public void takeAllItems(Room room) {
        if (room.getItems() == null || room.getItems().isEmpty()) {
            System.out.println("这个房间里没有任何物件可以拾取。");
            return;
        }
        // 使用新 List 提取键值以避免遍历 Map 的同时被 takeItem 移除键值引发并发修改异常(ConcurrentModificationException)
        List<String> itemNames = new ArrayList<>(room.getItems().keySet());
        for (String itemName : itemNames) {
            takeItem(itemName, room);
        }
    }

    /**
     * 玩家丢弃单一物品的核心业务逻辑。
     *
     * @param itemName 要丢弃的物品名称
     * @param room 当前所处的房间（作为丢弃物的落脚点）
     */
    public void dropItem(String itemName, Room room) {
        Item item = inventory.remove(itemName);
        if (item == null) {
            System.out.println("你的背包里没有这个物品！");
            return;
        }
        // 丢弃成功，扣减玩家负重并将物品归还至当前房间
        this.currentWeight -= item.getWeight();
        room.addItem(item);
        System.out.println("你从背包丢弃了: [" + itemName + "]");
    }

    /**
     * 玩家一键丢弃背包内全部物品的逻辑实现（drop all）。
     *
     * @param room 当前所处的房间（作为丢弃物的落脚点）
     */
    public void dropAllItems(Room room) {
        if (inventory.isEmpty()) {
            System.out.println("你的随身背包空空如也，没有什么可以丢弃的。");
            return;
        }
        // 使用新 List 提取避免并发修改异常
        List<String> itemNames = new ArrayList<>(inventory.keySet());
        for (String itemName : itemNames) {
            dropItem(itemName, room);
        }
    }

    /**
     * 拼接并格式化输出背包状态与当前玩家的负重详情。
     *
     * @return String 格式化后的背包信息字符串
     */
    /**
     * 拼接并格式化输出背包状态与当前玩家的负重详情。
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "随身背包：目前没有携带任何物品。 (负重: 0/" + getMaxWeight() + "kg)";
        }
        StringBuilder returnString = new StringBuilder("随身背包物件：\n");
        for (String itemName : inventory.keySet()) {
            Item item = inventory.get(itemName);
            returnString.append("  * ")
                    .append(itemName)
                    .append(" (重量: ").append(item.getWeight()).append("kg)\n");
        }
        return returnString.append(">> 当前负重状态: ")
                .append(currentWeight).append("kg / ")
                .append(maxWeight).append("kg").toString();
    }
    /**
     * 永久提升玩家的最大负重能力（用于 Magic Cookie 等道具的增益）。
     *
     * @param bonusWeight 增加的重量数值
     */
    public void increaseMaxWeight(int bonusWeight) {
        this.maxWeight += bonusWeight;
        System.out.println("✨ 感觉到一股力量涌动！你的负重上限提升了 " + bonusWeight + "kg！");
        System.out.println(">> 当前最大负重: " + this.maxWeight + "kg");
    }

    /**
     * 消耗背包中的指定物品。
     *
     * @param itemName 物品名称
     * @return boolean 若背包中有此物品并成功消耗返回 true
     */
    public boolean consumeItem(String itemName) {
        if (inventory.containsKey(itemName)) {
            Item item = inventory.remove(itemName);
            this.currentWeight -= item.getWeight();
            return true;
        }
        return false;
    }

}