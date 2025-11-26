import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FractalTree extends Canvas {
	/* Variables with class-wide visibility */
	private static boolean slowMode;

	private static final int capacity = 10;
	private static int lineCount = 0;
	private static final BlockingQueue<Line> queue = new ArrayBlockingQueue<>(capacity);
	private static boolean producerActive = true;

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

	/* Recursive function for calculating all drawcalls for the fractal tree */
	public static void makeFractalTree(int x, int y, int angle, int height) {

		if (slowMode) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		if (height == 0)
			return;

		int x2 = x + (int) (Math.cos(Math.toRadians(angle)) * height * 7);
		int y2 = y + (int) (Math.sin(Math.toRadians(angle)) * height * 7);
		try {

			Line l = new Line(x, y, x2, y2);
			FractalTree.queue.put(l);
		} catch (InterruptedException ex) {

			System.err.println("makeFractalTree was interupted");
		}

		makeFractalTree(x2, y2, angle - 20, height - 1);
		makeFractalTree(x2, y2, angle + 20, height - 1);
	}

	public void drawFractalSegment(Graphics g) {
		try {
			while (producerActive) {
				g.setColor(createColor(FractalTree.lineCount));
				Line l = FractalTree.queue.take();
				l.draw(g);
				FractalTree.lineCount += 1;

			}
		} catch (InterruptedException ex) {
		}
	}

	/* Code for EDT */
	/* Must only contain swing code (draw things on the screen) */
	/* Must not contain calculations (do not use math and compute libraries here) */
	/*
	 * No need to understand swing, a simple endless loop that draws lines is enough
	 */
	@Override
	public void paint(Graphics g) {
		drawFractalSegment(g);

		try {
			Thread.sleep(200);
		} catch (InterruptedException ie) {

			ie.printStackTrace();
		}
	}

	/* Code for main thread */
	public static void main(String args[]) {

		/* Parse args */
		slowMode = args.length != 0 && Boolean.parseBoolean(args[0]);

		/* Initialize graphical elements and EDT */
		Thread segmentProducer = new Thread(() -> {
			makeFractalTree(390, 480, -90, 11);
			producerActive = false;
			System.out.println("producer has finished");

		});
		Thread drawingConsumer = new Thread(() -> {
			FractalTree tree = new FractalTree();
			JFrame frame = new JFrame();
			frame.setSize(800, 600);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setBackground(new Color(20, 20, 20));
			frame.add(tree);

			System.out.println("consumer has finished");
		});
		segmentProducer.start();
		drawingConsumer.start();
		/* Log success as last step */
		System.out.println("Main has finished");
	}
}
