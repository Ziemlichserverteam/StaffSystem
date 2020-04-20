package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.LogDAO;
import de.staticfx.staffsystem.db.ReportDAO;
import de.staticfx.staffsystem.objects.MessageLog;
import de.staticfx.staffsystem.objects.Report;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player.");
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.reports")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","ReportPrefix"));
            return false;
        }

        if(args.length <  1) {
            p.sendMessage(Main.reportprefix + "§cUse /reports [view/edit/finish/delete]");
            return false;
        }

        if(args[0].equalsIgnoreCase("view")) {
            p.openInventory(buildPage(0));
            if(!Main.playerPageHashMap.containsKey(p)) {
                Main.playerPageHashMap.put(p,0);
            }else{
                Main.playerPageHashMap.replace(p,0);
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("edit")) {
            if(args.length != 2) {
                p.sendMessage(Main.reportprefix + "§cUse /reports edit [ID]");
                return false;
            }

            try {
                if(ReportDAO.getInstance().isPlayerEditing(p)) {
                    p.sendMessage(Main.getInstance().getConfigString("AlreadyEditing","ReportPrefix"));
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }

            int id;
            try{
                id = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidID","ReportPrefix"));
                return false;
            }
            Report report;
            OfflinePlayer reportedPlayer;
            try {
                 report = ReportDAO.getInstance().getReport(id);
                 if(ReportDAO.INSTANCE.getReport(id) == null) {
                     p.sendMessage(Main.getInstance().getConfigString("ReportDoesNotExist","ReportPrefix"));
                     return false;
                 }
                 reportedPlayer = report.getReportedPlayer();
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }

            if(report.getEditingPlayer() != null) {
                p.sendMessage(Main.getInstance().getConfigString("AlreadyEdited","ReportPrefix"));
                return false;
            }


            try {
                for(Report report1 : ReportDAO.getInstance().getAllReports()) {
                    if(report1.getReportedPlayer().getUniqueId() == report.getReportedPlayer().getUniqueId()) {
                        if(report1.getReason().equalsIgnoreCase(report.getReason())) {
                            Player reportingPlayer = report1.getReportingPlayer().getPlayer();
                            if(reportingPlayer != null) {
                                reportingPlayer.sendMessage(Main.getInstance().getConfigString("ReportIsNowBeeingEditied","ReportPrefix"));
                                reportingPlayer.playSound(reportingPlayer.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }




            if(isMuteReason(report.getReason())) {
                try {
                    ReportDAO.getInstance().updateEditingPlayer(id, p);

                    p.sendMessage("§aLoading log for reportid §c" + id + "§7...");
                    MessageLog log = LogDAO.getInstance().getMessageLog(id);

                    for (int i = log.getMessages().size(); i > 0 ; i-- ) {
                        String timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(log.getTimes().get(i - 1));
                        p.sendMessage("§c" + timeFormat + ": §a" + log.getMessages().get(i - 1));
                    }
                    p.sendMessage("§aMessages where send by: §c" + reportedPlayer.getName());
                }   catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                    return false;
                }
                return true;
            }

            if(reportedPlayer.getPlayer() == null) {
                p.sendMessage(Main.getInstance().getConfigString("PlayerOffline","ReportPrefix"));
                return true;
            }

            Player target = reportedPlayer.getPlayer();
            p.setGameMode(GameMode.SPECTATOR);
            p.performCommand("essentials:v on");
            Main.reportLastLocation.put(p,p.getLocation());
            p.teleport(target.getLocation());
            try {
                ReportDAO.getInstance().updateEditingPlayer(id,p);
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }
            p.sendMessage(Main.getInstance().getConfigString("NowEditing","ReportPrefix").replaceAll("%id%",Integer.toString(id)));
            return true;
        }
        if(args[0].equalsIgnoreCase("finish")) {
            if(args.length != 2 ) {
                p.sendMessage(Main.reportprefix + "§cUse /reports finish [true/false]");
                return false;
            }

            try {
                for(Report report : ReportDAO.getInstance().getAllReports()) {
                    if(report.getEditingPlayer() != null) {
                        if(report.getEditingPlayer().getUniqueId() == p.getUniqueId()) {
                            if(args[1].equalsIgnoreCase("true")) {
                                ReportDAO.getInstance().removeReport(report.getId());
                                p.sendMessage(Main.getInstance().getConfigString("ReportFinished","ReportPrefix"));
                                LogDAO.getInstance().storeLog(report.getId(),generateLegacyID());
                                deleteAllReportsWithSameReason(report);
                                if(Main.reportLastLocation.containsKey(p)) {
                                    p.teleport(Main.reportLastLocation.get(p));
                                    Main.reportLastLocation.remove(p);
                                    p.setGameMode(GameMode.SURVIVAL);
                                }
                                return true;
                            }
                            if(args[1].equalsIgnoreCase("false")) {
                                ReportDAO.getInstance().removeReport(report.getId());
                                LogDAO.getInstance().removeLog(report.getId());
                                p.sendMessage(Main.getInstance().getConfigString("ReportFinished","ReportPrefix"));
                                deleteAllReportsWithSameReason(report);

                                if(Main.reportLastLocation.containsKey(p)) {
                                    p.teleport(Main.reportLastLocation.get(p));
                                    Main.reportLastLocation.remove(p);
                                    p.setGameMode(GameMode.SURVIVAL);
                                }


                                return true;
                            }
                            p.sendMessage(Main.reportprefix + "§cUse /reports finish [true/false]");
                            return true;
                        }
                    }
                }
                p.sendMessage(Main.getInstance().getConfigString("YouAreNotEditing","ReportPrefix"));
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length != 2) {
                p.sendMessage(Main.reportprefix + "§cUse /reports delete [id]");
                return false;
            }
            int id;
            try{
                id = Integer.parseInt(args[1]);
            }catch (Exception e) {
                p.sendMessage(Main.getInstance().getConfigString("InvalidID","ReportPrefix"));
                return false;
            }
            Report report;
            try {
                report = ReportDAO.getInstance().getReport(id);
                if(ReportDAO.INSTANCE.getReport(id) == null) {
                    p.sendMessage(Main.getInstance().getConfigString("ReportDoesNotExist","ReportPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }
            try {
                ReportDAO.getInstance().removeReport(report.getId());
                if(isMuteReason(report.getReason())) {
                    LogDAO.getInstance().removeLog(id);
                }
                deleteAllReportsWithSameReason(report);
            } catch (SQLException e) {
                e.printStackTrace();
                p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                return false;
            }

            p.sendMessage(Main.getInstance().getConfigString("ReportDeleted","ReportPrefix").replaceAll("%id%",id + ""));
        }

        return false;
    }


    public static ItemStack getItemStackForReport(Report report) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§9" + (report.getId()));
        String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(report.getTimeStamp()));
        List<String> lore = new ArrayList<>(Arrays.asList("§cReported §7 - §e" + report.getReportedPlayer().getName(),"§cReporting §7 - §e" + report.getReportingPlayer().getName(),"§cAmount §7 - §e" + report.getAmount(),"§cReason §7 - §e" + report.getReason(), "§cDate §7 - §e" + time));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void deleteAllReportsWithSameReason(Report report) throws SQLException {
        for(Report reportFromDatabase: ReportDAO.INSTANCE.getAllReports()) {
            if(reportFromDatabase.getReportedPlayer().getUniqueId() == report.getReportedPlayer().getUniqueId()) {
                if(report.getReason().equalsIgnoreCase(reportFromDatabase.getReason())) ReportDAO.INSTANCE.removeReport(reportFromDatabase.getId());
            }
        }
    }

    private boolean isMuteReason(String reason) {
        for(String muteReason : Main.mutereasons) {
            if(muteReason.equalsIgnoreCase(reason)) return true;
        }
        return false;
    }

    public static Inventory buildPage(int page) {

        Inventory reports = Bukkit.createInventory(null,6*9,"§cReports");


        try {
            int max = 5*9 - 1;

            List<Report> reportList = ReportDAO.getInstance().getAllReports();

            ItemStack placeHolder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
            ItemMeta placeHolderMeta = placeHolder.getItemMeta();
            placeHolderMeta.setDisplayName("§8´");
            placeHolder.setItemMeta(placeHolderMeta);


            ItemStack itemBack = new ItemStack(Material.RED_WOOL,1);
            ItemMeta itemBackMeta = itemBack.getItemMeta();
            itemBackMeta.setDisplayName("§c<- Last page");
            itemBack.setItemMeta(itemBackMeta);

            ItemStack itemForward = new ItemStack(Material.LIME_WOOL,1);
            ItemMeta itemForwardMeta = itemForward.getItemMeta();
            itemForwardMeta.setDisplayName("§aNext page ->");
            itemForward.setItemMeta(itemForwardMeta);


            reports.setItem(6*9 - 9, placeHolder);
            reports.setItem(6*9 - 8, placeHolder);
            reports.setItem(6*9 - 7, placeHolder);
            reports.setItem(6*9 - 6, itemBack);
            reports.setItem(6*9 - 5, placeHolder);
            reports.setItem(6*9 - 4, itemForward);
            reports.setItem(6*9 - 3, placeHolder);
            reports.setItem(6*9 - 2, placeHolder);
            reports.setItem(6*9 - 1, placeHolder);
            int o = 0;
            for(int i = page * max; i < max * page + 5*9; i++) {
                try{
                    if(reportList.get(i).getEditingPlayer() == null) {
                        if(ReportDAO.getInstance().isViewAble(reportList.get(i).getId())) {
                            reports.setItem(o, getItemStackForReport(reportList.get(i)));
                        }
                    }
                }catch (IndexOutOfBoundsException e) {
                    continue;
                }
                o++;
            }



            } catch (SQLException e) {
                e.printStackTrace();
            }
        return reports;
    }

    public int generateLegacyID() throws SQLException {
        Random rand = new Random();
        int random = rand.nextInt(1000000000);
        while (true) {
            if (!LogDAO.getInstance().getStoredLogsIDs().contains(random) || random <= 100000) {
                break;
            }else{
                random = rand.nextInt();
            }
        }
        return random;
    }

}
