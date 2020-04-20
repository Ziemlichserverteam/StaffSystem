package de.staticfx.staffsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PremiumDAO {

    private static PremiumDAO instance = new PremiumDAO();

    public static PremiumDAO getInstance() {
        return instance;
    }

    public void addPremiumDate(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO premium(UUID, timeStamp) VALUES(?,?)", uuid.toString(), System.currentTimeMillis());
        con.closeConnection();
    }

    public void removePremiumDate(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE from premium WHERE UUID = ?", uuid.toString());
        con.closeConnection();
    }

    public long getTimeStamp(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM premium where UUID = ?");
        ps.setString(1,uuid.toString());
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

    public boolean gavePremium(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM premium where UUID = ?");
        ps.setString(1, uuid.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
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






}
