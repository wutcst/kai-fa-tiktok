package cn.edu.whut.sept.zuul;

public class DropCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("你想丢弃什么？请指定物品名称 (例如: drop cookie 或 drop all)");
            return false;
        }

        String target = getSecondWord();
        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        // 判断是丢弃特定物品还是全部丢弃
        if (target.equalsIgnoreCase("all")) {
            player.dropAllItems(currentRoom);
        } else {
            player.dropItem(target, currentRoom);
        }

        // 打印更新后的背包状态
        System.out.println(player.getInventoryString());
        return false;
    }
}