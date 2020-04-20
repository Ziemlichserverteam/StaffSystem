package de.staticfx.staffsystem.events;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.db.MuteDAO;
import de.staticfx.staffsystem.objects.ChatLog;
import de.staticfx.staffsystem.objects.Mute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageEvent implements Listener {

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if(Main.vote) e.setCancelled(true);


        if(!Main.playerMessageHashMap.containsKey(p.getUniqueId())) {
            Main.playerMessageHashMap.put(p.getUniqueId(), new ChatLog(new ArrayList<>(), new ArrayList<>()));
        }

        while(Main.playerMessageHashMap.get(p.getUniqueId()).getMessages().size() > 25) {
            Main.playerMessageHashMap.get(p.getUniqueId()).getMessages().remove(0);
            Main.playerMessageHashMap.get(p.getUniqueId()).getTimes().remove(0);
        }

        Main.playerMessageHashMap.get(p.getUniqueId()).getMessages().add(e.getMessage());
        Main.playerMessageHashMap.get(p.getUniqueId()).getTimes().add(System.currentTimeMillis());


        try {
            if(MuteDAO.INSTANCE.hasActiveMutes(p.getUniqueId())) {
                if(e.getMessage().startsWith("/"))
                    return;
                for(Mute mute : MuteDAO.INSTANCE.getAllMutes(p.getUniqueId())) {
                    if(mute.isActive()) {
                        if(mute.getEndTime() > System.currentTimeMillis() || mute.isPermanent()) {
                            e.setCancelled(true);
                            p.sendMessage((Main.getInstance().getMuteMessage().replaceAll("%date%",mute.getUnbannendDate()).replaceAll("%reason%",mute.getReason()).replaceAll("%banid%",Integer.toString(mute.getBanid())).replaceAll("%punisher%",mute.getPunisher())));
                        }else{
                            MuteDAO.INSTANCE.setMuteActivity(false, mute.getBanid());
                            if(mute.getReason().startsWith("Vote:")) {
                                MuteDAO.getInstance().removeMute(mute.getBanid());
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if(!Main.globalChat) {
            p.sendMessage(Main.getPrefix() + "§cThe globalchat is muted at the moment");
            e.setCancelled(true);
        }

        if(Main.teamChatUser.contains(p)) {
            if(!AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
                Main.teamChatUser.remove(p);
                return;
            }

            if(e.getMessage().startsWith("/"))
                return;

            e.setCancelled(true);
            for(Player player: Main.teamChatUser) {
                player.sendMessage((Main.getInstance().getTeamChatFormat().replaceAll("%name%",p.getName()).replaceAll("%server%",p.getWorld().getName()).replaceAll("%message%",e.getMessage())));
            }
        }
    }

}
