package cn.edu.whut.sept.zuul.core;

import cn.edu.whut.sept.zuul.command.Command;
import cn.edu.whut.sept.zuul.command.CommandWords;

import java.util.Scanner;

public class Parser {
    private final CommandWords commands;  // holds all valid command words
    private final Scanner reader;         // source of command input

    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    public Command getCommand() {
        String inputLine;   // will hold the full input line
        String word1 = null;
        String word2 = null;

        System.out.print("> ");     // print prompt

        inputLine = reader.nextLine();

        Scanner tokenizer = new Scanner(inputLine);
        if (tokenizer.hasNext()) {
            word1 = tokenizer.next();      // get first word
            if (tokenizer.hasNext()) {
                word2 = tokenizer.next();      // get second word
            }
        }

        Command command = commands.get(word1);
        if (command != null) {
            command.setSecondWord(word2);
        }
        return command;
    }
    /**
     * 根据提供的字符串解析命令.
     *
     * @param inputLine 输入的指令字符串
     * @return 解析后的命令对象
     */
    public Command getCommand(String inputLine) {
        String word1 = null;
        String word2 = null;

        if (inputLine == null || inputLine.trim().isEmpty()) {
            return null;
        }

        java.util.Scanner tokenizer = new java.util.Scanner(inputLine);
        if (tokenizer.hasNext()) {
            word1 = tokenizer.next();
            if (tokenizer.hasNext()) {
                word2 = tokenizer.next();
            }
        }

        Command command = commands.get(word1);
        if (command != null) {
            command.setSecondWord(word2);
        }
        return command;
    }
    public void showCommands() {
        commands.showAll();
    }
}

