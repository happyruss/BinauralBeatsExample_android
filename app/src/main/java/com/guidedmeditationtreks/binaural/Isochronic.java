package com.guidedmeditationtreks.binaural;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Isochronic implements BeatsEngine {

	private final int SAMPLE_RATE = 44100;
	private final int FADER = 128;
	private final int AMPLITUDE_INCREMENT = 256;

	private int multiplier, sampleCount, frame;
	private boolean isOn, doRelease;
	private AudioTrack mAudio;
	private boolean isPlaying;

	public Isochronic(float frequency, float isoBeat) {

		int amplitudeMax = Helpers.getAdjustedAmplitudeMax(frequency);

		//period of the sine wave
		int sCount = (int) ((float) SAMPLE_RATE / frequency);

		//multiplier denotes half of a phase of an isochronic beat
		multiplier = (int) ((SAMPLE_RATE/2) / isoBeat);
		//sampleCount = Helpers.getLCM(sCount, multiplier * 2);
		sampleCount = multiplier * 2;
		int buffSize = sampleCount * 2;

		mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				buffSize, AudioTrack.MODE_STATIC);

		short samples[] = new short[sampleCount];
		int amplitude = 0;
		double twopi = 8. * Math.atan(1.);
		double phase = 0.0;
		isOn = true;

		frame = 0;

		for (int i = 0; i < sampleCount; i++) {

			//Amplitude will shift based on where we are in the period in isochronic tones
			frame++;
			if (frame == multiplier) {
				if (isOn)
					amplitude = amplitudeMax;
				else
					amplitude = 0;
				frame = 0;
				isOn = !isOn;
			}
			else if (frame <= FADER)
			{
				if (isOn)
				{
					amplitude = amplitude + AMPLITUDE_INCREMENT > amplitudeMax ? amplitudeMax : amplitude + AMPLITUDE_INCREMENT;
				}
				else
				{
					amplitude = amplitude - AMPLITUDE_INCREMENT < 0 ? 0 : amplitude - AMPLITUDE_INCREMENT;
				}
			}

			samples[i] = (short) (amplitude * Math.sin(phase));
			if (i % sCount == 0) {
				phase = 0.0;
			}
			phase += twopi * frequency / SAMPLE_RATE;
		}
		mAudio.write(samples, 0, sampleCount);
		mAudio.setStereoVolume(0.0f, 0.0f);
		Helpers.napThread();
	}

	public void release() {
		doRelease = true;
		stop();
	}

	public void start() {
		mAudio.reloadStaticData();
		mAudio.setLoopPoints(0, sampleCount, -1);
		isPlaying = true;
		mAudio.play();
		Helpers.napThread();
		mAudio.setStereoVolume(1f, 1f);
	}

	public void stop() {

		mAudio.setStereoVolume(0.0f, 0.0f);
		Helpers.napThread();
		mAudio.stop();
		isPlaying = false;
		if (doRelease) {
			mAudio.flush();
			mAudio.release();
		}
	}

	public boolean getIsPlaying() { return isPlaying; }

}
