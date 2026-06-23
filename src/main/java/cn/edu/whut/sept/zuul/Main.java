package cn.edu.whut.sept.zuul;
import cn.edu.whut.sept.zuul.gui.GameGUI;
import cn.edu.whut.sept.zuul.core.Game;

public class Main {

    public static void main(String[] args) {
        boolean useCli = false;
        if (args.length > 0 && args[0].equals("--cli")) {
            useCli = true;
        }

        if (useCli) {
            Game game = new Game();
            game.play();
        } else {
            Game game = new Game();
            GameGUI gui = new GameGUI(game);
            gui.show();
            // GUI 作为伴侣显示后，在主线程启动 CLI 命令循环，支持在终端输入 'go south' 等命令并同步刷新 GUI 视窗。
            game.play();
        }
    }
}
