package de.staticfx.staffsystem.commands;


import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.*;
import de.staticfx.staffsystem.filemanagment.ConfigManagment;
import de.staticfx.staffsystem.filemanagment.IDManagment;
import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.ID;
import de.staticfx.staffsystem.objects.Mute;
import de.staticfx.staffsystem.objects.Report;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PunishCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(("You must be a player!"));
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.punish")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
            return false;
        }

        if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getConfigString("LoginFirst","BanPrefix"));
            return false;
        }

        if(args.length == 0) {
            p.sendMessage((Main.banPrefix + "§a Valid IDS:"));
            p.sendMessage((" "));
            for(ID id : IDManagment.INSTANCE.getAllIDs()) {
                String time = id.getTime();
                if(time.endsWith( "p")) time = "permanent";
                p.sendMessage((Main.getInstance().getIdFormat().replaceAll("%id%",Integer.toString(id.getId())).replaceAll("%reason%",id.getReason()).replaceAll("%time%",time).replaceAll("%type%",id.getType().toString())));
            }
             return false;
        }

        if(args[0].equalsIgnoreCase("help")) {
            p.sendMessage( Main.banPrefix + "§4StaticStaffSystem");
            p.sendMessage("§7/punish add [PLAYER] [ID] §8-> §ebans the player");
            p.sendMessage("§7/punish remove [BANID] §8-> §eremoves a ban");
            p.sendMessage("§7/punish edit [BANID] [TIME] §8-> §esets a ban with a new time");
            p.sendMessage("§7/punish delete [BANID] §8-> §eremoves a ban from the database");
            p.sendMessage("§7/punish help §8-> §eshows a list of usefully commands");
            return true;
        }

        if(args[0].equalsIgnoreCase("add")) {
            if(args.length != 3) {
                p.sendMessage((Main.banPrefix + " §cUse: /punish add [PLAYER] [ID]"));
                if(IDManagment.INSTANCE.getAllIDs().isEmpty()) {
                    p.sendMessage((Main.banPrefix + " §cThere are no IDS yet."));
                    return false;
                }
                return false;
            }

            int id;

            try{
                id = Integer.parseInt(args[2]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanPrefix"));
                return false;
            }

            if(!IDManagment.INSTANCE.doesIDExist(id)) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidID","BanPrefix"));
                return false;
            }

            ID ID = IDManagment.INSTANCE.getID(id);
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

            if(player == null) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","BanPrefix"));
                return false;
            }
            if(!p.hasPermission("sts.hartban")) {
                if(Main.perms.playerHas(null,player,"sts.softteam")) {
                    p.sendMessage(Main.getInstance().getConfigString("SameGroup","BanPrefix"));
                    return false;
                }
            }


            try {
                if(AdminDAO.INSTANCE.isPlayerUnbannable(player.getUniqueId())) {
                    p.sendMessage(Main.getInstance().getConfigString("Ubannable","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }


            if(ID.getType().toString().toUpperCase().equals("BAN")) {
                if(!p.hasPermission("sts.ban")) {
                    p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
                    return false;
                }
                try {
                    if(BanDAO.getInstance().hasActiveBans(player.getUniqueId())) {
                        p.sendMessage(Main.getInstance().getConfigString("PlayerAlreadyBanned","BanPrefix"));
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }

                long time = Main.getInstance().timeToMilliSeconds(ID.getTime());
                boolean extended = false;

                try {
                    if(BanDAO.getInstance().getBansForReasonAmount(player.getUniqueId(), ID.getReason()) == 1) {
                        time = time * 2;
                        extended = true;
                    }else if(BanDAO.getInstance().getBansForReasonAmount(player.getUniqueId(), ID.getReason()) > 2) {
                        ID.setPermanent(true);
                        extended = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }

                int banID;
                Random rand = new Random();
                int random = rand.nextInt(100000);

                while (true) {
                    try {
                        if (!BanDAO.INSTANCE.doesBanExist(random) && !MuteDAO.INSTANCE.doesMuteExist(random)) {
                            break;
                        }else{
                            random = rand.nextInt();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                        return false;
                    }
                }

                banID = random;

                try {
                    BanDAO.INSTANCE.createBan(new Ban(banID, player.getUniqueId(),ID.getReason(),System.currentTimeMillis() + time, p.getName(), System.currentTimeMillis(), ID.getType(), ID.isPermanent(), new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis() + time)), true));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }

                if(extended) {
                    p.sendMessage(Main.getInstance().getConfigString("AutomatticlyExtended","BanPrefix"));
                }

                String timeFormat;

                if(ID.isPermanent()) {
                    timeFormat = "PERMANENT";
                }else{
                    timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis() + time));
                }


                for(Player sender : Main.team) {
                    sender.sendMessage((Main.getInstance().getNotifyFormat().replaceAll("%player%",player.getName()).replaceAll("%punisher%",p.getName()).replaceAll("%id%",Integer.toString(ID.getId())).replaceAll("%banid%",Integer.toString(random))));;
                }


                if(player.getPlayer() != null) {
                    player.getPlayer().kickPlayer((Main.getInstance().getBanScreen().replaceAll("%reason%",ID.getReason()).replaceAll("%banid%",Integer.toString(random)).replaceAll("%punisher%",p.getName()).replaceAll("%unbanned%",timeFormat)));
                }

                try {
                    for(Report report : ReportDAO.INSTANCE.getAllReports()) {
                        if(report.getEditingPlayer() != null) {
                            if(report.getEditingPlayer().getUniqueId() == p.getUniqueId()) {
                                p.performCommand("reports finish true");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


                return false;
            }else if(ID.getType().toString().toUpperCase().equals("MUTE")) {

                try {
                    if (MuteDAO.getInstance().hasActiveMutes(player.getUniqueId())) {
                        p.sendMessage(Main.getInstance().getConfigString("PlayerAlreadyBanned","BanPrefix"));
                        return false;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }

                long time = Main.getInstance().timeToMilliSeconds(ID.getTime());

                boolean extended = false;


                try {
                    if (MuteDAO.getInstance().getMutesForReasonAmount(p.getUniqueId(), ID.getReason()) == 1) {
                        time = Main.getInstance().timeToMilliSeconds(ID.getReason()) * 2;
                        extended = true;
                    }else if(MuteDAO.getInstance().getMutesForReasonAmount(p.getUniqueId(), ID.getReason()) > 2) {
                        ID.setPermanent(true);
                        extended = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }


                int banID;

                Random rand = new Random();

                int random = rand.nextInt(100000);

                while (true) {
                    try {
                        if (!(MuteDAO.INSTANCE.doesMuteExist(random) && (MuteDAO.getInstance().doesMuteExist(random)))) {
                            break;
                        }
                        random = rand.nextInt();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                        return false;
                    }
                }

                banID = random;

                String timeDisplay;

                if (ID.isPermanent()) {
                    timeDisplay = "PERMANENT";
                } else {
                    timeDisplay = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis() + time));
                }

                try {
                    MuteDAO.INSTANCE.createBan(new Mute(banID, player.getUniqueId(), ID.getReason(), System.currentTimeMillis() + time, p.getName(), System.currentTimeMillis(), ID.getType(), ID.isPermanent(), timeDisplay, true));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                    return false;
                }

                if(extended) {
                    p.sendMessage(Main.getInstance().getConfigString("AutomatticlyExtended","BanPrefix"));
                }

                for (Player sender : Main.team) {
                    sender.sendMessage((Main.getInstance().getNotifyFormat().replaceAll("%player%",player.getName()).replaceAll("%punisher%",p.getName()).replaceAll("%id%",Integer.toString(ID.getId())).replaceAll("%banid%",Integer.toString(random))));;
                }

                try {
                    for(Report report : ReportDAO.INSTANCE.getAllReports()) {
                        if(report.getEditingPlayer() != null) {
                            if(report.getEditingPlayer().getUniqueId() == p.getUniqueId()) {
                                p.performCommand("reports finish true");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                return false;

            }
        }else if(args[0].equalsIgnoreCase("reload")) {
            if(!p.hasPermission("sts.reload")) {
                p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
                return false;
            }
            IDManagment.INSTANCE.saveFile();
            IDManagment.INSTANCE.loadFile();
            ConfigManagment.INSTANCE.saveFile();
            ConfigManagment.INSTANCE.loadFile();
            p.sendMessage(Main.getInstance().getConfigString("Reloaded","BanPrefix"));
            return false;
        }else if(args[0].equalsIgnoreCase("remove")) {
            if(!p.hasPermission("sts.remove")) {
                p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
                return false;
            }
            if(args.length != 2) {
                p.sendMessage((Main.banPrefix + "§c Use: /punish remove [BANID]"));
                return false;
            }

            int banid;

            try{
                banid = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanPrefix"));
                return false;
            }

            try {
                if(BanDAO.INSTANCE.doesBanExist(banid)) {
                    if(!BanDAO.INSTANCE.getBan(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }

                    BanDAO.INSTANCE.setBanActivity(false,banid);
                    p.sendMessage(Main.getInstance().getConfigString("SetBanIDToInactive","BanPrefix"));
                    return false;

                }else if(MuteDAO.INSTANCE.doesMuteExist(banid)) {
                    if(!MuteDAO.INSTANCE.getMute(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }
                    MuteDAO.INSTANCE.setMuteActivity(false,banid);
                    p.sendMessage(Main.getInstance().getConfigString("SetBanIDToInactive","BanPrefix"));
                    return false;
                }else{
                    p.sendMessage(Main.getInstance().getConfigString("BanDoesNotExist","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }


        }else if(args[0].equalsIgnoreCase("edit")) {
            if(!p.hasPermission("sts.edit")) {
                p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
                return false;
            }
            if(args.length != 3) {
                p.sendMessage((Main.banPrefix + "§c Use: /punish edit [BANID] [TIME]"));
                return false;
            }

            int banid;
            boolean permanent = false;

            try{
                banid = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanPrefix"));
                return false;
            }

            String time = args[2];

            if(!Main.getInstance().validTime(time)) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidTime","BanPrefix"));
                return false;
            }

            if(time.endsWith("p"))
                permanent = true;

            try {
                if(BanDAO.INSTANCE.doesBanExist(banid)) {
                    if(!BanDAO.INSTANCE.getBan(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }

                    if(permanent) {
                        BanDAO.INSTANCE.setBanPermanent(true,banid);
                        p.sendMessage(Main.getInstance().getConfigString("RefreshedEndtime","BanPrefix"));
                        return false;
                    }else{
                        p.sendMessage(Long.toString(Main.getInstance().timeToMilliSeconds(time)));
                        BanDAO.INSTANCE.setBanPermanent(false,banid);
                        BanDAO.INSTANCE.setBanEndTime(System.currentTimeMillis() + Main.getInstance().timeToMilliSeconds(time),banid);
                        p.sendMessage(Main.getInstance().getConfigString("RefreshedEndtime","BanPrefix"));
                        return false;
                    }

                }else if(MuteDAO.INSTANCE.doesMuteExist(banid)) {
                    if(!MuteDAO.INSTANCE.getMute(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }

                    if(permanent) {
                        MuteDAO.INSTANCE.setMutePermanent(true,banid);
                        p.sendMessage(Main.getInstance().getConfigString("RefreshedEndtime","BanPrefix"));
                        return false;
                    }else{
                        p.sendMessage(Long.toString(Main.getInstance().timeToMilliSeconds(time)));
                        MuteDAO.INSTANCE.setMutePermanent(false,banid);
                        MuteDAO.INSTANCE.setMuteEndTime(System.currentTimeMillis() + Main.getInstance().timeToMilliSeconds(time),banid);
                        p.sendMessage(Main.getInstance().getConfigString("RefreshedEndtime","BanPrefix"));
                        return false;
                    }
                }else{
                    p.sendMessage(Main.getInstance().getConfigString("BanDoesNotExist","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }
        }else if(args[0].equalsIgnoreCase("delete")) {
            if(!p.hasPermission("sts.delete")) {
                p.sendMessage(Main.getInstance().getConfigString("NoPermission","BanPrefix"));
                return false;
            }
            if(args.length != 2) {
                p.sendMessage((Main.banPrefix + "§c Use: /punish delete [BANID]"));
                return false;
            }

            int banid;


            try{
                banid = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidNumber","BanPrefix"));
                return false;
            }


            try {
                if(BanDAO.INSTANCE.doesBanExist(banid)) {
                    if(!BanDAO.INSTANCE.getBan(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }

                    BanDAO.INSTANCE.removeBan(banid);
                    p.sendMessage(Main.getInstance().getConfigString("BanRemoved","BanPrefix"));
                    return false;

                }else if(MuteDAO.INSTANCE.doesMuteExist(banid)) {
                    if(!MuteDAO.INSTANCE.getMute(banid).isActive()) {
                        p.sendMessage(Main.getInstance().getConfigString("PunishAlreadyInactive","BanPrefix"));
                        return false;
                    }
                    MuteDAO.INSTANCE.removeMute(banid);
                    p.sendMessage(Main.getInstance().getConfigString("BanRemoved","BanPrefix"));
                    return false;

                }else{
                    p.sendMessage(Main.getInstance().getConfigString("BanDoesNotExist","BanPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
                return false;
            }
        }
        p.sendMessage((Main.banPrefix + "§a Valid IDS:"));
        p.sendMessage((" "));
        for(ID id : IDManagment.INSTANCE.getAllIDs()) {
            String time = id.getTime();
            if(time.endsWith( "p")) time = "permanent";
            p.sendMessage((Main.getInstance().getIdFormat().replaceAll("%id%",Integer.toString(id.getId())).replaceAll("%reason%",id.getReason()).replaceAll("%time%",time).replaceAll("%type%",id.getType().toString())));
        }
        return false;
    }
}
