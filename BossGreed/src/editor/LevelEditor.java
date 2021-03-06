package editor;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import entities.Arrow;
import entities.ArrowKey;
import entities.Bat;
import entities.Box;
import entities.Brick;
import entities.Bush;
import entities.Cactus;
import entities.Cloud;
import entities.Coin;
import entities.Dead;
import entities.Doorjam;
import entities.Gem;
import entities.Grav;
import entities.Hang;
import entities.Hills;
import entities.Ice;
import entities.Lbox;
import entities.Ledge;
import entities.Loff;
import entities.News;
import entities.Platform;
import entities.Rope;
import entities.Shape;
import entities.Sky;
import entities.Skyline;
import entities.Text;
import entities.Wall;
import entities.Wheel;

public class LevelEditor
{
	// 1280x800 for high res, 1024x600 for low res
	protected static final int EDITOR_RESOLUTION_X = 1024;			// width of the level editor screen
	protected static final int EDITOR_RESOLUTION_Y = 600;			// height of the level editor screen
	protected final static int GAME_RESOLUTION_X = 640;		// game dimensions
	protected final static int GAME_RESOLUTION_Y = 480;		// of BossGreed
	public static final int MAX_GRID_SIZE = 200;			// maximum size for the grid
	public static final int MIN_GRID_SIZE = 10;				// minimum size for the grid
	public static final int MAX_TYPE_SIZE = 25;				// maximum size for the 'type' var
	public static final int MIN_TYPE_SIZE = 0;				// minimum size for the 'type' var

	// for convenience, dependent on the above constants
	int TOP = (EDITOR_RESOLUTION_Y - GAME_RESOLUTION_Y) / 2 - 1 - 70;
	int BOTTOM = (EDITOR_RESOLUTION_Y + GAME_RESOLUTION_Y) / 2 + 1 - 70;
	int LEFT = (EDITOR_RESOLUTION_X - GAME_RESOLUTION_X) / 2 - 1;
	int RIGHT = (EDITOR_RESOLUTION_X + GAME_RESOLUTION_X) / 2 - 1;

	// not final, so you can change within the editor
	int FONT_SIZE = 24;				// (this is automatically changed if lowRes)
	int GRID_SIZE = 50;				// CHANGE THIS for a different default grid size
	int CAMERA_SCROLL_SPEED = 5;	// CHANGE THIS to adjust the WASD scroll speed
	int FUDGE_X = 320;				// don't worry about this
	int FUDGE_Y = 91;				// or this
	int THICKNESS = 1;				// CHANGE THIS to adjust the IJKL thickness

	double r, g, b;					// for the sky

	int transX = GAME_RESOLUTION_X / 2, transY = GAME_RESOLUTION_Y / 2, mouseX,
			mouseY, width = 26, height = 26;
	float startX, startY;
	int [] Keys = new int[20];		// make an array to hold the keys for controls

	int picsX = 50, picsY = BOTTOM + 50, picsW = 50, picsH = 50;		// for the bottom pics grid
	int buttonCode = 1;				// used to choose which instance int to change
	int pointerX, pointerY;			// for the ^ used to show the current piece
	int page = 1;					// used for the bottom pics
	int MAX_PAGES = 1;

	int currentOrder = 3;

	boolean drawGrid = true, lowRes = EDITOR_RESOLUTION_X < 1280,
			settingPartner, settingStart;
	boolean mouseLockX = false, mouseLockY = false;

	private String currShape = "Box", inputValue, skyHex = "default",
			fileName = "Unnamed";
	private Shape selected, current = new Box(0, 0, 0, 0);

	private List<Shape> shapes = new ArrayList<Shape>(20);
	private List<Shape> bottomShapes = new ArrayList<Shape>(20);

	// CHANGE THIS to make it smaller if you think this is excessive.
	public Sky background = new Sky(-5000, -5000, 10000, 10000);

	UnicodeFont uniFont;

	public static Texture left, right, gright, gleft, icel, cliffi, cliffv,
			icev, deadi, deadi1, deadi2, deadi3, deadi4, deadv, deadv1, deadv2,
			deadv3, deadv4, coini, door, doorv, gravflip, gravflip2, gravflip3,
			gravflip4, gravflip5, gravflip6, gravflip7, gravflip8, gravflip9,
			gravflip10, gravflip11, gravflip12, gravflip13, gravflip14,
			gravflip15, gravflip16, gravflip17, gravflip18, Lon, Loff, brick,
			brickv, wallpaper;
	public static Texture cloud1, cloud2, cloud3, cloud4, cloud5, cloud6,
			cloud7, cloud8, cloud9, cloud10, lboxi, doorjam, doorjamv, woodi,
			ledgei, ropei, hangi, hangv, wheeli, wheeli2, sky1, sky2, sky3,
			sky4, sky5, sky6, a1, a2, a3, a4, a5, esc, space, words, words2,
			words3, words4, words5, words6, words7, words8, words9, words10,
			words11, words12, words13, words14, words15, words16, words17,
			words18, words19, words20, words21, words22;
	public static Texture p, pr, pre, pres, press, news;

	public static Texture cliffdesert, cliffdesert2, desertbush, cactus,
			desertplatform, desertplatform1, desertplatform2, desertplatform3,
			desertplatform4, desertplatform5, desertplatform6, desertplatform7,
			desertplatform8, desertplatform9, desertplatform10,
			desertplatform11, desertback;

	public LevelEditor()
	{
		initGL();
		adjustResolution();
		initFonts();
		initKeys();
		initTextures();

		assignPic(current);
		drawShapes();

		while (!Display.isCloseRequested())
		{
			glClear(GL_COLOR_BUFFER_BIT);	// wipe the screen

			mouse();

			mouseInput();
			input();
			render();
			drawText();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);
	}

