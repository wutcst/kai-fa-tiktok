package cn.edu.whut.sept.zuul;

/**
 * 游戏业务总控制器。
 * 负责游戏全局初始化、主循环维护以及指令派发。
 * 本版本引入了事件驱动状态机，用于管理特定物品运送任务并触发剧情。
 *
 * @author Zuul开发组
 * @version 1.1
 */
public class Game {
    /** 负责解析用户键盘输入的解析器 */
    private final Parser parser;
    /** 游戏内的玩家单例实体 */
    private final Player player;

    // ----- 关键场景引用：为了供状态机直接修改房间状态而声明为全局实例变量 -----
    /** 计算机实验室：触发隐藏任务的关键场景 */
    private Room lab;
    /** 隐藏场景：完成任务后才会开启的超级核心机房 */
    private Room secretRoom;

    // ----- 剧情控制状态位 -----
    /** 任务状态机标志：钥匙运送解密任务是否已经达成 */
    private boolean keyTaskCompleted = false;

    /**
     * 初始化游戏控制器。
     * 生成玩家、配置解析器，并构建所有的游戏房间及连接路线。
     */
    public Game() {
        player = new Player("玩家A", 50);
        parser = new Parser();
        createRooms();
    }

    /**
     * 构建所有基础房间、初始化隐藏房间、投放测试物品，并连接初始地图。
     */
    private void createRooms() {
        Room outside, theater, pub, office;

        // 1. 实例化场景房间
        outside = new Room("大学主入口");
        theater = new Room("阶梯教室");
        pub = new Room("校园酒吧");
        lab = new Room("计算机实验室");
        office = new Room("机房管理办公室");

        // 2. 初始化隐藏新场景（暂时游离于地图之外，没有可用连线进入）
        secretRoom = new Room("地下秘密核心机房（满墙闪烁着蓝光的超级计算机阵列）");

        // 3. 在特定场景放置初始测试物品
        outside.addItem(new Item("cookie", "魔法饼干", 2));
        lab.addItem(new Item("book", "算法导论", 12));
        lab.addItem(new Item("computer", "旧款笔记本电脑", 8));
        office.addItem(new Item("key", "机房黄铜钥匙", 1)); // 核心任务物品

        // 4. 建立初始常规场景的东南西北连接
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        theater.setExit("west", outside);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        office.setExit("west", lab);

        // 5. 设置玩家出生点
        player.setCurrentRoom(outside);
    }

    /**
     * 事件驱动与剧情状态机逻辑检测引擎。
     * 【调用时机说明】：每次玩家改变空间位置（移动/回退）时触发调用。
     * 检测多重前置条件是否满足，若是，则推动并改变游戏的世界线。
     */
    public void checkTasks() {
        // 触发条件验证：任务未完成 AND 玩家正处于实验室 AND 玩家背包里拿着任务要求的 'key' 物品
        if (!keyTaskCompleted && player.getCurrentRoom() == lab && player.hasItem("key")) {

            // 状态机翻转，避免重复触发
            keyTaskCompleted = true;

            // 【特效1】 改变场景环境：重置实验室的房间描述
            lab.setDescription("计算机实验室（中央旧服务器阵列处由于暗门开启，地面露出了一个向下延申的洞口）");

            // 【特效2】 建立新通路：为实验室动态挂载向下（down）通往秘密机房的出口路线
            lab.setExit("down", secretRoom);

            // 【特效3】 打印史诗级任务达成提示
            System.out.println("\n=================================================");
            System.out.println("✨【 任务完成：隐藏的世界线已被开启！ 】✨");
            System.out.println("当你携带 [key] 踏入实验室时，角落里那台老旧的服务器突然发出轰鸣声。");
            System.out.println("你走过去用黄铜钥匙插入隐蔽的锁孔，咔哒一声，地面一块合金钢板缓缓滑开，");
            System.out.println("露出了一个全新的向下出口：[down]！");
            System.out.println("=================================================\n");
        }
    }

    /**
     * 游戏主循环。
     * 会持续捕获输入命令直至玩家触发退出条件。
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
     * 打印游戏启动时的欢迎横幅与当前房间详细描述。
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("欢迎来到《World of Zuul》扩展重构版本!");
        System.out.println("输入 'help' 获取所有可运行命令.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    /**
     * 获取当前游戏内的主角（玩家）实例。
     *
     * @return Player 玩家实体对象
     */
    public Player getPlayer() {
        return player;
    }
}