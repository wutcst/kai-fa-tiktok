package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.core.Game;

/**
 * 拾取物品命令类.
 * 负责处理玩家在游戏中拾取(take)物品的操作逻辑。
 * 支持拾取特定物品或拾取房间内的全部(all)物品，自带重量校验。
 *
 * @author 你的名字/组员B
 * @version 1.0
 */
public class TakeCommand extends Command {

    /**
     * 执行拾取命令的具体逻辑.
     *
     * @param game 当前游戏主对象实例，用于获取玩家等上下文状态
     * @return boolean 始终返回 false，表示操作完成后游戏应继续运行
     */
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("你想拿走什么？请指定物品名称 (例如: take cookie 或 take all)");
            return false;
        }

        String target = getSecondWord();
        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        // 判断是拿走特定物品还是全部拿走
        if (target.equalsIgnoreCase("all")) {
            player.takeAllItems(currentRoom);
        } else {
            player.takeItem(target, currentRoom);
        }

        // 每次操作完，打印当前背包与负重状态
        System.out.println(player.getInventoryString());
        return false;
    }
}