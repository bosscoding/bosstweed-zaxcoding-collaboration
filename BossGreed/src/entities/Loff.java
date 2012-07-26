package entities;

import game.GameOn;

public class Loff extends Shape
{		
	public Loff(double x, double y, double width, double height)
	{
		super(x, y, width, height);
		code = 16;
		name = "Loff";
		defaultWidth = 16;
		defaultHeight = 16;
	}

	@Override
	public void draw()
	{
		textureStart();
		
		if(!on)
			GameOn.Loff.bind();
		else
			GameOn.Lon.bind();
		
		textureVertices();
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}
	
	@Override
	public void interact(Box player) 
	{
		// nothing
	}

}
