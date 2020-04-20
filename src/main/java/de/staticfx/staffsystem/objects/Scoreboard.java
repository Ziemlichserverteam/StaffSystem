package de.staticfx.staffsystem.objects;

import de.staticfx.staffsystem.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class Scoreboard {



    public static void generateScoreboard(Player p) {
        Economy eco = Main.eco;
        org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.getObjective("sidebar");

        if(objective == null) {
            objective = board.registerNewObjective("sidebar","dummy");
        }

        objective.setDisplayName("»» §3Ziemlich.eu §r««");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);




        //1-2 placeholder
        objective.getScore("  ").setScore(24);

        //rang
        objective.getScore("§aRang:").setScore(23);

        String rank;
        String group = Main.perms.getPrimaryGroup(p);
        if(group.equalsIgnoreCase("default")) {
            rank = "Kein Rang";
        }else{
            rank = Main.chat.getPlayerPrefix(p).substring(0,Main.chat.getPlayerPrefix(p).length() - 7).replaceAll("&","§");
        }

        Team rankTeam = board.registerNewTeam("rankTeam");
        rankTeam.setPrefix("§7»§a" + rank);
        rankTeam.addEntry("§a");
        objective.getScore("§a").setScore(22);

        //placerholder 2-4
        objective.getScore("   ").setScore(21);
        objective.getScore("    ").setScore(20);


        //money
        objective.getScore("§aDukaten:").setScore(19);
        Team moneyTeam = board.registerNewTeam("moneyTeam");
        moneyTeam.setPrefix("§7»§6 " + Main.eco.getBalance(p));
        moneyTeam.addEntry("§b");
        objective.getScore("§b").setScore(18);

        //placerholder 4-6
        objective.getScore("     ").setScore(17);
        objective.getScore("      ").setScore(16);

        //world
        objective.getScore("§aWelt:").setScore(15);

        Team worldTeam = board.registerNewTeam("worldTeam");
        worldTeam.setPrefix("§7»§3 " + p.getLocation().getWorld().getName());
        worldTeam.addEntry("§3");
        objective.getScore("§3").setScore(14);


        //placeholder 6-8
        objective.getScore("       ").setScore(13);
        objective.getScore("        ").setScore(12);

        //spielerteam
        objective.getScore("§aSpieler:").setScore(11);


        Team playerTeam = board.registerNewTeam("playerTeam");
        playerTeam.setPrefix("§7»§a " + Bukkit.getOnlinePlayers().size() + "§7/§2" + Bukkit.getMaxPlayers());
        playerTeam.addEntry("§7");
        objective.getScore("§7").setScore(10);

        p.setScoreboard(board);

    }

    public static void startUpdater() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    org.bukkit.scoreboard.Scoreboard board = p.getScoreboard();
                    Objective obj = board.getObjective("sidebar");

                    Team rankTeam = board.getTeam("rankTeam");

                    String rank;
                    String group = Main.perms.getPrimaryGroup(p);
                    if(group.equalsIgnoreCase("default")) {
                        rank = "Kein Rang";
                    }else{
                        rank = Main.chat.getPlayerPrefix(p).substring(0,Main.chat.getPlayerPrefix(p).length() - 7).replaceAll("&","§");
                    }


                    if(rankTeam == null) {
                        rankTeam = board.registerNewTeam("rankTeam");
                        rankTeam.setPrefix("§7»§a " + rank);
                        rankTeam.addEntry("§a");
                        obj.getScore("§a").setScore(22);
                    }else{
                        rankTeam.setPrefix("§7»§a " + rank);
                    }

                    Team moneyTeam = board.getTeam("moneyTeam");

                    if(moneyTeam == null) {
                        moneyTeam = board.registerNewTeam("moneyTeam");
                        moneyTeam.setPrefix("§7»§6 " + Main.eco.getBalance(p));
                        moneyTeam.addEntry("§b");
                        obj.getScore("§b").setScore(19);
                    }else{
                        moneyTeam.setPrefix("§7»§6 " + Main.eco.getBalance(p));
                    }



                    Team worldTeam = board.getTeam("worldTeam");


                    if(worldTeam == null) {
                        worldTeam = board.registerNewTeam("worldTeam");
                        worldTeam.setPrefix("§7»§3 " + p.getLocation().getWorld().getName());
                        worldTeam.addEntry("§3");
                        obj.getScore("§3").setScore(16);
                    }else{
                        worldTeam.setPrefix("§7»§3 " + p.getLocation().getWorld().getName());
                    }

                    Team playerTeam = board.getTeam("playerTeam");


                    if(playerTeam == null) {
                        playerTeam = board.registerNewTeam("playerTeam");
                        playerTeam.setPrefix("§7»§a " + Bukkit.getOnlinePlayers().size() + "§7/§2" + Bukkit.getMaxPlayers());
                        playerTeam.addEntry("§7");
                        obj.getScore("§7").setScore(12);
                    }else{
                        playerTeam.setPrefix("§7»§a " + Bukkit.getOnlinePlayers().size() + "§7/§2" + Bukkit.getMaxPlayers());
                    }
                }
            }
        }, 0, 5);
    }





}
