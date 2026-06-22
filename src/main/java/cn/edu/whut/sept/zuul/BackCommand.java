package cn.edu.whut.sept.zuul;

import java.util.Stack;

/**
 * 回退命令类.
 * 允许玩家回退到上一个房间，通过重复输入可连续逐层回退至游戏起点。
 */
public class BackCommand extends Command {
    @Override
    public boolean execute(Game game) {
        // 按照 Zuul 框架惯例，检查是否有不必要的第二参数
        if (hasSecondWord()) {
            System.out.println("回退指令不需要指定参数！(请直接输入 'back')");
            return false;
        }

        Player player = game.getPlayer();
        Stack<Room> history = player.getRoomHistory();

        // 高级机制核心拦截：检查历史记录栈是否为空（是否已经退无可退）
        if (history.isEmpty()) {
            System.out.println("【回退拦截】你已经身处游戏起点（最初的房间），无法再往回退了！");
            return false;
        }

        // 从栈顶弹出最近一次停留的房间
        Room previousRoom = history.pop();
        
        // 将玩家当前所在房间修改为回退后的房间
        player.setCurrentRoom(previousRoom);

        // 打印回退成功提示及新房间的完整描述
        System.out.println("<< 成功回退到上一个场景 <<");
        System.out.println(previousRoom.getLongDescription());
        
        return false;
    }
}