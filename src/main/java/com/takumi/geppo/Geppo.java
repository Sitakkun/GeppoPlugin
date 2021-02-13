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
        this.getCommand("geppo").setExecutor(new CommandListener(this));
        this.getCommand("geppo").setTabCompleter(new TabComp());
        Bukkit.getPluginManager().registerEvents(this,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Bukkit.getScheduler().runTaskLater(this, task ->{
            String playerName = e.getPlayer().getName();
            if(!playerHashtable.containsKey(playerName)){
                return;
            }
            GeppoPlayer player = playerHashtable.get(playerName);
            player.resetJumpCount();
            playerHashtable.put(playerName,player);
        }, 5L);
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

        if(ChronoUnit.SECONDS.between(playerData.getBeforeJumpTime(),nowJumpTime) > playerData.getInterval()){
            setGeppoCount(player.getName(),true);
        }
        //else{
        //    setGeppoCount(player.getName(),false);
        //}

        if(playerData.getJumpCount() < playerData.getLimit() && e.isSneaking()){
            if(!(player.isOnGround()) && player.getFallDistance() >= 0.05){
                // ジャンプの実行
                player.setVelocity(player.getLocation().getDirection().multiply(playerData.getMultiply()));
                if(playerData.getMode().equals("normal")){
                    player.setVelocity(new Vector(player.getVelocity().getX() / 2, playerData.getVelocity_Y(), player.getVelocity().getZ() / 2));
                }
                else if(playerData.getMode().equals("random")){
                    double velocity_X = 0.0;
                    double velocity_Y = 0.0;
                    double velocity_Z = 0.0;
                    if(playerData.getStrength().equals("normal")){
                        velocity_X = ((Math.random() * 0.5) - 0.25) + player.getVelocity().getX() / 2.0;
                        velocity_Y = (Math.random() * 0.75) - 0.05;
                        velocity_Z = ((Math.random() * 0.5) - 0.25) + player.getVelocity().getZ() / 2.0;
                    }
                    else if(playerData.getStrength().equals("rocket")){
                        velocity_X = ((Math.random() * 10.0) - 5.0) + player.getVelocity().getX() / 2.0;
                        velocity_Y = (Math.random() * 5.0) - 0.3;
                        velocity_Z = ((Math.random() * 10.0) - 5.0) + player.getVelocity().getZ() / 2.0;
                    }
                    player.setVelocity(new Vector(velocity_X, velocity_Y, velocity_Z));
                }
                // パーティクルの再生を判定
                if(playerData.getParticleFlg()){
                    spawnParticle(player);
                }
                // 前回ジャンプした時間を記録
                setJumpTime(player.getName());
                // ジャンプした回数を記録
                setGeppoCount(player.getName(),false);
            }
        }

    }


    //Public Method
    public Geppo getInstance(){
        return instance;
    }

    public Hashtable<String, GeppoPlayer> getplayerHashtable(){
        return  playerHashtable;
    }

    //Protected Method
    protected void initPlayerHashtable(){
        playerHashtable.clear();
        Bukkit.getOnlinePlayers().stream().forEach(p -> playerHashtable.put(p.getName(),new GeppoPlayer(p)));
    }

    protected boolean setAllowFlg(String playerName, boolean allowFlg){
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

    protected boolean setInterval(String playerName,int intervalSec){
        if(playerName.equals("@a")){
            for(GeppoPlayer p : playerHashtable.values()){
                p.setInterval(intervalSec);
                p.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + p.getPlayer().getName() + "のインターバルが" + String.valueOf(intervalSec) + "秒に変更されました。");
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        player.setInterval(intervalSec);
        player.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + player.getPlayer().getName() + "のインターバルが" + String.valueOf(intervalSec) + "秒に変更されました。");

        return true;
    }

    protected boolean setStrength(String playerName, String strength){
        if(playerName.equals("@a")) {
            for (GeppoPlayer p : playerHashtable.values()) {
                boolean result = p.changeJumpStrength(strength);
                if(result){
                    p.getPlayer().sendMessage(ChatColor.BLUE + "[GeppoPlugin]:" + p.getPlayer().getName() + "のジャンプ力が[" + strength.toUpperCase() + "]に変更されました。");
                }
                else{
                    return false;
                }
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }

        GeppoPlayer player = playerHashtable.get(playerName);
        boolean result = player.changeJumpStrength(strength);

        if(result){
            player.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + player.getPlayer().getName() + "のジャンプ力が[" + strength.toUpperCase() + "]に変更されました。");
        }
        else {
            return false;
        }

        return true;

    }

    protected boolean setLimit(String playerName, int limit){
        if(playerName.equals("@a")){
            for(GeppoPlayer p : playerHashtable.values()){
                p.setLimit(limit);
                p.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + p.getPlayer().getName() + "のジャンプ上限数が" + String.valueOf(limit) + "回に変更されました。");
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        player.setLimit(limit);
        player.getPlayer().sendMessage(ChatColor.BLUE+"[GeppoPlugin]:" + player.getPlayer().getName() + "のジャンプ上限数が" + String.valueOf(limit) + "回に変更されました。");

        return true;
    }

    protected boolean setParticleFlg(String playerName, boolean particleFlg){
        if(playerName.equals("@a")){
            for(GeppoPlayer p : playerHashtable.values()){
                p.setParticleFlg(particleFlg);
                p.getPlayer().sendMessage(String.format(ChatColor.BLUE + "[GeppoPlugin]:" + p.getPlayer().getName() + String.format("パーティクルが[%s]になりました。",particleFlg ? "ON" : "OFF")));
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        player.setParticleFlg(particleFlg);
        player.getPlayer().sendMessage(String.format(ChatColor.BLUE + "[GeppoPlugin]:" + player.getPlayer().getName() + String.format("パーティクルが[%s]になりました。",particleFlg ? "ON" : "OFF")));
        return true;
    }

    protected boolean setMode(String playerName, String mode){
        if(playerName.equals("@a")){
            for(GeppoPlayer p : playerHashtable.values()){
                p.setMode(mode);
                p.getPlayer().sendMessage(String.format(ChatColor.BLUE + "[GeppoPlugin]:" + p.getPlayer().getName() + String.format("モードが[%s]になりました。",mode.toUpperCase())));
            }
            return true;
        }

        if(!playerHashtable.containsKey(playerName)){
            return false;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        player.setMode(mode);
        player.getPlayer().sendMessage(String.format(ChatColor.BLUE + "[GeppoPlugin]:" + player.getPlayer().getName() + String.format("モードが[%s]になりました。",mode.toUpperCase())));
        return true;
    }

    // Private Method
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

    private GeppoPlayer getPlayerInfo(String playerName){
        if(!playerHashtable.containsKey(playerName)){
            return null;
        }
        GeppoPlayer player = playerHashtable.get(playerName);
        return playerHashtable.get(playerName);
    }

    private void spawnParticle(Player p){
        Location location = p.getLocation();
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
