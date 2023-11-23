package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Gohome implements CommandExecutor, Listener {
    private final Sethomes pl;
    private int taskId;
    private final int cooldown;
    private final Map<String, Long> cooldownList = new HashMap();
    private final boolean cancelOnMove;
    private Location locale = null;
    private Player p;

    public Gohome(Sethomes plugin) {
        this.pl = plugin;
        this.cooldown = this.pl.getConfig().getInt("tp-cooldown");
        this.cancelOnMove = this.pl.getConfig().getBoolean("tp-cancelOnMove");
        this.pl.getServer().getPluginManager().registerEvents(this, this.pl);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else {
            this.p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("home")) {
                if (this.isTeleporting()) {
                    Chatutils.sendError(this.p, "You may not use this command while teleporting!");
                    return true;
                }

                String uuid = this.p.getUniqueId().toString();
                if (!this.cooldownList.containsKey(uuid) || this.cooldown <= 0 || this.p.hasPermission("homes.config_bypass")) {
                    return this.teleportHome(this.p, uuid, args);
                }

                long timeLeft = (Long)this.cooldownList.get(uuid) / 1000L + (long)this.cooldown - System.currentTimeMillis() / 1000L;
                if (timeLeft > 0L) {
                    Chatutils.sendInfo(this.p, StringUtils.replace(this.pl.getConfig().getString("tp-cooldown-msg"), "%s", String.valueOf(timeLeft)));
                    return true;
                }

                this.cooldownList.remove(uuid);
                if (this.teleportHome(this.p, uuid, args)) {
                    return true;
                }
            }

            if (cmd.getName().equalsIgnoreCase("home-of")) {
                if (!this.p.hasPermission("homes.home-of")) {
                    Chatutils.permissionError(this.p);
                    return false;
                } else if (this.isTeleporting()) {
                    Chatutils.sendError(this.p, "You may not use this command while teleporting!");
                    return true;
                } else if (args.length >= 1 && args.length <= 2) {
                    this.locale = this.p.getLocation();
                    OfflinePlayer targetP = Bukkit.getServer().getOfflinePlayer(args[0]);
                    if (!targetP.hasPlayedBefore()) {
                        Chatutils.sendError(this.p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has never played before!");
                        return false;
                    } else {
                        String uuid = targetP.getUniqueId().toString();
                        return this.teleportHomeOf(this.p, uuid, args);
                    }
                } else {
                    Chatutils.sendError(this.p, "ERROR: Incorrect number of arguments!");
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private boolean teleportHome(final Player p, final String uuid, String[] args) {
        this.locale = p.getLocation();
        if (args.length < 1) {
            if (!this.pl.hasUnknownHomes(uuid)) {
                Chatutils.sendError(p, "You have no Default Home!");
                return false;
            } else {
                if (this.pl.getConfig().getInt("tp-delay") > 0 && !p.hasPermission("homes.config_bypass")) {
                    this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
                        int delay;

                        {
                            this.delay = Gohome.this.pl.getConfig().getInt("tp-delay");
                        }

                        public void run() {
                            if (this.delay == 0) {
                                Gohome.this.pl.cancelTask(Gohome.this.taskId);
                                p.teleport(Gohome.this.pl.getPlayersUnnamedHome(uuid));
                                p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                                p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                                Gohome.this.cooldownList.put(uuid, System.currentTimeMillis());
                            } else {
                                p.sendTitle(ChatColor.GOLD + "Teleporting in " + this.delay + "...", (String)null, 0, 20, 0);
                                p.playNote(p.getLocation(), Instrument.DIDGERIDOO, Note.sharp(2, Note.Tone.F));
                                --this.delay;
                            }

                        }
                    }, 0L, 20L);
                } else {
                    p.teleport(this.pl.getPlayersUnnamedHome(uuid));
                    p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                    p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                    Chatutils.sendSuccess(p, "You have been teleported home!");
                    this.cooldownList.put(uuid, System.currentTimeMillis());
                }

                return true;
            }
        } else if (args.length > 1) {
            Chatutils.tooManyArgs(p);
            return false;
        } else if (this.pl.hasNamedHomes(uuid) && this.pl.getPlayersNamedHomes(uuid).containsKey(args[0])) {
            final String homeName = args[0];
            if (this.pl.getConfig().getInt("tp-delay") > 0 && !p.hasPermission("homes.config_bypass")) {
                this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
                    int delay;

                    {
                        this.delay = Gohome.this.pl.getConfig().getInt("tp-delay");
                    }

                    public void run() {
                        if (this.delay == 0) {
                            Gohome.this.pl.cancelTask(Gohome.this.taskId);
                            p.teleport(Gohome.this.pl.getNamedHomeLocal(uuid, homeName));
                            p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                            p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                            Gohome.this.cooldownList.put(uuid, System.currentTimeMillis());
                        } else {
                            p.sendTitle(ChatColor.GOLD + "Teleporting in " + this.delay + "...", (String)null, 5, 5, 5);
                            p.playNote(p.getLocation(), Instrument.DIDGERIDOO, Note.sharp(2, Note.Tone.F));
                            --this.delay;
                        }

                    }
                }, 0L, 20L);
            } else {
                p.teleport(this.pl.getNamedHomeLocal(uuid, args[0]));
                p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                Chatutils.sendSuccess(p, "You have been teleported home!");
                this.cooldownList.put(uuid, System.currentTimeMillis());
            }

            return true;
        } else {
            Chatutils.sendError(p, "You have no homes by that name!");
            return false;
        }
    }

    private boolean teleportHomeOf(final Player p, final String uuid, String[] args) {
        this.locale = p.getLocation();
        if (args.length == 1) {
            if (!this.pl.hasUnknownHomes(uuid)) {
                Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has no default home set!");
                return false;
            } else {
                if (this.pl.getConfig().getInt("tp-delay") > 0 && !p.hasPermission("homes.config_bypass")) {
                    this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
                        int delay;

                        {
                            this.delay = Gohome.this.pl.getConfig().getInt("tp-delay");
                        }

                        public void run() {
                            if (this.delay == 0) {
                                Gohome.this.pl.cancelTask(Gohome.this.taskId);
                                p.teleport(Gohome.this.pl.getPlayersUnnamedHome(uuid));
                                p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                                p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                                Gohome.this.cooldownList.put(p.getUniqueId().toString(), System.currentTimeMillis());
                            } else {
                                p.sendTitle(ChatColor.GOLD + "Teleporting in " + this.delay + "...", (String)null, 0, 20, 0);
                                p.playNote(p.getLocation(), Instrument.DIDGERIDOO, Note.sharp(2, Note.Tone.F));
                                --this.delay;
                            }

                        }
                    }, 0L, 20L);
                } else {
                    p.teleport(this.pl.getPlayersUnnamedHome(uuid));
                    p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                    p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                    Chatutils.sendSuccess(p, "You have been teleported!");
                    this.cooldownList.put(p.getUniqueId().toString(), System.currentTimeMillis());
                }

                return true;
            }
        } else {
            final String homeName = args[1];
            if (this.pl.hasNamedHomes(uuid) && this.pl.getPlayersNamedHomes(uuid).containsKey(homeName)) {
                if (this.pl.getConfig().getInt("tp-delay") > 0 && !p.hasPermission("homes.config_bypass")) {
                    this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
                        int delay;

                        {
                            this.delay = Gohome.this.pl.getConfig().getInt("tp-delay");
                        }

                        public void run() {
                            if (this.delay == 0) {
                                Gohome.this.pl.cancelTask(Gohome.this.taskId);
                                p.teleport(Gohome.this.pl.getNamedHomeLocal(uuid, homeName));
                                p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                                p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                                Gohome.this.cooldownList.put(p.getUniqueId().toString(), System.currentTimeMillis());
                            } else {
                                p.sendTitle(ChatColor.GOLD + "Teleporting in " + this.delay + "...", (String)null, 5, 5, 5);
                                p.playNote(p.getLocation(), Instrument.DIDGERIDOO, Note.sharp(2, Note.Tone.F));
                                --this.delay;
                            }

                        }
                    }, 0L, 20L);
                } else {
                    p.teleport(this.pl.getNamedHomeLocal(uuid, args[1]));
                    p.spawnParticle(Particle.PORTAL, p.getLocation(), 100);
                    p.playNote(p.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                    Chatutils.sendSuccess(p, "You have been teleported!");
                    this.cooldownList.put(p.getUniqueId().toString(), System.currentTimeMillis());
                }

                return true;
            } else {
                Chatutils.sendError(p, "That user has no homes by that name!");
                return false;
            }
        }
    }

    public boolean isTeleporting() {
        Iterator var1 = Bukkit.getScheduler().getPendingTasks().iterator();

        BukkitTask t;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            t = (BukkitTask)var1.next();
        } while(t.getTaskId() != this.taskId);

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer() == this.p && this.isTeleporting() && (e.getPlayer().getLocation().getX() != this.locale.getX() || e.getPlayer().getLocation().getY() != this.locale.getY()) && this.cancelOnMove && !e.getPlayer().hasPermission("homes.config_bypass")) {
            this.pl.cancelTask(this.taskId);
            Chatutils.sendInfo(e.getPlayer(), this.pl.getConfig().getString("tp-cancelOnMove-msg"));
            e.getPlayer().playNote(e.getPlayer().getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.F));
        }

    }
}
