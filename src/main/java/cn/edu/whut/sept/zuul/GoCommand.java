package cn.edu.whut.sept.zuul;

/**
 * 移动执行逻辑类。
 * 负责解析并执行玩家向特定方向移动（go指令）的操作。
 * 继承自 {@link Command} 抽象基类。
 *
 * @author Zuul开发组
 * @version 1.1
 */
public class GoCommand extends Command {

    /**
     * 执行移动命令的核心逻辑。
     *
     * @param game 当前的游戏总控制器实例
     * @return boolean 始终返回 false，表示游戏应继续运行而不退出
     */
    @Override
    public boolean execute(Game game) {
        // 1. 基础校验：如果没有第二个单词（即没有指定方向）
        if (!hasSecondWord()) {
            System.out.println("去哪里？请指定方向 (例如: go east)");
            return false;
        }

        String direction = getSecondWord();
        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        // 2. 逻辑校验：目标方向是否有出口
        if (nextRoom == null) {
            System.out.println("走不通，那里没有出口！");
        } else {
            // 3. 核心流转逻辑：将当前房间压入历史记录栈，随后改变玩家所在房间
            player.pushRoomToHistory(currentRoom);
            player.setCurrentRoom(nextRoom);

            // 【核心变更点说明】：在打印新的房间描述之前，先运行状态机任务检测。
            // 这是为了确保如果玩家触发了剧情（如暗门开启），新的环境描述能够立刻在控制台生效。
            game.checkTasks();

            // 4. 打印到达新房间后的详细描述
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
        return false;
    }
}