import wellen.*;
import wellen.dsp.*;

MidiOut mMidiOut;

/*
   The MIDI protocol specifies that a pitchbend value of 8192 (MSB of 64 and LSB of 0) means no bend.
   Thus, on the scale from 0 to 16,383, a value of 0 means maximum downward bend, 8,192 means no bend, 
   and 16,383 means maximum upward bend.
   
   Remember that all MIDI data bytes have their first (most significant) bit clear (0), so it’s really 
   only the other 7 bits that contain useful information. Thus each data byte has a useful range from 
   0 to 127. In the pitchbend message, we combine the two bytes (the LSB and the MSB) to make a single 
   14-bit value that has a range from 0 to 16,383. We do that by bit-shifting the MSB 7 bits to the left 
   and combining that with the LSB using a bitwise OR operation (or by addition).

   from: https://sites.uci.edu/camp2014/2014/04/30/managing-midi-pitchbend-messages/
*/

float bend = 0;
float increment = 0.001;

void setup() {
    size(200, 600);
    Wellen.dumpMidiOutputDevices();
    mMidiOut = new MidiOut("VirMIDI [hw:1,0,0]");
}

void draw() {
    background(255);
    
    bend += increment;
    if (bend > 1) bend = -1;
    
    float _y = map(bend,-1,1,height,0);
    line(0,_y,width,_y);
    
    int _pitchbend = int(map(bend,0,1,0,16383));
    byte data2 = byte (_pitchbend >> 7);
    byte data1 = byte (_pitchbend & 0x7F);
    mMidiOut.sendPitchBend(1, data1, data2);
}
