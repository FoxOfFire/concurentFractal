import javax.swing.*;
import java.awt.*;

public class FractalTree extends Canvas {
	/* Variables with class-wide visibility */
	private final Transfer trans;
	private int linesDrawn = 0;

	private FractalTree(Transfer trans) {
		this.trans = trans;
	}

	@Override
	public void paint(Graphics g) {
		while (!trans.consumerDone()) {
			try {
				trans.nextLine().draw(g);
				linesDrawn++;
			} catch (InterruptedException ex) {
				System.err.println("interupted");
			}

		}
		System.out.println("linesDrawn:" + linesDrawn);
	}

	/* Code for main thread */
	public static void main(String args[]) {

		/* Parse args */

		/* Initialize graphical elements and EDT */
		int x = 1000;
		int y = 1000;
		int height = 16;

		Transfer trans = new Transfer(height);

		ExecutorService service = new ExecutorService(x / 2, y / 20, 90, height, trans);

		FractalTree tree = new FractalTree(trans);
		JFrame frame = new JFrame();
		frame.setSize(x, y);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(20, 20, 20));
		frame.add(tree);

		service.start();
		/* Log success as last step */
		System.out.println("Main has finished");
	}
}
