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
     * 测试验证：点击“北(N)”按钮是否触发了 GoCommand 并移动了玩家位置
     */
    @Test
    public void testNorthButtonClickTriggersMove() throws Exception {
        // 1. 获取初始房间（大学主入口）
        Room startRoom = game.getPlayer().getCurrentRoom();
        assertEquals("大学主入口", startRoom.getShortDescription());

        // 2. 通过反射获取 GameGUI 中的私有成员 btnN (北方按钮)
        JButton btnN = (JButton) getPrivateField(gui, "btnN");
        assertNotNull(btnN, "未能通过反射获取到北方按钮对象");

        // 3. 模拟点击按钮（在 EDT 线程中执行）
        invokeAndWait(btnN::doClick);

        // 4. 验证结果
        // 根据 Game.java 的初始化逻辑，outside(主入口) 的 north 是 garden(迷雾园林)
        Room currentRoom = game.getPlayer().getCurrentRoom();
        assertNotEquals(startRoom, currentRoom, "点击按钮后玩家位置未发生改变");
        assertEquals("迷雾园林", currentRoom.getShortDescription(), "玩家未移动到预期的[迷雾园林]");
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