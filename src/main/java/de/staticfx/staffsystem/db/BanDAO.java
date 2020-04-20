package de.staticfx.staffsystem.db;

import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.Type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BanDAO {

    public static final BanDAO INSTANCE = new BanDAO();

    public static BanDAO getInstance() {
        return INSTANCE;
    }

    public void createBan(Ban ban) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO bans(BanID, UUID, Reason, endTime, Punisher, timeStamp, Type, Permanent, Unbanned, Active) VALUES(?, ?, ?, ?, ?,?, ?, ?,?, ?)", ban.getBanid(), ban.getUuid().toString(), ban.getReason(), ban.getEndTime(), ban.getPunisher(), ban.getTimestamp(), ban.getType().toString(),ban.isPermanent(), ban.getUnbannendDate(), ban.isActive());
        con.closeConnection();
    }

    public Ban getBan(int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM bans WHERE BanID = ?");
        ps.setInt(1, banID);
        ResultSet rs = ps.executeQuery();
        Ban ban;
        if(rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("UUID"));
            String reason = rs.getString("Reason");
            long endTime = rs.getLong("endTime");
            String Punisher = rs.getString("Punisher");
            long timeStamp = rs.getLong("timeStamp");
            Type type;
            try{
                type = Type.valueOf(rs.getString("Type"));
            }catch (Exception e) {
                throw new SQLException("Invalid type! Please check your DataBase!");
            }

            boolean permanent = rs.getBoolean("Permanent");
            String unbannend = rs.getString("Unbanned");
            boolean active = rs.getBoolean("Active");

            ban = new Ban(banID, uuid, reason, endTime, Punisher, timeStamp, type, permanent, unbannend, active);
            ps.close();
            rs.close();
            con.closeConnection();
            return ban;
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return null;

    }

    public boolean doesBanExist(int banID) throws SQLException{
        return (getBan(banID) != null);
    }

    public List<Ban> getAllBans(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM bans WHERE UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        ArrayList<Ban> bans = new ArrayList();
        while(rs.next()) {
            int banID = rs.getInt("BanID");
            String reason = rs.getString("Reason");
            long endTime = rs.getLong("endTime");
            String Punisher = rs.getString("Punisher");
            long timeStamp = rs.getLong("timeStamp");
            Type type;
            try{
                type = Type.valueOf(rs.getString("Type"));
            }catch (Exception e) {
                rs.close();
                ps.close();
                con.closeConnection();
                throw new SQLException("Invalid type! Please check your DataBase!");

            }

            boolean permanent = rs.getBoolean("Permanent");
            String unbannend = rs.getString("Unbanned");
            boolean active = rs.getBoolean("Active");
            bans.add(new Ban(banID, uuid, reason, endTime, Punisher, timeStamp, type, permanent, unbannend, active));
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return bans;
    }


    public boolean hadBeenBanned(UUID uuid) throws SQLException {
        return !getAllBans(uuid).isEmpty();
    }

    public void removeBan(int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM bans WHERE BanID = ?", banID);
        con.closeConnection();
    }

    public void removeAllBansFromPlayer(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM bans WHERE UUID = ?", uuid.toString());
        con.closeConnection();
    }

    public void setBanActivity(boolean activ, int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE bans SET Active = ? WHERE BanID = ?",activ,banID);
        con.closeConnection();
    }

    public void setBanPermanent(boolean permanent, int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE bans SET Permanent = ? WHERE BanID = ?",permanent,banID);
        con.closeConnection();
    }


    public void setBanEndTime(long endTime, int banid) throws SQLException{
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE bans SET endTime = ? WHERE BanID = ?", endTime, banid);
        con.closeConnection();
        con.openConnection();
        con.executeUpdate("UPDATE bans SET Unbanned = ? WHERE BanID = ?",new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(endTime)), banid);
        con.closeConnection();
    }

    public boolean hasActiveBans(UUID uuid) throws SQLException {
        return getAllBans(uuid).stream().anyMatch(Ban::isActive);
    }

    public boolean isBannedForReason(UUID uuid, String reason) throws SQLException {
        for(Ban ban : getAllBans(uuid)) {
            if(ban.getReason().equals(reason) ) {
                return true;
            }
        }
        return false;
    }

    public int getBansAmount(UUID uuid) throws SQLException {
       return getAllBans(uuid).size();
    }

    public int getBansForReasonAmount(UUID uuid, String reason) throws SQLException {
        int result = 0;
        List<Ban> bans = getAllBans(uuid);
        for(Ban ban : bans) {
            if(ban.getReason().equalsIgnoreCase(reason)) {
                result++;
            }
        }
        return result;
     }
}
