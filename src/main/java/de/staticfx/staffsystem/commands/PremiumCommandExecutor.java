package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.PremiumDAO;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Duration;

public class PremiumCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(("You must be a player!"));
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.premium")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","PremiumPrefix"));
            return false;
        }

        if(args.length != 1) {
            p.sendMessage(Main.premiumPrefix + " §cUse: /premium [player]");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","PremiumPrefix"));
            return false;
        }

        if(!Main.perms.getPrimaryGroup(target).equalsIgnoreCase("default")) {
            p.sendMessage(Main.getInstance().getConfigString("AlreadyHasGroup","PremiumPrefix"));
            return false;
        }

        try {
            if(PremiumDAO.getInstance().gavePremium(p.getUniqueId())) {
                if(PremiumDAO.getInstance().getTimeStamp(p.getUniqueId()) < System.currentTimeMillis() + Main.getInstance().timeToMilliSeconds("7d")) {
                    p.sendMessage(Main.getInstance().getConfigString("NeedToWait","PremiumPrefix"));
                    return false;
                }else{
                    PremiumDAO.getInstance().removePremiumDate(p.getUniqueId());
                    PremiumDAO.getInstance().addPremiumDate(p.getUniqueId());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " +  target.getName() + " parent addtemp premium 7d");
                    p.sendMessage(Main.getInstance().getConfigString("SetPremium","PremiumPrefix").replaceAll("%player%",target.getName()));
                    target.sendMessage(Main.getInstance().getConfigString("GotPremium","PremiumPrefix").replaceAll("%player%",p.getName()));

                    return true;
                }
            }else{
                PremiumDAO.getInstance().addPremiumDate(p.getUniqueId());
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " +  target.getName() + " parent addtemp premium 7d");
                p.sendMessage(Main.getInstance().getConfigString("SetPremium","PremiumPrefix").replaceAll("%player%",target.getName()));
                target.sendMessage(Main.getInstance().getConfigString("GotPremium","PremiumPrefix").replaceAll("%player%",p.getName()));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        p.sendMessage(Main.premiumPrefix + " §cUse: /premium [player]");
        return false;
    }
}
