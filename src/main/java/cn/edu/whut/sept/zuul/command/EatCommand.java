package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.core.Game;

/**
 * 吃物品命令类。
 * 特殊逻辑：如果吃掉的是 "cookie"，则提升玩家负重上限。
 */
public class EatCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("使用方法: eat [物品名称]");
            return false;
        }

        String itemName = getSecondWord();
        Player player = game.getPlayer();

        if (!player.hasItem(itemName)) {
            System.out.println("错误：你的背包里没有 '" + itemName + "'。");
            return false;
        }

        if (itemName.equals("魔法饼干")) {
            player.consumeItem("魔法饼干");
            player.increaseMaxWeight(20);
            System.out.println("【系统】你吃掉了魔法饼干，负重上限增加了 20kg！");
        }
        else if (itemName.equals("大容量登山包")) {
            player.consumeItem("大容量登山包");
            player.increaseMaxWeight(50);
            System.out.println("【系统】你换上了大容量登山包，负重上限永久提升了 50kg！");
        }
        else {
            System.out.println("这个东西不能吃，也没有直接的使用效果。");
        }

        game.notifyStatusChange();
        return false;
    }
}