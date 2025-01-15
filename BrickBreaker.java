import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//imports the needed packages to create movement as well 

public class BrickBreaker
{
	public static void main(String[] args)
	{
		JFrame obj = new JFrame();//creates game window
		GamePlay gamePlay = new GamePlay();
		obj.setBounds(10, 10, 900, 800);//Sets the size
		obj.setTitle("Brick Breaker Game");
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(gamePlay);
	}
}

class GamePlay extends JPanel implements KeyListener, ActionListener//inheritance
{
	private boolean play = false;//boolean to see if the game is active or not
	private int score = 0;
	private int level = 0;//sets at 0 for the instructions at the very start of the games
	
	private int totalBricks = 48;
	
	private Timer timer;
	private int delay = 8;//Speed of the game

	private int playerX = 400;//position on the X axis of the paddle
	
	private int ballposX = 250;//position on the X axis
	private int ballposY = 400;//position on the Y axis
	private int ballXdir = -1;//ball speed in the X dir
	private int ballYdir = -2;//ball speed in the Y dir

	private MapGenerator map;


	public GamePlay()
	{
		map = new MapGenerator(4,12);//creates the already set 4x12 array
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();
	}

	public void paint(Graphics g)//colors the game, displays text and features
	{
		//background
		g.setColor(Color.black);
		g.fillRect(1,1,900, 800);

		//Map
		map.draw((Graphics2D)g);

		//Score
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString(""+score, 820,30);
		g.drawString("Level: " + level, 20, 30);


		//Border
		g.setColor(Color.yellow);
		g.fillRect(0,0,10,800);
		g.fillRect(0,0,900,10);
		g.fillRect(890,0,10,800);

		//Paddle
		g.setColor(Color.green);
		g.fillRect(playerX, 750, 150, 8);

		//Ball
		g.setColor(Color.yellow);
		g.fillOval(ballposX, ballposY, 20,20);

		if(totalBricks <=0)//if all bricks are cleared
		{
			play = false;//pause game
			ballXdir = 0;
			ballYdir=0;
			g.setColor(Color.green);
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Level Cleared! Use arrow keys to continue!", 150,400);//is added down in the Keypressed section
		}

		if(ballposY>750)//if they lose
		{
			play = false;
			level = 1;//reset level to 1
			ballXdir=0;
			ballYdir=0;
			g.setColor(Color.red);
			g.setFont(new Font("serif", Font.BOLD, 40));
			g.drawString("Game Over, Score: " + score, 220,350);

			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Press Enter to Restart", 260, 400);//added in the Keypressed section
		}
		if (level == 0 && !play)//At the very start when opening the application

		{
			g.setColor(Color.green);
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Use the arrow keys to move", 275, 400);//down in the keypressed Section
			
		}

		g.dispose();
	}
	


	@Override//parts of the EventListener and ActionListener
	public void actionPerformed(ActionEvent e)
	{
		timer.start();
		if(play)
		{
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 750, 150, 8)))//if ball hits paddle only change Ydir
			{
				ballYdir = -ballYdir;
			}

			A: for(int i=0;i<map.map.length; i++)//Collision detecting
			{
				for(int j = 0; j<map.map[0].length; j++)
				{
					if (map.map[i][j] > 0)
					{
						int brickX = j*map.brickWidth + 50;
						int brickY = i*map.brickHeight + 50;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;

						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
						Rectangle brickRect = rect;

						if(ballRect.intersects(brickRect))//if ball touches brick
						{
							map.setBrickValue(0,i,j);//set brick value to 0
							totalBricks --;
							score += 50;

							if(ballposX + 19 <= brickRect.x || ballposX + 1> brickRect.x + brickRect.width)
							{
								ballXdir = -ballXdir;//if ball touches bricks horizontally rebound horizontally
							}
							else
							{
								ballYdir = -ballYdir;//else is vertical it rebounds vertically
							}
							break A;
						}
					}
				}
			}


			ballposX += ballXdir;
			ballposY += ballYdir;
			if (ballposX < 10)//If ball touches left wall
			{
				ballXdir = -ballXdir;
			}
			if(ballposY <10)//if ball touches ceiling
			{
				ballYdir=-ballYdir;
			}
			if(ballposX > 880)//if ball touches right wall
			{
				ballXdir = -ballXdir;
			}
		}
		repaint();
	}
	//Useless sections imported from the Key and Event listener
	@Override
	public void keyTyped(KeyEvent e)
	{}
	@Override
	public void keyReleased(KeyEvent e)
	{}

	@Override
	public void keyPressed(KeyEvent e)//when a specific key is pressed
	{
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT)//This is for the instruction page.
		{
			if (level==0)//instruction level
			{
				level = 1;
			}
			if (totalBricks<=0)//if they win they press arrow key to continue
			{
				level++;
                        	totalBricks = 48;
                        	map = new MapGenerator(4,12);
                        	play=true;
				delay = delay-2;//NEW ADDITION MAKING BALL FASTER EACH ROUND
                        	ballposX = 250;
                        	ballposY = 400;
                        	playerX = 400;
                        	ballXdir = -1;
                        	ballYdir = -2;
				timer.stop();
        			timer.setDelay(delay);
        			timer.start();
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			if(playerX>=800)
			{
				playerX=800;//prevents user from going out the screen on the right
			}
			else
			{
				moveRight();
			}	
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if (playerX<10)
			{
				playerX=10;//prevents user from going out the screen on the left
			}
			else
			{
				moveLeft();
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(!play)
			{
				play = true;//Enter to restart and resets everything except instructions
				ballposX = 250;
				ballposY = 400;
				delay = 8;
				ballXdir = -1;
				ballYdir = -2;
				playerX = 400;
				score = 0;
				totalBricks = 48;
				map = new MapGenerator(4, 12);
				timer.stop();
        			timer.setDelay(delay);
        			timer.start();

				repaint();
			}
		}
	}
	public void moveRight()
	{
		play = true;
		playerX+=50;//how much user will move
	}
	public void moveLeft()
	{
		play = true;
		playerX-=50;
	}
}

class MapGenerator
{
	public int map[][];//creating 2d array
	public int brickWidth;
	public int brickHeight;
	public MapGenerator(int row, int col)
	{
		map = new int[row][col];
		for (int i = 0;i<map.length; i++)
		{
			for (int j = 0; j<map[0].length; j++)
			{
				map[i][j] =1;
			}
		}
		brickWidth = 780/col;//size of each brick
		brickHeight = 150/row;
	}
	public void draw(Graphics2D g)
	{
		 for (int i = 0;i<map.length; i++)
                {
                        for (int j = 0; j<map[0].length; j++)
                        {
                               if ( map[i][j] >0)
			       {
				       g.setColor(Color.white);
				       g.fillRect(j*brickWidth +50, i*brickHeight +50, brickWidth, brickHeight);
					//Shows the gap in between each brick
				       g.setStroke(new BasicStroke(3));
				       g.setColor(Color.black);
				       g.drawRect(j*brickWidth+50, + i*brickHeight + 50, brickWidth, brickHeight);
			       }
                        }
                }
	}
	public void setBrickValue(int value, int row, int col)
	{
		map[row][col] = value;
	}


}
