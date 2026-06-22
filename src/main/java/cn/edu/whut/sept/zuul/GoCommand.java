package cn.edu.whut.sept.zuul;

/**
 * 移动执行逻辑类.
 * 每次过门成功之前，将原房间存入 Player 历史回退栈中，为高级 Back 功能打下基础.
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
            // 在踏入新房间前，将当前房间塞入历史记录栈中
            player.pushRoomToHistory(currentRoom);
            player.setCurrentRoom(nextRoom);
            System.out.println(nextRoom.getLongDescription());
        }
        return false;
    }
}