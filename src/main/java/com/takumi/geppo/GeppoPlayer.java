package com.takumi.geppo;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.time.LocalDateTime;


public class GeppoPlayer implements Listener {
    private  Player player;
    private int jumpCount;
    private LocalDateTime beforeJumpTime;
    private boolean allowFlg;

    public GeppoPlayer(Player player){
        this.player = player;
        this.beforeJumpTime = LocalDateTime.now();
        this.jumpCount = 0;
        this.allowFlg = false;
    }

    public void jumpCountUp(){
        jumpCount += 1;
    }

    public void resetJumpCount(){
        jumpCount = 0;
    }

    public int getJumpCount(){
        return  jumpCount;
    }

    public Player getPlayer(){
        return player;
    }

    public LocalDateTime getBeforeJumpTime(){
        return beforeJumpTime;
    }

    public boolean getAllowFlg(){
        return allowFlg;
    }

    public void setBeforeJumpTime(LocalDateTime beforeJumpTime){
        this.beforeJumpTime = beforeJumpTime;
    }

    public void setAllowFlg(boolean flg){
        this.allowFlg = flg;
    }
}
