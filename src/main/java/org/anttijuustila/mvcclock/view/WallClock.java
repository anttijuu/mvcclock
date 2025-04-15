package org.anttijuustila.mvcclock.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.anttijuustila.mvcclock.model.ClockTickSource;

public class WallClock extends JPanel implements Flow.Subscriber<Long> {

	private final TimeZone timeZone;
	private final ClockTickSource clock;
	private Subscription subscription;
	private final JLabel timeLabel = new JLabel("00:00:00");

	public WallClock(final ClockTickSource clock, final TimeZone timeZone) {
		super();
		setLayout( new BorderLayout());
		this.clock = clock;
		this.timeZone = timeZone;
		this.clock.subscribe(this);
		Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
		setBorder(BorderFactory.createTitledBorder(border, timeZone.getDisplayName(
			true, TimeZone.LONG, Locale.getDefault()
		), TitledBorder.CENTER, TitledBorder.TOP));
		add(timeLabel, BorderLayout.WEST);
		JButton closeButton = new JButton("X");
		closeButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		closeButton.setContentAreaFilled(false);
		closeButton.setForeground(Color.RED);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		add(closeButton, BorderLayout.EAST);
	}

	public void close() {
		subscription.cancel();
		Container parent = getParent();
		parent.remove(this);
		parent.revalidate();
		parent.repaint();
}

	@Override
	public void onSubscribe(final Subscription subscription) {
		this.subscription = subscription;
		subscription.request(Long.MAX_VALUE);
	}

	@Override
	public void onNext(final Long item) {
		final long currentTime = item;
		Date time = new Date(currentTime);
		ZonedDateTime zonedTime = time.toInstant().atZone(timeZone.toZoneId());
		
		timeLabel.setText(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()).format(zonedTime));
	}

	@Override 
	public void onError(final Throwable err) {
		JOptionPane.showMessageDialog(this, "Error: " + err.getMessage(),
				"Something wrong with clock",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onComplete() {
		JOptionPane.showMessageDialog(this, "End of Times have arrived",
				"MVC Clock",
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(200, 60);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 60);
	}
	
}
