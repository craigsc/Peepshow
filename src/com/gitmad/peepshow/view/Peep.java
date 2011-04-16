package com.gitmad.peepshow.view;

/**
 * TODO: Enter class description.
 */
public class Peep {
    public static enum PeepMediaType {
        URL(),
        VIDEO(),
        MUSIC();
        private PeepMediaType() { }
    }

    private final PeepMediaType type;
    public Peep(final PeepMediaType type) {
        this.type = type;
    }


}
