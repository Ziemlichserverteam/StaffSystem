package de.staticfx.staffsystem.db;
import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.objects.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountDAO {
    private static final AccountDAO INSTANCE = new AccountDAO();


    // persistence layer - datenbank
    // non-persistence layer - GUI


    public static AccountDAO getInstance() {
        return INSTANCE;
    }

    public void createAccount(Account account) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO accounts(UUID,Password,Salt,Rank,Grouppower) VALUES (?,?,?,?,?)",account.getUuid().toString(),account.getPassword(),account.getSalt(), account.getRank(),account.getGroupPower());
        con.closeConnection();
    }

    public void updateAccount(Account account) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE accounts SET Password = ?, Rank = ?, Grouppower = ? WHERE UUID = ?",account.getPassword(),account.getRank(),account.getGroupPower(),account.getUuid().toString());
        con.closeConnection();
    }
    public Account getAccount(UUID uuid) throws SQLException{
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM accounts where UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            String rank = rs.getString("Rank");
            String password = rs.getString("Password");
            int groupPower = rs.getInt("Grouppower");
            byte[] salt = rs.getBytes("Salt");
            rs.close();
            ps.close();
            con.closeConnection();
            return new Account(uuid,rank,password, Main.logedIn.contains(Main.getInstance().getServer().getPlayer(uuid)),groupPower,salt);
        }
        rs.close();
        ps.close();
        con.closeConnection();
        return null;
    }

    public byte[] getSalt(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM accounts where UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            byte[] salt = rs.getBytes("Salt");
            rs.close();
            ps.close();
            con.closeConnection();
            return salt;
        }
        rs.close();
        ps.close();
        con.closeConnection();
        return null;
    }


    public void login(UUID uuid) {
        Main.logedIn.add(Main.getInstance().getServer().getPlayer(uuid));
    }

    public void logOut(UUID uuid) {
        Main.logedIn.remove(Main.getInstance().getServer().getPlayer(uuid));
    }

    public void setPasswort(UUID uuid, String password) throws SQLException {
        Account account = getAccount(uuid);
        account.setPassword(password);
        updateAccount(account);
    }

    public void setRank(UUID uuid, String rank) throws SQLException {
        Account account = getAccount(uuid);
        account.setRank(rank);
        updateAccount(account);
    }

    public String getRank(UUID uuid) throws SQLException{
        return getAccount(uuid).getRank();
    }

    public boolean isLoggedIn(UUID uuid) {
        return Main.logedIn.contains(Main.getInstance().getServer().getPlayer(uuid));
    }

    public boolean doesPasswordMatch(UUID uuid, String password) throws SQLException {
        return password.equals(getAccount(uuid).getPassword());
    }

    public boolean hasAccount(UUID uuid) throws SQLException {
            return (getAccount(uuid) != null);
    }


}
