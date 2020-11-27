package de.hfkbremen.ton.examples_ext;

import de.hfkbremen.ton.DSP;
import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Ton;
import de.hfkbremen.ton.Trigger;
import de.hfkbremen.ton.Wavetable;
import processing.core.PApplet;

/**
 * this examples demonstrates how to use a trigger to convert alternating signals into events ( `trigger` ). the trigger
 * is continously fed a signal, whenever the signal cross the zero point an event is triggered. the trigger can be
 * configured to detect *rising-edge* ( signal previoulsy had a negative value ), *falling-edge* ( signal previoulsy had
 * a positive value ) signals or both.
 * <p>
 * it is common to use a low-frequency oscillator (LFO) to generate the signal for the trigger.
 */
public class SketchExampleDSP08Trigger extends PApplet {
    private final int[] mNotes = {Note.NOTE_C3, Note.NOTE_C4, Note.NOTE_A2, Note.NOTE_A3};
    private int mBeatCount;

    private Trigger mTrigger;
    private Wavetable mWavetable;
    private float mSignal;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mTrigger = new Trigger(this);
        mTrigger.trigger_falling_edge(true);
        mTrigger.trigger_falling_edge(true);

        mWavetable = new Wavetable(64); /* use wavetable as LFO */
        Wavetable.sine(mWavetable.wavetable());
        mWavetable.interpolate(true); /* interpolate between samples to remove *steps* from the signal */
        mWavetable.set_frequency(1.0f / 3.0f); /* set phase duration to 3SEC */

        Ton.start();
        DSP.start(this); /* DSP is only used to create trigger events */
    }

    public void draw() {
        background(255);
        noStroke();
        fill(0);
        float mScale = (mBeatCount % 2) * 0.25f + 0.25f;
        ellipse(width * 0.5f, height * 0.5f, width * mScale, width * mScale);
        /* draw current signal signal */
        stroke(255);
        ellipse(width * 0.5f, map(mSignal, -1.0f, 1.0f, 0, height), 10, 10);
    }

    public void mouseMoved() {
        /* set oscillation speed a value between 0.1SEC and 5SEC */
        mWavetable.set_frequency(1.0f / map(mouseX, 0, width, 0.1f, 5.0f));
    }

    public void audioblock(float[] pOutputSamples) {
        for (int i = 0; i < pOutputSamples.length; i++) {
            mSignal = mWavetable.output();
            mTrigger.input(mSignal);
        }
    }

    public void trigger() {
        mBeatCount++;
        int mNote = mNotes[mBeatCount % mNotes.length];
        Ton.note_on(mNote, 100);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleDSP08Trigger.class.getName());
    }
}