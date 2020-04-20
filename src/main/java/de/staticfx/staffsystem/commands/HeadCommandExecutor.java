package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.HeadDAO;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HeadCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to execute this command.");
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.head")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","HeadPrefix"));
            return false;
        }

        if(args.length != 1) {
            p.sendMessage(Main.headPrefix + "§cUse: /head [name]");
            return false;
        }

        String name = args[0];


        if(Main.perms.getPrimaryGroup(p).equalsIgnoreCase("agent")) {
            try {
                if(HeadDAO.getInstance().gaveHead(p.getUniqueId())) {
                    if(HeadDAO.getInstance().getTimeStamp(p.getUniqueId()) < HeadDAO.getInstance().getTimeStamp(p.getUniqueId()) + Duration.ofDays(28).toMillis()) {
                        p.sendMessage(Main.getInstance().getConfigString("AlreadyGaveHeadAgent","HeadPrefix"));
                        return false;
                    }else{
                        HeadDAO.getInstance().removeHeadDate(p.getUniqueId());
                        HeadDAO.getInstance().addHeadDate(p.getUniqueId());
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setOwner(name);
                        meta.setDisplayName(name);
                        skull.setItemMeta(meta);
                        p.getInventory().addItem(skull);
                        p.sendMessage(Main.getInstance().getConfigString("GaveSkull","HeadPrefix").replaceAll("%player%",name));
                        return true;
                    }
                }else{
                    HeadDAO.getInstance().addHeadDate(p.getUniqueId());
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(name);
                    meta.setDisplayName(name);
                    skull.setItemMeta(meta);
                    p.getInventory().addItem(skull);
                    p.sendMessage(Main.getInstance().getConfigString("GaveSkull","HeadPrefix").replaceAll("%player%",name));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }





        }else if(Main.perms.getPrimaryGroup(p).equalsIgnoreCase("ziemlich") || p.hasPermission("sts.softteam")) {
            try {
                if (HeadDAO.getInstance().gaveHead(p.getUniqueId())) {
                    if (HeadDAO.getInstance().getTimeStamp(p.getUniqueId()) < HeadDAO.getInstance().getTimeStamp(p.getUniqueId()) + Duration.ofDays(14).toMillis()) {
                        p.sendMessage(Main.getInstance().getConfigString("AlreadyGaveHeadZiemlich", "HeadPrefix"));
                        return false;
                    } else {
                        HeadDAO.getInstance().removeHeadDate(p.getUniqueId());
                        HeadDAO.getInstance().addHeadDate(p.getUniqueId());
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setOwner(name);
                        meta.setDisplayName(name);
                        skull.setItemMeta(meta);
                        p.getInventory().addItem(skull);
                        p.sendMessage(Main.getInstance().getConfigString("GaveSkull", "HeadPrefix").replaceAll("%player%", name));
                        return true;
                    }
                } else {
                    HeadDAO.getInstance().addHeadDate(p.getUniqueId());
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(name);
                    meta.setDisplayName(name);
                    skull.setItemMeta(meta);
                    p.getInventory().addItem(skull);
                    p.sendMessage(Main.getInstance().getConfigString("GaveSkull", "HeadPrefix").replaceAll("%player%", name));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }



                return false;


        }

        commandSender.sendMessage("You must be a player to execute this command.");

        return false;
    }
}
