package de.staticfx.staffsystem.commands;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

public class LoginCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(("You must be a Player!"));
            return false;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("sts.login")) {
            p.sendMessage(Main.getInstance().getConfigString("NoPermission","StaffPrefix"));
            return false;
        }

        if(args.length != 1) {
            p.sendMessage((Main.getPrefix() + "§c Use: /login [password]."));
            return false;
        }


        try {
            if(!AccountDAO.getInstance().hasAccount(p.getUniqueId())) {
                p.sendMessage(Main.getInstance().getConfigString("CreateAccountFirst","StaffPrefix"));
                return false;
            }
        } catch (SQLException e) {
            p.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
            e.printStackTrace();
            return false;
        }

        String password;
        try {
            password = hashPassword(args[0], AccountDAO.getInstance().getSalt(p.getUniqueId()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        try {
            if(!AccountDAO.getInstance().doesPasswordMatch(p.getUniqueId(),password)) {
                p.sendMessage(Main.getInstance().getConfigString("WrongPassword","StaffPrefix"));
                return false;
            }
        } catch (SQLException e) {
            p.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
            e.printStackTrace();
            return false;
        }


        if(AccountDAO.getInstance().isLoggedIn(p.getUniqueId())) {
            Main.logedIn.remove(p);
            Main.banNotifycations.remove(p);
            p.sendMessage(Main.getInstance().getConfigString("Logout","StaffPrefix"));
        }else{
            Main.logedIn.add(p);
            Main.banNotifycations.add(p);
            p.sendMessage(Main.getInstance().getConfigString("Login","StaffPrefix"));
        }
        return false;
    }

    private String hashPassword(String string, byte[] salt) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(salt);
            byte[] hash = md.digest(string.getBytes());
            return bytesToHex(hash);
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    private byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return  bytes;
    }

}
