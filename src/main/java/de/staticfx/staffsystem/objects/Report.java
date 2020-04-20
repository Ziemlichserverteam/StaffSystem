package de.staticfx.staffsystem.objects;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Report {


    private OfflinePlayer reportedPlayer;
    private OfflinePlayer reportingPlayer;
    private OfflinePlayer editingPlayer;
    private String reason;
    private int id;
    private Type type;
    private long timeStamp;
    private int amount;


    public Report(OfflinePlayer reportedPlayer, OfflinePlayer reportingPlayer, @Nullable OfflinePlayer editingPlayer, String reason, int id, long timestamp, int amount) {
        this.reportedPlayer = reportedPlayer;
        this.reportingPlayer = reportingPlayer;
        this.editingPlayer = editingPlayer;
        this.reason = reason;
        this.id = id;
        this.timeStamp = timestamp;
        this.amount = amount;
    }

    public OfflinePlayer getReportedPlayer() {
        return reportedPlayer;
    }

    public void setReportedPlayer(OfflinePlayer reportedPlayer) {
        this.reportedPlayer = reportedPlayer;
    }

    public OfflinePlayer getReportingPlayer() {
        return reportingPlayer;
    }

    public void setReportingPlayer(OfflinePlayer reportingPlayer) {
        this.reportingPlayer = reportingPlayer;
    }

    public OfflinePlayer getEditingPlayer() {
        return editingPlayer;
    }

    public void setEditingPlayer(OfflinePlayer editingPlayer) {
        this.editingPlayer = editingPlayer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
