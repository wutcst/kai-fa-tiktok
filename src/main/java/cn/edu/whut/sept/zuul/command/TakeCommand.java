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

        // 1. 处理“take all”的情况
        if (target.equalsIgnoreCase("all")) {
            player.takeAllItems(currentRoom);
            // 批量拾取后，我们统一打印一次最终状态
            System.out.println(">> 已尝试拾取房间内的所有物品。");
        }
        // 2. 处理拾取特定物品的情况
        else {
            // 【核心修改点】：使用 if 判断 takeItem 的返回值
            if (player.takeItem(target, currentRoom)) {
                // 只有返回 true 时（即既有该物品且没超重），才打印成功信息
                System.out.println(" [确认] 你已成功将 " + target + " 放入背包。");
            } else {
                // 如果返回 false，说明失败了（具体原因已经在 Player 类中通过 System.out 输出了）
                // 此时直接返回，不执行后面的负重状态显示
                return false;
            }
        }

        System.out.println(" >> 当前有效总负重: " + player.getCurrentWeight() + "/" + player.getMaxWeight() + "kg");
        return false;
    }
}