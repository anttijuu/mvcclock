package org.anttijuustila;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.anttijuustila.mvcclock.model.ClockTickSource;
import org.anttijuustila.mvcclock.view.WallClock;

public class WallClockApp implements ActionListener, ContainerListener {
    public static void main(String[] args) {
        new WallClockApp().run();
    }

    private JFrame mainFrame;
    private ClockTickSource clock;
    private JPanel containerPanel;

    private void run() {
        
        clock = new ClockTickSource();
        mainFrame = new JFrame("Wall Clocks");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        containerPanel = new JPanel(new FlowLayout());
        mainFrame.getContentPane().add(containerPanel);
        containerPanel.addContainerListener(this);
        
        JPanel addButtonPanel = new JPanel() {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(30, 30);
            }
        
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 60);
            }
        };
        addButtonPanel.setLayout(new BorderLayout(2, 2));
        JButton addWallClockButton = new JButton("+");
        addWallClockButton.setActionCommand("add-new-wallclock");
        addWallClockButton.addActionListener(this);
        addWallClockButton.setSize(new Dimension(40,40));
        addButtonPanel.add(addWallClockButton, BorderLayout.CENTER);
        containerPanel.add(addButtonPanel, BorderLayout.NORTH);
        containerPanel.add(new WallClock(clock, TimeZone.getDefault()), BorderLayout.CENTER);

        mainFrame.setSize(new Dimension(200, 800));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("add-new-wallclock")) {
            Set<String> zones = ZoneId.getAvailableZoneIds();
            List<String> zoneList = new ArrayList<String>(zones);
            Collections.sort(zoneList);
            String [] asArray = new String[zoneList.size()];
            zoneList.toArray(asArray);
            String selected = (String) JOptionPane.showInputDialog(null, "Select timezone",
                    "Create new WallClock", JOptionPane.QUESTION_MESSAGE, null, asArray, asArray[0]);
            if (selected != null) {
                ZoneId newZoneId = ZoneId.of(selected);
                TimeZone newZone = TimeZone.getTimeZone(newZoneId);
                containerPanel.add(new WallClock(clock, newZone), BorderLayout.CENTER);    
            }
        }
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        mainFrame.pack();
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        mainFrame.pack();
    }
}
