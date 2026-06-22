package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 命令词典类.
 * 维护游戏中所有合法的命令字以及对应的命令对象。
 * 用于对用户输入的命令进行解析和分发。
 *
 * @author B
 */
public class CommandWords
{
    private HashMap<String, Command> commands;

    /**
     * 构造函数，初始化并注册游戏中所有支持的命令集合.
     */
    public CommandWords()
    {
        commands = new HashMap<String, Command>();
        commands.put("go", new GoCommand());
        commands.put("help", new HelpCommand(this));
        commands.put("quit", new QuitCommand());
        commands.put("take", new TakeCommand());
        commands.put("drop", new DropCommand());
        commands.put("back", new BackCommand()); // 核心新增：注册 back 命令
    }

    /**
     * 根据命令词字符串获取对应的具体命令对象.
     *
     * @param word 用户输入的命令字
     * @return Command 对应的Command对象，如果不是合法命令则返回null
     */
    public Command get(String word)
    {
        return (Command)commands.get(word);
    }

    /**
     * 打印游戏中所有可用的命令词.
     */
    public void showAll()
    {
        // 补充泛型 <String> 以消除 Checkstyle / SonarLint 的“原始类型使用”警告
        for(Iterator<String> i = commands.keySet().iterator(); i.hasNext(); ) {
            System.out.print(i.next() + "  ");
        }
        System.out.println();
    }
}