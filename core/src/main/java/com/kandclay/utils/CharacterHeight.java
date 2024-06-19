package com.kandclay.utils;

public enum CharacterHeight {

    SHORT, AVERAGE, TALL;

    private static final CharacterHeight[] vals = values();

    public CharacterHeight next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
