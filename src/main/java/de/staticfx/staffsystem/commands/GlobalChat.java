package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to execute this command.");
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.globalchat")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","StaffPrefix"));
            return false;
        }

        if(args.length != 0) {
            p.sendMessage(Main.prefix + "§cUse: /globalchat");
            return false;
        }

        if(Main.globalChat) {
            Main.globalChat = false;
            p.sendMessage(Main.prefix + "§cGlobalchat set to false");
        }else{
            Main.globalChat = true;
            p.sendMessage(Main.prefix + "§cGlobalchat set to true");
        }


        return false;
    }
}
