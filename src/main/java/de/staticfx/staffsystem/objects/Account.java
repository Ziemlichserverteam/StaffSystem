package de.staticfx.staffsystem.objects;


import java.util.UUID;

public class Account {

    private UUID uuid;

    private String rank;

    private String password;

    private boolean loginState;

    private int groupPower;

    private byte[] salt;

    public Account(UUID uuid, String rank, String password, boolean loginState, int groupPower, byte[] salt) {
        this.uuid = uuid;
        this.rank = rank;
        this.password = password;
        this.loginState = loginState;
        this.groupPower = groupPower;
        this.salt = salt;
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public int getGroupPower() {
        return groupPower;
    }

    public void setGroupPower(int groupPower) {
        this.groupPower = groupPower;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
