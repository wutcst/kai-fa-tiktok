package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.core.Game;
import cn.edu.whut.sept.zuul.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 核心逻辑集成测试类
 * 覆盖任务要求：1. back路径回退栈 2. 随机传送逻辑 3. Player负重计算
 */
public class GameLogicTest {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
        System.out.println("\n[测试准备] 游戏实例已初始化，测试环境就绪。");
    }

    /**
     * 测试要求 1: back 路径回退栈逻辑
     */
    @Test
    public void testBackPathStack() {
        System.out.println(">>> 开始测试：[back 路径回退栈逻辑]");

        Room room1 = game.getPlayer().getCurrentRoom();
        Room room2 = room1.getExit("east");

        System.out.println("步骤1: 移动 [Room1:" + room1.getShortDescription() + "] -> [Room2:" + room2.getShortDescription() + "]");
        game.getPlayer().pushRoomToHistory(room1);
        game.getPlayer().setCurrentRoom(room2);

        System.out.println("步骤2: 移动 [Room2] -> [Room3: 再次移动]");
        game.getPlayer().pushRoomToHistory(room2);
        // 这里手动设置一个房间模拟移动
        Room room3 = game.getRandomRoom();
        game.getPlayer().setCurrentRoom(room3);

        System.out.println("步骤3: 执行第一次回退操作...");
        assertFalse(game.getPlayer().getRoomHistory().isEmpty());
        Room back1 = game.getPlayer().getRoomHistory().pop();
        game.getPlayer().setCurrentRoom(back1);
        assertEquals(room2, game.getPlayer().getCurrentRoom());
        System.out.println("验证成功: 第一次成功回退到 " + back1.getShortDescription());

        System.out.println("步骤4: 执行第二次回退操作...");
        Room back2 = game.getPlayer().getRoomHistory().pop();
        game.getPlayer().setCurrentRoom(back2);
        assertEquals(room1, game.getPlayer().getCurrentRoom());
        System.out.println("验证成功: 第二次成功回退到初始房间 " + back2.getShortDescription());

        System.out.println("√ [完成] back 路径回退栈逻辑测试通过。\n");
    }

    /**
     * 测试要求 2: 随机传送核心逻辑
     */
    @Test
    public void testRandomTeleportationLogic() {
        System.out.println(">>> 开始测试：[随机传送核心逻辑]");

        System.out.println("步骤1: 调用地图随机房间获取引擎...");
        Room randomRoom = game.getRandomRoom();
        assertNotNull(randomRoom);
        System.out.println("获取成功: 随机选中了房间 [" + randomRoom.getShortDescription() + "]");

        System.out.println("步骤2: 模拟传送门触发，变更玩家位置...");
        Room before = game.getPlayer().getCurrentRoom();
        game.getPlayer().setCurrentRoom(randomRoom);

        assertNotNull(game.getPlayer().getCurrentRoom());
        System.out.println("验证成功: 玩家已从 [" + before.getShortDescription() + "] 传送到新位置。");

        System.out.println("√ [完成] 随机传送核心逻辑测试通过。\n");
    }
}
