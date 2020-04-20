package de.staticfx.staffsystem.events;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.commands.ReportCommandExecutor;
import de.staticfx.staffsystem.commands.ReportsCommandExecutor;
import de.staticfx.staffsystem.db.ReportDAO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if(p.hasPermission("sts.team")) {
            if(p.getOpenInventory().getTitle().equalsIgnoreCase("§cReports")) {
                if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null) {
                    return;
                }

                String item = e.getCurrentItem().getItemMeta().getDisplayName();
                e.setCancelled(true);


                int page = Main.playerPageHashMap.get(p);

                if(item.equalsIgnoreCase("§c<- Last page")) {
                    if(page == 0) return;
                    p.openInventory(ReportsCommandExecutor.buildPage(page - 1));
                    Main.playerPageHashMap.replace(p,page - 1);
                    return;
                }
                int maxPage;
                try {
                     maxPage = ReportDAO.getInstance().getAllReports().size() / 5*9;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return;
                }

                if(item.equalsIgnoreCase("§aNext page ->")) {
                    if(page == maxPage) return;
                    p.openInventory(ReportsCommandExecutor.buildPage(page + 1));
                    Main.playerPageHashMap.replace(p,page + 1);
                    return;
                }


                e.setCancelled(true);
                String idSt = item.substring(2);
                int id = Integer.parseInt(idSt);
                p.performCommand("reports edit " + id);
                p.closeInventory();
            }
        }


    }

}
