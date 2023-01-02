package wellen.examples.DSP.analysis;

import processing.core.PApplet;
import wellen.Wellen;
import wellen.analysis.EnvelopeFollower;
import wellen.dsp.DSP;
import wellen.dsp.Wavetable;

public class ExampleDSPAnalysis02EnvelopeFollower extends PApplet {

    //@add import wellen.analysis.*;

    /*
     * this example demonstrates how to detect an envelope from an input signal. it uses that information to set the
     * amplitude of an oscillator.
     */

    private final EnvelopeFollower fEnvelopeFollower = new EnvelopeFollower();
    private float[] fEnvelopeFollowerBuffer;
    private final Wavetable fWavetable = new Wavetable();

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        fWavetable.set_waveform(10, Wellen.WAVEFORM_SQUARE);
        fWavetable.set_frequency(110);

        fEnvelopeFollower.set_attack(0.0002f);
        fEnvelopeFollower.set_release(0.0004f);

        DSP.start(this, 1, 1);
    }

    public void mouseMoved() {
        fEnvelopeFollower.set_attack(map(mouseX, 0, width, 0, Wellen.seconds_to_samples(0.1f)));
        fEnvelopeFollower.set_release(map(mouseY, 0, height, 0, Wellen.seconds_to_samples(0.1f)));
    }

    public void draw() {
        background(255);

        noStroke();
        fill(0);
        circle(width * 0.5f, height * 0.5f, height * 0.98f);

        fill(255);
        float mEnvelopeAverage = getEnvelopeAverage();
        circle(width * 0.5f, height * 0.5f, mEnvelopeAverage * 100);

        stroke(255);
        DSP.draw_buffers(g, width, height);
        Wellen.draw_buffer(g, width, height, fEnvelopeFollowerBuffer);
    }

    public void audioblock(float[] output_signal, float[] pInputSignal) {
        fEnvelopeFollowerBuffer = fEnvelopeFollower.process(pInputSignal);

        for (int i = 0; i < output_signal.length; i++) {
            fWavetable.set_amplitude(fEnvelopeFollowerBuffer[i]);
            output_signal[i] = fWavetable.output();
        }
    }

    private float getEnvelopeAverage() {
        float mEnvelopeAverage = 0;
        if (fEnvelopeFollowerBuffer != null) {
            for (float v : fEnvelopeFollowerBuffer) {
                mEnvelopeAverage += v;
            }
            mEnvelopeAverage /= fEnvelopeFollowerBuffer.length;
        }
        return mEnvelopeAverage;
    }

    public static void main(String[] args) {
        PApplet.main(ExampleDSPAnalysis02EnvelopeFollower.class.getName());
    }
}
