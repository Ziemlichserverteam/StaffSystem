package de.staticfx.staffsystem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPortalEvent implements Listener {

    @EventHandler
    public void onPlayerPortal(org.bukkit.event.player.PlayerPortalEvent e) {

        e.setCancelled(true);

    }

}
