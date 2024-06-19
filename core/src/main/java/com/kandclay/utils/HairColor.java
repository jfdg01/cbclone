package com.kandclay.utils;

public enum HairColor {
    BLONDE, BRUNETTE, REDHEAD;

    private static final HairColor[] vals = values();

    public HairColor next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}

