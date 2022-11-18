// Daniel Su
// Mr. Guglielmi
//June 22, 2021
//This program will play America's Game®, Wheel of Fortune. It features two game modes: Toss Up and Main Game. It reads and stores high score data in a text file.
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import hsa.Console;
import hsa.Message;

public class WheelOfFortune extends Thread {
	Console c;// variable to store the console
	PlayAudio p = new PlayAudio(c);// create new instance of the PlayAudio
	JFrame f = new JFrame();// the error window
	String hiddenPhrase;// string to store the hidden phrase
	char hiddenPhraseCharArray[];// char array to store the hidden phrase that is to be displayed on the game
									// board
	boolean alreadyGuessedChars[];// char array to store the already guessed chars for the main game
	boolean playingMainGame = false;// boolean to store whether the player is currently playing the main game. this
									// tells puzzle() whether or not to generate a new puzzle when it is called.
	int playerMoney[] = { 0, 0, 0 };// int array to store all the player's money
	String guess = "";// string to store the guess
	String playerNames[] = { "Player 1", "Player 2", "Player 3" };// string array to store the player names that are
																	// asked in playerInfo()
	byte currentPlayerTurn = 0;// variable to store the current player turn for the main game
	boolean comingFromInstructions = false;// boolean to say if the player is coming from instructions to say if to play
											// the music from the beginning
	short consonantReward = 0;// short to store the consonant reward the player spins in wheel() and is
								// rewarded in guessConsonant()
	Font GothamMedium = null;// font variable to store the gotham medium font
	Font HelveticaCondensedBlackSERegular58 = null;// font variable to store the game board font
	String category = "";// string to store the category
	byte tossUpRoundNumber = 1;// byte to store the toss up round number
	byte mainGameRoundNumber = 1;// byte to store the main game round number
	String[] data = new String[6];// array to store the data from the text file

	WheelOfFortune() {
		c = new Console(36, 160, "WHEEL OF FORTUNE");// create new console with size 1280 x 720 with name wheel of
														// fortune

	}

