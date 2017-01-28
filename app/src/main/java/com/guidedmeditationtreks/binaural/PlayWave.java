package com.guidedmeditationtreks.binaural;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWave {

	private final int SAMPLE_RATE = 44100;
	private AudioTrack mAudio;
	int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
	private int sampleCount;

	public PlayWave() {

		mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				buffsize, AudioTrack.MODE_STATIC);
	}

	public void setWave(int frequency) {
		sampleCount = (int) ((float) SAMPLE_RATE / frequency);
		short samples[] = new short[sampleCount];
		int amplitude = 32767;
		double twopi = 8. * Math.atan(1.);
		double phase = 0.0;

		for (int i = 0; i < sampleCount; i++) {
			samples[i] = (short) (amplitude * Math.sin(phase));
			phase += twopi * frequency / SAMPLE_RATE;
		}
		mAudio.write(samples, 0, sampleCount);
	}

	public void start() {
		mAudio.reloadStaticData();
		mAudio.setLoopPoints(0, sampleCount, -1);
		mAudio.play();
	}

	public void stop() {
		mAudio.stop();
	}

}
