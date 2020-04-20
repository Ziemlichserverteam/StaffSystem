package de.staticfx.staffsystem.objects;

public class ID {

    int id;
    String reason;
    Type type;
    String time;
    boolean permanent;



    public ID(int id, String reason, Type type, String time, boolean permanent) {
        this.id = id;
        this.reason = reason;
        this.type = type;
        this.time = time;
        this.permanent = permanent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}

