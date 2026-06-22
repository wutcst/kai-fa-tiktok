package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.core.Game;

/**
 * 物品清单命令类.
 * 同时打印出当前房间内所有物件及总重量，以及玩家随身携带的所有物件及总重量。
 */
public class ItemsCommand extends Command {

    @Override
    public boolean execute(Game game) {
        if (hasSecondWord()) {
            System.out.println("items 指令不需要额外参数。");
            return false;
        }

        Player player = game.getPlayer();
        Room room = player.getCurrentRoom();

        System.out.println("\n========== 物品与负重详单 ==========");

        // 1. 打印房间内的物品及其总重
        System.out.println("[当前房间]");
        System.out.println(room.getItemString());

        // 2. 打印玩家背包内的物品及其总重
        System.out.println("\n[个人背包]");
        System.out.println(player.getInventoryString());

        System.out.println("====================================\n");

        return false;
    }
}