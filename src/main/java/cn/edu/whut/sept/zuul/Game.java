package cn.edu.whut.sept.zuul;

/**
 * 游戏业务总控制器.
 * 引入了简单的事件驱动状态机，管理特定物品运送任务。
 */
public class Game {
    private final Parser parser;
    private final Player player;

    // 保存房间引用以供状态机改变其状态
    private Room lab;
    private Room secretRoom;
    
    // 任务状态机标志：钥匙运送解密任务是否完成
    private boolean keyTaskCompleted = false;

    public Game() {
        player = new Player("玩家A", 50); 
        parser = new Parser();
        createRooms();
    }

    private void createRooms() {
        Room outside, theater, pub, office;

        outside = new Room("大学主入口");
        theater = new Room("阶梯教室");
        pub = new Room("校园酒吧");
        lab = new Room("计算机实验室"); // 提取为实例变量
        office = new Room("机房管理办公室");
        
        // 开启任务后的隐藏新场景
        secretRoom = new Room("地下秘密核心机房（满墙闪烁着蓝光的超级计算机阵列）");

        // 放置初始测试物品
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
     * 核心新增：事件驱动与状态机逻辑检测引擎
     * 每次玩家移动或回退房间时触发调用
     */
    public void checkTasks() {
        // 条件状态机：如果任务未完成 且 玩家带着钥匙 且 玩家走进了计算机实验室
        if (!keyTaskCompleted && player.getCurrentRoom() == lab && player.hasItem("key")) {
            keyTaskCompleted = true; // 状态扭转
            
            // 1. 改变房间描述
            lab.setDescription("计算机实验室（中央旧服务器阵列处由于暗门开启，地面露出了一个向下延申的洞口）");
            
            // 2. 改变房间可用出口：动态增加向下（down）通往秘密机房的出口
            lab.setExit("down", secretRoom);
            
            // 3. 改变游戏进程：打印史诗级任务达成特效提示
            System.out.println("\n=================================================");
            System.out.println("✨【 任务完成：隐藏的世界线已被开启！ 】✨");
            System.out.println("当你携带 [key] 踏入实验室时，角落里那台老旧的服务器突然发出轰鸣声。");
            System.out.println("你走过去用黄铜钥匙插入隐蔽的锁孔，咔哒一声，地面一块合金钢板缓缓滑开，");
            System.out.println("露出了一个全新的向下出口：[down]！");
            System.out.println("=================================================\n");
        }
    }

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

    private void printWelcome() {
        System.out.println();
        System.out.println("欢迎来到《World of Zuul》扩展重构版本!");
        System.out.println("输入 'help' 获取所有可运行命令.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    public Player getPlayer() {
        return player;
    }
}