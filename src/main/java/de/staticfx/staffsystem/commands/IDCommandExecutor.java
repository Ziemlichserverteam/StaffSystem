package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.filemanagment.IDManagment;
import de.staticfx.staffsystem.objects.ID;
import de.staticfx.staffsystem.objects.Type;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IDCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage((Main.banPrefix + "§c You must be a player!"));
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.id")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","BanPrefix"));
            return false;
        }

        if(args.length != 5) {
            p.sendMessage((Main.banPrefix + " §cUse: /id create [ID] [Reason] [Time] [Type]"));
            return false;
        }

        if(args[0].equalsIgnoreCase("create")) {
            int id;

            try{
                id = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanPrefix"));
                return false;
            }

            if(IDManagment.INSTANCE.doesIDExist(id)) {
                p.sendMessage(Main.getInstance().getConfigString("IDAlreadyExists","BanPrefix"));
                return false;
            }

            String reason = args[2];

            if(!Main.getInstance().validTime(args[3])) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidTime","BanPrefix"));
                return false;
            }

            String time = args[3];
            boolean permanent = args[3].endsWith("p");

            Type type;

            try{
                type = Type.valueOf(args[4]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidTYPE","BanPrefix"));
                return false;
            }

            ID ID = new ID(id, reason, type, time, permanent);

            IDManagment.INSTANCE.saveID(ID);

            p.sendMessage(Main.getInstance().getConfigString("SavedID","BanPrefix"));
            return false;



        }
        p.sendMessage((Main.banPrefix + " §cUse: /id create [ID] [Reason] [Time] [Type]"));
        return false;
    }

}
