package de.staticfx.staffsystem.db;

import de.staticfx.staffsystem.objects.ChatLog;
import de.staticfx.staffsystem.objects.MessageLog;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogDAO {


    public static final LogDAO INSTANCE = new LogDAO();

    public static LogDAO getInstance() {
        return INSTANCE;
    }

    public void createLog(int id, Player p, ChatLog log) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        System.out.println(log.getMessages().size());
        for(int i = log.getMessages().size(); i > 0; i--) {
            System.out.print(1);
            con.executeUpdate("INSERT INTO logs(UUID, timeStamp, message, logID) VALUES(?, ?, ?, ?)",p.getUniqueId().toString(), log.getTimes().get(i - 1), log.getMessages().get(i - 1), id);
        }
        con.closeConnection();
    }

    public MessageLog getMessageLog(int id) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM logs WHERE logID = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        List<Long> times = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        while(rs.next()) {
            long time = rs.getLong("timeStamp");
            String message = rs.getString("message");
            times.add(time);
            messages.add(message);
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return new MessageLog(times,messages);
    }

    public void removeLog(int id) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM logs WHERE logID = ?", id);
        con.closeConnection();
    }

    public void storeLog(int id, int newID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE logs SET logID = ? WHERE logID = ?",newID,id);
        con.closeConnection();
    }

    public List<Integer> getStoredLogsIDs() throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM logs");

        List<Integer> ids = new ArrayList<>();

        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int id = rs.getInt("logID");
            if(!ids.contains(id)) ids.add(id);
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return ids;
    }

    public OfflinePlayer getPlayerForID(int id) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT UUID FROM logs WHERE logID = ?");
        ps.setInt(1,id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            UUID player = UUID.fromString(rs.getString("UUID"));
            ps.close();
            rs.close();
            con.closeConnection();
            return Bukkit.getOfflinePlayer(player);
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return null;
    }

}