	// a method which will write data to a file
	// this data is preprogrammed in
	public void splashScreen() {
		p.playClip("crowd.wav");// play clip crowd.wav
		c.setColor(new Color(0, 2, 29));// dark navy blue
		try {
			BufferedImage someImage;// buffered image
			someImage = ImageIO.read(new File("logoSplashScreen.png"));// splash screen png stored in variable someImage
			int down = 1;// variable to make the image go down

			for (int i = 1; i < 350; i += (100 / down)) {// loop to draw the navy blue background and image on top 350
															// times, each time the image is slightly lower to give the
															// illusion it is moving downwards
				c.drawImage(someImage, 182, -200 + i, null);

				c.fillRect(0, 0, 1280, 720);

				c.drawImage(someImage, 182, -200 + i, null);
				down += 1;// increase the down variable by 1

				try {
					Thread.sleep(20);// 20 milliseconds in between each frame
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int left = 1;// variable to move the image left as it increases in size
			int up = 1;// variable to move the image up as it increases in size

			for (double i = 1; i < 2; i += 0.01) {// loop to draw the image slightly bigger 200 times to give the
													// illusion it is growing

				c.drawImage(someImage.getScaledInstance((int) (someImage.getWidth() * i),
						(int) (someImage.getHeight() * i), someImage.SCALE_FAST), (int) (182 - (left)), 149 - up, null);
				c.fillRect(0, 0, 1280, 720);
				c.drawImage(someImage.getScaledInstance((int) (someImage.getWidth() * i),
						(int) (someImage.getHeight() * i), someImage.SCALE_FAST), (int) (182 - (left)), 149 - up, null);
				left += 4;
				up++;
				Thread.sleep((1));// 1 millisecond between frames
			}
		} catch (Exception e) {

		}
		mainMenu();// call method mainMenu
	}

	private void readData() {
		String line = "";// variable to store the currently read line from the File in
		try {

			BufferedReader input = new BufferedReader(new FileReader("playerInfo.txt"));// set the buffered reader to
																						// the file
			int count = 0;// the loop counter
			line = input.readLine();// set the line to be the first line in the file
			while (line != null) {// while line is not null
				data[count] = line;// store the data in the array[current line]
				line = input.readLine();// read the next line
				count++;
			}
		} catch (Exception e) {
			c.println("data save error");// if the file can't be read
		}
	}

	public void mainMenu() {// main menu
		if (!comingFromInstructions) {// if it is the first time loading the main menu
			p.play("mainTheme.wav");// play the main theme using sourcedataline
		}

		char menuSelection;// char to store the menu selection
		boolean valid = false;// loop controller
		BufferedImage background = null;// background image
		BufferedImage logo = null;// logo image

		try {
			background = ImageIO.read(new File("mainMenuBackground.png"));
			logo = ImageIO.read(new File("logo.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readData();
		c.drawImage(background, 0, 0, null);// draw the background
		c.drawImage(logo, 399, 17, null);// draw the logo
		c.setColor(new Color(33, 62, 241));// set the color to the lighter blue
		// draw the light blue borders around the menu buttons
		c.fillRoundRect(310, 251, 661, 81, 6, 6);
		c.fillRoundRect(310, 358, 661, 81, 6, 6);
		c.fillRoundRect(310, 470, 661, 81, 6, 6);
		c.fillRoundRect(310, 582, 661, 81, 6, 6);
		c.setColor(new Color(6, 14, 36));// set the color to dark blue
		// draw the dark blue insides of the menu buttons
		c.fillRoundRect(314, 255, 653, 73, 6, 6);
		c.fillRoundRect(314, 362, 653, 73, 6, 6);
		c.fillRoundRect(314, 474, 653, 73, 6, 6);
		c.fillRoundRect(314, 586, 653, 73, 6, 6);

		try {
			GothamMedium = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))// load
																														// the
																														// font
																														// file
					.deriveFont(Font.PLAIN, 50);// set create the font from the loaded font from the font file
		} catch (Exception e) {
			c.println("font not found");
		} // create the custom font they use on the show from .ttf file

		c.setColor(Color.white);// set the color to white
		c.setFont(GothamMedium);// set the font
		// draw the menu options on the buttons
		c.drawString("1. New Game", 475, 306);
		c.drawString("2. Instructions", 469, 414);
		c.drawString("3. Hall Of Fame", 462, 525);
		c.drawString("4. Exit", 563, 637);

		while (!valid) {
			menuSelection = c.getChar();// get user input
			valid = true;// end the loop
			if (menuSelection != '1' && menuSelection != '2' && menuSelection != '3' && menuSelection != '4') {// if it
																												// isnt
																												// 1 2 3
																												// or 4
				JOptionPane.showMessageDialog(f, "Please only enter a number from 1 to 4.", "Error",
						JOptionPane.WARNING_MESSAGE);// display error message window
				valid = false;// continue the loop
			}
			switch (menuSelection) {
			case '1':// new game
				p.sourceDataLine.stop();// stop the music
				playerInfo();
				break;
			case '2':// instructions
				instructions();
				break;
			case '3':// hall of fame
				displayPlayerInfo();
				break;
			case '4':// goodbye
				exit();
				break;
			}
		}

	}

	public void playerInfo() {// method for getting the player names
		try {
			BufferedImage someImage = ImageIO.read(new File("playerInfo.png"));// load the image of the 3 contestants
			c.drawImage(someImage, 0, 0, null);// draws the image
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}

		// booleans for if the name is valid
		boolean player1NameValid = false;
		boolean player2NameValid = false;
		boolean player3NameValid = false;

		// load and draw the first highlighted player image
		try {
			BufferedImage someImage = ImageIO.read(new File("playerInfoPlayer1.png"));
			c.drawImage(someImage, 0, 0, null);
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}

		while (!player1NameValid) {// loop to get the player name
			playerNames[0] = JOptionPane.showInputDialog("Please enter player 1's name:"); // accepts
																							// input
																							// from
																							// JOptionPane
			if (playerNames[0] == null || playerNames[0].equals("") || playerNames[0].equals(" ")) {// if they didnt
																									// enter anything

				JOptionPane.showMessageDialog(null, "Error null entry, please enter a name"); // error message
				player1NameValid = false;
			}

			else {
				player1NameValid = true; // else ends loop
			}
		}

		// load and draw the second highlighted player image
		try {
			BufferedImage someImage = ImageIO.read(new File("playerInfoPlayer2.png"));
			c.drawImage(someImage, 0, 0, null);
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}

		while (!player2NameValid) {// loop to get the player name
			playerNames[1] = JOptionPane.showInputDialog("Please enter player 1's name:"); // accepts
																							// input
																							// from
																							// JOptionPane

			if (playerNames[1] == null || playerNames[1].equals("") || playerNames[1].equals(" ")) {// if they didnt
																									// enter anything

				JOptionPane.showMessageDialog(null, "Error null entry, please enter a name"); // error message
				player2NameValid = false;
			}

			else {
				player2NameValid = true; // else ends loop
			}
		}

		// load and draw the third highlighted player image
		try {
			BufferedImage someImage = ImageIO.read(new File("playerInfoPlayer3.png"));
			c.drawImage(someImage, 0, 0, null);
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}

		while (!player3NameValid) {// loop to get the player name
			playerNames[2] = JOptionPane.showInputDialog("Please enter player 3's name:"); // accepts
																							// input
																							// from
																							// JOptionPane

			if (playerNames[2] == null || playerNames[2].equals("") || playerNames[2].equals(" ")) {// if they didnt
																									// enter anything

				JOptionPane.showMessageDialog(null, "Error null entry, please enter a name"); // error message
				player3NameValid = false;
			}

			else {
				player3NameValid = true; // else ends loop
			}
		}

		c.clear();// clears the screen
		roundOrderController();
	}

	public void roundOrderController() {// method for organizing the flow of the game
		if (mainGameRoundNumber == 1) {// initial few rounds
			tossUp();
			tossUpRoundNumber = 2;
			tossUp();
			mainGame();
		} else if (mainGameRoundNumber == 2) {// after the first round of main game is done
			playingMainGame = false;
			currentPlayerTurn = 1;
			mainGame();
		} else if (mainGameRoundNumber == 3) {// after the 2nd round of main game is done
			playingMainGame = false;
			currentPlayerTurn = 1;
			mainGame();
		} else if (mainGameRoundNumber == 4) {// after the 3rd round of main game is done
			playingMainGame = false;
			// 3 more toss ups
			tossUpRoundNumber = 3;
			tossUp();
			tossUpRoundNumber = 4;
			tossUp();
			tossUpRoundNumber = 5;
			tossUp();
			mainGame();// one last main game round
		}
		endScreen();// end screen displaying money totals and who won
	}

	public void puzzle() {
		emptyGameBoard();// draw the empty game board

		// draw the player scoreboards
		c.setColor(new Color(201, 191, 194));
		c.fillRect(272, 19, 230, 93);
		c.fillRect(520, 19, 230, 93);
		c.fillRect(766, 19, 230, 93);
		c.setColor(new Color(180, 28, 76));
		c.fillRect(272, 25, 230, 81);
		c.setColor(new Color(214, 139, 33));
		c.fillRect(520, 25, 230, 81);
		c.setColor(new Color(9, 12, 177));
		c.fillRect(766, 25, 230, 81);
		c.setColor(Color.white);

		c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 27));// draw the player information
		c.drawString(playerNames[0].toUpperCase(), 390 - (playerNames[0].length() * 9), 58);
		c.drawString(playerNames[1].toUpperCase(), 635 - (playerNames[0].length() * 9), 58);
		c.drawString(playerNames[2].toUpperCase(), 885 - (playerNames[0].length() * 9), 58);

		c.drawString("$" + Integer.toString(playerMoney[0]), 380 - ((Integer.toString(playerMoney[0]).length() * 9)),
				88);
		c.drawString("$" + Integer.toString(playerMoney[1]), 625 - ((Integer.toString(playerMoney[1]).length() * 9)),
				88);
		c.drawString("$" + Integer.toString(playerMoney[2]), 875 - ((Integer.toString(playerMoney[2]).length() * 9)),
				88);
		int wordIndex = 0;// int to store the randomly generated word
		byte categoryIndex = 0;// byte to store which category has been randomly chosen

		if (playingMainGame == false) {// if not playing the main game, generate a new word and display it
			wordIndex = (int) (Math.random() * 12 + 0);// randomly select a word from the category
			categoryIndex = (byte) (Math.random() * 10 + 0); // randomly select a category

			switch (categoryIndex) {
			case 1:
				String advertisingSlogans[] = { "YOUR FLEXIBLE FRIEND", "THINK         DIFFERENT",
						"THE WORLDS    LOCAL BANK", "THE ULTIMATE DRIVING      MACHINE", "RED BULL GIVES YOU WINGS",
						"THE CHOICE  OF A GENERATION", "IM LOVIN IT", "GUINNESS IS   GOOD FOR YOU",
						"ALWAYS COCA   COLA", "FINGER LICKIN GOOD",
						"BREAKFAST OF CHAMPIONS, HAVE A BREAK HAVE A KIT KAT", "HOME OF THE   WHOPPER", "JUST DO IT" };
				hiddenPhrase = advertisingSlogans[wordIndex];// set the hidden phrase to the generated phrase
				category = "ADVERTISING SLOGANS";// set the category
				break;
			case 2:
				String atSchool[] = { "PLAYING SPORTS", "PUTTING UP    YOUR HAND", "PASSING NOTES IN CLASS",
						"EATING LUNCH", "FAILING       MATHEMATICS", "COMPARING     HOMEWORK",
						"DOING A       PHYSICS TEST", "MAKING A      PRESENTATION", "RESEARCHING IN THE LIBRARY",
						"WIPING THE    BLACKBOARD", "TALKING WITH  FRIENDS", "GETTING A     REPORT CARD",
						"ORGANIZING    LOCKER" };
				hiddenPhrase = atSchool[wordIndex];// set the hidden phrase to the generated phrase
				category = "AT SCHOOL";// set the category
				break;
			case 3:
				String computerAndVideoGames[] = { "WORLD OF      WARCRAFT", "TOMB RAIDER", "THE LEGEND OF ZELDA",
						"RESIDENT EVIL", "SUPER MARIO   BROS", "MARIO KART", "  LEAGUE OF     LEGENDS",
						"GRAND THEFT   AUTO V", "FINAL FANTASY", "MINECRAFT", "PORTAL", "HALF LIFE",
						"CALL OF DUTY  MODERN WARFARE" };
				hiddenPhrase = computerAndVideoGames[wordIndex];// set the hidden phrase to the generated phrase
				category = "COMPUTER AND VIDEO GAMES";// set the category
				break;
			case 4:
				String countries[] = { "UNITED KINGDOM", "UNITED ARAB   EMIRATES", "THE           NETHERLANDS",
						"THE MALDIVES", "THE BAHAMAS", "SWITZERLAND", "SOUTH KOREA", "UNITED STATES OF AMERICA",
						"CANADA", "NEW ZEALAND", "MADAGASCAR", "SAUDI ARABIA", "CZECH REPUBLIC" };
				hiddenPhrase = countries[wordIndex];// set the hidden phrase to the generated phrase
				category = "COUNTRIES";// set the category
				break;
			case 5:
				String films[] = { "THE TRUMAN SHOW", "THE LORD OF   THE RINGS", "THE HORSE     WHISPERER",
						"INTERSTELLAR", "THE BIG       LEBOWSKI", "THE GODFATHER", "FORREST GUMP", "PULP FICTION",
						"THE AVENGERS", "JURASSIC PARK", "BACK TO THE   FUTURE", "RESERVOIR DOGS",
						"THE DARK      KNIGHT" };
				hiddenPhrase = films[wordIndex];// set the hidden phrase to the generated phrase
				category = "FILMS";// set the category
				break;
			case 6:
				String fictionalCharacters[] = { "WINNIE THE    POOH", "SPONGEBOB     SQUAREPANTS", "SPIDER MAN",
						"SNOW WHITE", "SHERLOCK      HOLMES", "LUKE SKYWALKER", "INDIANA JONES", "HOMER SIMPSON",
						"MICKEY MOUSE", "HARRY POTTER", "DARTH VADER", "CHARLIE BROWN", "BART SIMPSON" };
				hiddenPhrase = fictionalCharacters[wordIndex];// set the hidden phrase to the generated phrase
				category = "FICTIONAL CHARACTERS";// set the category
				break;
			case 7:
				String internet[] = { "YOUTUBE VIDEO", "WEB BROWSER", "WATCHING      NETFLIX", "SEARCH ENGINE",
						"MOZILLA       FIREFOX", "INTERNET      EXPLORER", "GOOGLE CHROME", "FACEBOOK USER",
						"EMAIL ADDRESS", "CREATING A    WEBSITE", "CHANGING PASSWORD", "BOOKMARKING A PAGE",
						"DOWNLOADING   DATA" };
				hiddenPhrase = internet[wordIndex];// set the hidden phrase to the generated phrase
				category = "INTERNET";// set the category
				break;

			case 8:
				String musicalInstruments[] = { "VIBRAPHONE", "UPRIGHT PIANO", "SPANISH GUITAR",
						"  SOPRANO       SAXOPHONE", "SLIDE TROMBONE", "PICCOLO FLUTE", "MOUTH ORGAN", "FRENCH HORN",
						"   BARITONE      SAXOPHONE", "DOUBLE BASS   GUITAR", "CONTRABASS    TROMBONE",
						"EIGHT STRING  GUITAR", "ELECTRONIC KEYBOARD" };
				hiddenPhrase = musicalInstruments[wordIndex];// set the hidden phrase to the generated phrase
				category = "MUSICAL INSTRUMENTS";// set the category
				break;
			case 9:
				String sayings[] = { "THIRD TIME IS THE CHARM", "THE END     JUSTIFIES THE MEANS",
						"ROME WASNT    BUILT IN A DAY", "REVENGE IS A DISH BEST     SERVED COLD", "LESS IS MORE",
						"IT TAKES TWO  TO TANGO", "BLOOD IS    THICKER THAN  WATER", "CURIOSITY   KILLED THE CAT",
						"A PICTURE   PAINTS A      THOUSAND WORDS", "A PENNY     SAVED IS A    PENNY EARNED",
						"A FRIEND IN NEED IS A     FRIEND INDEED", "A BIRD IN   THE HAND IS   WORTH TWO IN  THE BUSH",
						"PENNY FOR     YOUR THOUGHTS" };
				hiddenPhrase = sayings[wordIndex];// set the hidden phrase to the generated phrase
				category = "SAYINGS";// set the category
				break;
			}

			// hiddenPhrase = "DEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDEBUGDE";

			char hiddenPhraseCharArray[] = hiddenPhrase.toCharArray();
																// in order for the white squares to be drawn

			int count = 0;// loop counter
			byte column = 0;// column number in the grid
			byte row = 1;// row number in the grid

			if (hiddenPhraseCharArray.length < 29) {// if the phrase is less than 29 letters start on row 2
				row = 2;
			} else {
			}
			// draw the purple rectangle that shows the category
			c.setColor(Color.white);
			c.fillRect(177, 602, 900, 50);
			c.setColor(new Color(36, 22, 73));
			c.fillRect(177, 605, 900, 44);

			try {
				GothamMedium = Font
						.createFont(Font.TRUETYPE_FONT,
								new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))// load font from file
						.deriveFont(Font.PLAIN, 36);// set the font
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (FontFormatException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} // create the custom font they use on the show from .ttf file

			c.setFont(GothamMedium);
			c.setColor(Color.white);
			c.drawString(category, 625 - (category.length() * 12), 641);// draw the category on the purple rectangle

			while (count != hiddenPhraseCharArray.length) {// loop to draw the white squares one by one from left to
															// right up to down like in the show
				if (hiddenPhraseCharArray[count] != ' ') {// if the letter is not a space

					c.setColor(new Color(254, 255, 237));// off white they use in the show
					switch (row) {
					case 1:// row 1

						c.fillRect(227 + column * 70, 182, 47, 62);// draw the white square

						if (column == 11) {// if the column is 11

							// redraw parts of the rectangle to correct for it being slightly misaligned
							c.fillRect(994, 182, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 182, 5, 62);
						}

						count++;// increase the count
						column++;// move onto the next column
						break;
					case 2:// row 2
						c.fillRect(157 + column * 70, 267, 47, 62);

						if (column == 12) {

							c.fillRect(994, 267, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 267, 7, 62);
						}
						if (column == 13) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(1061, 267, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1108, 267, 7, 62);
						}
						count++;
						column++;
						break;
					case 3:// row 3
						c.fillRect(157 + column * 70, 353, 47, 62);

						if (column == 12) {

							c.fillRect(994, 353, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 353, 5, 62);
						}
						if (column == 13) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(1061, 353, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1108, 353, 7, 62);
						}
						count++;
						column++;
						break;
					case 4:// row 4
						c.fillRect(227 + column * 70, 439, 47, 62);

						if (column == 11) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(994, 439, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 439, 5, 62);
						}

						count++;
						column++;
						break;
					}

					if (row == 1 && column == 12) {// if the column is at the end, start a new row
						row++;
						column = 0;
					}
					if (row == 4 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 2 && column == 14) {
						row++;
						column = 0;
					}
					if (row == 3 && column == 14) {
						row++;
						column = 0;
					}

				} else if (hiddenPhraseCharArray[count] == ' ') {// if the currently loaded character in the array is a
																	// space
					// dont draw a white square but move on to the next column
					column++;
					count++;
					if (row == 1 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 4 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 2 && column == 14) {
						row++;
						column = 0;
					}
					if (row == 3 && column == 14) {
						row++;
						column = 0;
					}
				}

				try {
					Thread.sleep(43);// delay the drawing of each square by 43 milliseconds

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		if (playingMainGame == true) {// if playing the main game currently
			int count = 0;// count the loop loops
			byte column = 0;// column number
			byte row = 1;// row number

			// draw the purple rectangle that has the category
			c.setColor(Color.white);
			c.fillRect(177, 617, 900, 50);
			c.setColor(new Color(36, 22, 73));
			c.fillRect(177, 620, 900, 44);

			try {
				GothamMedium = Font
						.createFont(Font.TRUETYPE_FONT,
								new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))// load and set the
																									// font
						.deriveFont(Font.PLAIN, 36);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FontFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // create the custom font they use on the show from .ttf file

			c.setFont(GothamMedium);// set the font
			c.setColor(Color.white);
			c.drawString(category, 625 - (category.length() * 12), 656);// draw the category on the purple rectangle

			if (hiddenPhraseCharArray.length < 29) {// if the phrase is less than 29 letters start on row 2
				row = 2;
			} else {
			}

			// the same loop as above but without the delay of 43 milliseconds
			while (count != hiddenPhraseCharArray.length) {
				if (hiddenPhraseCharArray[count] != ' ') {

					c.setColor(new Color(254, 255, 237));
					switch (row) {
					case 1:

						c.fillRect(227 + column * 70, 182, 47, 62);

						if (column == 11) {

							c.fillRect(994, 182, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 182, 5, 62);
						}

						count++;
						column++;
						break;
					case 2:
						c.fillRect(157 + column * 70, 267, 47, 62);

						if (column == 12) {

							c.fillRect(994, 267, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 267, 7, 62);
						}
						if (column == 13) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(1061, 267, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1108, 267, 7, 62);
						}
						count++;
						column++;
						break;
					case 3:
						c.fillRect(157 + column * 70, 353, 47, 62);

						if (column == 12) {

							c.fillRect(994, 353, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 353, 5, 62);
						}
						if (column == 13) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(1061, 353, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1108, 353, 7, 62);
						}
						count++;
						column++;
						break;
					case 4:
						c.fillRect(227 + column * 70, 439, 47, 62);

						if (column == 11) {
							c.setColor(new Color(254, 255, 237));
							c.fillRect(994, 439, 47, 62);
							c.setColor(Color.black);
							c.fillRect(1041, 439, 5, 62);
						}

						count++;
						column++;
						break;
					}

					if (row == 1 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 4 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 2 && column == 14) {
						row++;
						column = 0;
					}
					if (row == 3 && column == 14) {
						row++;
						column = 0;
					}

				} else if (hiddenPhraseCharArray[count] == ' ') {
					column++;
					count++;
					if (row == 1 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 4 && column == 12) {
						row++;
						column = 0;
					}
					if (row == 2 && column == 14) {
						row++;
						column = 0;
					}
					if (row == 3 && column == 14) {
						row++;
						column = 0;
					}
				}
			}
		}
	}

	// method for the little "who's the first one to guess it" game they play at the
	// beginning of the show
	public void tossUp() {
		int reward = 0;// variable to store the reward depending on what round it is
		// set the reward amount
		if (tossUpRoundNumber == 1) {
			reward = 1000;
		} else if (tossUpRoundNumber == 2 || tossUpRoundNumber == 3 || tossUpRoundNumber == 4
				|| tossUpRoundNumber == 5) {
			reward = 2000;
		}
		boolean playerHasAttemptedToGuess[] = { false, false, false };// boolean to store if each player has attempted
																		// to guess
		int revealIndex = 0;// index number to randomly choose which char in the char array to reveal
		boolean playerGuessedCorrectly = false;// boolean for if the player guessed correctly
		p.playClip("puzzleReveal.wav");// play the puzzle reveal sound
		puzzle();// draw the board and the white squares

		JOptionPane.showMessageDialog(f, ("Players, use the keys 1, 2, and 3 to guess! Press OK when you're ready!"), // tell
																														// the
																														// players
																														// the
																														// controls
				"Toss Up", JOptionPane.INFORMATION_MESSAGE);// display error message window
		JOptionPane.showMessageDialog(f, ("Toss Up Round " + tossUpRoundNumber), "Toss Up", // tell the players the
																							// current game mode and
																							// round number
				JOptionPane.INFORMATION_MESSAGE);// display error message window
		TossUpInput t = new TossUpInput(c);// create new instance of class TossUpInput
		t.start();// start TossUpInput

		try {
			HelveticaCondensedBlackSERegular58 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("font.ttf")))
					.deriveFont(Font.PLAIN, 58);// create the custom font they use on the show from .ttf file
		} catch (Exception e) {
			System.out.println("error loading font");
		}
		c.setFont(HelveticaCondensedBlackSERegular58);// set the font
		c.setColor(Color.black);// set the color

		hiddenPhraseCharArray = hiddenPhrase.toCharArray();
		int count = 0;// loop counter
		int column = 0;// column number
		byte row = 1;// row number

		boolean alreadyRevealedIndexes[] = new boolean[hiddenPhraseCharArray.length];// already revealed indexes boolean
																						// array to store which of the
																						// randomly generated chars in
																						// the array have already been
																						// revealed, so the random
																						// generator doesnt generate
																						// them again

		for (int i = 0; i < hiddenPhraseCharArray.length; i++) {//set all the already revealed indexes to false
			alreadyRevealedIndexes[i] = false;
		}

		p.play("tossUpMusic.wav");//play the toss up music

		while (count != hiddenPhraseCharArray.length && t.attemptingToGuess == false) {//over arching while loop that generates and draws the randomly generated chars

			boolean revealIndexValid = false;//loop controller

			revealIndex = (int) (Math.random() * hiddenPhraseCharArray.length);//randomly generate a char in the array to be displayed

			while (!revealIndexValid) {//while it is not valid
				revealIndex = (int) (Math.random() * hiddenPhraseCharArray.length);//randomly generate a char in the array to be displayed again

				if (alreadyRevealedIndexes[revealIndex] == false) {//if this randomly generated char has not been revealed yet
					revealIndexValid = true;//end the loop
				} else if (alreadyRevealedIndexes[revealIndex] == true) {//if this randomly generated char has already been revealed
					revealIndexValid = false;//continue the loop

					revealIndex = (int) (Math.random() * hiddenPhraseCharArray.length);//generate another char
				}
			}

			if (revealIndex < 12) {//similar row and column chooser to the one seen in puzzle, but adapted to the randomness of the revealIndex
				row = 1;
				column = revealIndex;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			} else if (revealIndex > 11 && revealIndex < 26) {
				row = 2;
				column = revealIndex - 12;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			} else if (revealIndex > 25 && revealIndex < 40) {
				row = 3;
				column = revealIndex - 26;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			} else if (revealIndex > 39 && revealIndex < 52) {
				row = 4;
				column = revealIndex - 40;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			}
			//if the phrase is shorter, do it on rows 2 and 3
			if (revealIndex < 14 && hiddenPhraseCharArray.length < 29) {
				row = 2;
				column = revealIndex;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			}
			if (revealIndex > 13 && revealIndex < 28 && hiddenPhraseCharArray.length < 29) {
				row = 3;
				column = revealIndex - 14;//subtract the amount of previous spaces in the previous rows to get the column number for the current row
			}

			switch (row) {//draws the randomly generated char on the row determined above and the column also determined above
			case 1:
				c.drawString(Character.toString(hiddenPhraseCharArray[revealIndex]), 237 + column * 70, 237);
				break;
			case 2:
				c.drawString(Character.toString(hiddenPhraseCharArray[revealIndex]), 167 + column * 70, 321);
				break;
			case 3:
				c.drawString(Character.toString(hiddenPhraseCharArray[revealIndex]), 167 + column * 70, 405);
				break;
			case 4:
				c.drawString(Character.toString(hiddenPhraseCharArray[revealIndex]), 237 + column * 70, 491);
				break;
			}

			if (t.attemptingToGuess == false) {//if the player isnt attempting the guess
				try {
					Thread.sleep(1000);//delay until the next letter reveal
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (t.attemptingToGuess == true) {//if the player has pressed 1 2 or 3 and TossUpInput has registered that
				if (playerHasAttemptedToGuess[t.charInputToInt - 1] == true) {//determine if the currently guessing player has already tried to guess
					JOptionPane.showMessageDialog(f,
							("You have already attempted to guess! Give the other players a try!"),
							"Your Attempted Guess", JOptionPane.INFORMATION_MESSAGE);// display error message window
					t.attemptingToGuess = false;//kick them out and continue the drawing of the letters
					
				} else if (playerHasAttemptedToGuess[t.charInputToInt - 1] == false) {//if they havent tried to guess already
					boolean inputValid = false;//loop controller

					while (!inputValid) {//loop to get the guess
						try {
							p.playClip("ding.wav");//play the ding sound
							guess = JOptionPane.showInputDialog("Please enter your guess:");//ask the user for their guess

							inputValid = true;//end the loop
							if (guess == null) {//if the guess is empty ask again
								inputValid = false;//continue the loop
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(f, ("Please input an answer!"), "Your Attempted Guess",
									JOptionPane.INFORMATION_MESSAGE);// display error message window
							inputValid = false;//continue the loop
						}
					}

					playerHasAttemptedToGuess[t.charInputToInt - 1] = true;

					String attemptedGuess = (guess.toUpperCase()).replaceAll("\\s", "");//convert the user input to all uppercase without spaces

					String rawHiddenPhrase = hiddenPhrase.replaceAll("\\s", "");//convert the hidden phrase to without spaces

					if (attemptedGuess.equals(rawHiddenPhrase)) {//if they match
						playerGuessedCorrectly = true;
						p.sourceDataLine.stop();//stop the music
						p.playClip("tossUpSolve.wav");//play the toss up solve clip
						t.attemptingToGuess = true;
						switch (t.charInputToInt) {//check which player guessed correctly
						case 1:
							playerMoney[0] += reward;//add the money to the player's balance
							currentPlayerTurn = 0;//set the player turn for the main game
							break;
						case 2:
							playerMoney[1] += reward;//add the money to the player's balance
							currentPlayerTurn = 1;//set the player turn for the main game
							break;
						case 3:
							playerMoney[2] += reward;//add the money to the player's balance
							currentPlayerTurn = 2;//set the player turn for the main game
							break;
						}
						int drawTheRestOfThePhraseCount = 0;//loop counter
						
						while (drawTheRestOfThePhraseCount < hiddenPhrase.length()) {//loop to draw the rest of the phrase
							//this is pretty much the same drawing letters loop as above but without the randomness
							if (drawTheRestOfThePhraseCount < 12) {
								row = 1;
								column = drawTheRestOfThePhraseCount;
							} else if (drawTheRestOfThePhraseCount > 11 && drawTheRestOfThePhraseCount < 26) {
								row = 2;
								column = drawTheRestOfThePhraseCount - 12;
							} else if (drawTheRestOfThePhraseCount > 25 && drawTheRestOfThePhraseCount < 40) {
								row = 3;
								column = drawTheRestOfThePhraseCount - 26;
							} else if (drawTheRestOfThePhraseCount > 39 && drawTheRestOfThePhraseCount < 52) {
								row = 4;
								column = drawTheRestOfThePhraseCount - 40;
							}
							if (drawTheRestOfThePhraseCount < 14 && hiddenPhraseCharArray.length < 29) {
								row = 2;
								column = drawTheRestOfThePhraseCount;
							}
							if (drawTheRestOfThePhraseCount > 13 && drawTheRestOfThePhraseCount < 28
									&& hiddenPhraseCharArray.length < 29) {
								row = 3;
								column = drawTheRestOfThePhraseCount - 14;
							}

							switch (row) {
							case 1:
								c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),
										237 + column * 70, 237);

								break;
							case 2:
								c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),
										167 + column * 70, 321);

								break;
							case 3:
								c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),
										167 + column * 70, 405);

								break;
							case 4:
								c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),
										237 + column * 70, 491);

								break;
							}
							drawTheRestOfThePhraseCount++;
						}
						
						//draw the player scoreboards
						c.setColor(new Color(201, 191, 194));
						c.fillRect(272, 19, 230, 93);
						c.fillRect(520, 19, 230, 93);
						c.fillRect(766, 19, 230, 93);
						c.setColor(new Color(180, 28, 76));
						c.fillRect(272, 25, 230, 81);
						c.setColor(new Color(214, 139, 33));
						c.fillRect(520, 25, 230, 81);
						c.setColor(new Color(9, 12, 177));
						c.fillRect(766, 25, 230, 81);
						c.setColor(Color.white);

						c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 27));//set the font
						//draw the player names and balances
						c.drawString(playerNames[0].toUpperCase(), 390 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[1].toUpperCase(), 635 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[2].toUpperCase(), 885 - (playerNames[0].length() * 9), 58);

						c.drawString("$" + Integer.toString(playerMoney[0]),
								380 - ((Integer.toString(playerMoney[0]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[1]),
								625 - ((Integer.toString(playerMoney[1]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[2]),
								875 - ((Integer.toString(playerMoney[2]).length() * 9)), 88);

						t.stop();//stop listening for input with TossUpInput

						JOptionPane.showMessageDialog(f, ("Press OK to Continue"), "Toss Up",
								JOptionPane.INFORMATION_MESSAGE);// display message window
					}

					if (playerGuessedCorrectly == false && playerHasAttemptedToGuess[t.charInputToInt - 1] == true) {//if the player guessed and it's wrong

						JOptionPane.showMessageDialog(f, ("Wrong Answer!"), "Your Attempted Guess",
								JOptionPane.INFORMATION_MESSAGE);// display message window
						t.attemptingToGuess = false;
					}
				}
			}

			count++;//increase the over arching while loop count
			alreadyRevealedIndexes[revealIndex] = true;//set the currently revealed letter in the already revealed indexes array to true

		}
		if (!playerGuessedCorrectly) {//if the no player guesssed correctly
			p.sourceDataLine.stop();//stop the music
			p.playClip("bankrupt.wav");//play the sad sound
			JOptionPane.showMessageDialog(f, ("No players guessed within the allotted time!"), "Toss Up",
					JOptionPane.INFORMATION_MESSAGE);// display error message window
			t.stop();//stop listening for input
		}
	}

	private void drawAlreadyGuessedLetters() {//method to draw the already guessed letters in main game
		c.setFont(HelveticaCondensedBlackSERegular58);//set the font
		c.setColor(Color.black);
		
		for (int d = 0; d < hiddenPhraseCharArray.length; d++) {//for loop to repeat for the length of the hidden phrase
			if (alreadyGuessedChars[d] == true) {//if the boolean is true at that index
				int row = 0;//row number
				int column = 0;//colum number
				
				//set the row number based on d
				//set the column number based on d subtracted by number of previous spaces
				if (d < 12) {
					row = 1;
					column = d;
				} else if (d > 11 && d < 26) {
					row = 2;
					column = d - 12;
				} else if (d > 25 && d < 40) {
					row = 3;
					column = d - 26;
				} else if (d > 39 && d < 52) {
					row = 4;
					column = d - 40;
				}
				if (d < 14 && hiddenPhraseCharArray.length < 29) {
					row = 2;
					column = d;
				}
				if (d > 13 && d < 28 && hiddenPhraseCharArray.length < 29) {
					row = 3;
					column = d - 14;
				}
				
				//draw the letter
				switch (row) {
				case 1:
					c.drawString(Character.toString(hiddenPhraseCharArray[d]), 237 + column * 70, 237);
					break;
				case 2:
					c.drawString(Character.toString(hiddenPhraseCharArray[d]), 167 + column * 70, 321);
					break;
				case 3:
					c.drawString(Character.toString(hiddenPhraseCharArray[d]), 167 + column * 70, 405);
					break;
				case 4:
					c.drawString(Character.toString(hiddenPhraseCharArray[d]), 237 + column * 70, 491);
					break;
				}
			}
		}
	}

	public void mainGame() {//main game method, serves as also the view board option in the game options menu
		try {
			HelveticaCondensedBlackSERegular58 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("font.ttf")))
					.deriveFont(Font.PLAIN, 58);// create the custom font they use on the show from .ttf file
		} catch (Exception e) {
			System.out.println("error loading font");
		}
		if (!playingMainGame) {
			p.playClip("puzzleReveal.wav");//puzzle reveal sound only once for the first time the main game is loaded
		}
		puzzle();
		if (!playingMainGame) {
			JOptionPane.showMessageDialog(f, ("Main Game Round " + mainGameRoundNumber), "Main Game",//tell the user the game mode and round numebr
					JOptionPane.INFORMATION_MESSAGE);// display error message window
			hiddenPhraseCharArray = hiddenPhrase.toCharArray();//set the hidden phrase char array
			alreadyGuessedChars = new boolean[hiddenPhraseCharArray.length];//set the already guessed chars boolean array

			for (int i = 0; i < hiddenPhraseCharArray.length; i++) {//set every boolean at the index to false
				alreadyGuessedChars[i] = false;
			}
		}
		drawAlreadyGuessedLetters();//draw the already guessed letters

		JOptionPane.showMessageDialog(f, ("Press OK to Continue"), "Main Game", JOptionPane.INFORMATION_MESSAGE);// display
																													// dialog

		gameOptionsMenu();//call game options menu
	}

	public void wheel() {//method to spin the wheel and determine the user's consonant guess reward
		Color lightComplimentary[] = { new Color(241, 33, 33), new Color(241, 214, 33), new Color(33, 62, 241) };//color array to display the necessary color based on the current player turn
		Color darkComplimentary[] = { new Color(36, 6, 6), new Color(36, 36, 6), new Color(6, 14, 36) };//color array to display the necessary color based on the current player turn
		boolean loseATurn = false;//boolean to tell if the user rolled lose a turn
		boolean bankrupt = false;//boolean to tell if the user rolled bankrupt

		//draw the background
		c.setColor(lightComplimentary[currentPlayerTurn]);
		c.fillRect(0, 0, 1280, 720);
		c.setColor(darkComplimentary[currentPlayerTurn]);
		c.fillRect(14, 14, 1252, 692);
		c.setColor(lightComplimentary[currentPlayerTurn]);
		
		//draw the player name and balance
		c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 85));
		c.drawString((playerNames[currentPlayerTurn]), 105, 148);
		c.drawString('$' + Integer.toString(playerMoney[currentPlayerTurn]), 102, 225);
		
		//draw the press any key to spin text
		c.setColor(Color.white);
		try {
			GothamMedium = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 60);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // create the custom font they use on the show from .ttf file

		c.setFont(GothamMedium);
		c.drawString("Press Any", 103, 554);
		c.drawString("Key To Spin!", 103, 616);

		
		int startingPosition = (int) (Math.random() * 240 + 1);//randomly select a starting position
		BufferedImage staticWheel;//staticWheel picture to display before the user spins the wheel
		try {
			staticWheel = ImageIO.read(new File("wheel (" + startingPosition + ").png"));
			c.drawImage(staticWheel, 650, 60, null);//draw the static wheel
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//draw the arrow indicator
		c.setColor(Color.black);
		int x[] = { 937, 957, 946 };
		int y[] = { 46, 46, 82 };
		c.fillPolygon(x, y, 3);
		c.setColor(Color.white);
		int x1[] = { 939, 955, 946 };
		int y1[] = { 48, 48, 80 };
		c.fillPolygon(x1, y1, 3);

		
		BufferedImage animatedWheel;//animated wheel bufferedimage variable
		int totalNumberOfFramesRendered = 0;//int to tell the total number of frames rendered
		int i = startingPosition;//starting position int
		int amountOfFramesBeforeStopRotating = (int) (Math.random() * 190 + 155);//randomly generate the amount of time the wheel will spin

//              i=1;
//              amountOfFramesBeforeStopRotating=97;

		int frameTime = 1;//int to store the milliseconds between frames
		
		c.getChar();//pause until input
		c.setColor(darkComplimentary[currentPlayerTurn]);//draw over the "press any key to spin!"
		c.fillRect(66, 449, 453, 197);
		c.setColor(Color.white);
		c.drawString("Spinning..", 103, 554);//draw the spinning text
		
		try {
			boolean alreadyPlayedSound = false;
			boolean spinning = true;//loop controller
			p.playClip("wheelSpinning.wav");//play the wheel spinning sound effect
			while (spinning) {

				animatedWheel = ImageIO.read(new File("wheel (" + i + ").png"));//set the animated wheel variable to the loaded file that is one of the 240 pngs
				c.drawImage(animatedWheel, 650, 60, null);//draw (i)th wheel in the sequence

				//draw the arrow indicator
				c.setColor(Color.black);
				c.fillPolygon(x, y, 3);
				c.setColor(Color.white);
				c.fillPolygon(x1, y1, 3);

				try {
					Thread.sleep(frameTime);//pause until the next frame for the specified time
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (i == 240) {//if the index gets to the end of the 240 pngs reset it back to 1
					i = 1;
				}

				i++;//image index load increase by 1
				totalNumberOfFramesRendered++;//total number of frames rendered increase by 1
				if (totalNumberOfFramesRendered > amountOfFramesBeforeStopRotating) {//if the total number of frames is greater than the randomly generated amount of frames
					if (!alreadyPlayedSound) {
						p.clip.stop();//stop the wheel spinning sound
						p.playClip("wheelSlowingDown.wav");//play the wheel slowing down sound
						alreadyPlayedSound = true;
					}
					frameTime = frameTime + 1;//increase the frametime, thereby slowing down the wheel
					if (frameTime == 90) {//stop the wheel once the frametime has reached 90
						spinning = false;//stop the loop
					}
				}

				
			}
			
			//determine which money wedge the triangle indicator stopped on
			if (i >= 1 && i <= 6) {
				loseATurn = true;
			} else if (i >= 7 && i <= 16) {
				consonantReward = 300;
			} else if (i >= 17 && i <= 26) {
				consonantReward = 400;
			} else if (i >= 27 && i <= 36) {
				consonantReward = 600;
			} else if (i >= 37 && i <= 45) {
				bankrupt = true;
			} else if (i >= 46 && i <= 56) {
				consonantReward = 900;
			} else if (i >= 57 && i <= 65) {
				consonantReward = 700;
			} else if (i >= 66 && i <= 75) {
				consonantReward = 500;
			} else if (i >= 76 && i <= 85) {
				consonantReward = 900;
			} else if (i >= 86 && i <= 96) {
				consonantReward = 300;
			} else if (i >= 97 && i <= 105) {
				consonantReward = 400;
			} else if (i >= 106 && i <= 116) {
				consonantReward = 550;
			} else if (i >= 117 && i <= 125) {
				consonantReward = 800;
			} else if (i >= 127 && i <= 136) {
				consonantReward = 500;
			} else if (i >= 137 && i <= 146) {
				consonantReward = 300;
			} else if (i >= 147 && i <= 156) {
				consonantReward = 600;
			} else if (i >= 157 && i <= 166) {
				consonantReward = 300;
			} else if (i >= 167 && i <= 176) {
				consonantReward = 5000;
			} else if (i >= 177 && i <= 185) {
				consonantReward = 600;
			} else if (i >= 186 && i <= 189) {
				bankrupt = true;
			} else if (i >= 190 && i <= 192) {
				consonantReward = 10000;
			} else if (i >= 193 && i <= 195) {
				bankrupt = true;
			} else if (i >= 196 && i <= 205) {
				consonantReward = 700;
			} else if (i >= 206 && i <= 215) {
				consonantReward = 450;
			} else if (i >= 216 && i <= 226) {
				consonantReward = 350;
			} else if (i >= 227 && i <= 235) {
				consonantReward = 800;
			} else if (i >= 236 && i <= 240) {
				loseATurn = true;
			}

			c.setColor(darkComplimentary[currentPlayerTurn]);//draw over the "spinning..."
			c.fillRect(66, 449, 453, 197);
			c.setColor(Color.white);
			//draw the press any key to continue message
			c.drawString("Press Any", 103, 554);
			c.drawString("Key To Continue..", 103, 616);

			if (bankrupt) {//if the wheel landed on bankrupt
				p.playClip("bankrupt.wav");//play the bankrupt sound
				
				//draw over the money shown
				c.setColor(darkComplimentary[currentPlayerTurn]);
				c.fillRect(88, 73, 331, 184);
				playerMoney[currentPlayerTurn] = 0;//set the current player's money to 0
				//redraw the updated amount of money
				c.setColor(lightComplimentary[currentPlayerTurn]);
				c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 85));
				c.drawString((playerNames[currentPlayerTurn]), 105, 148);
				c.drawString('$' + Integer.toString(playerMoney[currentPlayerTurn]), 102, 225);
				// change the current turn to the next player
				switch (currentPlayerTurn) {
				case 0:
					currentPlayerTurn = 1;
					break;
				case 1:
					currentPlayerTurn = 2;
					break;
				case 2:
					currentPlayerTurn = 0;
					break;
				}
				JOptionPane.showMessageDialog(f, ("Oh no! You've gone bankrupt! Press OK to continue.."), "Wheel",
						JOptionPane.INFORMATION_MESSAGE);// display error message window
				gameOptionsMenu();//go back to menu
			} else if (loseATurn) {
				// change the current turn to the next player
				switch (currentPlayerTurn) {
				case 0:
					currentPlayerTurn = 1;
					break;
				case 1:
					currentPlayerTurn = 2;
					break;
				case 2:
					currentPlayerTurn = 0;
					break;
				}
				JOptionPane.showMessageDialog(f, ("Oh no! You've lost a turn! Press OK to continue.."), "Wheel",
						JOptionPane.INFORMATION_MESSAGE);// display error message window
				gameOptionsMenu();//go back to menu
			} else {//if they didnt land on bankrupt or lose a turn
				c.getChar();//pause the program
				guessConsonant();//move on to the guess consonant screen
			}
		} catch (IOException e) {
			System.out.println("File not found :(");
		}
	}

	private void guessConsonant() {
		guessTimer o = new guessTimer(c);//create new instance of the 5 second guess timer
		puzzle();//draw the puzzle
		drawAlreadyGuessedLetters();//draw all already guessed letters
		
		//draw the gray circle the timer is in
		c.setColor(new Color(183, 183, 183));
		c.fillOval(1137, 607, 75, 75);
		c.setColor(Color.black);
		try {
			GothamMedium = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 72);
			c.setFont(GothamMedium);
		} catch (Exception e) {
			
		}
		// create the custom font they use on the show from .ttf file
		c.setFont(GothamMedium);
		c.drawString(String.valueOf(5), 1150, 670);//draw the starting amount of the timer, 5
		boolean typedValid = false;//loop controller
		char guess = 'a';//variable to store the guess
		c.setFont(HelveticaCondensedBlackSERegular58);//set the font

		JOptionPane.showMessageDialog(f, ("Type the consonant you wish to guess!"), "Consonant Guess",
				JOptionPane.INFORMATION_MESSAGE);// display error message window
		o.start();//start the timer
		while (!typedValid) {
			boolean guessedCorrectly = false;//boolean to remember if they guessed right or not
			guess = c.getChar();//get the guess

			if (o.outOfTime) {//if the timer goes to 0
				o.stop();//stop the timer
				guessedCorrectly = false;
				typedValid = false;
				//change the current player turn to the next person
				switch (currentPlayerTurn) {
				case 0:
					currentPlayerTurn = 1;
					break;
				case 1:
					currentPlayerTurn = 2;
					break;
				case 2:
					currentPlayerTurn = 0;
					break;
				}
				gameOptionsMenu();//go back to menu
			}
			guess = (String.valueOf(guess).toUpperCase()).charAt(0);//set the guess to uppercase

			if (guess == 'Q' || guess == 'W' || guess == 'R' || guess == 'T' || guess == 'Y' || guess == 'P'
					|| guess == 'S' || guess == 'D' || guess == 'F' || guess == 'G' || guess == 'H' || guess == 'J'
					|| guess == 'K' || guess == 'L' || guess == 'Z' || guess == 'X' || guess == 'C' || guess == 'V'
					|| guess == 'B' || guess == 'N' || guess == 'M') {//if the guess is a consonant
				
				int i = 0;//loop counter
				while (i < hiddenPhraseCharArray.length) {//while loop to loop until i equals the length of the hidden phrase
					if (guess == hiddenPhraseCharArray[i] && alreadyGuessedChars[i] == false) {//check if the guess char matches the char in the hidden phrase char array
						o.stop();//stop the timer
						c.setFont(HelveticaCondensedBlackSERegular58);//set the font
						p.playClip("ding.wav");//play the ding noise
						alreadyGuessedChars[i] = true;//set the already guessed chars array at index i to true
						int row = 0;//row number
						int column = 0;//column number
						
						//set the row and column numbers based on what i is
						if (i < 12) {
							row = 1;
							column = i;
						} else if (i > 11 && i < 26) {
							row = 2;
							column = i - 12;
						} else if (i > 25 && i < 40) {
							row = 3;
							column = i - 26;
						} else if (i > 39 && i < 52) {
							row = 4;
							column = i - 40;
						}
						if (i < 14 && hiddenPhraseCharArray.length < 29) {
							row = 2;
							column = i;
						}
						if (i > 13 && i < 28 && hiddenPhraseCharArray.length < 29) {
							row = 3;
							column = i - 14;
						}
						switch (row) {
						case 1:

							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(227 + column * 70, 182, 47, 62);//draw the blue reveal square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 182, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 182, 5, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (Exception e) {
							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(227 + column * 70, 182, 47, 62);//draw the white square over the blue square
							if (column == 11) {//redraw parts of the square to correct for misalignment

								c.fillRect(994, 182, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 182, 5, 62);
							}
							//draw the consonant
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 237 + column * 70, 237);
							break;
						case 2:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(157 + column * 70, 267, 47, 62);//draw the blue reveal square
							if (column == 12) {//redraw parts of the square to correct for misalignment

								c.fillRect(994, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 267, 7, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 267, 7, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (InterruptedException e) {
							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(157 + column * 70, 267, 47, 62);//draw the white square over the blue square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 267, 7, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 267, 7, 62);
							}
							//draw the consonant
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 167 + column * 70, 321);

							break;
						case 3:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(157 + column * 70, 353, 47, 62);//draw the blue reveal square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 353, 5, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 353, 7, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (InterruptedException e) {
							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(157 + column * 70, 353, 47, 62);//draw the white square over the blue square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 353, 5, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 353, 7, 62);
							}
							//draw the consonant
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 167 + column * 70, 405);
							break;
						case 4:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(227 + column * 70, 439, 47, 62);//draw the blue reveal square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 439, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 439, 5, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds
							} catch (InterruptedException e) {
							}
							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(227 + column * 70, 439, 47, 62);//draw the white square over the blue square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 439, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 439, 5, 62);
							}
							//draw the consonant
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 237 + column * 70, 491);

							break;
						}
						playerMoney[currentPlayerTurn] += consonantReward;//increase the player money by the consonant reward
						
						//draw the player scoreboards
						c.setColor(new Color(201, 191, 194));
						c.fillRect(272, 19, 230, 93);
						c.fillRect(520, 19, 230, 93);
						c.fillRect(766, 19, 230, 93);
						c.setColor(new Color(180, 28, 76));
						c.fillRect(272, 25, 230, 81);
						c.setColor(new Color(214, 139, 33));
						c.fillRect(520, 25, 230, 81);
						c.setColor(new Color(9, 12, 177));
						c.fillRect(766, 25, 230, 81);
						c.setColor(Color.white);
						
						//draw the player names and balances
						c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 27));
						c.drawString(playerNames[0].toUpperCase(), 390 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[1].toUpperCase(), 635 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[2].toUpperCase(), 885 - (playerNames[0].length() * 9), 58);
						c.drawString("$" + Integer.toString(playerMoney[0]),
								380 - ((Integer.toString(playerMoney[0]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[1]),
								625 - ((Integer.toString(playerMoney[1]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[2]),
								875 - ((Integer.toString(playerMoney[2]).length() * 9)), 88);
						c.setFont(HelveticaCondensedBlackSERegular58);
						guessedCorrectly = true;//set guessedcorrectly to true
						typedValid = true;//set typed valid to true

					} else if (guess == hiddenPhraseCharArray[i] && alreadyGuessedChars[i] == true) {//if the player guesses a consonant that's already been guessed
						o.stop();//stop the timer
						p.playClip("Buzzer.wav");//play the buzzer sound
						JOptionPane.showMessageDialog(f, ("This letter has already been guessed!"), "Consonant Guess",
								JOptionPane.INFORMATION_MESSAGE);// display error message window
						i = 100;//end the loop by setting i to over the length of the hidden phrase
						guessedCorrectly = true;
						typedValid = false;
						//change the current player turn to the next player
						switch (currentPlayerTurn) {
						case 0:
							currentPlayerTurn = 1;
							break;
						case 1:
							currentPlayerTurn = 2;
							break;
						case 2:
							currentPlayerTurn = 0;
							break;
						}
						gameOptionsMenu();//go back the the menu
					}
					i++;
				}

				if (guessedCorrectly == false) {//if guessed correctly was false
					o.stop();//stop the timer
					p.playClip("Buzzer.wav");//play the buzzer sound
					JOptionPane.showMessageDialog(f, ("The consonant you typed did not appear in the puzzle!"),
							"Consonant Guess", JOptionPane.INFORMATION_MESSAGE);// display error message window
					//change the current player turn to the next player
					switch (currentPlayerTurn) {
					case 0:
						currentPlayerTurn = 1;
						break;
					case 1:
						currentPlayerTurn = 2;
						break;
					case 2:
						currentPlayerTurn = 0;
						break;
					}
					typedValid = true;//set typed valid to true
				}
			} else if (guess != 'Q' && guess != 'W' && guess != 'R' && guess != 'T' && guess != 'Y' && guess != 'P'
					&& guess != 'S' && guess != 'D' && guess != 'F' && guess != 'G' && guess != 'H' && guess != 'J'
					&& guess != 'K' && guess != 'L' && guess != 'Z' && guess != 'X' && guess != 'C' && guess != 'V'
					&& guess != 'B' && guess != 'N' && guess != 'M') {//if the user input is not a consonant
				o.suspend();//pause the timer
				JOptionPane.showMessageDialog(f, ("The character you typed is not a consonant!"), "Consonant Guess",
						JOptionPane.INFORMATION_MESSAGE);// display error message window
				o.resume();//continue the timer
				typedValid = false;//set the typed valid to false
			}
		}
		try {
			Thread.sleep(1000);//pause for 1 second
		} catch (Exception e) {
		}
		gameOptionsMenu();//go back to menu
	}

	private void guessVowel() {
		guessTimer o = new guessTimer(c);//create new instance of the guess timer
		puzzle();//draw the puzzle
		drawAlreadyGuessedLetters();//draw all already guessed letters
		boolean typedValid = false;//loop controller
		char guess = 'a';//char variable to hold the user's guess
		c.setFont(HelveticaCondensedBlackSERegular58);//set the font

		JOptionPane.showMessageDialog(f, ("Type the vowel you wish to guess!"), "Vowel Guess",
				JOptionPane.INFORMATION_MESSAGE);// display error message window
		o.start();
		while (!typedValid) {
			boolean guessedCorrectly = false;//boolean to remember if they guessed right or not
			guess = c.getChar();//get the guess
			if (o.time == 0) {//if the timer goes to 0
				o.stop();//stop the timer
				guessedCorrectly = false;
				typedValid = false;//end the loop
				//change the current player turn to the next person
				switch (currentPlayerTurn) {
				case 0:
					currentPlayerTurn = 1;
					break;
				case 1:
					currentPlayerTurn = 2;
					break;
				case 2:
					currentPlayerTurn = 0;
					break;
				}
				gameOptionsMenu();//go back to menu
			}
			
			guess = (String.valueOf(guess).toUpperCase()).charAt(0);//set the guess to uppercase
			
			playerMoney[currentPlayerTurn] -= 250;
			if (guess == 'A' || guess == 'E' || guess == 'I' || guess == 'O' || guess == 'U') {//if the guess is a vowel
				int i = 0;//loop counter
				while (i < hiddenPhraseCharArray.length) {//while loop to loop until i equals the length of the hidden phrase
					if (guess == hiddenPhraseCharArray[i] && alreadyGuessedChars[i] == false) {//check if the guess char matches the char in the hidden phrase char array
						o.stop();//stop the timer
						c.setFont(HelveticaCondensedBlackSERegular58);//set the font
						p.playClip("ding.wav");//play the ding noise
						alreadyGuessedChars[i] = true;//set the already guessed chars array at index i to true
						int row = 0;//row number
						int column = 0;//column number
						
						//set the row and column numbers based on what i is
						if (i < 12) {
							row = 1;
							column = i;
						} else if (i > 11 && i < 26) {
							row = 2;
							column = i - 12;
						} else if (i > 25 && i < 40) {
							row = 3;
							column = i - 26;
						} else if (i > 39 && i < 52) {
							row = 4;
							column = i - 40;
						}
						if (i < 14 && hiddenPhraseCharArray.length < 29) {
							row = 2;
							column = i;
						}
						if (i > 13 && i < 28 && hiddenPhraseCharArray.length < 29) {
							row = 3;
							column = i - 14;
						}
						switch (row) {
						case 1:

							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(227 + column * 70, 182, 47, 62);//draw the blue reveal square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 182, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 182, 5, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (InterruptedException e) {

							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(227 + column * 70, 182, 47, 62);//draw the white square over the blue square
							if (column == 11) {//redraw parts of the square to correct for misalignment

								c.fillRect(994, 182, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 182, 5, 62);
							}
							//draw the vowel
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 237 + column * 70, 237);
							break;
						case 2:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(157 + column * 70, 267, 47, 62);//draw the blue reveal square
							if (column == 12) {//redraw parts of the square to correct for misalignment

								c.fillRect(994, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 267, 7, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 267, 7, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (Exception e) {
							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(157 + column * 70, 267, 47, 62);//draw the white square over the blue square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 267, 7, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 267, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 267, 7, 62);
							}
							//draw the vowel
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 167 + column * 70, 321);
							break;
						case 3:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(157 + column * 70, 353, 47, 62);//draw the blue reveal square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 353, 5, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 353, 7, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds

							} catch (Exception e) {

							}

							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(157 + column * 70, 353, 47, 62);//draw the white square over the blue square
							if (column == 12) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 353, 5, 62);
							}
							if (column == 13) {//redraw parts of the square to correct for misalignment
								c.fillRect(1061, 353, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1108, 353, 7, 62);
							}
							//draw the vowel
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 167 + column * 70, 405);
							break;
						case 4:
							c.setColor(new Color(5, 20, 231));// blue
							c.fillRect(227 + column * 70, 439, 47, 62);//draw the blue reveal square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 439, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 439, 5, 62);
							}
							try {
								Thread.sleep(1300);//sleep for 1.3 seconds
							} catch (Exception e) {
							}
							c.setColor(new Color(254, 255, 237));//set the color to white
							c.fillRect(227 + column * 70, 439, 47, 62);//draw the white square over the blue square
							if (column == 11) {//redraw parts of the square to correct for misalignment
								c.fillRect(994, 439, 47, 62);
								c.setColor(Color.black);
								c.fillRect(1041, 439, 5, 62);
							}
							//draw the vowel
							c.setColor(Color.black);
							c.drawString(Character.toString(hiddenPhraseCharArray[i]), 237 + column * 70, 491);

							break;
						}
						
						//draw the player scoreboards
						c.setColor(new Color(201, 191, 194));
						c.fillRect(272, 19, 230, 93);
						c.fillRect(520, 19, 230, 93);
						c.fillRect(766, 19, 230, 93);
						c.setColor(new Color(180, 28, 76));
						c.fillRect(272, 25, 230, 81);
						c.setColor(new Color(214, 139, 33));
						c.fillRect(520, 25, 230, 81);
						c.setColor(new Color(9, 12, 177));
						c.fillRect(766, 25, 230, 81);
						c.setColor(Color.white);

						//draw the player names and balances
						c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 27));
						c.drawString(playerNames[0].toUpperCase(), 390 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[1].toUpperCase(), 635 - (playerNames[0].length() * 9), 58);
						c.drawString(playerNames[2].toUpperCase(), 885 - (playerNames[0].length() * 9), 58);

						c.drawString("$" + Integer.toString(playerMoney[0]),
								380 - ((Integer.toString(playerMoney[0]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[1]),
								625 - ((Integer.toString(playerMoney[1]).length() * 9)), 88);
						c.drawString("$" + Integer.toString(playerMoney[2]),
								875 - ((Integer.toString(playerMoney[2]).length() * 9)), 88);
						c.setFont(HelveticaCondensedBlackSERegular58);
						guessedCorrectly = true;//set guessed correctly to true
						typedValid = true;//end the loop

					} else if (guess == hiddenPhraseCharArray[i] && alreadyGuessedChars[i] == true) {//if the player guesses a consonant that's already been guessed
						o.stop();//stop the timer
						p.playClip("Buzzer.wav");//play the buzzer sound
						JOptionPane.showMessageDialog(f, ("This letter has already been guessed!"), "Vowel Guess",
								JOptionPane.INFORMATION_MESSAGE);// display error message window
						i = 100;//end the loop by setting i to over the length of the hidden phrase
						guessedCorrectly = true;
						typedValid = false;
						//change the current player turn to the next player
						switch (currentPlayerTurn) {
						case 0:
							currentPlayerTurn = 1;
							break;
						case 1:
							currentPlayerTurn = 2;
							break;
						case 2:
							currentPlayerTurn = 0;
							break;
						}
						gameOptionsMenu();//go back the the menu
					}
					i++;
				}

				if (guessedCorrectly == false) {//if guessed correctly was false
					o.stop();//stop the timer
					p.playClip("Buzzer.wav");//play the buzzer sound
					JOptionPane.showMessageDialog(f, ("The vowel you typed did not appear in the puzzle!"),
							"Consonant Guess", JOptionPane.INFORMATION_MESSAGE);// display error message window
					//change the current player turn to the next player
					switch (currentPlayerTurn) {
					case 0:
						currentPlayerTurn = 1;
						break;
					case 1:
						currentPlayerTurn = 2;
						break;
					case 2:
						currentPlayerTurn = 0;
						break;
					}
					typedValid = true;//end the loop
				}
			} else if (guess != 'A' && guess != 'E' && guess != 'I' && guess != 'O' && guess != 'U') {//if the user input is not a vowel
				o.suspend();//pause the timer
				JOptionPane.showMessageDialog(f, ("The character you typed is not a vowel!"), "Vowel Guess",
						JOptionPane.INFORMATION_MESSAGE);// display error message window
				o.resume();//continue the timer
				typedValid = false;//continue the loop
			}
		}
		try {
			Thread.sleep(1000);//pause for 1 second
		} catch (Exception e) {

		}
		gameOptionsMenu();//go back to menu
	}

	private void guessPhrase() {
		puzzle();//draw the puzzle
		drawAlreadyGuessedLetters();//draw the already guessed letters
		boolean inputValid = false;//loop controller

		while (!inputValid) {
			try {
				guess = JOptionPane.showInputDialog("Please enter your guess:");//get user input

				inputValid = true;//end the loop
				if (guess == null) {//error check for null entry
					inputValid = false;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(f, ("Please input an answer!"), "Your Attempted Guess",
						JOptionPane.INFORMATION_MESSAGE);// display error message window
				inputValid = false;
			}
		}

		String attemptedGuess = (guess.toUpperCase()).replaceAll("\\s", "");//convert user input to all uppercase with no spaces

		String rawHiddenPhrase = hiddenPhrase.replaceAll("\\s", "");//convert the hidden phrase to no spaces

		if (attemptedGuess.equals(rawHiddenPhrase)) {//if the player's converted guess matches the converted hidden phrase
			p.playClip("puzzleSolve.wav");//play the puzzle solve sound effect

			int drawTheRestOfThePhraseCount = 0;//loop counter
			int row = 0;//row number
			int column = 0;//column number
			c.setFont(HelveticaCondensedBlackSERegular58);
			while (drawTheRestOfThePhraseCount < hiddenPhrase.length()) {//loop to draw the rest of the phrase
				
				//set the rows and columns based on the drawTheRestOfThePhraseCount
				if (drawTheRestOfThePhraseCount < 12) {
					row = 1;
					column = drawTheRestOfThePhraseCount;
				} else if (drawTheRestOfThePhraseCount > 11 && drawTheRestOfThePhraseCount < 26) {
					row = 2;
					column = drawTheRestOfThePhraseCount - 12;
				} else if (drawTheRestOfThePhraseCount > 25 && drawTheRestOfThePhraseCount < 40) {
					row = 3;
					column = drawTheRestOfThePhraseCount - 26;
				} else if (drawTheRestOfThePhraseCount > 39 && drawTheRestOfThePhraseCount < 52) {
					row = 4;
					column = drawTheRestOfThePhraseCount - 40;
				}
				if (drawTheRestOfThePhraseCount < 14 && hiddenPhraseCharArray.length < 29) {
					row = 2;
					column = drawTheRestOfThePhraseCount;
				}
				if (drawTheRestOfThePhraseCount > 13 && drawTheRestOfThePhraseCount < 28
						&& hiddenPhraseCharArray.length < 29) {
					row = 3;
					column = drawTheRestOfThePhraseCount - 14;
				}

				switch (row) {
				case 1:
					c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),//draw the letter
							237 + column * 70, 237);
					break;
				case 2:
					c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),//draw the letter
							167 + column * 70, 321);
					break;
				case 3:
					c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),//draw the letter
							167 + column * 70, 405);
					break;
				case 4:
					c.drawString(Character.toString(hiddenPhraseCharArray[drawTheRestOfThePhraseCount]),//draw the letter
							237 + column * 70, 491);
					break;

				}
				drawTheRestOfThePhraseCount++;
			}
			JOptionPane.showMessageDialog(f, ("Congrats! You solved the puzzle! Press OK to continue.."),
					"Your Attempted Guess", JOptionPane.INFORMATION_MESSAGE);// display message window
			mainGameRoundNumber++;//increase the main game round number by 1
			roundOrderController();//return to the round order controller for the next round
		}
		if (!(attemptedGuess.equals(rawHiddenPhrase))) {//if the player got it wrong
			p.playClip("buzzer.wav");//play sound buzzer effect
			//change the current player turn to the next player
			switch (currentPlayerTurn) {
			case 0:
				currentPlayerTurn = 1;
				break;
			case 1:
				currentPlayerTurn = 2;
				break;
			case 2:
				currentPlayerTurn = 0;
				break;
			}
			JOptionPane.showMessageDialog(f, ("Wrong!"),
					"Your Attempted Guess", JOptionPane.INFORMATION_MESSAGE);// display message window
			gameOptionsMenu();//go back to menu

		}
	}

	public void gameOptionsMenu() {//game options menu
		boolean gameOptionsMenuValid = false;//loop controller
		playingMainGame = true;//set the status of playing main game to true so puzzle() renders the correct stuff next time it is called
		char selection = ' ';//char to store the menu selection
		Color lightComplimentary[] = { new Color(241, 33, 33), new Color(241, 214, 33), new Color(33, 62, 241) };//color array to display the necessary color based on the current player turn
		Color darkComplimentary[] = { new Color(36, 6, 6), new Color(36, 36, 6), new Color(6, 14, 36) };//color array to display the necessary color based on the current player turn
		//draw the background
		c.setColor(lightComplimentary[currentPlayerTurn]);
		c.fillRect(0, 0, 1280, 720);
		c.setColor(darkComplimentary[currentPlayerTurn]);
		c.fillRect(14, 14, 1252, 692);
		
		c.setColor(Color.white);//set color to white
		try {
			GothamMedium = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 72);
		} catch (Exception e) {

		} // create the custom font they use on the show from .ttf file

		c.setFont(GothamMedium);//set the font to gotham medium
		//draw the menu options
		c.drawString("Menu Options:", 59, 133);
		c.drawString("1. Guess A Consonant", 59, 230);
		c.drawString("2. Buy A Vowel for $250", 59, 332);
		c.drawString("3. Solve The Puzzle", 59, 444);
		c.drawString("4. View The Board", 59, 547);
		
		//draw the player graphic character thing on the right
		c.setColor(lightComplimentary[currentPlayerTurn]);
		c.fillOval(992, 371, 188, 200);
		c.fillArc(900, 585, 369, 250, 0, 180);
		
		
		c.setFont(new Font("Eras Bold ITC", Font.PLAIN, 70));//set the font

		//draw the current player name
		c.drawString(playerNames[currentPlayerTurn].toUpperCase(),
				1350 - (playerNames[currentPlayerTurn].length() * 65), 114);
		//draw the current player balance
		c.drawString("$" + Integer.toString(playerMoney[currentPlayerTurn]),
				1350 - ((((Integer.toString(playerMoney[0]).length()) + 1) * 65)), 186);

		while (!gameOptionsMenuValid) {//loop to get input
			selection = c.getChar();//get input
			if (selection == '1' || selection == '2' || selection == '3' || selection == '4') {//if the menu selection is 1 2 3 or 4
				gameOptionsMenuValid = true;//end the loop
				if (selection == '1') {//guess a consonant
					wheel();
				} else if (selection == '2' && playerMoney[currentPlayerTurn] > 250) {//guess a vowel, if the player has more than $250
					guessVowel();

				} else if (selection == '2' && playerMoney[currentPlayerTurn] < 250) {//stop the player from guessing a vowel because they cant afford a vowel
					JOptionPane.showMessageDialog(f, ("You do not have enough money to purchase a vowel!"), "Game Menu",
							JOptionPane.INFORMATION_MESSAGE);// display error message window
					gameOptionsMenu();//go back to menu
				} else if (selection == '3') {//solve the puzzle
					guessPhrase();
				} else if (selection == '4') {//view the board
					mainGame();

				}
			} else if (selection != '1' || selection != '2' || selection != '3' || selection != '4') {//if the menu selections is not 1 2 3 or 4
				gameOptionsMenuValid = false;
				JOptionPane.showMessageDialog(null, "Error, please select a valid option"); // error message
			}
		}
	}

	private void displayPlayerInfo() {//this method displays the hall of fame featured on the main menu.
		BufferedImage background = null;//bufferedimage variable to store the background
		boolean deleted = false;//boolean to store whether the player has deleted all the data or not

		try {
			background = ImageIO.read(new File("mainMenuBackground.png"));//read the png

		} catch (Exception e) {

		}

		//intialize and load all of the gotham font sizes that will be used in this method
		Font gotham54 = null;
		Font gotham50 = null;
		Font gotham41 = null;
		Font gotham39 = null;
		Font gotham36 = null;
		Font gotham31 = null;

		try {
			gotham54 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 54);
			gotham50 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 50);
			gotham41 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 41);
			gotham39 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 39);
			gotham36 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 36);
			gotham31 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 31);

		} catch (Exception e) {
		} 
		
		
		c.drawImage(background, 0, 0, null);//draw the background image
		c.setColor(new Color(33, 62, 241));//set color to light blue
		c.fillRoundRect(134, 83, 1011, 554, 13, 13);//draw the rounded blue border around the contextual menu
		c.setColor(new Color(6, 14, 36));//set color to dark blue
		c.fillRoundRect(142, 91, 995, 538, 13, 13);//draw the rounded dark blue border around the contextual menu

		c.setColor(new Color(0, 255, 168));// aqua green
		c.setFont(gotham36);//set the font
		c.drawString("Press any key to return to the Main Menu..", 186, 599);
		c.setFont(gotham54);//set the font
		c.drawString("Contestant Hall of Fame", 308, 154);

		if (!data[0].equals("")) {//if the first line of the file is not blank
			//draw the top 3 players of all time header
			c.setColor(Color.white);
			c.setFont(gotham31);
			c.drawString("Top 3 Players of All Time:", 186, 209);
			
			//draw the top 3 player positions and ending balance words
			c.setFont(gotham41);
			c.drawString("1.", 186, 263);
			c.drawString("Ending Balance:", 186, 315);
			c.drawString("2.", 186, 375);
			c.drawString("Ending Balance:", 186, 426);
			c.drawString("3.", 186, 488);
			c.drawString("Ending Balance:", 186, 539);

			//draw the player names next to the 1. 2. and 3.
			c.setColor(new Color(237, 240, 0));// yellow
			c.drawString((data[0]), 228, 263);
			c.drawString(data[2], 239, 374);
			c.drawString(data[4], 239, 487);

			//draw the money next to the Ending Balance:s
			c.setColor(new Color(255, 0, 252));
			c.drawString("$" + data[1], 529, 315);
			c.drawString("$" + data[3], 529, 426);
			c.drawString("$" + data[5], 529, 540);

			//draw the rounded rectangle around the reset button
			c.setColor(new Color(33, 62, 241));//light blue
			c.fillRoundRect(861, 224, 216, 203, 13, 13);
			
			c.setColor(new Color(235, 0, 61));//danger red
			c.setFont(gotham39);//set the font
			//draw the press c to rest all scores text on the 'button'
			c.drawString("Press 'C'", 881, 282);
			c.drawString("to reset", 881, 331);
			c.drawString("all scores", 881, 382);
			
			
		} else if (data[0].equals("")) {//if the first line of the file is blank
			deleted = true;//set deleted status to true
			c.setFont(gotham50);//set the font
			c.setColor(new Color(235, 0, 61));//danger red
			//draw the warning message
			c.drawString("There is no score data available!", 239, 330);
			c.drawString("Please complete a game first!", 273, 393);

		}

		boolean valid = false;//loop controller
		char menuSelection;//char to store the user input
		while (!valid) {//loop to get the user input
			menuSelection = c.getChar();//get the user input
			menuSelection = ((String.valueOf(menuSelection)).toUpperCase()).charAt(0);//set the user input to upper case
			if (menuSelection == 'C' && deleted == false) {//if the user input was 'c' and the status of deleted data is false
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all score data?",//ask the user if they are sure
						"Hall of Fame", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					data[0] = "";//clear the data to blank
					data[1] = "";//clear the data to blank
					data[2] = "";//clear the data to blank
					data[3] = "";//clear the data to blank
					data[4] = "";//clear the data to blank
					data[5] = "";//clear the data to blank

					PrintWriter output;//PrintWriter object

					// try to open the file
					try {
						output = new PrintWriter(new FileWriter("playerInfo.txt"));//set the printwriter object and open the file

						// this loop will write all the data in the array to the file, with each element
						// on a new line
						for (int i = 0; i < data.length; i++) {//for loop to loop 6 times

							output.println(data[i]);//write the data to the file on each line
						}
						output.close(); // this line will save the file
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "file cant be opened"); // error message
					}
					JOptionPane.showMessageDialog(f, ("Deleted all score data"), "Hall of Fame",
							JOptionPane.INFORMATION_MESSAGE);// deletion of data confirmation message
					valid = false;
					displayPlayerInfo();//redraw display player info
				} else {
					valid = false;
				}
			} else if (menuSelection != 'C') {//if the user input is anything else
				comingFromInstructions = true;//set to true
				mainMenu();//go back to the main menu
			}
		}

	}

	public void endScreen() {//this method will calculate who won and if needed update the leaderboard data as well as it will display the end screen at the end of the game
		readData();//call method to read the data from the file

		BufferedImage background = null;//new buffered image to store the background
		//gotham in 3 font sizes font variables
		Font gotham55 = null;
		Font gotham41 = null;
		Font gotham36 = null;
		
		//determine the winner
		byte winner = 0;
		if (playerMoney[0] > playerMoney[1] && playerMoney[0] > playerMoney[2]) {//if player 1 more than player 2 and 3
			winner = 0;
			// player 1 is the winner
		} else if (playerMoney[1] > playerMoney[0] && playerMoney[1] > playerMoney[2]) {//if player 2 more than player 1 and 3
			winner = 1;
			// player 2 is the winner
		} else if (playerMoney[2] > playerMoney[1] && playerMoney[2] > playerMoney[0]) {//if player 3 more than player 2 and 1
			winner = 2;
			// player 3 is the winner
		} else if (playerMoney[0] == playerMoney[2] && playerMoney[1] == playerMoney[2]//if all balances are equal
				&& playerMoney[0] == playerMoney[1]) {
			winner = 99;
			// there was no winner
		} else if (playerMoney[0] > playerMoney[1] && playerMoney[0] == playerMoney[2]) {//if 2 of the players have equal balances
			winner = 100;
			// player 1 and player 3 are the winners
		} else if (playerMoney[0] == playerMoney[1] && playerMoney[0] > playerMoney[2]) {//if 2 of the players have equal balances
			winner = 101;
			// player 1 and player 2 are the winners
		} else if (playerMoney[2] == playerMoney[1] && playerMoney[2] > playerMoney[0]) {//if 2 of the players have equal balances
			winner = 102;
			// player 3 and player 2 are the winners
		} else {
			winner = 99;
			// there was no winner
		}
		
		//if the file is not blank, I have to compare the scores of the current players with those stored in the file and replace the top players if the current players' scores are higher
		if (!data[0].equals("")) {
			if (playerMoney[0] > Integer.parseInt(data[1])) {//if player 1 has a higher balance than the 1st person on the leaderboard
				data[0] = playerNames[0];//store the name 
				data[1] = (String.valueOf(playerMoney[0]));//store the balance
			} else if (playerMoney[1] > Integer.parseInt(data[1])) {//if player 2 has a higher balance than the 1st person on the leaderboard
				data[0] = playerNames[1];//store the name 
				data[1] = (String.valueOf(playerMoney[1]));//store the balance
			} else if (playerMoney[2] > Integer.parseInt(data[1])) {//if player 3 has a higher balance than the 1st person on the leaderboard
				data[0] = playerNames[2];//store the name 
				data[1] = (String.valueOf(playerMoney[2]));//store the balance
			}

			if (playerMoney[0] > Integer.parseInt(data[3]) && playerMoney[0] < Integer.parseInt(data[1])) {//if player 1 has a higher balance than the 2nd person on the leaderboard but lower than the 1st
				data[2] = playerNames[0];//store the name 
				data[3] = (String.valueOf(playerMoney[0]));//store the balance
			} else if (playerMoney[1] > Integer.parseInt(data[3]) && playerMoney[1] < Integer.parseInt(data[1])) {//if player 2 has a higher balance than the 2nd person on the leaderboard but lower than the 1st
				data[2] = playerNames[1];//store the name 
				data[3] = (String.valueOf(playerMoney[1]));//store the balance
			} else if (playerMoney[2] > Integer.parseInt(data[3]) && playerMoney[2] < Integer.parseInt(data[1])) {//if player 3 has a higher balance than the 2nd person on the leaderboard but lower than the 1st
				data[2] = playerNames[2];//store the name 
				data[3] = (String.valueOf(playerMoney[2]));//store the balance
			}

			if (playerMoney[0] > Integer.parseInt(data[5]) && playerMoney[0] < Integer.parseInt(data[3])) {//if player 1 has a higher balance than the 3rd person on the leaderboard but lower than the 2nd
				data[4] = playerNames[0];//store the name 
				data[5] = (String.valueOf(playerMoney[0]));//store the balance
			} else if (playerMoney[1] > Integer.parseInt(data[5]) && playerMoney[1] < Integer.parseInt(data[3])) {//if player 2 has a higher balance than the 3rd person on the leaderboard but lower than the 2nd
				data[4] = playerNames[1];//store the name 
				data[5] = (String.valueOf(playerMoney[1]));//store the balance
			} else if (playerMoney[2] > Integer.parseInt(data[5]) && playerMoney[2] < Integer.parseInt(data[3])) {//if player 3 has a higher balance than the 3rd person on the leaderboard but lower than the 2nd
				data[4] = playerNames[2];//store the name 
				data[5] = (String.valueOf(playerMoney[2]));//store the balance
			}
			
			//if the file is blank, i have to determine who is first second and third and store those player datas in the file
		} else if (data[0].equals("")) {
			if (playerMoney[0] > playerMoney[1] && playerMoney[0] > playerMoney[2] && playerMoney[2] > playerMoney[1]) {// if player 1 more than player 2 and more than player 3 and player 3 is more than player 2
				// 1-1st
				// 2-3rd
				// 3-2nd
				data[0] = playerNames[0];//store the name 
				data[1] = (String.valueOf(playerMoney[0]));//store the balance
				data[4] = playerNames[1];//store the name 
				data[5] = (String.valueOf(playerMoney[1]));//store the balance
				data[2] = playerNames[2];//store the name 
				data[3] = (String.valueOf(playerMoney[2]));//store the balance
			} else if (playerMoney[0] > playerMoney[1] && playerMoney[0] > playerMoney[2]
					&& playerMoney[1] > playerMoney[2]) {// if player 1 more than player 2 and more than player 3 and
															// player 3 is more than player 2
				// 1-1st
				// 2-2nd
				// 3-3rd
				data[0] = playerNames[0];//store the name 
				data[1] = (String.valueOf(playerMoney[0]));//store the balance
				data[2] = playerNames[1];//store the name 
				data[3] = (String.valueOf(playerMoney[1]));//store the balance
				data[4] = playerNames[2];//store the name 
				data[5] = (String.valueOf(playerMoney[2]));//store the balance
			}

			else if (playerMoney[1] > playerMoney[0] && playerMoney[1] > playerMoney[2]
					&& playerMoney[2] > playerMoney[0]) {// if player 2 more than player 1 and more than player 3 and
															// player 3 is more than player 1
				// 1-3rd
				// 2-1st
				// 3-2nd
				data[0] = playerNames[1];//store the name 
				data[1] = (String.valueOf(playerMoney[1]));//store the balance
				data[4] = playerNames[0];//store the name 
				data[5] = (String.valueOf(playerMoney[0]));//store the balance
				data[2] = playerNames[2];//store the name 
				data[3] = (String.valueOf(playerMoney[2]));//store the balance
			} else if (playerMoney[1] > playerMoney[0] && playerMoney[1] > playerMoney[2]
					&& playerMoney[0] > playerMoney[2]) {// if player 2 more than player 1 and more than player 3 and
															// player 3 is more than player 1
				// 1-2nd
				// 2-1st
				// 3-3rd
				data[0] = playerNames[1];//store the name 
				data[1] = (String.valueOf(playerMoney[1]));//store the balance
				data[4] = playerNames[2];//store the name 
				data[5] = (String.valueOf(playerMoney[2]));//store the balance
				data[2] = playerNames[0];//store the name 
				data[3] = (String.valueOf(playerMoney[0]));//store the balance
			}

			else if (playerMoney[2] > playerMoney[0] && playerMoney[2] > playerMoney[1]
					&& playerMoney[0] > playerMoney[1]) {// if player 3 more than player 1 and more than player 2 and
															// player 3 is more than player 1
				// 1-2nd
				// 2-3rd
				// 3-1st
				data[0] = playerNames[2];//store the name 
				data[1] = (String.valueOf(playerMoney[2]));//store the balance
				data[2] = playerNames[0];//store the name 
				data[3] = (String.valueOf(playerMoney[0]));//store the balance
				data[4] = playerNames[1];//store the name 
				data[5] = (String.valueOf(playerMoney[1]));//store the balance
			} else if (playerMoney[2] > playerMoney[0] && playerMoney[2] > playerMoney[1]
					&& playerMoney[1] > playerMoney[0]) {// if player 3 more than player 1 and more than player 2 and
															// player 3 is more than player 1
				// 1-3rd
				// 2-2nd
				// 3-1st
				data[0] = playerNames[2];//store the name 
				data[1] = (String.valueOf(playerMoney[2]));//store the balance
				data[4] = playerNames[0];//store the name 
				data[5] = (String.valueOf(playerMoney[0]));//store the balance
				data[2] = playerNames[1];//store the name 
				data[3] = (String.valueOf(playerMoney[1]));//store the balance
			}
		}
		
		PrintWriter output;// definition for a PrintWriter object

		try {
			output = new PrintWriter(new FileWriter("playerInfo.txt"));

			// this loop will write all the data in the array to the file, with each element
			// on a new line
			for (int i = 0; i < data.length; i++) {

				output.println(data[i]);//write the data array to seperate lines in the file
			}
			output.close(); // this line will save the file
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}

		///////////////////
		try {
			background = ImageIO.read(new File("mainMenuBackground.png"));//set the buffered image to the main menu background png

		} catch (Exception e) {
			new Message("File not found :(", "hmm");
		}
		
		c.drawImage(background, 0, 0, null);//draw the image
		c.setColor(new Color(33, 62, 241));//light blue
		c.fillRoundRect(134, 83, 1011, 554, 13, 13);//draw the round border
		c.setColor(new Color(6, 14, 36));//dark blue
		c.fillRoundRect(142, 91, 995, 538, 13, 13);//draw the dark blue round rectangle

		//initialize all the fonts
		try {
			gotham55 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 55);
			gotham41 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 41);
			gotham36 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))
					.deriveFont(Font.PLAIN, 36);
		} catch (Exception e) {
			new Message("File not found :(", "hmm");
		} 

		c.setFont(gotham55);//set the font
		c.setColor(new Color(0, 255, 168));// aqua green
		c.drawString("Game Summary", 422, 155);//draw the game summary header
		c.setFont(gotham36);//set the font
		c.drawString("Press any key to return to the Main Menu..", 193, 585);//draw the press any key message at the bottom
		c.setFont(gotham41);//set the font
		c.setColor(new Color(237, 240, 0));// yellow
		if (winner != 99 && winner != 100 && winner != 101 && winner != 102) {//if 1 player won
			c.drawString(("Winner: " + playerNames[winner]), 209, 219);
		} else if (winner == 99) {//if there were no winners
			c.drawString(("There was no winner!"), 209, 219);
		} else if (winner == 100) {//if player 1 and player 3 won
			c.drawString(("Winner: " + playerNames[0] + " and " + playerNames[2]), 209, 219);
		} else if (winner == 101) {//if player 1 and player 2 won
			c.drawString(("Winner: " + playerNames[0] + " and " + playerNames[1]), 209, 219);
		} else if (winner == 102) {//if player 2 and player 3 won
			c.drawString(("Winner: " + playerNames[2] + " and " + playerNames[1]), 209, 219);
		}
		
		c.setColor(Color.white);//set the color
		//draw the final balances of each player
		c.drawString((playerNames[0] + "'s Final Balance: $" + playerMoney[0]), 209, 300);
		c.drawString((playerNames[1] + "'s Final Balance: $" + playerMoney[1]), 209, 400);
		c.drawString((playerNames[2] + "'s Final Balance: $" + playerMoney[2]), 209, 500);
		
		comingFromInstructions = false;//set coming from instructions to false to play the music
		c.getChar();//pause and wait for input
		mainMenu();//go back to main menu
	}

	private void emptyGameBoard() {//this method will draw the empty game board		
		try {
			BufferedImage someImage = ImageIO.read(new File("gameBackground.png"));//load image from png file
			c.drawImage(someImage, 0, 0, null);//draw the game background
		} catch (IOException e) {
			new Message("File not found :(", "hmm");
		}
		//draw the game board outline borders
		c.setColor(new Color(55, 152, 255));
		c.fillRect(172, 130, 922, 424);
		c.fillRect(102, 219, 1060, 246);
		c.setColor(new Color(0, 0, 5));
		c.fillRect(126, 229, 1012, 227);
		c.fillRect(193, 144, 878, 399);
		c.setColor(new Color(214, 216, 218));
		c.fillRect(188, 147, 887, 387);
		c.fillRect(119, 235, 1026, 213);
		c.setColor(new Color(137, 154, 255));
		c.fillRect(192, 150, 879, 380);
		c.fillRect(122, 238, 1020, 206);
		c.setColor(new Color(152, 141, 147));
		c.fillRect(141, 258, 984, 170);
		c.fillRect(209, 167, 846, 349);
		c.setColor(Color.black);
		c.fillRect(218, 173, 828, 338);
		c.fillRect(150, 259, 966, 166);

		//draw the turquoise vertical lines
		c.setColor(new Color(0, 164, 141));
		for (int i = 0; i < 11; i++) {
			c.fillRect(283 + i * 70, 174, 4, 338);
		}

		//draw the turquoise horizontal lines
		for (int i = 0; i < 3; i++) {
			c.fillRect(218, 254 + i * 86, 829, 4);
		}
		
		c.fillRect(150, 340, 69, 4);
		c.fillRect(214, 258, 4, 167);
		c.fillRect(1048, 258, 4, 167);
		c.fillRect(1047, 340, 69, 4);

		//draw the turquoise rectangles for row 1
		for (int i = 0; i < 11; i++) {
			fillTurquoiseRect(227 + i * 70, 182);// B1-L1
		}
		fillTurquoiseRect(994, 182);// M1
		
		//draw the turquoise rectangles for row 2
		for (int i = 0; i < 12; i++) {
			fillTurquoiseRect(157 + i * 70, 267);// A2-L2
		}
		fillTurquoiseRect(994, 267);// M2
		fillTurquoiseRect(1061, 267);// N2
		
		//draw the turquoise rectangles for row 3
		for (int i = 0; i < 12; i++) {
			fillTurquoiseRect(157 + i * 70, 353);// A3-L3
		}
		fillTurquoiseRect(994, 353);// M3
		fillTurquoiseRect(1061, 353);// N3
		
		//draw the turquoise rectangles for row 4
		for (int i = 0; i < 11; i++) {
			fillTurquoiseRect(227 + i * 70, 439);// B4-L4
		}
		fillTurquoiseRect(994, 439);// M4
	}

	private void fillTurquoiseRect(int x, int y) {//this method will draw a turquoise rectangle of 47 length and 62 height when given the coordinates
		c.setColor(new Color(1, 153, 124));//turquoise
		c.fillRect(x, y, 47, 62);//draw the rectangle
	}

	public void instructions() {//this method will display the instructions
		BufferedImage background = null;//background image

		try {
			background = ImageIO.read(new File("mainMenuBackground.png"));//read the background image from png file

		} catch (Exception e) {
		}
		
		c.drawImage(background, 0, 0, null);//draw the background
		
		
		c.setColor(new Color(33, 62, 241));//light blue
		c.fillRoundRect(134, 83, 1011, 554, 13, 13);//draw the rounded border
		c.setColor(new Color(6, 14, 36));//dark blue
		c.fillRoundRect(142, 91, 995, 538, 13, 13);//draw the dark rounded rectangle on top
		
		//fonts gotham in sizes 58 and 36
		Font gotham58 = null;
		Font gotham36 = null;

		try {
			gotham58 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))//load font from file
					.deriveFont(Font.PLAIN, 50);//set size to  50
			gotham36 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))//load font from file
					.deriveFont(Font.PLAIN, 36);//set size to 36
		} catch (Exception e) {
			
		}
		c.setFont(gotham58);//set font
		//draw the instructions header
		c.setColor(new Color(0, 255, 168));
		c.drawString("Instructions", 496, 148);
		c.setFont(gotham36);//set font
		//draw the coninue message at the bottom
		c.drawString("Press any key to continue..", 193, 585);
		//draw the instructions for toss up on page 1
		c.setColor(Color.white);
		c.drawString("In the Toss Up rounds, the board automatically", 216, 243);
		c.drawString("reveals letters one by one until a player rings", 216, 290);
		c.drawString("in with the correct solution. The winner collects", 216, 337);
		c.drawString("a cash prize of $1000 and will have the first", 216, 385);
		c.drawString("turn during the main game.", 216, 432);
		c.getChar();//wait for user input
		//redraw the light blue border and dark blue inner round rectangle
		c.setColor(new Color(33, 62, 241));
		c.fillRoundRect(134, 83, 1011, 554, 13, 13);
		c.setColor(new Color(6, 14, 36));
		c.fillRoundRect(142, 91, 995, 538, 13, 13);
		//redraw the instructions header
		c.setFont(gotham58);
		c.setColor(new Color(0, 255, 168));
		c.drawString("Instructions", 496, 148);
		//redraw the press any key continue message at the bottom
		c.setFont(gotham36);
		c.drawString("Press any key to return to the Main Menu..", 193, 585);
		c.setColor(Color.white);//set color
		//draw the instructions for the main game page 2
		c.drawString("In the main game, contestants have three", 216, 217);
		c.drawString("options: spin the wheel and call a consonant,", 216, 265);
		c.drawString("buy a vowel for $250, or solve the puzzle. Each", 216, 311);
		c.drawString("consonant is worth the cash value of the wedge", 216, 359);
		c.drawString("the wheel lands on. Contestants can continue", 216, 406);
		c.drawString("spinning the wheel until they miss a letter or", 216, 453);
		c.drawString("spin a Bankrupt or Lose a Turn.", 216, 501);
		c.getChar();//wait for user input
		comingFromInstructions = true;//dont play music
		mainMenu();//go back to the main menu

	}

	public void exit() {//this method will draw a message and credits and then confirm the exiting of the program
		BufferedImage background = null;//background image 

		try {
			background = ImageIO.read(new File("mainMenuBackground.png"));//read the background image from png file

		} catch (Exception e) {
			new Message("file not found","error");
		}
		
		c.drawImage(background, 0, 0, null);//draw the background
		//draw the light blue round border
		c.setColor(new Color(33, 62, 241));
		c.fillRoundRect(130, 217, 1011, 276, 13, 13);
		//draw the dark blue inside round rectangle
		c.setColor(new Color(6, 14, 36));
		c.fillRoundRect(138, 225, 995, 260, 13, 13);
		Font gotham40 = null;//create new font

		try {
			gotham40 = Font
					.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream("GothamMedium.ttf")))//read font from file
					.deriveFont(Font.PLAIN, 40);//initialize font size 40
		} catch (Exception e) {

		}
		c.setColor(Color.white);//set color
		c.setFont(gotham40);//set font
		c.drawString("Thanks for playing", 187, 300);//draw thanks for playing message
		c.setColor(new Color(237, 240, 0));// yellow
		c.drawString("Wheel Of Fortune!", 574, 300);//draw wheel of fortune in yellow
		c.setColor(new Color(0, 255, 168));// aqua green
		c.drawString("Created by Daniel Su", 187, 433);//draw credits

		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Main Menu",//ask the user if they are sure they want to quit
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {//display joption pane dialog with yes or no buttons
			System.exit(0);//if yes, exit the program
		} else {//if no, go back to main menu
			comingFromInstructions = true;//dont play music
			mainMenu();//go back to the main menu
		}

	}

	public static void main(String[] args) {

		WheelOfFortune m = new WheelOfFortune();// create new instance of the class
		m.splashScreen();//call method splashScreen()

	}
}
