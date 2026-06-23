package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.core.Game;
import cn.edu.whut.sept.zuul.model.*;
import java.util.Random;

public class GoCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("去哪里？");
            return false;
        }

        String direction = getSecondWord();
        Player player = game.getPlayer();
        Room current = player.getCurrentRoom();
        Room nextRoom = current.getExit(direction);

        if (nextRoom == null) {
            System.out.println("那里没有路！");
            return false;
        }

        // 1. [特性] 能源反应堆准入检测
        if (nextRoom.getShortDescription().equals("能源反应堆") && !player.hasItem("辐射盾牌")) {
            System.out.println("【警告】那里辐射太强！你需要 [辐射盾牌] 才能进入。");
            return false;
        }

        // 2. [特性] 迷雾园林迷失逻辑
        if (nextRoom.getShortDescription().equals("迷雾园林") && !player.hasItem("量子指南针"))  {
            if (new Random().nextDouble() < 0.5) {
                System.out.println("你在园林的迷雾中迷失了方向，不知不觉走回了原点...");
                return false;
            }
        }

        // 执行移动
        player.pushRoomToHistory(current);

        if (nextRoom instanceof TransporterRoom) {
            Room dest = ((TransporterRoom) nextRoom).getDestination(game);
            player.setCurrentRoom(dest);
        } else {
            player.setCurrentRoom(nextRoom);
        }

        // 重点：增加胜利条件检测
        Room target = player.getCurrentRoom();
        if (target.getShortDescription().equals("能源反应堆")) {
            game.triggerVictory(); // 触发胜利
        }

        game.checkTasks();
        boolean hasLight = player.hasItem("战术手电");
        System.out.println(player.getCurrentRoom().getLongDescription(hasLight));
        return false;
    }
}