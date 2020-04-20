package de.staticfx.staffsystem.db;

import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.Mute;
import de.staticfx.staffsystem.objects.Type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MuteDAO {

    public static final MuteDAO INSTANCE = new MuteDAO();

    public static MuteDAO getInstance() {
        return INSTANCE;
    }

    public void createBan(Mute ban) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("INSERT INTO mutes(BanID, UUID, Reason, endTime, Punisher, timeStamp, Type, Permanent, Unbanned, Active) VALUES(?, ?, ?, ?, ?,?, ?, ?,?, ?)", ban.getBanid(), ban.getUuid().toString(), ban.getReason(), ban.getEndTime(), ban.getPunisher(), ban.getTimestamp(), ban.getType().toString(),ban.isPermanent(), ban.getUnbannendDate(), ban.isActive());
        con.closeConnection();
    }

    public Mute getMute(int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM mutes WHERE BanID = ?");
        ps.setInt(1, banID);
        ResultSet rs = ps.executeQuery();
        Mute ban;
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

            ban = new Mute(banID, uuid, reason, endTime, Punisher, timeStamp, type, permanent, unbannend, active);
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

    public boolean doesMuteExist(int banID) throws SQLException{
        return (getMute(banID) != null);
    }

    public List<Mute> getAllMutes(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM mutes WHERE UUID = ?");
        ps.setString(1,uuid.toString());
        ResultSet rs = ps.executeQuery();
        ArrayList<Mute> bans = new ArrayList();
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
                throw new SQLException("Invalid type! Please check your DataBase!");
            }

            boolean permanent = rs.getBoolean("Permanent");
            String unbannend = rs.getString("Unbanned");
            boolean active = rs.getBoolean("Active");
            bans.add(new Mute(banID, uuid, reason, endTime, Punisher, timeStamp, type, permanent, unbannend, active));
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return bans;
    }


    public boolean hadBeenMuted(UUID uuid) throws SQLException {
        return !getAllMutes(uuid).isEmpty();
    }

    public void removeMute(int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM mutes WHERE BanID = ?", banID);
        con.closeConnection();
    }

    public void removeAllMutesFromPlayer(UUID uuid) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM mutes WHERE UUID = ?", uuid.toString());
        con.closeConnection();
    }

    public void setMuteActivity(boolean activ, int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE mutes SET Active = ? WHERE BanID = ?",activ,banID);
        con.closeConnection();
    }

    public boolean hasActiveMutes(UUID uuid) throws SQLException {
        return getAllMutes(uuid).stream().anyMatch(Mute::isActive);
    }

    public boolean isMutedForReason(UUID uuid, String reason) throws SQLException {
        for(Mute ban : getAllMutes(uuid)) {
            if(ban.getReason().equals(reason) ) {
                return true;
            }
        }
        return false;
    }

    public int getMutesAmount(UUID uuid) throws SQLException {
        return getAllMutes(uuid).size();
    }

    public void setMuteEndTime(long endTime, int banid) throws SQLException{
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE mutes SET endTime = ? WHERE BanID = ?", endTime, banid);
        con.closeConnection();
        con.openConnection();
        con.executeUpdate("UPDATE mutes SET Unbanned = ? WHERE BanID = ?",new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(endTime)), banid);
        con.closeConnection();
    }

    public void setMutePermanent(boolean permanent, int banID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE mutes SET Permanent = ? WHERE BanID = ?", permanent,banID);
        con.closeConnection();
    }

    public int getMutesForReasonAmount(UUID uuid, String reason) throws SQLException {
        int result = 0;
        for(Mute ban : getAllMutes(uuid)) {
            if(ban.getReason().equalsIgnoreCase(reason)) {
                result++;
            }
        }
        return result;
    }


}
