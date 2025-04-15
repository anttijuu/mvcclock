package org.anttijuustila.mvcclock.model;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.Flow.Subscriber;

public class ClockTickSource {

	private Timer timer;
	private SubmissionPublisher<Long> publisher;

	public void subscribe(final Subscriber<Long> subscriber) {
		if (publisher == null) {
			publisher = new SubmissionPublisher<Long>(ForkJoinPool.commonPool(), 10);
		}
		publisher.subscribe(subscriber);
		if (publisher.getNumberOfSubscribers() == 1) {
			start();
		}
	}

	private void start() {
		System.out.println("Starting the publishing timer");
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				publisher.offer(System.currentTimeMillis(), null);
				if (!publisher.hasSubscribers()) {
					stop();
				}
			}
		}, 1000, 1000);
	}

	private void stop() {
		System.out.println("Stopping the publishing timer");
		timer.cancel();
		timer = null;	
		publisher.close();
		publisher = null;
	}

}
