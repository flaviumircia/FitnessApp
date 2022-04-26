package com.flaviu_mircia.walkit_fitness_app;

public class Stats {
    private int isScreenOn;
    private int isbetweenRestingHours;
    private double luxQuantity;
    private double db;

    public Stats() {

    }

    public Stats(int isScreenOn, int isbetweenRestingHours, double luxQuantity, double db) {
        this.isScreenOn = isScreenOn;
        this.isbetweenRestingHours = isbetweenRestingHours;
        this.luxQuantity = luxQuantity;
        this.db = db;
    }


    public int getIsScreenOn() {
        return isScreenOn;
    }

    public void setIsScreenOn(int isScreenOn) {
        this.isScreenOn = isScreenOn;
    }

    public int getIsbetweenRestingHours() {
        return isbetweenRestingHours;
    }

    public void setIsbetweenRestingHours(int isbetweenRestingHours) {
        this.isbetweenRestingHours = isbetweenRestingHours;
    }

    public double getLuxQuantity() {
        return luxQuantity;
    }

    public void setLuxQuantity(double luxQuantity) {
        this.luxQuantity = luxQuantity;
    }

    public double getDb() {
        return db;
    }

    public void setDb(double db) {
        this.db = db;
    }
}
