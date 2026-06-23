package cn.edu.whut.sept.zuul.gui;

import cn.edu.whut.sept.zuul.core.Game;
import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class GameGUI {
    private final Game game;

    private JFrame frame;
    private RoomPanel roomPanel;
    private JTextArea logArea;
    private JLabel statusBarLabel;
    private DefaultListModel<String> bagListModel;
    private JList<String> bagList;

    public GameGUI(Game game) {
        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        UIManager.put("Button.font", globalFont);
        UIManager.put("Label.font", globalFont);
        UIManager.put("TextArea.font", new Font("Microsoft YaHei", Font.PLAIN, 14));
        this.game = game;
        initUI();
        setupGameListeners();
    }

    private void initUI() {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        frame = new JFrame("World of Zuul - 主窗口与视觉渲染 (任务1 & 任务2)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 650);
        frame.setLocationRelativeTo(null);

        Color darkBackground = new Color(24, 24, 24);
        Color panelBackground = new Color(30, 30, 30);
        Color textColor = new Color(220, 220, 220);
        Color accentColor = new Color(79, 79, 229);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBackground);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(mainPanel);

        // ==========================================
        // 1. 左侧面板：操作按钮区 (九宫格罗盘)
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(darkBackground);
        leftPanel.setPreferredSize(new Dimension(240, 0));

        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBackground(panelBackground);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                " 操作按钮区 (待绑定) ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));

        JPanel compassGrid = new JPanel(new GridLayout(3, 3, 5, 5));
        compassGrid.setBackground(panelBackground);
        compassGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnN = createStyledButton("N (北)", accentColor);
        JButton btnS = createStyledButton("S (南)", accentColor);
        JButton btnE = createStyledButton("E (东)", accentColor);
        JButton btnW = createStyledButton("W (西)", accentColor);
        JButton btnUp = createStyledButton("Up (上)", new Color(39, 174, 96));
        JButton btnDown = createStyledButton("Down (下)", new Color(192, 57, 43));

        compassGrid.add(btnUp);
        compassGrid.add(btnN);
        compassGrid.add(new JLabel(""));
        compassGrid.add(btnW);
        compassGrid.add(new JLabel("🧭", SwingConstants.CENTER));
        compassGrid.add(btnE);
        compassGrid.add(btnDown);
        compassGrid.add(btnS);
        compassGrid.add(new JLabel(""));

        controlPanel.add(compassGrid, BorderLayout.CENTER);
        leftPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // ==========================================
        // 2. 中央面板：中央放置 RoomPanel 视觉呈现区，下方放置 文本显示区
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(darkBackground);

        // 房间视觉呈现逻辑 (在界面中央实时显示当前房间场景)
        roomPanel = new RoomPanel();
        roomPanel.setPreferredSize(new Dimension(0, 350));
        centerPanel.add(roomPanel, BorderLayout.CENTER);

        // 文本显示区 (JTextArea)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(15, 15, 15));
        logArea.setForeground(new Color(190, 220, 190));
        logArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));
        logScrollPane.setPreferredSize(new Dimension(0, 220));
        centerPanel.add(logScrollPane, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. 背包列表区 (bagList) - 位于右侧
        // ==========================================
        JPanel bagPanel = new JPanel(new BorderLayout(5, 5));
        bagPanel.setBackground(panelBackground);
        bagPanel.setPreferredSize(new Dimension(200, 0));
        bagPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                " 背包列表区 ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));

        bagListModel = new DefaultListModel<>();
        bagList = new JList<>(bagListModel);
        bagList.setBackground(new Color(24, 24, 24));
        bagList.setForeground(textColor);
        bagList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        JScrollPane bagScrollPane = new JScrollPane(bagList);
        bagScrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));
        bagPanel.add(bagScrollPane, BorderLayout.CENTER);

        mainPanel.add(bagPanel, BorderLayout.EAST);

        // ==========================================
        // 4. 状态栏 (statusBarLabel) - 位于最底部
        // ==========================================
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(panelBackground);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        statusBarLabel = new JLabel("状态栏: 初始化中...");
        statusBarLabel.setForeground(textColor);
        statusBarLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        statusPanel.add(statusBarLabel, BorderLayout.CENTER);

        mainPanel.add(statusPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.setBorder(BorderFactory.createLineBorder(baseColor.darker(), 1));
        return button;
    }

    private void setupGameListeners() {
        game.setupRedirectedOutput();

        game.addOutputListener(text -> SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }));

        game.addStatusListener(this::refreshUI);
    }

    public void refreshUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshUI);
            return;
        }

        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        // 1. 同步更新场景图片与描述 (任务2)
        roomPanel.setRoom(currentRoom);

        // 2. 同步更新状态栏
        if (player != null && currentRoom != null) {
            statusBarLabel.setText(String.format("玩家: %s | 当前位置: %s | 负重上限: %dkg",
                    "探险者",
                    currentRoom.getShortDescription(),
                    player.getMaxWeight()
            ));
        }

        // 3. 同步更新背包列表
        bagListModel.clear();
        Map<String, Item> inventory = player.getInventory();
        if (inventory != null) {
            inventory.forEach((name, item) -> {
                bagListModel.addElement(name + " (" + item.getWeight() + "kg)");
            });
        }
    }

    public void show() {
        frame.setVisible(true);
        refreshUI();
    }
}