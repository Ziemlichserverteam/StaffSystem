package de.staticfx.staffsystem.db;

import de.staticfx.staffsystem.objects.Report;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class VoteDAO {

    public static final VoteDAO INSTANCE = new VoteDAO();

    public static VoteDAO getInstance() {
        return INSTANCE;
    }


    public void addVote(UUID uuid, long timStamp, boolean ban) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO votes(UUID,timeStamp, ban) VALUES(?,?,?)",uuid.toString(),timStamp,ban);
        con.closeConnection();
    }

    public void removeVote(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM votes WHERE UUID = ?",uuid.toString());
        con.closeConnection();
    }

    public boolean hasVote(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM votes where UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            rs.close();
            ps.close();
            con.closeConnection();
            return true;
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return false;
    }

    public long getTimeStamp(UUID uuid,boolean ban) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM votes where UUID = ? AND ban = ?");
        ps.setString(1,uuid.toString());
        ps.setBoolean(2,ban);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            long timeStamp = rs.getLong("timeStamp");
            rs.close();
            ps.close();
            con.closeConnection();
            return timeStamp;
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return 0;
    }

    public boolean isBan(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM votes where UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            boolean timeStamp = rs.getBoolean("ban");
            rs.close();
            ps.close();
            con.closeConnection();
            return timeStamp;
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return false;
    }




}
