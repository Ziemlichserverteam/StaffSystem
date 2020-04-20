package de.staticfx.staffsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AdminDAO {

    public final static AdminDAO INSTANCE = new AdminDAO();

    public void addPlayer(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO unbannable(UUID) VALUES(?)",uuid.toString());
        con.closeConnection();
    }

    public void removePlayer(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM unbannable WHERE UUID = ?",uuid.toString());
        con.closeConnection();
    }

    public UUID getPlayerUnbannable(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM unbannable WHERE UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            UUID uuid1 = UUID.fromString(rs.getString("UUID"));
            ps.close();
            rs.close();
            con.closeConnection();
            return uuid1;
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return null;
    }

    public boolean isPlayerUnbannable(UUID uuid) throws SQLException{
        return (getPlayerUnbannable(uuid) != null);
    }


}
