/*
 * Wellen
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2022 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package wellen;

import java.util.ArrayList;

import static wellen.Wellen.NO_INPOINT;
import static wellen.Wellen.NO_OUTPOINT;
import static wellen.Wellen.SIGNAL_PROCESSING_IGNORE_IN_OUTPOINTS;

/**
 * manages a collection of {@link DSPTrack}s. each track processes audio signals and is updated via the method
 * <pre><code>void&nbsp;beat(int)</code></pre>.
 * <p>
 * note, that since {@link DSPComposition} implements {@link DSPTrack} it can also be added as a track to another
 * composition.
 */
public class DSPComposition extends DSPTrack {
    public static boolean VERBOSE = false;

    private final ArrayList<DSPTrack> mTracks = new ArrayList<>();
    private int mBeat = SIGNAL_PROCESSING_IGNORE_IN_OUTPOINTS;

    public ArrayList<DSPTrack> tracks() {
        return mTracks;
    }

    public DSPTrack track(int pID) {
        return mTracks.get(pID);
    }

    public Signal output_signal() {
        final Signal mSignalSum = new Signal();
        for (DSPTrack t : mTracks) {
            if (mBeat == SIGNAL_PROCESSING_IGNORE_IN_OUTPOINTS || evaluate_in_outpoints(t, mBeat)) {
                final Signal mSignal = t.output_signal();
                if (mSignal.num_channels() == 1) {
                    mSignalSum.left_add(mSignal.left() * t.volume);
                    mSignalSum.right_add(mSignal.left() * t.volume);
                } else if (mSignal.num_channels() == 2) {
                    mSignalSum.left_add(mSignal.left() * t.volume);
                    mSignalSum.right_add(mSignal.right() * t.volume);
                } else if (mSignal.num_channels() > 2) {
                    mSignalSum.left_add(mSignal.left() * t.volume);
                    mSignalSum.right_add(mSignal.right() * t.volume);
                    if (VERBOSE) {
                        System.out.println(
                        "+++ track ID" + t.ID + " does not emit mono or stereo signal. number of channels is: " + mSignal.num_channels());
                    }
                } else {
                    if (VERBOSE) {
                        System.out.println("+++ track ID" + t.ID + " does not emit a signals number of channels is 0");
                    }
                }
            }
        }
        return mSignalSum;
    }

    private boolean evaluate_in_outpoints(DSPTrack pTrack, int pBeat) {
        final boolean mNoInOutPoint = (pTrack.in == NO_INPOINT && pTrack.out == NO_OUTPOINT);
        if (mNoInOutPoint) {
            return true;
        }
        final boolean mIsBeyondInPoint = (pBeat >= pTrack.in);
        final boolean mIsBeforeOutPoint = (pBeat <= pTrack.out) || (pTrack.out == NO_OUTPOINT) || pTrack.loop;
        final boolean mWithinInOutPoint = mIsBeyondInPoint && mIsBeforeOutPoint;
        return mWithinInOutPoint;
    }

    public void beat(int pBeat) {
        mBeat = get_relative_position(pBeat);
        for (DSPTrack c : mTracks) {
            if (evaluate_in_outpoints(c, mBeat)) {
                c.beat(mBeat);
            }
        }
    }
}
