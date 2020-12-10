import wellen.*; 
import netP5.*; 
import oscP5.*; 

/*
 * this example demonstrates how to use the OSC tone engine to send OSC commands. the defined OSC address patterns
 * can be found in `ToneEngineOSC`
 */

int mNote;

void settings() {
    size(640, 480);
}

void setup() {
    Tone.start("osc", "127.0.0.1", "7001");
}

void draw() {
    background(Tone.is_playing() ? 255 : 0);
}

void mousePressed() {
    Tone.instrument(mouseX < width / 2.0 ? 0 : 1);
    mNote = 45 + (int) random(0, 12);
    Tone.note_on(mNote, 127);
}

void mouseReleased() {
    Tone.note_off(mNote);
}
