package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.db.BanDAO;
import de.staticfx.staffsystem.db.MuteDAO;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class HistoryCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player!");
            return false;
        }

        Player p = (Player) commandSender;


        if(!p.hasPermission("sts.history")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","StaffPrefix"));
            return false;
        }

        if(args.length != 3) {
            p.sendMessage((Main.banPrefix + "§cUse /history [PLAYER] [BAN/MUTE] CLEAR"));
            return false;
        }


        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);

        if(op == null) {
            p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","BanPrefix"));
            return false;
        }

        if(!args[2].equalsIgnoreCase("clear")) {
            p.sendMessage((Main.banPrefix + "§cUse /history [PLAYER] [BAN/MUTE] CLEAR"));
            return false;
        }


        if(args[1].equalsIgnoreCase("ban")) {
            try {
                if(BanDAO.INSTANCE.getAllBans(op.getUniqueId()).isEmpty()) {
                    p.sendMessage(Main.getInstance().getConfigString("NoBansAtAll","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }


            try {
                BanDAO.INSTANCE.removeAllBansFromPlayer(op.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }
            p.sendMessage(Main.getInstance().getConfigString("ClearedHistory","BanPrefix"));
            return false;

        }else if(args[1].equalsIgnoreCase("mute")) {
            try {
                if(MuteDAO.INSTANCE.getAllMutes(op.getUniqueId()).isEmpty()) {
                    p.sendMessage(Main.getInstance().getConfigString("NoBansAtAll","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }


            try {
                MuteDAO.INSTANCE.removeAllMutesFromPlayer(op.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }

            p.sendMessage(Main.getInstance().getConfigString("ClearedHistory","BanPrefix"));
            return false;
        }


        p.sendMessage((Main.banPrefix + "§cUse /history [PLAYER] [BAN/MUTE] CLEAR"));
        return false;
    }

}
