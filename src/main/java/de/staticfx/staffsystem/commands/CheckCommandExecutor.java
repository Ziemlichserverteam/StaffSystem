package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.db.BanDAO;
import de.staticfx.staffsystem.db.MuteDAO;
import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.Mute;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player!");
            return false;
        }

        Player p = (Player) commandSender;


        if(!p.hasPermission("sts.check")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","StaffPrefix"));
            return false;
        }

        if(args.length < 1) {
            p.sendMessage((Main.banPrefix + "§cUse /check BANID/PLAYER [BANID/PLAYER]"));
            return false;
        }

        if(args[0].equalsIgnoreCase("banid")) {

            if(args.length != 2) {
                p.sendMessage((Main.banPrefix + "§cUse /check BANID [BANID]"));
                return false;
            }

            int banID;

            try{
                banID = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanSystem"));
                return false;
            }


            try {
                if(BanDAO.INSTANCE.doesBanExist(banID)) {
                    Ban ban = BanDAO.INSTANCE.getBan(banID);
                    p.sendMessage((Main.banPrefix + "§aInformation about §c" + banID));
                    p.sendMessage((" "));
                    p.sendMessage(("§7Type » §c" + ban.getType().toString()));
                    p.sendMessage(("§7Reason » §c" + ban.getReason()));
                    p.sendMessage(("§7Endtime » §c" + ban.getUnbannendDate()));
                    p.sendMessage(("§7Punisher » §c" + ban.getPunisher()));
                    p.sendMessage(("§7Banned On » §c" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(ban.getTimestamp()))));
                    return false;
                }else if(MuteDAO.INSTANCE.doesMuteExist(banID)){
                    Mute ban = MuteDAO.INSTANCE.getMute(banID);
                    p.sendMessage((Main.banPrefix + "§aInformation about §c" + banID));
                    p.sendMessage((" "));
                    p.sendMessage(("§7Type » §c" + ban.getType().toString()));
                    p.sendMessage(("§7Reason » §c" + ban.getReason()));
                    p.sendMessage(("§7Endtime » §c" + ban.getUnbannendDate()));
                    p.sendMessage(("§7Punisher » §c" + ban.getPunisher()));
                    p.sendMessage(("§7Banned On » §c" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(ban.getTimestamp()))));
                    return false;
                }

                p.sendMessage((Main.getInstance().getConfigString("BanDoesNotExist","BanPrefix")));

            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanSystem"));
                return false;
            }


        }else if(args[0].equalsIgnoreCase("player")) {

            if(args.length != 2) {
                p.sendMessage((Main.banPrefix + "§cUse /check PLAYER [PLAYER]"));
                return false;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

            if(offlinePlayer == null) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","BanSystem"));
                return false;
            }

            try {

                p.sendMessage((Main.banPrefix + "§7Bans from §a" + offlinePlayer.getName()));

                if(BanDAO.INSTANCE.getAllBans(offlinePlayer.getUniqueId()).isEmpty()) {
                    p.sendMessage((" "));
                    p.sendMessage(Main.getInstance().getConfigString("NoBansAtAll","BanPrefix"));
                    p.sendMessage((" "));
                }else{
                    for(Ban ban : BanDAO.INSTANCE.getAllBans(offlinePlayer.getUniqueId())) {
                        p.sendMessage((" "));
                        p.sendMessage(("§7BanID » §c" + ban.getBanid()));
                        p.sendMessage(("§7Type » §c" + ban.getType().toString()));
                        p.sendMessage(("§7Reason » §c" + ban.getReason()));
                        p.sendMessage(("§7Endtime » §c" + ban.getUnbannendDate()));
                        p.sendMessage(("§7Punisher » §c" + ban.getPunisher()));
                        p.sendMessage(("§7Active » §c" + ban.isActive()));
                        p.sendMessage(("§7Banned On » §c" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(ban.getTimestamp()))));
                        p.sendMessage((" "));
                        p.sendMessage(("§8§m-------------------------"));
                    }
                }


                p.sendMessage(Main.banPrefix + "§7Mutes from §a" + offlinePlayer.getName());

                if(MuteDAO.INSTANCE.getAllMutes(offlinePlayer.getUniqueId()).isEmpty()) {
                    p.sendMessage((" "));
                    p.sendMessage(Main.getInstance().getConfigString("NoBansAtAll","BanPrefix"));
                    p.sendMessage((" "));
                }else{
                    for(Mute ban : MuteDAO.INSTANCE.getAllMutes(offlinePlayer.getUniqueId())) {
                        p.sendMessage((" "));
                        p.sendMessage(("§7BanID » §c" + ban.getBanid()));
                        p.sendMessage(("§7Type » §c" + ban.getType().toString()));
                        p.sendMessage(("§7Reason » §c" + ban.getReason()));
                        p.sendMessage(("§7Endtime » §c" + ban.getUnbannendDate()));
                        p.sendMessage(("§7Punisher » §c" + ban.getPunisher()));
                        p.sendMessage(("§7Active » §c" + ban.isActive()));
                        p.sendMessage(("§7Banned On » §c" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(ban.getTimestamp()))));
                        p.sendMessage((" "));
                        p.sendMessage(("§8§m-------------------------"));
                    }
                }
                return false;

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        p.sendMessage((Main.banPrefix + "§cUse /check BANID/PLAYER [BANID/PLAYER]"));

        return false;
    }


}
