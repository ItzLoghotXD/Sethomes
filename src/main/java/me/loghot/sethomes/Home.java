package me.loghot.sethomes;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Home {
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String world;
    private String homeName;
    private String desc = null;

    public Home(Location l) {
        this.setWorld(((World)Objects.requireNonNull(l.getWorld())).getName());
        this.setX(l.getX());
        this.setY(l.getY());
        this.setZ(l.getZ());
        this.setYaw(l.getYaw());
        this.setPitch(l.getPitch());
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String w) {
        this.world = w;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHomeName() {
        return this.homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public Location toLocation() {
        return new Location(Bukkit.getServer().getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ(), this.getPitch(), this.getYaw());
    }

    public String toString() {
        return "Home Name: " + this.getHomeName() + "\nHome Desc: " + this.getDesc() + "\nLocation: " + this.toLocation().toString() + "\n";
    }
}
