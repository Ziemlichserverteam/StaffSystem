package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.objects.Shop;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command command, String string, String[] args) {

        if(!(s instanceof Player)) {
            s.sendMessage("Du musst ein Spieler sein");
            return false;
        }
        Player p = ((Player) s);

        if(args.length != 0) {
            p.sendMessage(Main.shopPrefix + "§cUse: /shop");
            return false;
        }

        if(!p.hasPermission("sts.shop")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","ShopPrefix"));
            return false;
        }


        if(p.hasPermission("sts.team")) {
            Main.getShop().openShop(p);
            return false;
        }

        if(p.getGameMode() == GameMode.CREATIVE) {
            p.sendMessage(Main.getInstance().getConfigString("CantOpenInGamemode","ShopPrefix"));
            return false;
        }

        Main.getShop().openShop(p);
        return false;
    }
}
