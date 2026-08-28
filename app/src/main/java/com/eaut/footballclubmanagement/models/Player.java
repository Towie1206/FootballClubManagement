package com.eaut.footballclubmanagement.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "players")
public class Player {
    @PrimaryKey
    private int id;
    private String fullName;
    private String position;
    private int jerseyNumber;
    private String healthStatus;
    private int ovr;
    private int goals;
    private int mvp;

    public Player(int id, String fullName, String position, int jerseyNumber, String healthStatus, int ovr, int goals, int mvp) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.healthStatus = healthStatus;
        this.ovr = ovr;
        this.goals = goals;
        this.mvp = mvp;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPosition() { return position; }
    public int getJerseyNumber() { return jerseyNumber; }
    public String getHealthStatus() { return healthStatus; }
    public int getOvr() { return ovr; }
    public int getGoals() { return goals; }
    public int getMvp() { return mvp; }
}
