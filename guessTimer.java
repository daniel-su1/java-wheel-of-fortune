// Daniel Su
// Mr. Guglielmi
//June 22, 2021
//This program will create a timer for use in guessConsonant and guessVowel.
import java.awt.*;
import hsa.Console;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class guessTimer extends Thread
{
    Console c; //the output console
    short time = 5;
    boolean outOfTime=false;
    // constructor for time class
    guessTimer (Console c){
	this.c = c; // passes the console c from the main program
    }


    public void run (){
    	PlayAudio p = new PlayAudio(c);//create new instance of the PlayAudio class
    	Font GothamMedium=null;//new font
		try {
			GothamMedium = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 72);
		} catch (Exception e) {
		}
		boolean countDown=true;//loop controller
    	while(countDown) {
    		try {
				Thread.sleep(1000);//pause for 1 second
			} catch (Exception e) {
			}
    		time--;//subtract time by 1
    		//draw the gray circle
        	c.setColor(new Color(183,183,183));
    		c.fillOval(1137, 607, 75, 75);
    		//draw the current time left
    		c.setColor(Color.black);
    		c.setFont(GothamMedium);
    		c.drawString(String.valueOf(time), 1150, 670);
    		
    		if(time==0) {//if time hits 0
				p.playClip("Buzzer.wav");//play the buzzer sound
    			JOptionPane.showMessageDialog(new JFrame(), ("You ran out of time! Press a key to continue.."), "Consonant Guess",
    					JOptionPane.INFORMATION_MESSAGE);// display error message window
    			outOfTime=true;//set boolean outOfTime to true
    			countDown=false;//stop the loop
    		}
    	}
    }

} // Time class
