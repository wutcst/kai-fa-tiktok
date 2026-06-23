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
    private DefaultListModel<String> roomItemsListModel;
    private JList<String> roomItemsList;
    private JProgressBar weightBar;
    private JLabel bagCountLabel;

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
        logArea.setBackground(new Color(20, 20, 20));
        logArea.setForeground(new Color(180, 255, 180));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));
        logScrollPane.setPreferredSize(new Dimension(0, 220));
        centerPanel.add(logScrollPane, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. 右侧面板：[个人背包] + [房间物品列表]
        // ==========================================
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 10)); // 上下平分
        rightPanel.setBackground(darkBackground);
        rightPanel.setPreferredSize(new Dimension(220, 0));

        // --- A. 个人背包列表 ---
        JPanel bagPanel = new JPanel(new BorderLayout(5, 5));
        bagPanel.setBackground(panelBackground);
        bagPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                "  随身背包 ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));
        bagListModel = new DefaultListModel<>();
        bagList = new JList<>(bagListModel);
        bagList.setBackground(new Color(24, 24, 24));
        bagList.setForeground(textColor);
        bagPanel.add(new JScrollPane(bagList), BorderLayout.CENTER);

        // --- B. 房间物品列表 (新增) ---
        JPanel roomItemsPanel = new JPanel(new BorderLayout(5, 5));
        roomItemsPanel.setBackground(panelBackground);
        roomItemsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                " 🔍 房间可见物品 ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13), textColor
        ));
        roomItemsListModel = new DefaultListModel<>();
        roomItemsList = new JList<>(roomItemsListModel);
        roomItemsList.setBackground(new Color(24, 24, 24));
        roomItemsList.setForeground(new Color(150, 200, 255)); // 蓝色字体区分
        roomItemsPanel.add(new JScrollPane(roomItemsList), BorderLayout.CENTER);

        rightPanel.add(bagPanel);
        rightPanel.add(roomItemsPanel);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // ==========================================
        // 4. 底部状态栏 (保持原样)
        // ==========================================
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // 将状态栏分为左中右三块
        statusPanel.setBackground(new Color(30, 30, 30));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 63, 65)), // 顶部边框线
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

// --- 左侧：角色信息 ---
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftStatus.setOpaque(false);
        JLabel playerIcon = new JLabel(" 探险者");
        playerIcon.setForeground(new Color(155, 89, 182)); // 紫色
        playerIcon.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        leftStatus.add(playerIcon);

// 新增：背包物品计数
        JLabel bagCountLabel = new JLabel(" 0 件");
        bagCountLabel.setForeground(new Color(200, 200, 200));
        this.bagCountLabel = bagCountLabel; // 需在类成员中声明
        leftStatus.add(bagCountLabel);

// --- 中间：当前位置 ---
        JPanel centerStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        centerStatus.setOpaque(false);
        JLabel locIcon = new JLabel("📍 当前位置:");
        locIcon.setForeground(new Color(52, 152, 219)); // 蓝色
        statusBarLabel = new JLabel("初始化中...");
        statusBarLabel.setForeground(Color.WHITE);
        statusBarLabel.setFont(new Font("Microsoft YaHei", Font.ITALIC, 13));
        centerStatus.add(locIcon);
        centerStatus.add(statusBarLabel);

// --- 右侧：负重系统 ---
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightStatus.setOpaque(false);

        weightBar = new JProgressBar(0, 100);
        weightBar.setPreferredSize(new Dimension(160, 16));
        weightBar.setStringPainted(true);
        weightBar.setFont(new Font("Consolas", Font.BOLD, 10));
        weightBar.setBackground(new Color(45, 45, 45));
        weightBar.setForeground(new Color(46, 204, 113));
        weightBar.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));

        JLabel weightIcon = new JLabel("容量");
        rightStatus.add(weightIcon);
        rightStatus.add(weightBar);

