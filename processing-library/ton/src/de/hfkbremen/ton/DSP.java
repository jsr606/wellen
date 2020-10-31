package de.hfkbremen.ton;

import processing.core.PApplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DSP implements AudioBufferRenderer {

    //    void audioblock(float[] pOutputLeft,
    //                    float[] pOutputRight,
    //                    float[] pInputLeft,
    //                    float[] pInputRight) {}
    private static final String METHOD_NAME = "audioblock";
    private static AudioBufferPlayer mAudioPlayer;
    private static DSP mInstance = null;
    private final PApplet mPApplet;
    private Method mMethod = null;
    private float[] mCurrentBufferLeft;
    private float[] mCurrentBufferRight;

    public DSP(PApplet pPApplet) {
        mPApplet = pPApplet;
        try {
            mMethod = pPApplet.getClass().getDeclaredMethod(METHOD_NAME, float[].class, float[].class);
        } catch (NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public static DSP start(PApplet pPApplet) {
        if (mInstance == null) {
            mInstance = new DSP(pPApplet);
            mAudioPlayer = new AudioBufferPlayer(mInstance);
        }
        return mInstance;
    }

    public static int sample_rate() {
        return mAudioPlayer == null ? 0 : mAudioPlayer.sample_rate();
    }

    public static int buffer_size() {
        return mAudioPlayer == null ? 0 : mAudioPlayer.buffer_size();
    }

    public static float[] buffer_left() {
        return mInstance == null ? null : mInstance.mCurrentBufferLeft;
    }

    public static float[] buffer_right() {
        return mInstance == null ? null : mInstance.mCurrentBufferRight;
    }

    public void render(float[][] pSamples) {
        try {
            mMethod.invoke(mPApplet, pSamples[0], pSamples[1]);
            mCurrentBufferLeft = pSamples[0];
            mCurrentBufferRight = pSamples[1];
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
