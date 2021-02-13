package com.takumi.geppo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class TabComp implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String Label, String[] args){
        List<String> l = new ArrayList<String>();
        if (cmd.getName().equals("geppo")) {
            if (args.length == 1) {
                l.add("on");
                l.add("off");
                l.add("init");
            }
            else if(args.length == 2) {
                if(args[0].equals("on") || args[0].equals("off")){
                    l.add("@a");
                    Bukkit.getOnlinePlayers().stream().forEach(p -> l.add(p.getName()));
                }
            }
        }
        return l;
    }
}
