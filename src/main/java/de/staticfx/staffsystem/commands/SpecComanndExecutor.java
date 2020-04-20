package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecComanndExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command command, String string, String[] args) {

        if(!(s instanceof Player)) {
            s.sendMessage("Du musst ein Spieler sein");
            return false;
        }
        Player p = ((Player) s);

        if(!p.hasPermission("sts.reports")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","ReportPrefix"));
            return false;
        }

        if(args.length != 0) {
            p.sendMessage(Main.reportprefix + "§cUse /spec");
            return false;
        }

        if(p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.CREATIVE) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage("§aSpec mode activated.");
        }else{
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage("§cSpec mode deactivated.");
        }


        return false;
    }
}
