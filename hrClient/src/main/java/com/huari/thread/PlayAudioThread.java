package com.huari.thread;

import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class PlayAudioThread extends Thread {

	boolean haveNewAudio = false;
	boolean runPlayAudio = true;
	int FREQUENCY = 22050;
	public AudioTrack at;
	public int audioBuffersize;
	public byte[] audioBuffer;
	
	public LinkedList<byte[]> m_in_q;
//	public int anum = 0;
//	public byte[] huancun;
//	public int hindex = 0;

	public void setRunPlayAudio(boolean b) {
		runPlayAudio = b;
	}

	public PlayAudioThread() {
		audioBuffersize = AudioTrack.getMinBufferSize(FREQUENCY,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioBuffersize = 4096;
		Log.i("BUFFER缓存的大小是", audioBuffersize + "  ");
		at = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				audioBuffersize*2 , AudioTrack.MODE_STREAM);
		audioBuffer = new byte[audioBuffersize*2];
		
		m_in_q = new LinkedList<byte[]>(); 
	}

	public void setHaveNewAudio(boolean have) {
		haveNewAudio = have;
	}

	public byte[] getAudioBuffer() {
		return audioBuffer;
	}

	public LinkedList<byte[]> getAudiolist() {
		return m_in_q;
	}
	
	@Override
	public void run() {
		at.play();
		
		while (runPlayAudio) {
			if (m_in_q.size() >= 4) {
				at.write(m_in_q.removeFirst(), 0, m_in_q.removeFirst().length);

				//while (at.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
//					try {
//						sleep(10,0);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				//}

			}
		}
		at.stop();
		at.release();
	}

}
