package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.TransporterRoom;
import cn.edu.whut.sept.zuul.core.Game;

public class GoCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("去哪里？");
            return false;
        }

        String direction = getSecondWord();
        Player player = game.getPlayer();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("那里没有路！");
        } else {
            player.pushRoomToHistory(player.getCurrentRoom());

            // 【特殊机制检测】：如果是传输房间
            if (nextRoom instanceof TransporterRoom) {
                System.out.println("你踏入了 " + nextRoom.getShortDescription() + "...");
                System.out.println("一阵天旋地转，你感觉空间发生了剧烈的折叠！");

                // 核心逻辑：强制重定向到随机房间
                Room destination = game.getRandomRoom();
                player.setCurrentRoom(destination);
            } else {
                player.setCurrentRoom(nextRoom);
            }

            game.checkTasks();
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
        return false;
    }
}