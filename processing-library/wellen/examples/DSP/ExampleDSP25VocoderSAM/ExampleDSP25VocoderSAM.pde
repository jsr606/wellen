import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrate how to use a vocoder with SAM. see the vocoder example for further explanation.
 */
Vocoder   mVocoder;
Wavetable mVocoderCarrierOsc;
SAM       mSAM;
void settings() {
    size(640, 480);
}
void setup() {
    Wellen.dumpAudioInputAndOutputDevices();
    mVocoderCarrierOsc = new Wavetable();
    Wavetable.fill(mVocoderCarrierOsc.get_wavetable(), Wellen.WAVEFORM_SAWTOOTH);
    mVocoderCarrierOsc.set_frequency(55);
    mVocoderCarrierOsc.set_amplitude(1.0f);
    mVocoder = new Vocoder(24, 4, Wellen.DEFAULT_SAMPLING_RATE, 1);
    mVocoder.set_volume(8);
    mSAM = new SAM();
    mSAM.set_speed(60);
    DSP.start(this, 1);
    Beat.start(this, 25);
}
void draw() {
    background(255);
    stroke(0);
    DSP.draw_buffers(g, width, height);
}
void beat(int beatCount) {
    mSAM.say("Harder, Better, Faster, Stronger");
}
void mouseMoved() {
    mVocoder.set_formant_shift(map(mouseX, 0, width, 0.25f, 2.5f));
    mVocoder.set_reaction_time(map(mouseY, 0, height, 0.002f, 0.1f));
}
void keyPressed() {
    if (key == '1') {
        mVocoderCarrierOsc.set_frequency(27.5f);
    }
    if (key == '2') {
        mVocoderCarrierOsc.set_frequency(55.0f);
    }
    if (key == '3') {
        mVocoderCarrierOsc.set_frequency(110.0f);
    }
    if (key == '4') {
        mVocoderCarrierOsc.set_frequency(220.0f);
    }
}
void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        float mCarrier   = mVocoderCarrierOsc.output();
        float mModulator = mSAM.output() * 0.5f;
        output_signal[i] = mVocoder.process(mCarrier, mModulator);
    }
}
