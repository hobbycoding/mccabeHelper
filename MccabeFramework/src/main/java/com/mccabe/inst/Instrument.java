package com.mccabe.inst;

import com.mccabe.Mccabe;

public class Instrument extends Mccabe {
    public static void main(String[] args) throws Exception {
        setProperties(args);
        Instrument instrument = new Instrument();
        instrument.start();
    }

    private void start() {

    }
}
