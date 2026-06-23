package cn.edu.whut.sept.zuul.gui;

import cn.edu.whut.sept.zuul.core.Game;
import cn.edu.whut.sept.zuul.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.awt.EventQueue;

import static org.junit.jupiter.api.Assertions.*;

public class GameGUITest {

    private Game game;
    private GameGUI gui;

    @BeforeEach
    public void setUp() throws Exception {
        // 初始化游戏逻辑
        game = new Game();

        // 在 UI 线程中初始化 GUI，确保线程安全
        invokeAndWait(() -> {
            gui = new GameGUI(game);
            gui.show();
        });
    }

    /**
     * 测试验证：点击“东(E)”按钮是否触发了 GoCommand 并移动了玩家到[阶梯教室]
     */
    @Test
    public void testEastButtonClickTriggersMove() throws Exception {
        // 1. 验证初始房间（大学主入口）
        Room startRoom = game.getPlayer().getCurrentRoom();
        assertEquals("大学主入口", startRoom.getShortDescription(), "初始位置应为大学主入口");

        // 2. 通过反射获取 GameGUI 中的私有成员 btnE (东方按钮)
        // 注意：变量名需与 GameGUI.java 中定义的成员变量名一致
        JButton btnE = (JButton) getPrivateField(gui, "btnE");
        assertNotNull(btnE, "未能通过反射获取到东方按钮对象");

        // 3. 模拟点击按钮（在 EDT 线程中执行，确保 Swing 线程安全）
        invokeAndWait(btnE::doClick);

        // 4. 验证结果
        Room currentRoom = game.getPlayer().getCurrentRoom();

        // 逻辑校验：outside 的 east 应该是 theater (阶梯教室)
        assertNotEquals(startRoom, currentRoom, "点击向东按钮后玩家位置未发生改变");
        assertEquals("阶梯教室", currentRoom.getShortDescription(), "玩家未移动到预期的[阶梯教室]");

        System.out.println("测试通过：成功从 [" + startRoom.getShortDescription() +
                "] 移动到了 [" + currentRoom.getShortDescription() + "]");
    }

    /**
     * 测试验证：点击“后退(Back)”按钮逻辑
     */
    @Test
    public void testBackButtonAction() throws Exception {
        // 先移动到北边
        invokeAndWait(() -> {
            try {
                JButton btnN = (JButton) getPrivateField(gui, "btnN");
                btnN.doClick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Room roomAfterMove = game.getPlayer().getCurrentRoom();

        // 获取后退按钮并点击
        JButton btnBack = (JButton) getPrivateField(gui, "btnBack");
        invokeAndWait(btnBack::doClick);

        Room roomAfterBack = game.getPlayer().getCurrentRoom();
        assertEquals("大学主入口", roomAfterBack.getShortDescription(), "回退功能未能返回初始房间");
    }

    // ================= 辅助工具方法 =================

    /**
     * 辅助方法：通过反射获取私有属性
     */
    private Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * 辅助方法：确保 Swing 操作在事件分发线程中同步执行，解决 Issue 中的多线程冲突隐患
     */
    private void invokeAndWait(Runnable runnable) throws Exception {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeAndWait(runnable);
        }
        // 给一点点渲染时间
        Thread.sleep(100);
    }
}