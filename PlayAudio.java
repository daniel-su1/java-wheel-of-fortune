// Daniel Su
// Mr. Guglielmi
//June 22, 2021
//This program will play audio when given the name of a audio file
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import hsa.Console;

public class PlayAudio extends Thread{
	Console c;//create console c
	SourceDataLine sourceDataLine;//sourcedataline is a global variable so i can call sourcedataline.stop from wheel of fortune
	Clip clip;////clip is a global variable so i can call clip.stop from wheel of fortune
	PlayAudio(Console c) {
		this.c = c;// passes the console c from the main program
	}
	public void playClip(String fileName)//method for playing clips or short audio files that are small enough to be loaded into memory
	{
	    try {
			AudioInputStream ais = AudioSystem
					.getAudioInputStream(new File(fileName));//read audio file

	        DataLine.Info dataInfo = new DataLine.Info(Clip.class, ais.getFormat());//get the format of the audio file

	        if (AudioSystem.isLineSupported(dataInfo)) {
	            clip = (Clip)AudioSystem.getLine(dataInfo);//initialize the clip
	            clip.open(ais);//open the audio file
	            clip.start();//start playing the clip
             
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void play(String fileName) {//method for streaming the data from a larger audio file
		try {
			
			AudioInputStream ais = AudioSystem
					.getAudioInputStream(new File(fileName));//read audio file
			
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, ais.getFormat());//get the format
			
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);//initialize sourceDataLine
			
			new PlayThread(ais, sourceDataLine, ais.getFormat()).start();//start playing
		}
		
		catch(Exception e) {
			throw new RuntimeException (e);
		}
	}
	
	public class PlayThread extends Thread {//class inside of a class because when I made a seperate class file and created an instance the audio was really really glitchy
		private AudioInputStream ais;
		private AudioFormat format;
		public SourceDataLine sourceDataLine;

		byte tempBuffer[] = new byte[10000];//set the buffer to allow for the audio file size

		public PlayThread(AudioInputStream ais, SourceDataLine sourceDataLine, AudioFormat format) {
			this.ais = ais;// passes the audio input stream from the main class
			this.sourceDataLine = sourceDataLine;// passes the sourcedataline from the main class
			this.format = format;// passes the format from the main class
		}

		public void run() {
			try {
				sourceDataLine.open(this.format);//opens the line with the format
				sourceDataLine.start();//start playing

				int cnt;
				while ((cnt = this.ais.read(tempBuffer, 0, tempBuffer.length)) != -1) {//while the tempbuffer length is not -1
					if (cnt > 0) {//if the tempbuffer is greater than 0
						sourceDataLine.write(tempBuffer, 0, cnt);//writes audio data to the mixer via the sourcedataline
					}
				}

				sourceDataLine.drain();//drains the queued data
				sourceDataLine.close();//closes the sourcedataline

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}