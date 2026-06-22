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
            System.out.println("你想吃什么？");
            return false;
        }

        String itemName = getSecondWord();
        Player player = game.getPlayer();

        // 1. 检查背包里是否有这个东西
        if (!player.hasItem(itemName)) {
            System.out.println("你的背包里没有 [" + itemName + "]。");
            return false;
        }

        // 2. 执行吃掉的逻辑
        if (itemName.equalsIgnoreCase("cookie")) {
            System.out.println("你吃掉了魔法饼干，味道好极了！");
            player.consumeItem("cookie"); // 从背包移除
            player.increaseMaxWeight(20);  // 永久提升 20kg 负重
        } else {
            System.out.println("你吃掉了 " + itemName + "，但似乎除了饱腹感外没有任何变化。");
            player.consumeItem(itemName);
        }

        return false;
    }
}