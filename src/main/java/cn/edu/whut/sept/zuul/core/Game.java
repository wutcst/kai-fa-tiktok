package cn.edu.whut.sept.zuul.core;

import cn.edu.whut.sept.zuul.command.Command;
import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.TransporterRoom;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
/**
 * 游戏业务总控制器。
 * 负责游戏全局初始化、主循环维护以及指令派发。
 * 本版本引入了事件驱动状态机，用于管理特定物品运送任务并触发剧情。
 *
 * @author Zuul开发组
 * @version 1.1
 */
public class Game {
    /**
     * 负责解析用户键盘输入的解析器
     */
    private final Parser parser;
    /**
     * 游戏内的玩家单例实体
     */
    private final Player player;

    // ----- 关键场景引用：为了供状态机直接修改房间状态而声明为全局实例变量 -----
    /**
     * 计算机实验室：触发隐藏任务的关键场景
     */
    private Room lab;
    /**
     * 隐藏场景：完成任务后才会开启的超级核心机房
     */
    private Room secretRoom;

    // ----- 剧情控制状态位 -----
    /**
     * 任务状态机标志：钥匙运送解密任务是否已经达成
     */
    private boolean keyTaskCompleted = false;

    private StoryListener storyListener;

    private final List<Room> allRooms;
    private final Random random = new Random();

    public interface StoryListener {
        void onStoryEvent(String title, String message);
    }

    // ----- GUI 监听器与标准输出重定向机制 -----
    public interface GameOutputListener {
        void onMessage(String msg);
    }

    public interface GameStatusListener {
        void onStatusChange();
    }

    public void setStoryListener(final StoryListener listener) {
        this.storyListener = listener;
    }

    private final List<GameOutputListener> outputListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final List<GameStatusListener> statusListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final java.io.PrintStream originalOut = System.out;

    /**
     * 初始化游戏控制器。
     * 生成玩家、配置解析器，并构建所有的游戏房间及连接路线。
     */
    public Game() {
        allRooms = new ArrayList<>();
        player = new Player("探险者", 50);
        parser = new Parser();
        createRooms();
    }

    public void addOutputListener(GameOutputListener listener) {
        outputListeners.add(listener);
    }

    public void addStatusListener(GameStatusListener listener) {
        statusListeners.add(listener);
    }

    public void notifyStatusChange() {
        for (GameStatusListener listener : statusListeners) {
            listener.onStatusChange();
        }
    }

