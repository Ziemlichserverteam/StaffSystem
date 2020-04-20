package de.staticfx.staffsystem.events;

import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.db.ReportDAO;
import de.staticfx.staffsystem.objects.Report;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class LogOutEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        try {
            if(AccountDAO.getInstance().hasAccount(event.getPlayer().getUniqueId())) {
                AccountDAO.getInstance().logOut(event.getPlayer().getUniqueId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            for(Report report : ReportDAO.INSTANCE.getAllReports()) {
                if(report.getEditingPlayer() != null) {
                    if(report.getEditingPlayer().getUniqueId() == p.getUniqueId()) {
                        ReportDAO.INSTANCE.updateEditingPlayer(report.getId(),null);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }



}
