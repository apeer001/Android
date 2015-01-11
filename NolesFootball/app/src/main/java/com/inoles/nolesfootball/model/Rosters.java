package com.inoles.nolesfootball.model;

import java.util.Comparator;

public class Rosters {
    public String mFirstName;
    public String mLastName;
    public String mPosition;
    public int mIsCoach;
    public String mShirtNumber;

    public static final Comparator<Rosters> NAME = new Comparator<Rosters>() {
        @Override
        public int compare(Rosters rosters, Rosters rosters2) {
            if ("Fisher".equals(rosters.mLastName)) {
                return 0;
            }
            return rosters.mLastName.compareTo(rosters2.mLastName);
        }
    };

    public static final Comparator<Rosters> NUMBER = new Comparator<Rosters>() {
        @Override
        public int compare(Rosters rosters, Rosters rosters2) {
            return rosters.mShirtNumber.compareTo(rosters2.mShirtNumber);
        }
    };
}
