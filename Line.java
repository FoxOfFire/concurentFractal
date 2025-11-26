import java.awt.Color;
import java.awt.Graphics;

public class Line {
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	public int height;
	public boolean drawn = false;

	public Line(int x1, int y1, int x2, int y2, int height) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.height = height;
	}

	private Color createColor(int height) {
		int moder = 3;
		int red = 3 - height % moder;
		int green = 3 - (height / moder) % moder;
		int blue = 3 - (height / (moder * moder)) % moder;
		red = red * 64 + 63;
		green = green * 64 + 63;
		blue = blue * 64 + 63;

		return new Color(red, green, blue);
	}

	public void draw(Graphics g) {
		if (drawn)
			return;

		g.setColor(createColor(height));
		g.drawLine(x1, y1, x2, y2);
		drawn = true;
	}
}