	public void adjustResolution()
	{
		// CHANGE THIS if you find problems with lowRes mode
		if (lowRes)
		{
			FONT_SIZE = 18;
			GRID_SIZE = 40;
			TOP += 30;
			BOTTOM += 30;
			LEFT += 50;
			RIGHT += 50;
			//	picsW = 35;
			//	picsH = 35;
			//	picsY -= 15;
			FUDGE_X = 242;				// don't worry about this
			FUDGE_Y = 21;				// or this		
		}
	}

	// for selecting a shape
	public Shape getShape()
	{
		Shape ans = null;
		if (mouseY + transY >= TOP && mouseY + transY <= BOTTOM)
		{
			for (Shape shape : shapes)
			{
				if (mouseX >= shape.getX()
						&& (mouseX <= shape.getX() + shape.getWidth())
						&& mouseY >= shape.getY()
						&& (mouseY <= shape.getY() + shape.getHeight())
						&& !shape.name.equals("Sky"))
				{
					ans = shape;
					if (!settingPartner && !settingStart)
						shape.selected = true;
				} else
				{
					shape.selected = false;
				}
			}
		}
		if (mouseY + transY >= BOTTOM)
		{
			for (Shape shape : bottomShapes)
			{
				if (mouseX + transX >= shape.getX()
						&& (mouseX + transX <= shape.getX() + shape.getWidth())
						&& mouseY + transY >= shape.getY()
						&& (mouseY + transY <= shape.getY() + shape.getHeight())
						&& page == shape.editorPage)
				{
					currShape = shape.name;
					current = getCurrShape();
					if (current.name.equals("Cloud"))
						current.type = 1;
					assignPic(current);
					ans = current;
					pointerX = (int) (shape.getX() + (shape.getWidth() - FONT_SIZE) / 2) + 5;
					pointerY = (int) (shape.getY() + (shape.getHeight() + FONT_SIZE) / 2) + 5;

				}
			}
		}
		return ans;
	}

	private void render()
	{
		background.editorDraw();

		current.setPosition(mouseX, mouseY);
		current.setWidth(width);
		current.setHeight(height);
		current.displayOrder = currentOrder;
		assignPic(current);

		// draw the game box
		glBegin(GL_LINE_LOOP);
		glVertex2f(LEFT, TOP);
		glVertex2f(RIGHT, TOP);
		glVertex2f(RIGHT, BOTTOM);
		glVertex2f(LEFT, BOTTOM);
		glEnd();

		translate();

		// this is where the level itself goes

		for (int i = 1; i <= 4; i++)
			for (Shape shape : shapes)
				if (shape.displayOrder == i)
					shape.editorDraw();

		current.editorDraw();

		// end level

		drawBoundary();
		glPopMatrix();

		for (Shape shape : bottomShapes)
			if (page == shape.editorPage)
				shape.editorDraw();

		//	drawButtons(); 

		if (drawGrid)
			drawGrid();

	}

