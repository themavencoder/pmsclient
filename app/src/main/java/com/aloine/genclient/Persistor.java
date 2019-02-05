package com.aloine.genclient;

public class Persistor {

    private static Persistor sInstance = null;
    private int check = 0;
    private String name;
    private String macaddress;

    private Persistor() {

    }
    public static Persistor getInstance() {
        if (sInstance == null) {
            sInstance = new Persistor();
        }
        return sInstance;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }
}
