package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Iterator;

public class CommandWords
{
    private HashMap<String, Command> commands;

    public CommandWords()
    {
        commands = new HashMap<String, Command>();
        commands.put("go", new GoCommand());
        commands.put("help", new HelpCommand(this));
        commands.put("quit", new QuitCommand());
        
        // ========= 累加注册的所有扩展命令 =========
        commands.put("take", new TakeCommand());
        commands.put("drop", new DropCommand());
        commands.put("back", new BackCommand()); // 核心新增：注册 back 命令
    }

    public Command get(String word)
    {
        return (Command)commands.get(word);
    }

    public void showAll()
    {
        for(Iterator i = commands.keySet().iterator(); i.hasNext(); ) {
            System.out.print(i.next() + "  ");
        }
        System.out.println();
    }
}