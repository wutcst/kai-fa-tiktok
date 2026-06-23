package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.core.Game;

/**
 * 查看命令类.
 * 允许玩家随时查看当前房间的详细信息，包括描述、出口以及物品列表。
 */
public class LookCommand extends Command {
    @Override
    public boolean execute(Game game) {
        System.out.println("--- 环顾四周 ---");
        boolean hasLight = game.getPlayer().hasItem("战术手电");
        System.out.println(game.getPlayer().getCurrentRoom().getLongDescription(hasLight));
        return false;
    }
}