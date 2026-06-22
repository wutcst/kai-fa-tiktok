package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.core.Game;

/**
 * 查看命令类.
 * 允许玩家随时查看当前房间的详细信息，包括描述、出口以及物品列表。
 */
public class LookCommand extends Command {

    @Override
    public boolean execute(Game game) {
        if (hasSecondWord()) {
            System.out.println("look 指令不需要额外参数。请直接输入 'look'。");
            return false;
        }

        System.out.println("--- 环顾四周 ---");
        // 复用 Room 类中已经封装好的 getLongDescription 方法
        // 该方法已包含：位置描述、可见出口、房间物品明细（含描述与重量）
        System.out.println(game.getPlayer().getCurrentRoom().getLongDescription());

        return false;
    }
}