	public static void assignPic(Shape temp)
	{
		// this is where the heavy lifting is done to
		// determine which picture to show

		if (temp.name.equals("Arrow"))
		{
			if (temp.i < 10)
				temp.setPic(a1);
			else if (temp.i >= 10 && temp.i < 20)
				temp.setPic(a2);
			else
				temp.i = 0;
		}

		if (temp.name.equals("ArrowKey"))
		{
			if (temp.type == 0)
				temp.setPic(a3);
			else if (temp.type == 1)
				temp.setPic(a4);
			else if (temp.type == 2)
				temp.setPic(a5);
			else if (temp.type == 3)
				temp.setPic(esc);
			else if (temp.type == 4)
				temp.setPic(space);
		}

		if (temp.name.equals("Bat"))
		{
			if (temp.vert)
				temp.setPic(cliffv);
			else if (temp.type == 1)
				temp.setPic(cliffdesert);
			else if (temp.type == 2)
				temp.setPic(cliffdesert2);
			else
				temp.setPic(cliffi);
		}

		if (temp.name.equals("Box"))
			temp.setPic(right);

		if (temp.name.equals("Brick"))
		{
			if (temp.vert)
				temp.setPic(brickv);
			else
				temp.setPic(brick);
		}
		if (temp.name.equals("Bush"))
			temp.setPic(desertbush);

		if (temp.name.equals("Cactus"))
			temp.setPic(cactus);

		if (temp.name.equals("Cloud"))
		{
			if (temp.type == 1)
				temp.setPic(cloud1);
			else if (temp.type == 2)
				temp.setPic(cloud2);
			else if (temp.type == 3)
				temp.setPic(cloud3);
			else if (temp.type == 4)
				temp.setPic(cloud4);
			else if (temp.type == 5)
				temp.setPic(cloud5);
			else if (temp.type == 6)
				temp.setPic(cloud6);
			else if (temp.type == 7)
				temp.setPic(cloud7);
			else if (temp.type == 8)
				temp.setPic(cloud8);
			else if (temp.type == 9)
				temp.setPic(cloud9);
			else if (temp.type == 10)
				temp.setPic(cloud10);
		}

		if (temp.name.equals("Coin"))
			temp.setPic(coini);

		if (temp.name.equals("Dead"))
		{
			if (temp.vert)
			{
				if (temp.i <= 10)
					temp.setPic(deadv);
				else if (temp.i <= 20)
					temp.setPic(deadv1);
				else if (temp.i <= 30)
					temp.setPic(deadv2);
				else if (temp.i <= 40)
					temp.setPic(deadv3);
				else if (temp.i <= 50)
					temp.setPic(deadv4);
				if (temp.i == 50)
					temp.i = 0;
			} else
			{
				if (temp.i <= 10)
					temp.setPic(deadi);
				else if (temp.i <= 20)
					temp.setPic(deadi1);
				else if (temp.i <= 30)
					temp.setPic(deadi2);
				else if (temp.i <= 40)
					temp.setPic(deadi3);
				else if (temp.i <= 50)
					temp.setPic(deadi4);
				if (temp.i == 50)
					temp.i = 0;
			}
		}

		if (temp.name.equals("Doorjam"))
		{
			if (!temp.vert)
				temp.setPic(doorjam);
			else
				temp.setPic(doorjamv);
		}

		if (temp.name.equals("Gem"))
		{
			if (!temp.vert)
				temp.setPic(door);
			else
				temp.setPic(doorv);
		}

		if (temp.name.equals("Grav"))
		{
			temp.setPic(gravflip);
		}

		if (temp.name.equals("Hang"))
		{
			if (!temp.vert)
				temp.setPic(hangi);
			else
				temp.setPic(hangv);
		}

		if (temp.name.equals("Hills"))
		{
			temp.setPic(desertback);
		}

		if (temp.name.equals("Ice"))
		{
			if (temp.vert)
				temp.setPic(icev);
			else
				temp.setPic(icel);
		}

		if (temp.name.equals("Lbox"))
			temp.setPic(lboxi);

		if (temp.name.equals("Ledge"))
			temp.setPic(ledgei);

		if (temp.name.equals("Loff"))
		{
			if (!temp.on)
				temp.setPic(Loff);
			else
				temp.setPic(Lon);
		}

		if (temp.name.equals("News"))
			temp.setPic(news);

		if (temp.name.equals("Platform"))
		{
			temp.setPic(desertplatform);
		}
		if (temp.name.equals("Rope"))
			temp.setPic(ropei);

		if (temp.name.equals("Skyline"))
		{
			if (temp.type == 0)
				temp.setPic(sky1);
			else if (temp.type == 1)
				temp.setPic(sky2);
			else if (temp.type == 2)
				temp.setPic(sky3);
			else if (temp.type == 3)
				temp.setPic(sky4);
			else if (temp.type == 4)
				temp.setPic(sky5);
			else if (temp.type == 5)
				temp.setPic(sky6);
		}

		if (temp.name.equals("Text"))
		{
			if (temp.type == 0)
				temp.setPic(words);
			else if (temp.type == 1)
				temp.setPic(words2);
			else if (temp.type == 2)
				temp.setPic(words3);
			else if (temp.type == 3)
				temp.setPic(words4);
			else if (temp.type == 4)
				temp.setPic(words5);
			else if (temp.type == 5)
				temp.setPic(words6);
			else if (temp.type == 6)
				temp.setPic(words7);
			else if (temp.type == 7)
				temp.setPic(words8);
			else if (temp.type == 8)
				temp.setPic(words9);
			else if (temp.type == 9)
				temp.setPic(words10);
			else if (temp.type == 10)
				temp.setPic(words11);
			else if (temp.type == 11)
				temp.setPic(words12);
			else if (temp.type == 12)
				temp.setPic(words13);
			else if (temp.type == 13)
				temp.setPic(words14);
			else if (temp.type == 14)
				temp.setPic(words15);
			else if (temp.type == 15)
				temp.setPic(words16);
			else if (temp.type == 16)
				temp.setPic(words17);
			else if (temp.type == 17)
				temp.setPic(words18);
			else if (temp.type == 18)
				temp.setPic(words19);
			else if (temp.type == 19)
				temp.setPic(words20);
			else if (temp.type == 20)
				temp.setPic(words21);
			else if (temp.type == 21)
				temp.setPic(words22);
		}

		if (temp.name.equals("Wall"))
			temp.setPic(wallpaper);

		if (temp.name.equals("Wheel"))
		{
			if (temp.on)
				temp.setPic(wheeli);
			else
				temp.setPic(wheeli2);
		}
	}

	public Shape getCurrShape()
	{
		Shape temp = new Box(0, 0, 0, 0);

		if (currShape == "Arrow")
			temp = new Arrow(mouseX, mouseY, width, height);
		if (currShape == "ArrowKey")
			temp = new ArrowKey(mouseX, mouseY, width, height);
		if (currShape == "Bat")
			temp = new Bat(mouseX, mouseY, width, height);
		if (currShape == "Box")
			temp = new Box(mouseX, mouseY, width, height);
		if (currShape == "Brick")
			temp = new Brick(mouseX, mouseY, width, height);
		if (currShape == "Bush")
			temp = new Bush(mouseX, mouseY, width, height);
		if (currShape == "Cactus")
			temp = new Cactus(mouseX, mouseY, width, height);
		if (currShape == "Cloud")
			temp = new Cloud(mouseX, mouseY, width, height);
		if (currShape == "Coin")
			temp = new Coin(mouseX, mouseY, width, height);
		if (currShape == "Dead")
			temp = new Dead(mouseX, mouseY, width, height);
		if (currShape == "Doorjam")
			temp = new Doorjam(mouseX, mouseY, width, height);
		if (currShape == "Gem")
			temp = new Gem(mouseX, mouseY, width, height);
		if (currShape == "Grav")
			temp = new Grav(mouseX, mouseY, width, height);
		if (currShape == "Hang")
			temp = new Hang(mouseX, mouseY, width, height);
		if (currShape == "Hills")
			temp = new Hills(mouseX, mouseY, width, height);
		if (currShape == "Ice")
			temp = new Ice(mouseX, mouseY, width, height);
		if (currShape == "Lbox")
			temp = new Lbox(mouseX, mouseY, width, height);
		if (currShape == "Ledge")
			temp = new Ledge(mouseX, mouseY, width, height);
		if (currShape == "Loff")
			temp = new Loff(mouseX, mouseY, width, height);
		if (currShape == "News")
			temp = new News(mouseX, mouseY, width, height);
		if (currShape == "Platform")
			temp = new Platform(mouseX, mouseY, width, height);
		if (currShape == "Rope")
			temp = new Rope(mouseX, mouseY, width, height);
		if (currShape == "Sky")
			temp = new Sky(mouseX, mouseY, width, height);
		if (currShape == "Skyline")
			temp = new Skyline(mouseX, mouseY, width, height);
		if (currShape == "Text")
			temp = new Text(mouseX, mouseY, width, height);
		if (currShape == "Wall")
			temp = new Wall(mouseX, mouseY, width, height);
		if (currShape == "Wheel")
			temp = new Wheel(mouseX, mouseY, width, height);

		if (temp.defaultWidth > 0 && (mouseY + transY > BOTTOM))
		{
			width = temp.defaultWidth;
			height = temp.defaultHeight;
		}

		return temp;
	}

