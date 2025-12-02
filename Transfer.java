
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Transfer {

	public final int capacity;
	public final int height;
	public final BlockingQueue<Line> queue;

	public AtomicInteger linesRemaining;
	public AtomicInteger producerActive;
	public AtomicInteger maxActiveProducers;

	public Transfer(int height) {
		this.height = height;
		this.capacity = 128;
		this.queue = new ArrayBlockingQueue<>(capacity);

		this.producerActive = new AtomicInteger(1);
		this.linesRemaining = new AtomicInteger((int) (Math.pow(2, height)) - 1);
		this.maxActiveProducers = new AtomicInteger(0);
	}

	public synchronized void terminateExec() {
		int active = producerActive.decrementAndGet();
		if (active == 0) {
			int lines = linesRemaining.get();

			System.out.println("Executor finished");
			System.out.println("Lines dropped:" + lines);
			System.out.println("max threads:" + maxActiveProducers.get());
		}
		notify();
	}

	public synchronized void putLine(Line l) {
		while (queue.size() == capacity) {
			try {
				wait();
			} catch (InterruptedException ex) {
			}

		}
		linesRemaining.decrementAndGet();
		try {
			queue.put(l);
		} catch (InterruptedException ex) {
			System.err.println("makeFractalTree was interupted");
		}
		notify();

	}

	public synchronized void attemptToSpawnThread(Thread service) {
		while (producerActive.get() >= capacity) {
			try {
				wait();
			} catch (InterruptedException ex) {
			}
		}
		int current = producerActive.incrementAndGet();
		if (maxActiveProducers.get() < current)
			maxActiveProducers.set(current);
		service.run();
		notify();
	}

	public synchronized boolean consumerDone() {
		boolean done = linesRemaining.get() == 0 && queue.isEmpty();
		if (done) {
			System.out.println("consumer has finished");
		}
		notify();
		return done;
	}

	public synchronized Line nextLine() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		Line l = queue.take();
		notify();
		return l;

	}
}
