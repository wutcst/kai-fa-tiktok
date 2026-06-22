package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;
    private Room room;

    @BeforeEach
    public void setUp() {
        player = new Player("测试者", 10); // 初始负重上限 10kg
        room = new Room("测试房间");
    }

    @Test
    public void testTakeItemWithinWeight() {
        Item lightItem = new Item("book", "一本轻便的书", 2);
        room.addItem(lightItem);

        boolean success = player.takeItem("book", room);

        assertTrue(success);
        assertEquals(2, player.getCurrentWeight());
        assertFalse(room.getItems().containsKey("book"));
    }

    @Test
    public void testTakeItemOverWeight() {
        Item heavyItem = new Item("anvil", "沉重的铁砧", 15);
        room.addItem(heavyItem);

        boolean success = player.takeItem("anvil", room);

        assertFalse(success, "重量超出上限，不应拾取成功");
        assertEquals(0, player.getCurrentWeight());
        assertTrue(room.getItems().containsKey("anvil"), "物品应留在房间内");
    }

    @Test
    public void testIncreaseMaxWeightByEatingCookie() {
        player.increaseMaxWeight(20);
        assertEquals(30, player.getMaxWeight());
    }
}