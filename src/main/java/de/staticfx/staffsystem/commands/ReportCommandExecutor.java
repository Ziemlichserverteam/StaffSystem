package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.LogDAO;
import de.staticfx.staffsystem.db.ReportDAO;
import de.staticfx.staffsystem.objects.Report;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Random;

public class ReportCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {


        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cYou don´t have the permissions for this command.");
            return false;
        }

        Player p = (Player) commandSender;

        if(!p.hasPermission("sts.report")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","ReportPrefix"));
            return false;
        }

        if(args.length != 2) {
            p.sendMessage((Main.reportprefix + "§c Use: /report [user] [reason]"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            p.sendMessage(Main.getInstance().getConfigString("InvalidPlayer","ReportPrefix"));
            return false;
        }

        if(target.getUniqueId() == p.getUniqueId()) {
            p.sendMessage(Main.getInstance().getConfigString("CantReportYourself","ReportPrefix"));
            return false;
        }


        String givenReason = args[1];
        if(!doesReasonExist(givenReason)) {
            p.sendMessage((Main.reportprefix + "§cPlease use a valid reason."));
            for(String reason : Main.reasons) {
                p.sendMessage("§7" + reason);
            }
            return false;
        }

        try {
            for(Report report : ReportDAO.getInstance().getAllReports()) {
                if(report.getReportedPlayer().getUniqueId() == target.getUniqueId()) {
                    if(report.getReason().equalsIgnoreCase(givenReason)) {
                        if(report.getReportingPlayer().getUniqueId() == p.getUniqueId()) {
                            p.sendMessage(Main.getInstance().getConfigString("AlreadyReported","ReportPrefix"));
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(Main.getInstance().getConfigString("InternalError"));
            return false;
        }

        for(String muteReason : Main.mutereasons) {
            if(muteReason.equalsIgnoreCase(givenReason)) {
                if(Main.playerMessageHashMap.get(target.getUniqueId()).getMessages().size() < 1) {
                    p.sendMessage(Main.getInstance().getConfigString("DidNotWriteAnyMessage","ReportPrefix"));
                    return false;
                }
            }
        }

        Report report;
        int id;
        try {
            id = generateID();
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(Main.getInstance().getConfigString("InternalError"));
            return false;
        }


        try {
            for(Report report1 : ReportDAO.getInstance().getAllReports()) {
                if(report1.getReportedPlayer().getUniqueId() == target.getUniqueId()) {
                    if(report1.getReason().equalsIgnoreCase(givenReason)) {

                        report = new Report(target, p, null, givenReason, id, System.currentTimeMillis(),1);
                        ReportDAO.getInstance().createReport(report,false);
                        ReportDAO.getInstance().setAmount(report1.getId(), report1.getAmount() + 1);

                        p.sendMessage(Main.getInstance().getConfigString("ReportCreated","ReportPrefix"));
                        for(Player team : Main.team) {
                            team.sendMessage((Main.getInstance().getReportFormat().replaceAll("%player%",target.getName()).replaceAll("%punisher%",p.getName()).replaceAll("%id%",givenReason)).replaceAll("%banid%",Integer.toString(id)));
                            net.md_5.bungee.api.chat.TextComponent textC = new net.md_5.bungee.api.chat.TextComponent();
                            textC.setText("§a§lClick here to edit");
                            textC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aEdit the report.").create()));
                            textC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports edit " + report1.getId()));
                            team.spigot().sendMessage(textC);
                        }

                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(Main.getInstance().getConfigString("InternalError"));
            return false;
        }

        report = new Report(target, p, null, givenReason, id, System.currentTimeMillis(),1);


        try {
            ReportDAO.getInstance().createReport(report,true);
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(Main.getInstance().getConfigString("InternalError"));
            return false;
        }

        p.sendMessage(Main.getInstance().getConfigString("ReportCreated","ReportPrefix"));
        for(Player team : Main.team) {
            team.sendMessage((Main.getInstance().getReportFormat().replaceAll("%player%",target.getName()).replaceAll("%punisher%",p.getName()).replaceAll("%id%",givenReason)).replaceAll("%banid%",Integer.toString(id)));
            net.md_5.bungee.api.chat.TextComponent textC = new net.md_5.bungee.api.chat.TextComponent();
            textC.setText("§a§lClick here to edit");
            textC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aEdit the report.").create()));
            textC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports edit " + id));
            team.spigot().sendMessage(textC);
        }

        if(isMuteReason(givenReason)) {
            if(Main.playerMessageHashMap.get(target.getUniqueId()).getMessages().size() > 0) {
                try {
                    LogDAO.getInstance().createLog(id, target, Main.playerMessageHashMap.get(target.getUniqueId()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(Main.getInstance().getConfigString("InternalError"));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean doesReasonExist(String reason) {
        for(String reasonFromList : Main.reasons) {
            if(reason.equalsIgnoreCase(reasonFromList)) return true;
        }
        return false;
    }

    private int generateID() throws SQLException {
        Random rand = new Random();
        int random = rand.nextInt(100000);
        while (true) {
            if (!ReportDAO.INSTANCE.doesReportExist(random)) {
                break;
            }else{
                random = rand.nextInt();
            }
        }
        return random;
    }

    private boolean isMuteReason(String reason) {
        for(String muteReason : Main.mutereasons) {
            if(muteReason.equalsIgnoreCase(reason)) return true;
        }
        return false;
    }


}
