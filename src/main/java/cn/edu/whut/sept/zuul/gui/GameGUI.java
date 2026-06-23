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
    private JTextField commandInput;
    private JButton btnN, btnS, btnE, btnW, btnUp, btnDown;
    private JButton btnLook, btnBack, btnEat, btnItems;

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
        // --- 1. 基础皮肤设置 (保持不变) ---
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
        }

        frame = new JFrame("World of Zuul - 增强控制版");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 650);
        frame.setLocationRelativeTo(null);

        Color darkBackground = new Color(24, 24, 24);
        Color panelBackground = new Color(30, 30, 30);
        Color textColor = new Color(220, 220, 220);
        Color accentColor = new Color(79, 79, 229);
        Color btnColor = new Color(52, 73, 94); // 预设按钮颜色

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBackground);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(mainPanel);

        // ==========================================
        // 1. 左侧面板：包含 [方向控制] + [快捷操作]
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(darkBackground);
        leftPanel.setPreferredSize(new Dimension(240, 0));

        // --- A. 方向控制 (原九宫格罗盘) ---
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBackground(panelBackground);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                " 方向控制 ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));

        JPanel compassGrid = new JPanel(new GridLayout(3, 3, 5, 5));
        compassGrid.setBackground(panelBackground);
        compassGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 注意：这里去掉了 JButton 前缀，直接赋值给成员变量
        btnN = createStyledButton("N (北)", accentColor);
        btnS = createStyledButton("S (南)", accentColor);
        btnE = createStyledButton("E (东)", accentColor);
        btnW = createStyledButton("W (西)", accentColor);
        btnUp = createStyledButton("Up (上)", new Color(39, 174, 96));
        btnDown = createStyledButton("Down (下)", new Color(192, 57, 43));

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

        // --- B. 新增：快捷操作面板 (放在左侧面板底部) ---
        JPanel shortcutPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        shortcutPanel.setBackground(panelBackground);
        shortcutPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                " 常用快捷键 ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));
        btnLook = createStyledButton("看看四周", btnColor);
        btnBack = createStyledButton("撤销/后退", btnColor);
        btnEat = createStyledButton("吃点东西", btnColor);
        btnItems = createStyledButton("查看物品", btnColor);
        shortcutPanel.add(btnLook);
        shortcutPanel.add(btnBack);
        shortcutPanel.add(btnEat);
        shortcutPanel.add(btnItems);
        leftPanel.add(shortcutPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // ==========================================
        // 2. 中央面板：包含 [指令输入框] + [场景图] + [日志输出]
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(darkBackground);

        // --- A. 新增：指令输入框 (放在顶部) ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(darkBackground);
        commandInput = new JTextField();
        commandInput.setBackground(new Color(35, 35, 35));
        commandInput.setForeground(Color.WHITE);
        commandInput.setCaretColor(Color.WHITE);
        commandInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JLabel promptLabel = new JLabel(" 指令输入: ");
        promptLabel.setForeground(accentColor);
        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(commandInput, BorderLayout.CENTER);
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // --- B. 场景图 (中间) ---
        roomPanel = new RoomPanel();
        roomPanel.setPreferredSize(new Dimension(0, 350));
        centerPanel.add(roomPanel, BorderLayout.CENTER);

        // --- C. 日志输出 (底部) ---
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
        // 3. 右侧背包区 (保持原样)
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
        // 4. 底部状态栏 (保持原样)
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
        // 1. 绑定方向按钮
        btnN.addActionListener(e -> game.executeCommand("go north"));
        btnS.addActionListener(e -> game.executeCommand("go south"));
        btnE.addActionListener(e -> game.executeCommand("go east"));
        btnW.addActionListener(e -> game.executeCommand("go west"));
        btnUp.addActionListener(e -> game.executeCommand("go up"));
        btnDown.addActionListener(e -> game.executeCommand("go down"));

        // 2. 绑定快捷键
        btnLook.addActionListener(e -> game.executeCommand("look"));
        btnBack.addActionListener(e -> game.executeCommand("back"));
        btnItems.addActionListener(e -> game.executeCommand("items"));
        btnEat.addActionListener(e -> {
            if (game.getPlayer().hasItem("cookie")) {
                game.executeCommand("eat cookie");
            } else {
                System.out.println("提示：你的背包里现在没有饼干。");
            }
        });

        // 3. 绑定回车输入事件
        commandInput.addActionListener(e -> {
            String input = commandInput.getText().trim();
            if (!input.isEmpty()) {
                game.executeCommand(input);
                commandInput.setText(""); // 运行后清空
            }
        });
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