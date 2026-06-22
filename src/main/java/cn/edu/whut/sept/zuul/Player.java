package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * 独立玩家实体类.
 * 负责托管背包逻辑、重量计算、以及多层 back 回退历史记录栈.
 * * @author 组员B
 * @version 1.0
 */
public class Player {
    private final String name;
    private Room currentRoom;
    private int maxWeight;
    private int currentWeight;
    private final HashMap<String, Item> inventory;
    private final Stack<Room> roomHistory; // 用于多层无限回退

    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.inventory = new HashMap<>();
        this.roomHistory = new Stack<>();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    public void pushRoomToHistory(Room room) {
        roomHistory.push(room);
    }

    /**
     * 玩家拾取物品的核心业务逻辑.
     */
    public boolean takeItem(String itemName, Room room) {
        Item item = room.removeItem(itemName);
        if (item == null) {
            System.out.println("这里没有这个物品！");
            return false;
        }
        if (this.currentWeight + item.getWeight() > this.maxWeight) {
            System.out.println("【重量拦截】物品 [" + itemName + "] 太重了！超出你的负重能力极限。");
            room.addItem(item); // 拦截并放回房间
            return false;
        }
        inventory.put(item.getName(), item);
        this.currentWeight += item.getWeight();
        System.out.println("你成功将 [" + itemName + "] 放入背包。");
        return true;
    }

    /**
     * 核心新增：玩家拾取房间内的全部物品
     */
    public void takeAllItems(Room room) {
        if (room.getItems() == null || room.getItems().isEmpty()) {
            System.out.println("这个房间里没有任何物件可以拾取。");
            return;
        }
        // 必须复制一份Key集合，避免在循环中移除物品引发 ConcurrentModificationException
        List<String> itemNames = new ArrayList<>(room.getItems().keySet());
        for (String itemName : itemNames) {
            takeItem(itemName, room); // 复用单物品拾取逻辑，自带重量拦截
        }
    }

    /**
     * 玩家丢弃物品的核心业务逻辑.
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
     * 核心新增：玩家丢弃背包内的全部物品
     */
    public void dropAllItems(Room room) {
        if (inventory.isEmpty()) {
            System.out.println("你的随身背包空空如也，没有什么可以丢弃的。");
            return;
        }
        // 同样复制一份Key集合防止并发修改异常
        List<String> itemNames = new ArrayList<>(inventory.keySet());
        for (String itemName : itemNames) {
            dropItem(itemName, room); // 复用单物品丢弃逻辑
        }
    }

    /**
     * 拼接背包与当前玩家负重详情.
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "随身背包：当前没有任何物件。";
        }
        StringBuilder returnString = new StringBuilder("随身背包物件：");
        for (String itemName : inventory.keySet()) {
            returnString.append(" ").append(itemName).append("(").append(inventory.get(itemName).getWeight()).append("kg)");
        }
        return returnString + " | 负重状态: " + currentWeight + "kg/" + maxWeight + "kg";
    }
}