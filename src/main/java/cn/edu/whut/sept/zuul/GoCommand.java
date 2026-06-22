package cn.edu.whut.sept.zuul;

/**
 * 移动执行逻辑类.
 */
public class GoCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("去哪里？请指定方向 (例如: go east)");
            return false;
        }

        String direction = getSecondWord();
        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("走不通，那里没有出口！");
        } else {
            player.pushRoomToHistory(currentRoom);
            player.setCurrentRoom(nextRoom);
            
            // 核心变更点：在打印房间描述之前，先运行状态机任务检测
            game.checkTasks();
            
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
        return false;
    }
}