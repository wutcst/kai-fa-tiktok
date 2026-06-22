package cn.edu.whut.sept.zuul;

/**
 * 移动执行逻辑类.
 * 每次过门成功之前，将原房间存入 Player 历史回退栈中，为高级 Back 功能打下基础.
 *
 * @author 组员A
 * @version 1.0
 */
public class GoCommand extends Command {

    /**
     * 执行移动指令的业务逻辑.
     *
     * @param game 游戏控制主类实例
     * @return 游戏是否结束的信号
     */
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
            System.out.println(nextRoom.getLongDescription());
        }
        return false;
    }
}