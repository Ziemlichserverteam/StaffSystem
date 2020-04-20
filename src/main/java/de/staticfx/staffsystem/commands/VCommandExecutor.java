package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to execute this command.");
            return false;
        }

        Player p = (Player) commandSender;

        if(args.length != 1) {
            commandSender.sendMessage(Main.votePrefix + "§cUse /v 1/0");
            return false;
        }

        if(!Main.canVote) {
            p.sendMessage(Main.getInstance().getConfigString("CantVoteATM","VotePrefix"));
            return false;
        }


        if(args[0].equalsIgnoreCase("1")) {
            if(Main.voted.contains(p)) {
                p.sendMessage(Main.getInstance().getConfigString("AlreadyVoted","VotePrefix"));
                return false;
            }
            Main.voteYes++;
            Main.voted.add(p);
            p.sendMessage(Main.getInstance().getConfigString("Voted","VotePrefix").replaceAll("%vote%","§a§lYes"));
            return true;

        }

        if(args[0].equalsIgnoreCase("0")) {
            if(Main.voted.contains(p)) {
                p.sendMessage(Main.getInstance().getConfigString("AlreadyVoted","VotePrefix"));
                return false;
            }
            Main.voteNo++;
            Main.voted.add(p);
            p.sendMessage(Main.getInstance().getConfigString("Voted","VotePrefix").replaceAll("%vote%","§c§lNo"));
            return true;
        }

        commandSender.sendMessage(Main.votePrefix + "§cUse /v 1/0");
        return false;
    }
}
