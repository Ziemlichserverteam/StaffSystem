package de.staticfx.staffsystem.db;

import de.staticfx.staffsystem.objects.Ban;
import de.staticfx.staffsystem.objects.Mute;
import de.staticfx.staffsystem.objects.Report;
import de.staticfx.staffsystem.objects.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportDAO {

    public static final ReportDAO INSTANCE = new ReportDAO();

    public static ReportDAO getInstance() {
        return INSTANCE;
    }

    public void createReport(Report report, boolean view) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        String editingPlayer;
        if(report.getEditingPlayer() == null) {
            editingPlayer = null;
        }else{
            editingPlayer = report.getEditingPlayer().getUniqueId().toString();
        }
        con.executeUpdate("INSERT INTO reports(reportedPlayer, reportingPlayer, editingPlayer, reason, ID, timeStamp, amount, view) VALUES(?, ?, ?, ?, ?, ?, ?,?)", report.getReportedPlayer().getUniqueId().toString(), report.getReportingPlayer().getUniqueId().toString(), editingPlayer,report.getReason(),report.getId(), report.getTimeStamp(), report.getAmount(), view);
        con.closeConnection();
    }

    public Report getReport(int reportID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM reports WHERE ID = ?");
        ps.setInt(1, reportID);
        ResultSet rs = ps.executeQuery();
        Report report;
        if(rs.next()) {
            UUID reportedPlayer = UUID.fromString(rs.getString("reportedPlayer"));
            int amount = rs.getInt("amount");
            UUID reportingPlayer = UUID.fromString(rs.getString("reportingPlayer"));
            OfflinePlayer editingPlayer;
            long timeStamp = rs.getLong("timeStamp");
            if(rs.getString("editingPlayer") == null) {
                editingPlayer = null;
            }else{
                editingPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("editingPlayer")));
            }
            String reason = rs.getString("reason");

            report = new Report(Bukkit.getOfflinePlayer(reportedPlayer),Bukkit.getOfflinePlayer(reportingPlayer),editingPlayer,reason,reportID,timeStamp,amount);
            ps.close();
            rs.close();
            con.closeConnection();
            return report;
        }
        ps.close();
        rs.close();
        con.closeConnection();
        return null;
    }

    public List<Report> getAllReports() throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM reports ORDER BY amount DESC");
        ResultSet rs = ps.executeQuery();
        ArrayList<Report> reports = new ArrayList();
        while(rs.next()) {
            int reportID = rs.getInt("ID");
            int amount = rs.getInt("amount");
            OfflinePlayer reportedPlayer = Bukkit.getOfflinePlayer(UUID.fromString( rs.getString("reportedPlayer")));
            OfflinePlayer reportingPlayer = Bukkit.getOfflinePlayer(UUID.fromString( rs.getString("reportingPlayer")));
            long timeStamp = rs.getLong("timeStamp");
            OfflinePlayer editingPlayer;
            if(rs.getString("editingPlayer") == null) {
                editingPlayer = null;
            } else {
                editingPlayer= Bukkit.getOfflinePlayer(UUID.fromString( rs.getString("editingPlayer")));
            }
            String reason = rs.getString("reason");
            reports.add(new Report(reportedPlayer,reportingPlayer,editingPlayer,reason,reportID,timeStamp,amount));
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return reports;
    }

    public void updateEditingPlayer(int id, OfflinePlayer editingPlayer) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE reports SET editingPlayer = ? WHERE ID = ?", editingPlayer.getUniqueId().toString(), id);
        con.closeConnection();
    }



    public boolean doesReportExist(int reportID) throws SQLException{
        return (getReport(reportID) != null);
    }

    public void removeReport(int reportID) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("DELETE FROM reports WHERE ID = ?", reportID);
        con.closeConnection();
    }

    public boolean isPlayerEditing(Player p) throws SQLException {
        for(Report report : getAllReports()) {
            if(report.getEditingPlayer() != null) {
                if(report.getEditingPlayer().getUniqueId() == p.getUniqueId()) return true;
            }
        }
        return false;
    }

    public void setAmount(int id, int amount) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        con.executeUpdate("UPDATE reports SET amount = ? WHERE ID = ?", amount, id);
        con.closeConnection();
    }

    public boolean isViewAble(int id) throws SQLException {
        DataBaseConnection con = DataBaseConnection.INSTANCE;
        con.openConnection();
        PreparedStatement ps = con.getConnection().prepareStatement("SELECT * FROM reports WHERE ID = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {

            boolean viewable = rs.getBoolean("view");

            rs.close();
            ps.close();
            con.closeConnection();
            return viewable;
        }

        rs.close();
        ps.close();
        con.closeConnection();
        return false;
    }
}
