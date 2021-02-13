package com.takumi.geppo;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;

public final class Geppo extends JavaPlugin implements Listener {
    private Geppo instance;
    //private List<GeppoPlayer> playerList = new ArrayList<GeppoPlayer>();
    private Hashtable<String, GeppoPlayer> playerHashtable = new Hashtable<String, GeppoPlayer>();

    boolean useflag = true;


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.instance = this;
        this.getCommand("geppo").setExecutor(this);
        this.getCommand("geppo").setTabCompleter(new TabComp());
        Bukkit.getPluginManager().registerEvents(this,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // コマンド処理
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        if(cmd.getName().equals("geppo")){
            if(!(sender.isOp())){
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:このコマンドの実行にはOP権限が必要です。");
            }
            else if (args.length > 2) {
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:引数が多いです。");
            }
            else if (args.length < 1) {
                sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:引数が少ないです。");
            }
            else {
                if(args.length == 1){
                    if(args[0].equals("init")){
                        initPlayerHashtable();
                        sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:プラグインが初期化されました。");
                    }
                    else{
                        sender.sendMessage(ChatColor.RED+"[GeppoPlugin]:コマンドの引数が足りないか、有効ではありません。");
                    }
                }
                else if(args.length == 2){
                    String plaerName = args[1];
                    if(args[0].equals("on")){
                        boolean result = setAllowFlg(plaerName,true);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + plaerName + "は存在しません。または引数が間違っています。");
                        }
                        useflag = true;
                    }
                    else if(args[0].equals("off")){
                        boolean result = setAllowFlg(plaerName,false);
                        if(!result){
                            sender.sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + plaerName + "は存在しません。または引数が間違っています。");
                        }
                        useflag = false;
                    }
                    else{
                        sender.sendMessage(ChatColor.YELLOW+"[GeppoPlugin]:引数に誤りがあります。");
                    }
                }
            }
        }
        return  true;
    }

    //Events
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskLater(this, task ->{
            e.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:"+e.getPlayer().getName());
            playerHashtable.put(e.getPlayer().getName(),new GeppoPlayer(e.getPlayer()));
        }, 10L);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e){
        String playerName = e.getPlayer().getName();
        playerHashtable.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        Player player = e.getPlayer();
        GeppoPlayer playerData = getPlayerInfo(player.getName());
        if(playerData == null){
            e.getPlayer().sendMessage(ChatColor.RED+"[GeppoPlugin]:致命的なエラー：Player情報を取得できませんでした。管理者に問い合わせください。");
            return;
        }

        if(!playerData.getAllowFlg()){
            return;
        }

        LocalDateTime nowJumpTime = LocalDateTime.now();

        e.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + ChronoUnit.SECONDS.between(playerData.getBeforeJumpTime(),nowJumpTime));

        if(ChronoUnit.SECONDS.between(playerData.getBeforeJumpTime(),nowJumpTime) > 1){
            setGeppoCount(player.getName(),true);
        }
        else{
            setGeppoCount(player.getName(),false);
        }

        if(playerData.getJumpCount() < 10){
            if(!(player.isOnGround()) && player.getFallDistance() >= 0.001){
                player.setVelocity(player.getLocation().getDirection().multiply(5));
                player.setVelocity(new Vector(player.getVelocity().getX(), 3D, player.getVelocity().getZ()));
                Location location = player.getLocation();
                for (int degree = 260; degree < 280; degree++) {
                    double radians = Math.toRadians(degree);
                    double x = Math.cos(radians);
                    double z = Math.sin(radians);
                    location.add(x,0,z);
                    location.getWorld().spawnParticle(Particle.FLAME, location,1,0.1,0.5,0.1);
                    location.subtract(x,0,z);
                }
            }
        }

        setJumpTime(player.getName());

    }


    //Public Method
    public Geppo getInstance(){
        return instance;
    }

    public Hashtable<String, GeppoPlayer> getplayerHashtable(){
        return  playerHashtable;
    }

    //Private Method
    private void initPlayerHashtable(){
        playerHashtable.clear();
        Bukkit.getOnlinePlayers().stream().forEach(p -> playerHashtable.put(p.getName(),new GeppoPlayer(p)));
    }

    private void setGeppoCount(String playerName,boolean resetFlg){
        if(!playerHashtable.containsKey(playerName)){
            return;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        if(resetFlg){
            player.resetJumpCount();
        }
        else{
            player.jumpCountUp();
        }
        playerHashtable.put(playerName,player);
    }

    private void setJumpTime(String playerName){
        if(!playerHashtable.containsKey(playerName)){
            return;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        player.setBeforeJumpTime(LocalDateTime.now());
        playerHashtable.put(playerName,player);
    }

    private boolean setAllowFlg(String playerName, boolean allowFlg){
        String onMessage = "に月歩の力が付与されました";
        String offMessage = "は月歩の力が剥奪されました";

        if(playerName.equals("@a")){
            for(GeppoPlayer p : playerHashtable.values()){
                p.setAllowFlg(allowFlg);
                if(allowFlg){
                    p.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + p.getPlayer().getName() + onMessage);
                }
                else{
                    p.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + p.getPlayer().getName() + offMessage);
                }
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }
        GeppoPlayer player = playerHashtable.get(playerName);

        player.setAllowFlg(allowFlg);
        playerHashtable.put(playerName,player);
        if(allowFlg){
            player.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + player.getPlayer().getName() + onMessage);
        }
        else {
            player.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + player.getPlayer().getName() + offMessage);
        }
        return true;
    }

    private GeppoPlayer getPlayerInfo(String playerName){
        if(!playerHashtable.containsKey(playerName)){
            return null;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        return playerHashtable.get(playerName);
    }
}
