package com.takumi.geppo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class TabComp implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String Label, String[] args){
        List<String> l = new ArrayList<String>();
        if (cmd.getName().equals("geppo")) {
            if (args.length == 1) {
                l.add("on");
                l.add("off");
                l.add("interval");
                l.add("init");
                l.add("strength");
                l.add("limit");
                l.add("particle");
                l.add("mode");
            }
            else if(args.length == 2) {
                if(args[0].equals("on") || args[0].equals("off") || args[0].equals("interval") || args[0].equals("strength") || args[0].equals("limit") || args[0].equals("particle") || args[0].equals("mode")){
                    l.add("@a");
                    Bukkit.getOnlinePlayers().stream().forEach(p -> l.add(p.getName()));
                }
            }
            else if(args.length == 3){
                if(args[0].equals("interval")){
                    l.add("1");
                    l.add("2");
                    l.add("3");
                    l.add("4");
                    l.add("5");
                    l.add("6");
                    l.add("7");
                    l.add("8");
                    l.add("9");
                    l.add("10");
                }
                else if(args[0].equals("strength")){
                    l.add("normal");
                    l.add("rocket");
                }
                else if(args[0].equals("limit")){
                    l.add("<limit>1から100の整数で指定してください。");
                }
                else if(args[0].equals("particle")){
                    l.add("on");
                    l.add("off");
                }
                else if(args[0].equals("mode")){
                    l.add("normal");
                    l.add("random");
                }
            }
        }

        String last = args[args.length -1].toLowerCase();
        return l.stream()
                .filter(opt -> opt.startsWith(last))
                .collect(Collectors.toList());
    }
}
