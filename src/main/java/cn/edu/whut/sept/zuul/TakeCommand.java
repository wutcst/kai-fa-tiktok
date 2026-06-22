package cn.edu.whut.sept.zuul;

public class TakeCommand extends Command {
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