package com.takumi.geppo;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.time.LocalDateTime;


public class GeppoPlayer implements Listener {
    private  Player player;
    private int jumpCount;
    private LocalDateTime beforeJumpTime;
    private boolean allowFlg;
    private int interval;
    private int multiply;
    private double velocity_Y;
    private int limit;
    private boolean particleFlg;
    private String mode;
    private String strength;

    public GeppoPlayer(Player player){
        this.player = player;
        this.beforeJumpTime = LocalDateTime.now();
        this.jumpCount = 0;
        this.allowFlg = false;
        this.interval = 1;
        this.multiply = 1;
        this.velocity_Y = 0.5D;
        this.limit = 10;
        this.particleFlg = false;
        this.mode = "normal";
        this.strength = "normal";
    }

    //Getter
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

    public int getInterval() { return interval; }

    public int getMultiply() { return multiply; }

    public double getVelocity_Y() {return velocity_Y;}

    public int getLimit() { return limit; }

    public boolean getParticleFlg() { return particleFlg; }

    public String getMode() { return mode; }

    public String getStrength() { return strength; }

    //Setter
    public void setBeforeJumpTime(LocalDateTime beforeJumpTime){
        this.beforeJumpTime = beforeJumpTime;
    }

    public void setAllowFlg(boolean flg){
        this.allowFlg = flg;
    }

    public void setInterval(int interval) { this.interval = interval; }

    public void setLimit(int limit) { this.limit = limit; }

    public void setParticleFlg(boolean particleFlg) { this.particleFlg = particleFlg; }

    public void setMode(String mode) { this.mode = mode; }



    //Public Method
    public void jumpCountUp(){
        jumpCount += 1;
    }

    public void resetJumpCount(){
        jumpCount = 0;
    }

    public boolean changeJumpStrength(String strength){
        if(!(strength.equals("normal") || strength.equals("rocket"))){
            return false;
        }
        if(strength.equals("normal")){
            this.strength = strength;
            this.multiply = 1;
            this.velocity_Y = 0.5D;
        }
        else if(strength.equals("rocket")){
            this.strength = strength;
            this.multiply = 5;
            this.velocity_Y = 2D;
        }
        return true;
    }
}
