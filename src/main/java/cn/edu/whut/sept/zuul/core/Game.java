package cn.edu.whut.sept.zuul.core;
import cn.edu.whut.sept.zuul.command.Command;
import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.TransporterRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private final List<Room> allRooms;
    private final Random random;
    /**
     * 初始化游戏控制器。
     * 生成玩家、配置解析器，并构建所有的游戏房间及连接路线。
     */
    public Game() {
        allRooms = new ArrayList<>();
        random = new Random();
        player = new Player("探险者", 50);
        parser = new Parser();
        createRooms();
    }

    /**
     * 构建所有基础房间、初始化隐藏房间、投放测试物品，并连接初始地图。
     * 开局在多个房间生成饼干（概率分布） 如果运气太差一个都没刷出来，在出生点放一个
     */
    private void createRooms() {
        Room outside, theater, pub, lab, office, storage;
        TransporterRoom portal; // 特殊传输房间

        // 1. 实例化场景
        outside = new Room("大学主入口");
        theater = new Room("阶梯教室");
        pub = new Room("校园酒吧");
        lab = new Room("计算机实验室");
        office = new Room("管理办公室");
        storage = new Room("黑暗的储藏室");
        // 创建特殊传输房间
        portal = new TransporterRoom("名为‘虚空之眼’的神秘传送门");

        // 将所有房间加入列表管理
        allRooms.add(outside); allRooms.add(theater); allRooms.add(pub);
        allRooms.add(lab); allRooms.add(office); allRooms.add(storage);
        allRooms.add(portal);

        // 2. 放置魔法饼干（随机选择一个房间放置，且不放在传送室）
        Room cookieRoom = allRooms.get(random.nextInt(allRooms.size() - 1));
        cookieRoom.addItem(new Item("cookie", "散发着微光的魔法饼干", 1));
        System.out.println("[系统调试] 魔法饼干已随机出现在: " + cookieRoom.getShortDescription());

        // 3. 其他常规物品放置
        office.addItem(new Item("key", "机房黄铜钥匙", 1));
        pub.addItem(new Item("wine", "一瓶陈年红酒", 3));

        // 4. 设置出口
        outside.setExit("east", theater); outside.setExit("south", lab);
        theater.setExit("west", outside);
        pub.setExit("east", outside);
        lab.setExit("north", outside); lab.setExit("east", office);
        office.setExit("west", lab); office.setExit("south", storage);
        storage.setExit("north", office);

        // 任何地方都可以进入传送门
        storage.setExit("down", portal);
        portal.setExit("up", storage); // 虽然传送门会随机传送，但仍保留一个出口逻辑

        player.setCurrentRoom(outside);

        for (Room room : allRooms) {
            if (!(room instanceof TransporterRoom) && random.nextDouble() < 0.3) {
                room.addItem(new Item("cookie", "散发着甜香的魔法饼干", 1));
            }
        }
        if (!player.getCurrentRoom().getItems().containsKey("cookie")) {
            player.getCurrentRoom().addItem(new Item("cookie", "新手福利饼干", 1));
        }
    }
    /**
     * 获取地图上随机一个普通房间（用于传送）。
     */
    public Room getRandomRoom() {
        return allRooms.get(random.nextInt(allRooms.size()));
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