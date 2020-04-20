package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player!");
            return false;
        }

        Player p = (Player) commandSender;


        if(!p.hasPermission("sts.kick")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","StaffPrefix"));
            return false;
        }

        if(args.length < 2) {
            p.sendMessage((Main.banPrefix + "§cUse /kick [PLAYER] [REASON...]"));
            return false;
        }


        Player target = Bukkit.getPlayer(args[0]);

        if(target == null)  {
            p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","BanPrefix"));
            return false;
        }

        String reason = "";

        for(int i = 1; i < args.length; i++) {
            reason = reason + args[i] + " ";
        }

        target.kickPlayer((reason));
        p.sendMessage((Main.banPrefix + "§aYou successfully kicked the player!"));
        return false;
    }
}
