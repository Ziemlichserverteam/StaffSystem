package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.sql.SQLException;

public class TCCommandExecutor implements CommandExecutor {




    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(("You must be a player!"));
            return false;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("sts.team")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","StaffPrefix"));
            return false;
        }

        try {
            if(!AccountDAO.getInstance().hasAccount(p.getUniqueId())) {
                p.sendMessage(Main.getInstance().getConfigString("CreateAccountFirst","StaffPrefix"));
                return false;
            }
        } catch (SQLException e) {
            p.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
            e.printStackTrace();
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","StaffPrefix"));
            return false;
        }

        if(Main.teamChatUser.contains(p)) {
            Main.teamChatUser.remove(p);
            p.sendMessage(Main.getInstance().getConfigString("LeftTC","StaffPrefix"));
        }else{
            Main.teamChatUser.add(p);
            p.sendMessage(Main.getInstance().getConfigString("JoinedTC","StaffPrefix"));
        }

        return false;

    }
}
