package cn.edu.whut.sept.zuul;

import java.util.Stack;

/**
 * 回退命令类。
 * 负责解析并执行玩家的 "back"（后退）指令，利用历史栈结构支持玩家回到上一个走过的房间。
 * 继承自 {@link Command} 抽象基类。
 *
 * @author Zuul开发组
 * @version 1.1
 */
public class BackCommand extends Command {

    /**
     * 执行回退操作逻辑。
     *
     * @param game 当前的游戏总控制器实例
     * @return boolean 始终返回 false，表示游戏应继续运行
     */
    @Override
    public boolean execute(Game game) {
        // 1. 参数校验：回退指令是一元指令，不可带参数
        if (hasSecondWord()) {
            System.out.println("回退指令不需要指定参数！(请直接输入 'back')");
            return false;
        }

        Player player = game.getPlayer();
        Stack<Room> history = player.getRoomHistory();

        // 2. 边界拦截：历史栈为空表示目前尚在出生点，或者是没有其他可退记录
        if (history.isEmpty()) {
            System.out.println("【回退拦截】你已经身处游戏起点，无法再往回退了！");
            return false;
        }

        // 3. 核心流转逻辑：从记录栈弹出上一间房，并变更当前角色位置
        Room previousRoom = history.pop();
        player.setCurrentRoom(previousRoom);

        // 【核心变更点说明】：回退操作本质上也是发生了空间移动，
        // 有可能会触发诸如“将钥匙带回特定房间”的任务，因此退回后同样需要运行一次状态机检测机制。
        game.checkTasks();

        // 4. 展现成功回退后的新场景
        System.out.println("<< 成功回退到上一个场景 <<");
        System.out.println(player.getCurrentRoom().getLongDescription());

        return false;
    }
}