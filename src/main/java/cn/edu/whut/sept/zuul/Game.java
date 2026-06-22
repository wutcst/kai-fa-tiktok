package cn.edu.whut.sept.zuul;

/**
 * 游戏业务总控制器.
 * 将原有的 currentRoom 解耦交由新增的 Player 类深度接管.
 *
 * @author 组员A
 * @version 1.0
 */
public class Game {
    private final Parser parser;
    private final Player player;

    /**
     * 游戏主类的构造函数，用于初始化游戏数据.
     */
    public Game() {
        player = new Player("玩家A", 50);
        parser = new Parser();
        createRooms();
    }

    /**
     * 内部私有方法：构建房间、设置出口与放置初始物品.
     */
    private void createRooms() {
        Room outside;
        Room theater;
        Room pub;
        Room lab;
        Room office;

        outside = new Room("大学主入口");
        theater = new Room("阶梯教室");
        pub = new Room("校园酒吧");
        lab = new Room("计算机实验室");
        office = new Room("机房管理办公室");

        outside.addItem(new Item("cookie", "魔法饼干", 2));
        lab.addItem(new Item("book", "算法导论", 12));
        lab.addItem(new Item("computer", "旧款笔记本电脑", 8));
        office.addItem(new Item("key", "机房黄铜钥匙", 1));

        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        theater.setExit("west", outside);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        office.setExit("west", lab);

        player.setCurrentRoom(outside);
    }

    /**
     * 游戏运行的主循环方法.
     */
    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            if (command == null) {
                System.out.println("我不明白这个输入指令...");
            } else {
                finished = command.execute(this);
            }
        }
        System.out.println("感谢参与，再见！");
    }

    /**
     * 内部方法：打印欢迎信息.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("欢迎来到《World of Zuul》扩展重构版本!");
        System.out.println("输入 'help' 获取所有可运行命令.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    /**
     * 获取当前游戏内活跃的玩家对象.
     *
     * @return 玩家实体对象
     */
    public Player getPlayer() {
        return player;
    }
}