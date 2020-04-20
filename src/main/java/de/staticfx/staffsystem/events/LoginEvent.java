package de.staticfx.staffsystem.events;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.db.AdminDAO;
import de.staticfx.staffsystem.db.BanDAO;
import de.staticfx.staffsystem.db.ReportDAO;
import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.ChatLog;
import de.staticfx.staffsystem.objects.Report;
import de.staticfx.staffsystem.objects.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {


        if(!Main.updaterStarted) {
            Scoreboard.startUpdater();
            Main.updaterStarted = true;
        }
        Scoreboard.generateScoreboard(e.getPlayer());


        Player player = e.getPlayer();
        if(!Main.playerMessageHashMap.containsKey(player.getUniqueId())) {
            Main.playerMessageHashMap.put(player.getUniqueId(), new ChatLog(new ArrayList<>(), new ArrayList<>()));
        }

        try {
            for(Report report : ReportDAO.INSTANCE.getAllReports()) {
                if(report.getEditingPlayer() != null) {
                    if(report.getEditingPlayer().getUniqueId() == player.getUniqueId()) {
                        player.sendMessage("§cYOU ARE EDITING A REPORT AT THE MOMENT. PLEASE FINISH IT AS SOON AS POSSIBLE.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        UUID uuid = player.getUniqueId();

        if(player.hasPermission("staffsystem.ban.ignore")) {
            try {
                if(!AdminDAO.INSTANCE.isPlayerUnbannable(player.getUniqueId()))
                AdminDAO.INSTANCE.addPlayer(uuid);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }else{
            try {
                if(AdminDAO.INSTANCE.isPlayerUnbannable(player.getUniqueId()))
                    AdminDAO.INSTANCE.removePlayer(uuid);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {
            if(BanDAO.INSTANCE.hasActiveBans(uuid)) {
                for(Ban ban : BanDAO.INSTANCE.getAllBans(uuid)) {
                    if(ban.isActive()) {
                        if(ban.getEndTime() > System.currentTimeMillis() || ban.isPermanent()) {
                            String timeDisplay;
                            if(ban.isPermanent()) {
                                timeDisplay = "PERMANENT";
                            }else{
                                timeDisplay = ban.getUnbannendDate();
                            }
                            player.kickPlayer((Main.getInstance().getBanScreen().replaceAll("%reason%",ban.getReason()).replaceAll("%banid%",Integer.toString(ban.getBanid())).replaceAll("%punisher%",ban.getPunisher()).replaceAll("%unbanned%",timeDisplay)));
                        }else{
                            BanDAO.INSTANCE.setBanActivity(false,ban.getBanid());
                            if(ban.getReason().startsWith("Vote:")) {
                                BanDAO.getInstance().removeBan(ban.getBanid());
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        if(player.hasPermission("sts.team")) {
            player.sendTitle("§aPlease don´t forget to login","§c/login [password]");
            Main.team.add(player);
        }

        try {
            if(!AccountDAO.getInstance().hasAccount(uuid)) {
                if(player.hasPermission("sts.team")) {
                    player.sendMessage((Main.getPrefix() + " §aYou don´t have a password yet. Create one by using \n§a/password [create] [password] [password]"));
                }
            }
        } catch (SQLException ex) {
            player.sendMessage(Main.getInstance().getConfigString("InternalError","BanPrefix"));
            ex.printStackTrace();
        }


    }

}
