package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.core.Game;

/**
 * 丢弃物品命令类.
 * 负责处理玩家在游戏中丢弃(drop)物品的操作逻辑。
 * 支持丢弃特定物品或使用 "all" 参数丢弃背包中的全部物品。
 *
 * @author 你的名字/组员B
 * @version 1.0
 */
public class DropCommand extends Command {

    /**
     * 执行丢弃命令的具体逻辑.
     *
     * @param game 当前游戏主干实例，用于获取玩家(Player)等上下文信息
     * @return boolean 返回 false 表示操作完成后游戏继续运行，不退出
     */
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("你想丢弃什么？请指定物品名称 (例如: drop cookie 或 drop all)");
            return false;
        }

        String target = getSecondWord();
        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        // 判断是丢弃特定物品还是全部丢弃
        if (target.equalsIgnoreCase("all")) {
            player.dropAllItems(currentRoom);
        } else {
            player.dropItem(target, currentRoom);
        }

        // 打印更新后的背包状态
        System.out.println(player.getInventoryString());
        return false;
    }
}