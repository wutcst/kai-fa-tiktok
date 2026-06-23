package cn.edu.whut.sept.zuul.model;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private Room currentRoom;
    private int maxWeight;
    private final HashMap<String, Item> inventory;
    private final Stack<Room> roomHistory;

    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.inventory = new HashMap<>();
        this.roomHistory = new Stack<>();
    }

    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public int getMaxWeight() { return maxWeight; }
    public void setMaxWeight(int maxWeight) { this.maxWeight = maxWeight; }
    private ActionFeedback feedback;
    public void setFeedback(ActionFeedback f) { this.feedback = f; }

    public interface ActionFeedback {
        void onAlert(String message);
    }

    /**
     * 核心逻辑扩展：如果有重力背带(harness)，其他物品重量计算减少20%
     */
    public int getCurrentWeight() {
        double total = 0;
        boolean hasHarness = inventory.containsKey("重力背带");
        for (Item item : inventory.values()) {
            if (hasHarness && !item.getName().equals("重力背带")) {
                total += item.getWeight() * 0.8;
            } else {
                total += item.getWeight();
            }
        }
        return (int) Math.round(total);
    }

    public Stack<Room> getRoomHistory() { return roomHistory; }
    public void pushRoomToHistory(Room room) { roomHistory.push(room); }

    public boolean hasItem(String itemName) {
        return inventory.containsKey(itemName);
    }

    public boolean takeItem(String itemName, Room room) {
        Item item = room.getItems().get(itemName);
        if (item == null) return false;

        int potentialWeight = getCurrentWeight();
        if (inventory.containsKey("重力背带") && !itemName.equals("重力背带")) {
            potentialWeight += (int)Math.round(item.getWeight() * 0.8);
        } else {
            potentialWeight += item.getWeight();
        }

        if (potentialWeight > this.maxWeight) {
            String alertMsg = "【负重拦截】你拿不动 " + itemName + " 了！(当前负重: " + getCurrentWeight() + "kg)";
            System.out.println(alertMsg);
            if (this.feedback != null) feedback.onAlert(alertMsg); // 触发反馈
            return false;
        }

        Item removedItem = room.removeItem(itemName);
        inventory.put(removedItem.getName(), removedItem);
        return true;
    }

    public void dropItem(String itemName, Room room) {
        Item item = inventory.remove(itemName);
        if (item != null) {
            room.addItem(item);
            System.out.println("你丢弃了 " + itemName);
        }
    }
    /**
     * 【补全方法】批量拾取房间内所有物品
     */
    public void takeAllItems(Room room) {
        if (room.getItems() == null || room.getItems().isEmpty()) {
            System.out.println("这个房间里没有任何物件可以拾取。");
            return;
        }
        // 使用新 List 包装 keySet 以避免 ConcurrentModificationException
        java.util.List<String> itemNames = new java.util.ArrayList<>(room.getItems().keySet());
        for (String itemName : itemNames) {
            takeItem(itemName, room);
        }
    }

    /**
     * 【补全方法】一键丢弃背包内全部物品
     */
    public void dropAllItems(Room room) {
        if (inventory.isEmpty()) {
            System.out.println("你的随身背包空空如也，没有什么可以丢弃的。");
            return;
        }
        // 使用新 List 包装 keySet 避免在遍历时删除元素导致的并发修改异常
        java.util.List<String> itemNames = new java.util.ArrayList<>(inventory.keySet());
        for (String itemName : itemNames) {
            dropItem(itemName, room);
        }
    }
    public String getInventoryString() {
        if (inventory.isEmpty()) return "背包空空如也。 (当前负重: 0/" + maxWeight + "公斤)";
        StringBuilder sb = new StringBuilder("随身背包物品：\n");
        for (Item item : inventory.values()) {
            sb.append(" - ").append(item.getName()).append(" (").append(item.getWeight()).append("公斤)\n");
        }
        sb.append(">> 当前有效总负重: ").append(getCurrentWeight()).append(" / ").append(maxWeight).append("公斤");
        return sb.toString();
    }

    public HashMap<String, Item> getInventory() { return inventory; }
    public void increaseMaxWeight(int bonus) { this.maxWeight += bonus; }

    public boolean consumeItem(String itemName) {
        return inventory.remove(itemName) != null;
    }
}