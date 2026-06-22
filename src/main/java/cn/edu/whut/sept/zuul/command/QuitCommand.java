package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.core.Game;

public class QuitCommand extends Command
{
    public boolean execute(Game game)
    {
        if(hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
