package de.hfkbremen.ton;

import processing.core.PApplet;

import static de.hfkbremen.ton.Ton.DEFAULT_SAMPLING_RATE;
import static de.hfkbremen.ton.Ton.OSC_NOISE;
import static de.hfkbremen.ton.Ton.OSC_SAWTOOTH;
import static de.hfkbremen.ton.Ton.OSC_SINE;
import static de.hfkbremen.ton.Ton.OSC_SQUARE;
import static de.hfkbremen.ton.Ton.OSC_TRIANGLE;

public class Wavetable implements DSPNodeOutput {

    public static final float DEFAULT_FREQUENCY = 220.0f;
    public static final float DEFAULT_AMPLITUDE = 0.75f;
    private final int mSamplingRate;
    private final float[] mWavetable;
    private float mFrequency;
    private float mStepSize;
    private float mArrayPtr;
    private float mAmplitude;
    private boolean mInterpolateSamples = false;

    public Wavetable(int pWavetableSize) {
        this(pWavetableSize, DEFAULT_SAMPLING_RATE);
    }

    public Wavetable(int pWavetableSize, int pSamplingRate) {
        mWavetable = new float[pWavetableSize];
        mSamplingRate = pSamplingRate;
        mArrayPtr = 0;
        mAmplitude = DEFAULT_AMPLITUDE;
        set_frequency(DEFAULT_FREQUENCY);
    }

    public static void fill(float[] pWavetable, int pWavetableType) {
        switch (pWavetableType) {
            case OSC_SINE:
                sine(pWavetable);
                break;
            case OSC_TRIANGLE:
                triangle(pWavetable);
                break;
            case OSC_SAWTOOTH:
                sawtooth(pWavetable);
                break;
            case OSC_SQUARE:
                square(pWavetable);
                break;
            case OSC_NOISE:
                noise(pWavetable);
                break;
        }
    }

    public static void sine(float[] pWavetable) {
        for (int i = 0; i < pWavetable.length; i++) {
            pWavetable[i] = PApplet.sin(2.0f * PApplet.PI * ((float) i / (float) (pWavetable.length)));
        }
    }

    public static void sawtooth(float[] pWavetable) {
        for (int i = 0; i < pWavetable.length; i++) {
            pWavetable[i] = 2.0f * ((float) i / (float) (pWavetable.length - 1)) - 1.0f;
        }
    }

    public static void triangle(float[] pWavetable) {
        final int q = pWavetable.length / 4;
        final float qf = pWavetable.length * 0.25f;
        for (int i = 0; i < q; i++) {
            pWavetable[i] = i / qf;
            pWavetable[i + (q * 1)] = (qf - i) / qf;
            pWavetable[i + (q * 2)] = -i / qf;
            pWavetable[i + (q * 3)] = -(qf - i) / qf;
        }
    }

    public static void square(float[] pWavetable) {
        for (int i = 0; i < pWavetable.length / 2; i++) {
            pWavetable[i] = 1.0f;
            pWavetable[i + pWavetable.length / 2] = -1.0f;
        }
    }

    public static void noise(float[] pWavetable) {
        for (int i = 0; i < pWavetable.length; i++) {
            pWavetable[i] = (float) (Math.random() * 2.0 - 1.0);
        }
    }

    public float get_frequency() {
        return mFrequency;
    }

    public void set_frequency(float pFrequency) {
        if (mFrequency != pFrequency) {
            mFrequency = pFrequency;
            mStepSize = mFrequency * ((float) mWavetable.length / (float) mSamplingRate);
        }
    }

    public void interpolate(boolean pInterpolateSamples) {
        mInterpolateSamples = pInterpolateSamples;
    }

    public float get_amplitude() {
        return mAmplitude;
    }

    public void set_amplitude(float pAmplitude) {
        mAmplitude = pAmplitude;
    }

    public float[] wavetable() {
        return mWavetable;
    }

    public float output() {
        mArrayPtr += mStepSize;
        final int i = (int) mArrayPtr;
        final float mFrac = mArrayPtr - i;
        final int j = i % mWavetable.length;
        mArrayPtr = j + mFrac;

        if (mInterpolateSamples) {
            float mNextSample = mWavetable[(j + 1) % mWavetable.length];
            float mSample = mWavetable[j];
            float mInterpolatedSample = mSample * (1.0f - mFrac) + mNextSample * mFrac;
            return mInterpolatedSample * mAmplitude;
        } else {
            return mWavetable[j] * mAmplitude;
        }
    }
}