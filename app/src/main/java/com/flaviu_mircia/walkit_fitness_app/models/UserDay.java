package com.flaviu_mircia.walkit_fitness_app.models;

public class UserDay {
    private int steps;
    private int weight;
    private String sleep;
    public UserDay(){
    }
    public UserDay(int steps){
        this.steps = steps;
    }
    public UserDay(int weight, String sleep) {
        this.weight = weight;
        this.sleep = sleep;
    }

    public UserDay( int steps,int weight, String sleep) {
        this.steps = steps;
        this.weight = weight;
        this.sleep = sleep;
    }
    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }
}
