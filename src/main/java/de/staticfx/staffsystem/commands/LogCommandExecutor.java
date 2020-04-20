package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.LogDAO;
import de.staticfx.staffsystem.objects.MessageLog;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class LogCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to execute that command.");
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.log")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","StaffPrefix"));
            return false;
        }

        if(args.length != 2) {
            p.sendMessage((Main.getPrefix() + "§c Use: /log [load/delete] [logID]."));
            return false;
        }



        int id;
        try{
            id = Integer.parseInt(args[1]);
        }catch (Exception e) {
            p.sendMessage((Main.getPrefix() + "§c Use: /log [load/delete] [logID]."));
            return false;
        }

        try {
            if(!LogDAO.getInstance().getStoredLogsIDs().contains(id)) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidID","BanPrefix"));
                return false;
            }

            if(args[0].equalsIgnoreCase("load")) {
                p.sendMessage("§aLoading log for reportid §c" + id + "§7...");
                MessageLog log = LogDAO.getInstance().getMessageLog(id);

                for (int i = log.getMessages().size(); i > 0 ; i-- ) {
                    String timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(log.getTimes().get(i - 1));
                    p.sendMessage("§c" + timeFormat + ": §a" + log.getMessages().get(i - 1));
                }
                p.sendMessage("§aMessages where send by: §c" + LogDAO.getInstance().getPlayerForID(id).getName());
                return false;
            }
            if(args[0].equalsIgnoreCase("delete")) {
                LogDAO.getInstance().removeLog(id);
                p.sendMessage((Main.getPrefix() + "§cLog §a" + id +" deleted."));
                return true;
            }

            p.sendMessage((Main.getPrefix() + "§c Use: /log [load/delete] [logID]."));

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}