	public void drawShapes()
	{
		Shape temp = new Arrow(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new ArrowKey(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Bat(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Box(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Brick(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Bush(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Cactus(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Cloud(picsX, picsY, picsW, picsH);
		temp.type = 1;
		assignAndMove(temp);
		temp = new Coin(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Dead(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Doorjam(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Gem(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Grav(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Hang(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Hills(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Ice(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Lbox(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Ledge(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Loff(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new News(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Platform(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Rope(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Skyline(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Text(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Wall(picsX, picsY, picsW, picsH);
		assignAndMove(temp);
		temp = new Wheel(picsX, picsY, picsW, picsH);
		assignAndMove(temp);

		page = 1;
	}

	public void assignAndMove(Shape temp)
	{
		assignPic(temp);
		bottomShapes.add(temp);
		temp.editorPage = page;

		picsX += 2 * picsW;
		if (picsX + 2 * picsW >= EDITOR_RESOLUTION_X
				&& picsY < EDITOR_RESOLUTION_Y)
		{
			picsX = 50;
			if (lowRes)
				picsY += picsH + 15;
			else
				picsY += picsH + 50;

			if (picsY + picsH >= EDITOR_RESOLUTION_Y)
			{
				if (lowRes)
					picsY = BOTTOM + 15;
				else
					picsY = BOTTOM + 50;
				page++;
				MAX_PAGES++;
			}
		}
	}

	private boolean mouseIn(int left, int right, int top, int bottom)
	{
		return mouseX + transX >= left && mouseX + transX <= right
				&& mouseY + transY > top && mouseY + transY < bottom;
	}

	private void mouseInput()
	{
		if (Mouse.isButtonDown(0) && mouseIn(LEFT, RIGHT, TOP, BOTTOM))
		{
			if (!settingPartner && !settingStart)
			{
				shapes.add(current);

				current = getCurrShape();
				if (current.name.equals("Cloud"))
					current.type = 1;
				assignPic(current);
			} else if (settingPartner)
			{
				selected.partner = getShape();
				if (selected.partner != null)
				{
					selected.partnerX = selected.partner.x;
					selected.partnerY = selected.partner.y;
				}
				settingPartner = false;
				selected.selected = true;	//lololol	
			} else if (settingStart)
			{
				startX = mouseX;
				startY = mouseY;

				settingStart = false;
			}
			fixMouse();
		}

		// clicking the buttons. this is ugly because it can easily be screwed
		// up if the interface shifts around, but at least it's very easy to change
		// ----Start left side buttons

		// CHANGE THIS if you have problems clicking the buttons. Although, you really shouldn't.

		//-- Width and height
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT, TOP + 3 * FONT_SIZE, TOP + 4 * FONT_SIZE))
		{
			inputValue = JOptionPane
					.showInputDialog("Enter a positive integer width.");
			if (inputValue != null && !inputValue.equals(""))
				width = Integer.parseInt(inputValue);
			fixMouse();
		}
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT, TOP + 4 * FONT_SIZE, TOP + 5 * FONT_SIZE))
		{
			inputValue = JOptionPane
					.showInputDialog("Enter a positive integer height.");
			if (inputValue != null && !inputValue.equals(""))
				height = Integer.parseInt(inputValue);
			fixMouse();
		}

		// --- Ints
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, 50, TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE))
			buttonCode = 1;			// i
		if (Mouse.isButtonDown(0)
				&& mouseIn(75, 175, TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE))
			buttonCode = 2;			// type
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, 200, TOP + 9 * FONT_SIZE, TOP + 10 * FONT_SIZE))
		{
			inputValue = JOptionPane
					.showInputDialog("Enter a (double) movement speed. Positive = right, Negative = left.");
			if (inputValue != null && !inputValue.equals(""))
				current.moveSpeed = Double.parseDouble(inputValue);
			fixMouse();
		}

		// --- Booleans

		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 70, TOP + 10 * FONT_SIZE + 5, TOP + 11
						* FONT_SIZE + 5))
		{
			current.moving = !current.moving;
			fixMouse();
		}

		if (Mouse.isButtonDown(0)
				&& mouseIn(0, 125, TOP + 11 * FONT_SIZE + 5, TOP + 12
						* FONT_SIZE + 5))
		{
			current.vert = !current.vert;
			fixMouse();
		}
		if (Mouse.isButtonDown(0)
				&& mouseIn(135, 245, TOP + 11 * FONT_SIZE + 5, TOP + 12
						* FONT_SIZE + 5))
		{
			current.on = !current.on;
			fixMouse();
		}
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 20, TOP + 14 * FONT_SIZE + 5, TOP + 15
						* FONT_SIZE + 5))
		{
			setSky();
			fixMouse();
		}
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 50, TOP + 16 * FONT_SIZE + 5, TOP + 17
						* FONT_SIZE + 5))
		{
			JOptionPane.showMessageDialog(null, "Click to set start position");
			settingStart = true;
			fixMouse();
		}
		if (Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 50, TOP + 17 * FONT_SIZE + 5, TOP + 18
						* FONT_SIZE + 5))
		{
			inputValue = JOptionPane
					.showInputDialog("Draw order? 1 = Background,"
							+ "2 = Scenery, 3 = Foreground, 4 = In front of bossgreed");
			currentOrder = Integer.parseInt(inputValue);
			fixMouse();
		}

		//-----End left side buttons

		//----Right side buttons
		if (Mouse.isButtonDown(0)
				&& mouseIn(RIGHT, EDITOR_RESOLUTION_X, TOP + 3 * FONT_SIZE, TOP
						+ 4 * FONT_SIZE))
		{
			settingPartner = true;
		}

		if (Mouse.isButtonDown(0)
				&& selected != null
				&& mouseIn(RIGHT + 5, EDITOR_RESOLUTION_X, TOP + 7 * FONT_SIZE,
						TOP + 10 * FONT_SIZE))
		{
			if (selected.partner != null)
			{
				if (mouseIn(RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 7 * FONT_SIZE, TOP + 8 * FONT_SIZE))
				{
					selected.partner.action = 1;
					selected.partner.visible = false;
				}
				if (mouseIn(RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE))
					selected.partner.action = 2;
				if (mouseIn(RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 9 * FONT_SIZE, TOP + 10 * FONT_SIZE))
				{
					movementStuff();
				}
			}
		}
		//-----End right side buttons

		if (Mouse.isButtonDown(0) && mouseY + transY >= BOTTOM)
		{
			Shape temp = getShape();
			if (temp != null)
				current = temp;
		}

		if (Mouse.isButtonDown(1) && mouseY + transY <= BOTTOM
				&& mouseY + transY >= TOP)
		{
			if (settingPartner)
				settingPartner = false;
			selected = getShape();
		}
	}

	private void input()
	{
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard
					.isKeyDown(Keyboard.KEY_RMETA))
				&& Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			fixKeyboard();
			save(shapes);
		} else if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard
					.isKeyDown(Keyboard.KEY_RMETA))
				&& Keyboard.isKeyDown(Keyboard.KEY_O))
		{
			fixKeyboard();
			load(shapes);
		} else
		{
			// CHANGE THIS if you want to change the button mapping,
			// or just add new functionality

			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				transY += CAMERA_SCROLL_SPEED;
			if (Keyboard.isKeyDown(Keyboard.KEY_S))
				transY -= CAMERA_SCROLL_SPEED;
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				transX += CAMERA_SCROLL_SPEED;
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				transX -= CAMERA_SCROLL_SPEED;

			if (Keyboard.isKeyDown(Keyboard.KEY_I) && (height - THICKNESS) >= 1)
				height -= THICKNESS;
			if (Keyboard.isKeyDown(Keyboard.KEY_K))
				height += THICKNESS;
			if (Keyboard.isKeyDown(Keyboard.KEY_L))
				width += THICKNESS;
			if (Keyboard.isKeyDown(Keyboard.KEY_J) && (width - THICKNESS) >= 1)
				width -= THICKNESS;

			if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)
					&& GRID_SIZE > MIN_GRID_SIZE)
				GRID_SIZE--;
			if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)
					&& GRID_SIZE < MAX_GRID_SIZE)
				GRID_SIZE++;

			if (Keyboard.isKeyDown(Keyboard.KEY_T))
			{
				drawGrid = !drawGrid;
				fixKeyboard();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_RBRACKET))
			{
				if (buttonCode == 1)
					current.i++;
				if (buttonCode == 2 && current.type < MAX_TYPE_SIZE)
					current.type++;

				fixKeyboard();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LBRACKET))
			{
				if (buttonCode == 1 && current.i > 0)
					current.i--;
				if (buttonCode == 2 && current.type > MIN_TYPE_SIZE)
					current.type--;

				fixKeyboard();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)
					|| Keyboard.isKeyDown(Keyboard.KEY_BACK))
				delete();

			if (Keyboard.isKeyDown(Keyboard.KEY_M) && selected != null)
			{
				current = selected;
				current.selected = false;
				currShape = current.name;
				selected = null;
				width = (int) current.getWidth();
				height = (int) current.getHeight();
			}

			mouseLockX = false;
			mouseLockY = false;

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				mouseLockX = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			{
				mouseLockY = true;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_MINUS) && page > 1)
			{
				page--;
				fixKeyboard();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS) && page < MAX_PAGES)
			{
				page++;
				fixKeyboard();
			}
		}
	}

	private void setSky()
	{
		// Sky stuff
		skyHex = JOptionPane.showInputDialog("Enter the six digit hex color");
		if (skyHex != null && !skyHex.equals(""))
		{
			r = Integer.parseInt(skyHex.substring(0, 2), 16) / 255.;
			g = Integer.parseInt(skyHex.substring(2, 4), 16) / 255.;
			b = Integer.parseInt(skyHex.substring(4, 6), 16) / 255.;

			background.setRGB(r, g, b);
		}
	}

	public void fixMouse()
	{
		Mouse.destroy();
		try
		{
			Mouse.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	public void fixKeyboard()
	{
		Keyboard.destroy();
		try
		{
			Keyboard.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	private void mouse()
	{
		// Retrieve the "true" coordinates of the mouse.
		if (!mouseLockX)
			mouseX = Mouse.getX() - transX;
		if (!mouseLockY)
			mouseY = EDITOR_RESOLUTION_Y - Mouse.getY() - 1 - transY;
	}

	private void movementStuff()
	{
		selected.partner.upDown = true;
		selected.partner.action = 3;
		selected.partner.downRight = true;

		inputValue = JOptionPane
				.showInputDialog("Move which way? (1 - Up/Down, else - Left/Right)");
		if (inputValue != null && !inputValue.equals(""))
		{
			if (!inputValue.equals("1"))
				selected.partner.upDown = false;
			if (selected.partner.upDown)
			{
				inputValue = JOptionPane
						.showInputDialog("Current bottom of the piece: "
								+ (selected.partner.y + selected.partner.height - FUDGE_Y)
								+ ", enter end Y.");
				if (inputValue != null && !inputValue.equals(""))
				{
					selected.partner.endPos = Double.parseDouble(inputValue)
							+ FUDGE_Y - selected.partner.height;
					selected.partner.startPos = selected.partner.y;
				}
			}
			if (!selected.partner.upDown)
			{
				inputValue = JOptionPane
						.showInputDialog("Current right side of the piece: "
								+ (selected.partner.x + selected.partner.width - FUDGE_X)
								+ ", enter end X.");
				if (inputValue != null && !inputValue.equals(""))
				{
					selected.partner.endPos = Double.parseDouble(inputValue)
							+ FUDGE_X - selected.partner.width;
					selected.partner.startPos = selected.partner.x;
				}
			}
			inputValue = JOptionPane.showInputDialog("Choose a (double) speed");
			if (inputValue != null && !inputValue.equals(""))
				selected.partner.moveSpeed = Double.parseDouble(inputValue);

			if (selected.partner.endPos < selected.partner.startPos)
			{
				// switch them
				double temp = selected.partner.endPos;
				double temp2 = selected.partner.startPos;
				selected.partner.endPos = temp2;
				selected.partner.startPos = temp;
				System.out.println("SWITCH");
				selected.partner.downRight = false;
			}
		}
	}

	public void delete()
	{
		// for some reason you have to do it like this,
		// you can't just shapes.remove(selected)
		Shape temp = new Box(0, 0, 0, 0);
		for (Shape shape : shapes)
			if (shape.selected)
				temp = shape;

		if (temp.selected)
			shapes.remove(temp);

		selected = null;
	}

	private void drawBoundary()
	{
		// this draws a black frame around the screen size to create the 'window-in-window' illusion
		glColor4f(0f, 0f, 0f, 1f);
		glRectd(-transX, -transY - CAMERA_SCROLL_SPEED, EDITOR_RESOLUTION_X
				- transX, TOP - 1 - transY);	// TOP
		glRectd(-transX - CAMERA_SCROLL_SPEED, -transY, LEFT - 1 - transX,
				EDITOR_RESOLUTION_Y - transY);	// left
		glRectd(-transX, BOTTOM + 1 - transY, EDITOR_RESOLUTION_X - transX,
				EDITOR_RESOLUTION_Y - transY + CAMERA_SCROLL_SPEED);	// BOTTOM
		glRectd(RIGHT + 1 - transX, -transY, EDITOR_RESOLUTION_X - transX
				+ CAMERA_SCROLL_SPEED, EDITOR_RESOLUTION_Y - transY);	// RIGHT
	}

	private void drawGrid()
	{
		// draw the grid
		glBegin(GL_LINES);

		glColor4f(1.0f, 1.0f, 1.0f, .75f);
		for (int i = 0; TOP + i < BOTTOM; i += GRID_SIZE)
		{
			glVertex2f(LEFT, TOP + i);
			glVertex2f(RIGHT, TOP + i);
		}
		for (int i = 0; LEFT + i < RIGHT; i += GRID_SIZE)
		{
			glVertex2f(LEFT + i, TOP);
			glVertex2f(LEFT + i, BOTTOM);
		}
		glDisable(GL_BLEND);
		glEnd();
	}

	private void drawText()
	{
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// CHANGE THIS if you want to add more text/debug stuff to the screen

		//---LEFT SIDE----

		uniFont.drawString(5, TOP, "Mouse: " + (mouseX + transX) + ","
				+ (mouseY + transY));
		uniFont.drawString(5, TOP + FONT_SIZE, "Game x,y: "
				+ (mouseX - LEFT - 1) + "," + (mouseY - TOP - 2));
		uniFont.drawString(5, TOP + 2 * FONT_SIZE, "CurrShape: " + currShape);
		uniFont.drawString(5, TOP + 3 * FONT_SIZE, "Width: " + width);
		uniFont.drawString(5, TOP + 4 * FONT_SIZE, "Height: " + height);
		uniFont.drawString(5, TOP + 5 * FONT_SIZE, "Grid size: " + GRID_SIZE);

		uniFont.drawString(5, TOP + 7 * FONT_SIZE, "---Instance variables---");

		String iString = "i: ", typeString = "type: ";

		if (buttonCode == 1)
			iString = "I= ";
		if (buttonCode == 2)
			typeString = "TYPE= ";

		uniFont.drawString(5, TOP + 8 * FONT_SIZE, iString + current.i
				+ "     " + typeString + current.type);
		uniFont.drawString(5, TOP + 9 * FONT_SIZE, "moveSpeed: "
				+ current.moveSpeed);
		uniFont.drawString(5, TOP + 10 * FONT_SIZE, "moving: " + current.moving);

		uniFont.drawString(5, TOP + 11 * FONT_SIZE, "vert: " + current.vert
				+ "   on: " + current.on);

		uniFont.drawString(5, TOP + 12 * FONT_SIZE, "---------------------");
		uniFont.drawString(5, TOP + 14 * FONT_SIZE, "Sky hex: 0x" + skyHex);
		uniFont.drawString(5, TOP + 15 * FONT_SIZE, "File name: " + fileName);
		uniFont.drawString(5, TOP + 16 * FONT_SIZE, "Start x,y: "
				+ (startX - FUDGE_X) + "," + (startY - FUDGE_Y));
		uniFont.drawString(5, TOP + 17 * FONT_SIZE, "Draw Order (1-4): "
				+ currentOrder);

		//---END LEFT SIDE

		//---RIGHT SIDE

		if (selected != null)
		{
			uniFont.drawString(RIGHT + 5, TOP, "Selected:" + selected.name);
			uniFont.drawString(RIGHT + 5, TOP + FONT_SIZE, "x,y pos: "
					+ (selected.x - FUDGE_X) + "," + (selected.y - FUDGE_Y));

			if (!settingPartner)
				uniFont.drawString(RIGHT + 5, TOP + 3 * FONT_SIZE,
						"*SET PARTNER*");
			else
				uniFont.drawString(RIGHT + 5, TOP + 3 * FONT_SIZE,
						"Click on partner");
			if (selected.partner == null)
				uniFont.drawString(RIGHT + 5, TOP + 4 * FONT_SIZE, "No partner");
			else
			{
				uniFont.drawString(RIGHT + 5, TOP + 4 * FONT_SIZE,
						"Has partner");
				uniFont.drawString(RIGHT + 5, TOP + 6 * FONT_SIZE, "Action:");
				uniFont.drawString(RIGHT + 5, TOP + 7 * FONT_SIZE,
						"        Appear");
				uniFont.drawString(RIGHT + 5, TOP + 8 * FONT_SIZE,
						"        Disappear");
				uniFont.drawString(RIGHT + 5, TOP + 9 * FONT_SIZE,
						"        Start moving");

				if (selected.partner.action > 0)
					uniFont.drawString(RIGHT + 5, TOP
							+ (selected.partner.action + 6) * FONT_SIZE,
							"  -->");
			}
		}

		if (pointerX > 0)
			uniFont.drawString(pointerX, pointerY, "^");

		// uniFont.drawString(55, 10, "Button!");
		// more text here

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	public void load(List<Shape> shapes)
	{
		fileName = JOptionPane
				.showInputDialog("Enter the filename to load please: ");
		if (fileName != null && !fileName.equals(""))
		{
			try
			{
				shapes.clear();
				ObjectInputStream IS = new ObjectInputStream(
						new FileInputStream(fileName));

				int size = IS.readInt();

				r = IS.readDouble();
				g = IS.readDouble();
				b = IS.readDouble();

				background.setRGB(r, g, b);
				//	shapes.add(background);

				for (int i = 0; i < size; i++)
				{
					int code = IS.readInt();
					Shape temp = Shape.load(IS, code);
					assignPic(temp);
					shapes.add(temp);
				}
				startX = IS.readFloat();
				startY = IS.readFloat();

				transX = GAME_RESOLUTION_X / 2;
				transY = GAME_RESOLUTION_Y / 2;

				IS.close();

				for (Shape shape : shapes)
					if (shape.partnerX != 0 && shape.partnerY != 0)
						for (Shape shaper : shapes)
							if (shape.partnerX == shaper.x
									&& shape.partnerY == shaper.y)
								shape.partner = shaper;

				System.out.println("Loaded!");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void save(List<Shape> shapes)
	{
		fileName = JOptionPane
				.showInputDialog("Enter the desired filename please: ");
		if (fileName != null && !fileName.equals(""))
		{
			try
			{
				ObjectOutputStream OS = new ObjectOutputStream(
						new FileOutputStream("levels/" + fileName));
				OS.writeInt(shapes.size());

				OS.writeDouble(r);
				OS.writeDouble(g);
				OS.writeDouble(b);

				for (Shape shape : shapes)
				{
					Shape.save(OS, shape);
				}

				OS.writeFloat(startX);
				OS.writeFloat(startY);

				OS.close();
				System.out.println("Saved!");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void translate()
	{
		glPushMatrix();
		glTranslatef(transX, 0, 0);
		glTranslatef(0, transY, 0);
	}

	private void initGL()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(EDITOR_RESOLUTION_X,
					EDITOR_RESOLUTION_Y));
			Display.setTitle("Level Editor");
			Display.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, EDITOR_RESOLUTION_X, EDITOR_RESOLUTION_Y, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@SuppressWarnings("unchecked")
	private void initFonts()
	{

		Font awtFont = new Font("", Font.PLAIN, FONT_SIZE);

		uniFont = new UnicodeFont(awtFont, FONT_SIZE, false, false);
		uniFont.addAsciiGlyphs();
		uniFont.addGlyphs(400, 600);           // Setting the unicode Range
		uniFont.getEffects().add(new ColorEffect(java.awt.Color.white));
		try
		{
			uniFont.loadGlyphs();
		}
		catch (SlickException e)
		{
		}
		;
	}

	private void initKeys()
	{
		// set the default control scheme
	}

	public void initTextures()
	{
		right = loadTexture("bagi1");

		sky1 = loadTexture("skyline1");
		sky2 = loadTexture("skyline2");
		sky3 = loadTexture("skyline3");
		sky4 = loadTexture("skyline4");
		sky5 = loadTexture("skyline5");
		sky6 = loadTexture("skyline6");
		icel = loadTexture("air");
		cliffi = loadTexture("cliff");
		cliffv = loadTexture("cliff2");
		icev = loadTexture("air1");
		deadi = loadTexture("dead");
		coini = loadTexture("coin2");
		gravflip = loadTexture("gravflip1");
		gravflip2 = loadTexture("gravflip2");
		gravflip3 = loadTexture("gravflip3");
		gravflip4 = loadTexture("gravflip4");
		gravflip5 = loadTexture("gravflip5");
		gravflip6 = loadTexture("gravflip6");
		gravflip7 = loadTexture("gravflip7");
		gravflip8 = loadTexture("gravflip8");
		gravflip9 = loadTexture("gravflip9");
		gravflip10 = loadTexture("gravflip10");
		gravflip11 = loadTexture("gravflip11");
		gravflip12 = loadTexture("gravflip12");
		gravflip13 = loadTexture("gravflip13");
		gravflip14 = loadTexture("gravflip14");
		gravflip15 = loadTexture("gravflip15");
		gravflip16 = loadTexture("gravflip16");
		gravflip17 = loadTexture("gravflip17");
		gravflip18 = loadTexture("gravflip18");
		Lon = loadTexture("lighton");
		Loff = loadTexture("lightoff");
		//ski = loadTexture("sky");
		cloud1 = loadTexture("cloud1");
		cloud2 = loadTexture("cloud2");
		cloud3 = loadTexture("cloud3");
		cloud4 = loadTexture("cloud4");
		cloud5 = loadTexture("cloud5");
		cloud6 = loadTexture("cloud6");
		cloud7 = loadTexture("cloud7");
		cloud8 = loadTexture("cloud8");
		cloud9 = loadTexture("cloud9");
		cloud10 = loadTexture("cloud10");
		lboxi = loadTexture("lbox");
		doorjam = loadTexture("doorjam");
		woodi = loadTexture("wood");
		brick = loadTexture("brick");
		brickv = loadTexture("brickv");
		a1 = loadTexture("arrow");
		a2 = loadTexture("arrow1");
		words = loadTexture("words");
		words2 = loadTexture("wordsLevel");
		words3 = loadTexture("wordsAbout");
		words4 = loadTexture("wordsExit");
		words5 = loadTexture("wordsControls");
		words6 = loadTexture("wordsMove");
		words7 = loadTexture("wordsJump");
		words8 = loadTexture("wordsMainMenu");
		words9 = loadTexture("level1");
		words10 = loadTexture("level2");
		words11 = loadTexture("wordsGameOver");
		words12 = loadTexture("wordsRestart");
		words13 = loadTexture("wordsYouWon");
		words14 = loadTexture("wordsWelcome");
		words15 = loadTexture("wordsIntroLevel");
		words16 = loadTexture("wordsto");
		words17 = loadTexture("wordsAvoidFire");
		words18 = loadTexture("wordsBossGreed");
		words19 = loadTexture("wordsSlidesonIce");
		words20 = loadTexture("wordsGravityFlipper");
		words21 = loadTexture("wordsCoinsAddWeightto");
		words22 = loadTexture("wordsAvoidspike");
		wallpaper = loadTexture("bgwallpaper3");
		p = loadTexture("wordsP");
		pr = loadTexture("wordsPr");
		pre = loadTexture("wordsPre");
		pres = loadTexture("wordsPres");
		press = loadTexture("wordsPress");
		news = loadTexture("news");

		a3 = loadTexture("arrowup");
		a4 = loadTexture("arrowleft");
		a5 = loadTexture("arrowright");
		esc = loadTexture("esc");
		space = loadTexture("spacebar");

		deadi1 = loadTexture("deadi1");
		deadi2 = loadTexture("deadi2");
		deadi3 = loadTexture("deadi3");
		deadi4 = loadTexture("deadi4");
		deadv = loadTexture("deadv");
		deadv1 = loadTexture("deadv1");
		deadv2 = loadTexture("deadv2");
		deadv3 = loadTexture("deadv3");
		deadv4 = loadTexture("deadv4");
		door = loadTexture("door");
		doorv = loadTexture("doorv");
		ropei = loadTexture("rope");
		hangi = loadTexture("hang");
		doorjamv = loadTexture("doorjamv");
		hangv = loadTexture("hangv");
		ledgei = loadTexture("ledge");
		wheeli = loadTexture("wheel");
		wheeli2 = loadTexture("wheel1");

		cactus = loadTexture("cactus");
		desertbush = loadTexture("desertbush");
		desertback = loadTexture("deserthills1");
		desertplatform = loadTexture("desertplatform");

		desertplatform1 = loadTexture("desertplatform1");
		desertplatform2 = loadTexture("desertplatform2");
		desertplatform3 = loadTexture("desertplatform3");
		desertplatform4 = loadTexture("desertplatform4");
		desertplatform5 = loadTexture("desertplatform5");
		desertplatform6 = loadTexture("desertplatform6");
		desertplatform7 = loadTexture("desertplatform7");
		desertplatform8 = loadTexture("desertplatform8");
		desertplatform9 = loadTexture("desertplatform9");
		desertplatform10 = loadTexture("desertplatform10");
		desertplatform11 = loadTexture("desertplatform11");
		cliffdesert = loadTexture("cliffdesert");
		cliffdesert2 = loadTexture("cliffdesert2");

	}

	public static Texture loadTexture(String key)
	{
		try
		{
			return TextureLoader.getTexture("png", new FileInputStream(
					new File("res/img/" + key + ".png")));
		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		return null;

	}

	public static void main(String [] args)
	{
		new LevelEditor();
	}

}
