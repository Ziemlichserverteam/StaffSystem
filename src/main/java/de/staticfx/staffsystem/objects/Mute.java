package de.staticfx.staffsystem.objects;

import java.util.UUID;

public class Mute {

    int banid;
    UUID uuid;
    String reason;
    long endTime;
    String punisher;
    long timestamp;
    Type type;
    boolean permanent;
    String unbannendDate;
    boolean active;


    public Mute(int banid, UUID uuid, String reason, long endTime, String punisher, long timestamp, Type type, boolean permanent, String unbannendDate, boolean active) {
        this.banid = banid;
        this.uuid = uuid;
        this.reason = reason;
        this.endTime = endTime;
        this.punisher = punisher;
        this.timestamp = timestamp;
        this.type = type;
        this.permanent = permanent;
        this.unbannendDate = unbannendDate;
        this.active = active;
    }

    public int getBanid() {
        return banid;
    }

    public void setBanid(int banid) {
        this.banid = banid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getPunisher() {
        return punisher;
    }

    public void setPunisher(String punisher) {
        this.punisher = punisher;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public String getUnbannendDate() {
        return unbannendDate;
    }

    public void setUnbannendDate(String unbannendDate) {
        this.unbannendDate = unbannendDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
