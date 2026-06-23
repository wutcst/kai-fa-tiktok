package cn.edu.whut.sept.zuul.model;

import cn.edu.whut.sept.zuul.core.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransporterRoom extends Room {
    public TransporterRoom(String description) {
        super(description);
    }

    /**
     * 根据玩家是否拥有指南针返回目的地
     */
    public Room getDestination(Game game) {
        List<Room> allRooms = new ArrayList<>(game.getAllRooms());
        allRooms.remove(this); // 从备选名单中移除当前传送门房间
        Random rand = new Random();
        if (game.getPlayer().hasItem("量子指南针")) {
            System.out.println("【量子指南针】侦测到稳定的空间锚点：大学主入口。");
            return allRooms.stream()
                    .filter(r -> r.getShortDescription().equals("大学主入口"))
                    .findFirst()
                    .orElse(allRooms.get(0));
        } else {
            // 真正随机传送
            return allRooms.get(rand.nextInt(allRooms.size()));
        }
    }
}