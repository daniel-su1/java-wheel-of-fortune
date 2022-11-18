
// The "TossUpInput" class.
import java.awt.*;

import javax.swing.JOptionPane;

import hsa.Console;

public class TossUpInput extends Thread{
	Console c; // The output console
	char input;
	int charInputToInt;
	boolean attemptingToGuess=false;
	TossUpInput(Console c) {
		this.c = c;
	}

	public void getUserInput() {
		while (true) {
			input = c.getChar();
			charInputToInt = Character.getNumericValue(input);
			switch(charInputToInt) {
			case 1:
				attemptingToGuess=true;
				break;
			case 2:
				attemptingToGuess=true;
				break;
			case 3:
				attemptingToGuess=true;
				break;
			}
		}
	}

	public void run() {
		this.getUserInput();
	}
}// TossUpInput class
