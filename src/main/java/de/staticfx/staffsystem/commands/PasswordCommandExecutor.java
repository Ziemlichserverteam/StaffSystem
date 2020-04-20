package de.staticfx.staffsystem.commands;


import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.db.AccountDAO;
import de.staticfx.staffsystem.objects.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

public class PasswordCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(("§cYou must be a player!"));
            return false;
        }

        Player player = (Player) sender;


        if(!player.hasPermission("sts.password")) {
            player.sendMessage(Main.getInstance().getConfigString("NoPermission","StaffPrefix"));
            return false;
        }

        if(args.length < 1) {
            player.sendMessage((Main.prefix + "§c Use: /password [create/update]."));
            return false;
        }

        if(args[0].equalsIgnoreCase("create")) {
            try {
                if(AccountDAO.getInstance().hasAccount(player.getUniqueId())) {
                    player.sendMessage(Main.getInstance().getConfigString("AlreadyAccount","StaffPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                player.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
                e.printStackTrace();
                return false;
            }

            if(args.length != 3) {
                player.sendMessage((Main.getInstance().getConfigString("StaffPrefix") + "§c Use: /password [create] [password] [repeatPassword]."));
                return false;
            }

            String password1 = args[1];

            String password2 = args[2];

            if(!password1.equals(password2)) {
                player.sendMessage(Main.getInstance().getConfigString("PasswordDontMatch","StaffPrefix"));
                return false;
            }
            byte[] salt = createSalt();
            String password = hashPassword(password1,salt);

            try {
                AccountDAO.getInstance().createAccount(new Account(player.getUniqueId(),null,password,true,0,salt));
            } catch (SQLException e) {
                player.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
                e.printStackTrace();
                return false;
            }

            player.sendMessage(Main.getInstance().getConfigString("CreatedAccount","StaffPrefix"));
            return false;
        }else if(args[0].equalsIgnoreCase("update")) {
            try {
                if(!AccountDAO.getInstance().hasAccount(player.getUniqueId())) {
                    player.sendMessage(Main.getInstance().getConfigString("CreateAccountFirst","StaffPrefix"));
                    return false;
                }
            } catch (SQLException e) {
                player.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
                e.printStackTrace();
                return false;
            }

            if(args.length != 3) {
                player.sendMessage((Main.prefix + "§c Use: /password [update] [oldPassword] [newPassword]."));
                return false;
            }


            String oldPassword = args[1];
            String newPassword = args[2];

            try {
                if(!AccountDAO.getInstance().doesPasswordMatch(player.getUniqueId(),hashPassword(oldPassword,AccountDAO.getInstance().getSalt(player.getUniqueId())))) {
                    player.sendMessage(Main.getInstance().getConfigString("WrongPassword","StaffPrefix"));
                    return false;
                }

            } catch (SQLException e) {
                player.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
                e.printStackTrace();
                return false;
            }

            if(newPassword.equals(oldPassword)) {
                player.sendMessage(Main.getInstance().getConfigString("NewPasswordCantBeOld","StaffPrefix"));
                return false;
            }

            try {
                AccountDAO.getInstance().setPasswort(player.getUniqueId(),hashPassword(newPassword,AccountDAO.getInstance().getSalt(player.getUniqueId())));
            } catch (SQLException e) {
                player.sendMessage(Main.getInstance().getConfigString("InternalError","StaffPrefix"));
                e.printStackTrace();
                return false;
            }

            player.sendMessage(Main.getInstance().getConfigString("UpdatedPassword","StaffPrefix"));
            return false;
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
