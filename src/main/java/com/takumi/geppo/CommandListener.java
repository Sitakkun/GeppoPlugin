package com.takumi.geppo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements CommandExecutor{
    private Geppo instance = null;

    CommandListener(){
    }

    CommandListener(Geppo instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        if(cmd.getName().equals("geppo")){
            if(!(sender.isOp())){
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:このコマンドの実行にはOP権限が必要です。");
            }
            else if (args.length > 3) {
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:引数が多いです。");
            }
            else if (args.length < 1) {
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:引数が少ないです。");
            }
            else {
                if(args.length == 1){
                    if(args[0].equals("init")){
                        instance.initPlayerHashtable();
                        sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:プラグインが初期化されました。");
                    }
                    else{
                        sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:コマンドの引数が足りないか、有効ではありません。");
                    }
                }
                else if(args.length == 2){
                    String playerName = args[1];
                    if(args[0].equals("on")){
                        boolean result = instance.setAllowFlg(playerName,true);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                        }
                    }
                    else if(args[0].equals("off")){
                        boolean result = instance.setAllowFlg(playerName,false);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                        }
                    }
                    else{
                        sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:コマンドの引数が足りないか、有効ではありません。");
                    }
                }
                else if(args.length == 3){
                    String playerName = args[1];
                    if(args[0].equals("interval")){
                        try{
                            int interval = Integer.parseInt(args[2]);
                            if(interval < 1 || interval > 10){
                                sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:インターバルは1秒～10秒の範囲で指定してください。");
                                return true;
                            }
                            boolean result = instance.setInterval(playerName,interval);
                            if(!result){
                                sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                            }
                        }
                        catch (NumberFormatException e){
                            sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:インターバルは整数で指定してください。");
                        }
                    }
                    else if(args[0].equals("strength")){
                        String strength = args[2];
                        boolean result = instance.setStrength(playerName, strength);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                        }
                    }
                    else if(args[0].equals("limit")){
                        try{
                            int limit = Integer.parseInt(args[2]);
                            if(limit < 1 || limit > 100){
                                sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:ジャンプ上限数は1以上100以下の整数で指定してください。");
                                return true;
                            }
                            boolean result = instance.setLimit(playerName,limit);
                            if(!result){
                                sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                            }
                        }
                        catch (NumberFormatException e){
                            sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:ジャンプ上限は整数で指定してください。");
                        }
                    }
                    else if(args[0].equals("particle")){
                        boolean particleFlg;
                        if(args[2].equals("on")){
                            particleFlg = true;
                        }
                        else if(args[2].equals("off")){
                            particleFlg = false;
                        }
                        else {
                            sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:particleの引数が間違っています。");
                            return true;
                        }
                        boolean result = instance.setParticleFlg(playerName,particleFlg);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                        }
                    }
                    else if(args[0].equals("mode")){
                        String mode = args[2];
                        if(!(mode.equals("normal") || mode.equals("random"))){
                            sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:modeの引数が間違っています。");
                            return true;
                        }
                        boolean result = instance.setMode(playerName,mode);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + playerName + "は存在しません。または引数が間違っています。");
                        }
                    }
                    else{
                        sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:コマンドの引数が足りないか、有効ではありません。");
                    }
                }
            }
        }
        return  true;
    }



}