// 将三部分添加到主状态面板
        statusPanel.add(leftStatus);
        statusPanel.add(centerStatus);
        statusPanel.add(rightStatus);

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
            String targetCookie = game.getPlayer().getInventory().keySet().stream()
                    .filter(name -> name.contains("饼干"))
                    .findFirst()
                    .orElse(null);

            if (targetCookie != null) {
                game.executeCommand("eat " + targetCookie);
            } else {
                System.out.println("提示：你的背包里现在没有可食用的饼干。");
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

        // 1. 实现背包物品右键点击菜单
        bagList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2) {
                    int index = bagList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        bagList.setSelectedIndex(index);
                        String selected = bagList.getSelectedValue();
                        String itemName = selected.split(" ")[0]; // 提取物品名称
                        showBagMenu(e.getComponent(), e.getX(), e.getY(), itemName);
                    }
                }
            }
        });

        // 2. 实现房间物品右键点击菜单
        roomItemsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2) {
                    int index = roomItemsList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        roomItemsList.setSelectedIndex(index);
                        String selected = roomItemsList.getSelectedValue();
                        String itemName = selected.split(" ")[0];
                        showRoomMenu(e.getComponent(), e.getX(), e.getY(), itemName);
                    }
                }
            }
        });

        // 3. 注册剧情弹窗逻辑
        game.setStoryListener((title, message) -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            });
        });

    }

    public void refreshUI() {
        // 1. 线程安全检查（保留）
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshUI);
            return;
        }

        Player player = game.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        if (player != null && currentRoom != null) {
            // 2. 同步更新场景图片（保留）
            roomPanel.setRoom(currentRoom);

            // 3. 【重点修改】更新中间的位置文字
            statusBarLabel.setText(currentRoom.getShortDescription());
            if (currentRoom.isDark()) {
                statusBarLabel.setForeground(new Color(241, 196, 15)); // 黑暗房间显示黄色
                statusBarLabel.setText(currentRoom.getShortDescription() + " (环境幽暗)");
            } else {
                statusBarLabel.setForeground(Color.WHITE);
            }

            // 4. 【重点修改】更新左侧的物品计数
            int itemCount = player.getInventory().size();
            bagCountLabel.setText("背包容量 " + itemCount + " 件物品");

            // 5. 【重点修改】更新右侧的负重条
            int curWeight = player.getCurrentWeight();
            int maxWeight = player.getMaxWeight();
            weightBar.setMaximum(maxWeight);
            weightBar.setValue(curWeight);
            weightBar.setString(curWeight + " / " + maxWeight + " kg");

            // 颜色反馈逻辑
            float ratio = (float) curWeight / maxWeight;
            if (ratio >= 1.0f) {
                weightBar.setForeground(new Color(231, 76, 60)); // 超载红色
            } else if (ratio >= 0.8f) {
                weightBar.setForeground(new Color(230, 126, 34)); // 警告橙色
            } else {
                weightBar.setForeground(new Color(46, 204, 113)); // 安全绿色
            }
        }

        // 6. 更新侧边栏列表（保留并更新）
        bagListModel.clear();
        game.getPlayer().getInventory().forEach((name, item) -> {
            bagListModel.addElement(name + " (" + item.getWeight() + "kg)");
        });

        roomItemsListModel.clear();
        game.getPlayer().getCurrentRoom().getItems().forEach((name, item) -> {
            roomItemsListModel.addElement(name + " (" + item.getWeight() + "kg)");
        });
    }

    private void showBagMenu(final Component comp, final int x, final int y, final String itemName) {
        JPopupMenu menu = new JPopupMenu();

        if (itemName.contains("饼干")) {
            JMenuItem eatItem = new JMenuItem("吃掉 " + itemName);
            eatItem.addActionListener(e -> game.executeCommand("eat " + itemName));
            menu.add(eatItem);
        } else if (itemName.contains("包")) {
            JMenuItem useItem = new JMenuItem("装备/背上 " + itemName);
            useItem.addActionListener(e -> game.executeCommand("eat " + itemName)); // 指向 eat 命令处理
            menu.add(useItem);
        }

        JMenuItem dropItem = new JMenuItem("丢弃物品");
        dropItem.addActionListener(e -> game.executeCommand("drop " + itemName));
        menu.add(dropItem);

        menu.show(comp, x, y);
    }

    private void showRoomMenu(final Component comp, final int x, final int y, final String itemName) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem takeItem = new JMenuItem("捡起物品");
        takeItem.addActionListener(e -> game.executeCommand("take " + itemName));
        menu.add(takeItem);
        menu.show(comp, x, y);
    }

    public void show() {
        frame.setVisible(true);
        refreshUI();
    }
}