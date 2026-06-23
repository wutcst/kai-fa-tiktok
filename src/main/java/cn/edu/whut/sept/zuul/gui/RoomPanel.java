package cn.edu.whut.sept.zuul.gui;

import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.TransporterRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class RoomPanel extends JPanel {
    private Room currentRoom;
    private BufferedImage backgroundImage;
    private String loadedImageName;

    public RoomPanel() {
        setPreferredSize(new Dimension(480, 260));
        setMinimumSize(new Dimension(300, 200));
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 1));
    }

    public void setRoom(Room room) {
        this.currentRoom = room;
        if (room != null && room.getImageName() != null) {
            String imageName = room.getImageName();
            if (!imageName.equals(loadedImageName)) {
                backgroundImage = loadImage(imageName);
                loadedImageName = imageName;
            }
        } else {
            backgroundImage = null;
            loadedImageName = null;
        }
        repaint();
    }

    private BufferedImage loadImage(String name) {
        try {
            URL url = getClass().getResource("/images/" + name);
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = bimg.createGraphics();
                g2.drawImage(img, 0, 0, null);
                g2.dispose();
                return bimg;
            }
        } catch (Exception e) {
            // Fallback will handle it
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (currentRoom == null) {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("正在加载游戏世界...", width / 2 - 60, height / 2);
            return;
        }

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, width, height, null);
            drawGlassBanner(g2d, width, height);
        } else {
            drawProceduralCard(g2d, width, height);
        }
    }

    private void drawGlassBanner(Graphics2D g2d, int width, int height) {
        int bannerHeight = 60;
        int bannerY = height - bannerHeight;

        g2d.setColor(new Color(15, 15, 15, 180));
        g2d.fillRect(0, bannerY, width, bannerHeight);

        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.drawLine(0, bannerY, width, bannerY);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        g2d.drawString(currentRoom.getShortDescription(), 20, bannerY + 25);

        g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("这里是冒险旅途中的一站。", 20, bannerY + 45);
    }

    private void drawProceduralCard(Graphics2D g2d, int width, int height) {
        String desc = currentRoom.getShortDescription();
        Color colorStart;
        Color colorEnd;
        String iconText = "📍";

        if (desc.contains("大学主入口")) {
            colorStart = new Color(41, 128, 185);
            colorEnd = new Color(39, 174, 96);
            iconText = "🏫";
        } else if (desc.contains("阶梯教室")) {
            colorStart = new Color(142, 68, 173);
            colorEnd = new Color(192, 57, 43);
            iconText = "🎭";
        } else if (desc.contains("校园酒吧")) {
            colorStart = new Color(230, 126, 34);
            colorEnd = new Color(110, 44, 2);
            iconText = "🍻";
        } else if (desc.contains("计算机实验室")) {
            colorStart = new Color(26, 188, 156);
            colorEnd = new Color(44, 62, 80);
            iconText = "💻";
        } else if (desc.contains("管理办公室")) {
            colorStart = new Color(127, 140, 141);
            colorEnd = new Color(52, 73, 94);
            iconText = "🔑";
        } else if (desc.contains("储藏室")) {
            colorStart = new Color(44, 62, 80);
            colorEnd = new Color(20, 20, 20);
            iconText = "📦";
        } else if (currentRoom instanceof TransporterRoom || desc.contains("传送门")) {
            colorStart = new Color(155, 89, 182);
            colorEnd = new Color(241, 196, 15);
            iconText = "🌀";
        } else if (desc.contains("神秘") || desc.contains("核心机房")) {
            colorStart = new Color(0, 242, 254);
            colorEnd = new Color(79, 79, 229);
            iconText = "🛡️";
        } else {
            colorStart = new Color(52, 73, 94);
            colorEnd = new Color(20, 30, 40);
            iconText = "🚪";
        }

        GradientPaint gp = new GradientPaint(0, 0, colorStart, 0, height, colorEnd);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);

        if (desc.contains("计算机实验室") || desc.contains("神秘") || desc.contains("核心机房")) {
            g2d.setColor(new Color(255, 255, 255, 20));
            int gridSize = 25;
            for (int x = 0; x < width; x += gridSize) {
                g2d.drawLine(x, 0, x, height);
            }
            for (int y = 0; y < height; y += gridSize) {
                g2d.drawLine(0, y, width, y);
            }
        }

        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.drawString(iconText, width - 150, height - 30);

        int cardX = 20;
        int cardY = 20;
        int cardW = width - 40;
        int cardH = height - 40;

        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g2d.setColor(new Color(255, 255, 255, 70));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        g2d.drawString(currentRoom.getShortDescription(), cardX + 25, cardY + 45);

        g2d.setFont(new Font("Microsoft YaHei", Font.ITALIC, 13));
        g2d.setColor(new Color(240, 240, 240, 220));
        g2d.drawString("冒险所在场景", cardX + 25, cardY + 75);

        g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        g2d.setColor(new Color(255, 255, 255, 240));

        String statusText = "🗺️ 空间状态稳定";
        if (currentRoom instanceof TransporterRoom) {
            statusText = "⚡ 空间折叠警报：此地具有强力随机传送能量！";
        } else if (desc.contains("黑暗")) {
            statusText = "🕯️ 四周围漆黑一片，必须小心翼翼前行。";
        } else if (desc.contains("暗门开启")) {
            statusText = "🚪 暗门已开启！地道通向全新的向下区域。";
        }
        g2d.drawString(statusText, cardX + 25, cardY + 120);

        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.drawLine(cardX + 25, cardY + 145, cardX + cardW - 25, cardY + 145);

        g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.drawString("SCENE CARD ENGINE V2.0", cardX + 25, cardY + cardH - 18);
    }
}