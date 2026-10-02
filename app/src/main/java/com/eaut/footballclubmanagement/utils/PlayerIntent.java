package com.eaut.footballclubmanagement.utils;

import android.content.Intent;

import com.eaut.footballclubmanagement.models.Player;

/** Single source of truth for transferring a complete player between screens. */
public final class PlayerIntent {
    private static final String PREFIX = "com.eaut.footballclubmanagement.player.";
    private static final String ID = PREFIX + "ID";
    private static final String NAME = PREFIX + "NAME";
    private static final String POSITION = PREFIX + "POSITION";
    private static final String JERSEY = PREFIX + "JERSEY";
    private static final String HEALTH = PREFIX + "HEALTH";
    private static final String OVR = PREFIX + "OVR";
    private static final String GOALS = PREFIX + "GOALS";
    private static final String ASSISTS = PREFIX + "ASSISTS";
    private static final String MATCHES = PREFIX + "MATCHES";
    private static final String MVP = PREFIX + "MVP";
    private static final String CLUB = PREFIX + "CLUB";
    private static final String PAC = PREFIX + "PAC";
    private static final String SHO = PREFIX + "SHO";
    private static final String PAS = PREFIX + "PAS";
    private static final String DRI = PREFIX + "DRI";
    private static final String DEF = PREFIX + "DEF";
    private static final String PHY = PREFIX + "PHY";

    private PlayerIntent() {
    }

    public static Intent putPlayer(Intent intent, Player player) {
        return intent
                .putExtra(ID, player.getId())
                .putExtra(NAME, player.getFullName())
                .putExtra(POSITION, player.getPosition())
                .putExtra(JERSEY, player.getJerseyNumber())
                .putExtra(HEALTH, player.getHealthStatus())
                .putExtra(OVR, player.getOvr())
                .putExtra(GOALS, player.getGoals())
                .putExtra(ASSISTS, player.getAssists())
                .putExtra(MATCHES, player.getMatches())
                .putExtra(MVP, player.getMvp())
                .putExtra(CLUB, player.getClub())
                .putExtra(PAC, player.getPac())
                .putExtra(SHO, player.getSho())
                .putExtra(PAS, player.getPas())
                .putExtra(DRI, player.getDri())
                .putExtra(DEF, player.getDef())
                .putExtra(PHY, player.getPhy());
    }

    public static Player readPlayer(Intent intent) {
        if (intent == null || !intent.hasExtra(ID)) {
            return null;
        }
        return new Player(
                intent.getIntExtra(ID, -1),
                intent.getStringExtra(NAME),
                intent.getStringExtra(POSITION),
                intent.getIntExtra(JERSEY, 0),
                intent.getStringExtra(HEALTH),
                intent.getIntExtra(OVR, 0),
                intent.getIntExtra(GOALS, 0),
                intent.getIntExtra(ASSISTS, 0),
                intent.getIntExtra(MATCHES, 0),
                intent.getIntExtra(MVP, 0),
                intent.getStringExtra(CLUB),
                intent.getIntExtra(PAC, 0),
                intent.getIntExtra(SHO, 0),
                intent.getIntExtra(PAS, 0),
                intent.getIntExtra(DRI, 0),
                intent.getIntExtra(DEF, 0),
                intent.getIntExtra(PHY, 0)
        );
    }
}
