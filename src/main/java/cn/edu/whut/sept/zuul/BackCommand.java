package cn.edu.whut.sept.zuul;

import java.util.Stack;

/**
 * 回退命令类.
 */
public class BackCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (hasSecondWord()) {
            System.out.println("回退指令不需要指定参数！(请直接输入 'back')");
            return false;
        }

        Player player = game.getPlayer();
        Stack<Room> history = player.getRoomHistory();

        if (history.isEmpty()) {
            System.out.println("【回退拦截】你已经身处游戏起点，无法再往回退了！");
            return false;
        }

        Room previousRoom = history.pop();
        player.setCurrentRoom(previousRoom);

        // 核心变更点：回退成功后，同样过一遍状态机检测
        game.checkTasks();

        System.out.println("<< 成功回退到上一个场景 <<");
        System.out.println(player.getCurrentRoom().getLongDescription());
        
        return false;
    }
}