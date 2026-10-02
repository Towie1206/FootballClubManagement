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
    private int assists;
    private int matches;
    private int mvp;
    
    // FIFA 25 Stats & Club
    private String club;
    private int pac;
    private int sho;
    private int pas;
    private int dri;
    private int def;
    private int phy;

    public Player(int id, String fullName, String position, int jerseyNumber, String healthStatus, int ovr, int goals, int assists, int matches, int mvp, String club, int pac, int sho, int pas, int dri, int def, int phy) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.healthStatus = healthStatus;
        this.ovr = ovr;
        this.goals = goals;
        this.assists = assists;
        this.matches = matches;
        this.mvp = mvp;
        this.club = club;
        this.pac = pac;
        this.sho = sho;
        this.pas = pas;
        this.dri = dri;
        this.def = def;
        this.phy = phy;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPosition() { return position; }
    public int getJerseyNumber() { return jerseyNumber; }
    public String getHealthStatus() { return healthStatus; }
    public int getOvr() { return ovr; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
    public int getMatches() { return matches; }
    public int getMvp() { return mvp; }
    
    public String getClub() { return club; }
    public int getPac() { return pac; }
    public int getSho() { return sho; }
    public int getPas() { return pas; }
    public int getDri() { return dri; }
    public int getDef() { return def; }
    public int getPhy() { return phy; }
}