    public void setupRedirectedOutput() {
        try {
            // 使用明确的 UTF_8 字符集创建 PrintStream
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) {
                    // 不建议单字节写入，但为了兼容性，转存到原始输出
                    originalOut.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) {
                    originalOut.write(b, off, len);
                    // 关键点：使用 UTF_8 将字节数组整体解码为字符串
                    String text = new String(b, off, len, StandardCharsets.UTF_8);
                    notifyOutput(text);
                }
            }, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyOutput(String msg) {
        for (GameOutputListener listener : outputListeners) {
            listener.onMessage(msg);
        }
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * 构建所有基础房间、初始化隐藏房间、投放测试物品，并连接初始地图。
     * 开局在多个房间生成饼干（概率分布） 如果运气太差一个都没刷出来，在出生点放一个
     */
    private void createRooms() {
        // --- 1. 实例化原有房间 ---
        Room outside = new Room("大学主入口"); outside.setImageName("outside.png");
        Room theater = new Room("阶梯教室"); theater.setImageName("theater.png");
        Room pub = new Room("校园酒吧"); pub.setImageName("pub.png");
        lab = new Room("计算机实验室"); lab.setImageName("lab.png");
        secretRoom = new Room("地下核心机房"); secretRoom.setImageName("secret.png");
        Room office = new Room("管理办公室"); office.setImageName("office.png");
        Room storage = new Room("黑暗的储藏室"); storage.setImageName("storage.png");
        TransporterRoom portal = new TransporterRoom("虚空之眼"); portal.setImageName("portal.png");

        // --- 2. 实例化【新增】房间 ---
        Room garden = new Room("迷雾园林"); garden.setImageName("mistygar.png");
        Room library = new Room("自动化图书馆"); library.setImageName("autolib.png");
        Room reactor = new Room("能源反应堆"); reactor.setImageName("energy.png");
        Room sewers = new Room("废弃的下水道"); sewers.setImageName("sewer.png");

        // 设置特殊属性
        sewers.setDark(true); // 下水道是黑暗的
        storage.setDark(true);

        // --- 3. 建立连接 ---
        outside.setExit("north", garden);
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        garden.setExit("south", outside);
        theater.setExit("west", outside);
        theater.setExit("east", library);
        library.setExit("west", theater);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        office.setExit("west", lab);
        office.setExit("south", storage);
        storage.setExit("north", office);
        storage.setExit("west", sewers);
        storage.setExit("down", portal);
        sewers.setExit("east", storage);
        secretRoom.setExit("south", reactor);
        reactor.setExit("north", secretRoom);
        secretRoom.setExit("up", outside);
        pub.setExit("east", outside);
        // --- 4. 投放【新增】道具 ---
        garden.addItem(new Item("量子指南针", "定位空间异常", 1));
        office.addItem(new Item("战术手电", "照亮黑暗区域", 2));
        storage.addItem(new Item("重力背带", "使背负变轻", 2));
        library.addItem(new Item("大容量登山包", "大幅提升负重限制", 3));
        office.addItem(new Item("黄铜钥匙", "一把古旧的钥匙", 1));
        pub.addItem(new Item("辐射盾牌", "进入反应堆的凭证", 5));

        // 也可以给某些房间加点饼干
        theater.addItem(new Item("魔法饼干", "吃了可以增加负重", 1));

        allRooms.addAll(Arrays.asList(outside, theater, pub, lab, secretRoom, office, storage, portal, garden, library, reactor, sewers));
        player.setCurrentRoom(outside);
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
        // 判断条件改为中文名 "黄铜钥匙"
        if (!keyTaskCompleted && player.getCurrentRoom() == lab && player.hasItem("黄铜钥匙")) {
            keyTaskCompleted = true;
            lab.setDescription("计算机实验室（中央服务器处由于暗门开启，露出了一个向下的洞口）");
            lab.setExit("down", secretRoom);

            String title = "✨ 发现隐藏区域";
            String msg = "当你携带 [黄铜钥匙] 踏入实验室时，老旧服务器发出轰鸣...\n"
                    + "地面钢板缓缓滑开，露出了一个全新的向下出口：[向下/down]！";

            System.out.println("\n" + title + "\n" + msg);
            if (storyListener != null) {
                storyListener.onStoryEvent(title, msg);
            }
        }
        notifyStatusChange();
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
    /**
     * 【新增方法】获取地图上所有房间的列表。
     * 供 TransporterRoom 计算随机传送目的地。
     *
     * @return 包含所有房间的 List
     */
    public List<Room> getAllRooms() {
        return allRooms;
    }
    /**
     * 处理单条指令字符串并执行。
     * 用于 GUI 按钮或文本框触发。
     *
     * @param inputLine 完整的指令字符串（如 "go north"）
     */
    public void executeCommand(final String inputLine) {
        System.out.println("\n----------------------------------------");

        Command command = parser.getCommand(inputLine);
        if (command == null) {
            System.out.println(" 无法识别的指令: " + inputLine);
        } else {
            command.execute(this);
        }
        notifyStatusChange();
    }

    public void triggerVictory() {
        String title = " 任务达成：文明之光";
        String msg = "你带着[辐射盾牌]成功进入了核心反应堆区域。\n"
                + "通过终端指令，你重新启动了大学的能源中枢，整座校园瞬间灯火通明！\n\n"
                + "恭喜你，你完成了最终任务，成为了校园的英雄！";

        System.out.println("\n" + title + "\n" + msg);
        if (storyListener != null) {
            storyListener.onStoryEvent(title, msg);
        }
    }
}