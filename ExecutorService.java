public class ExecutorService extends Thread {

	private int x = 390;
	private int y = 480;
	private int angle = -90;
	private int height = 11;

	private final Transfer trans;

	public ExecutorService(int x, int y, int angle, int height, Transfer trans) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.height = height;

		this.trans = trans;
	}

	private void makeFractalTree(int x, int y, int angle, int height) {

		if (height == 0) {
			trans.terminateExec();
			return;
		}

		int x2 = x + (int) (Math.cos(Math.toRadians(angle)) * height * 7);
		int y2 = y + (int) (Math.sin(Math.toRadians(angle)) * height * 7);

		Line l = new Line(x, y, x2, y2, height);
		trans.putLine(l);
		ExecutorService service = new ExecutorService(x2, y2, angle - 20, height - 1, this.trans);

		trans.attemptToSpawnThread(service);
		makeFractalTree(x2, y2, angle + 20, height - 1);
	}

	@Override
	public void run() {

		makeFractalTree(x, y, angle, height);

		// System.out.println("producer(" + height + " has finished");

	}
}
