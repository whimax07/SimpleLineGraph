package org.example;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private final Timer timer = new Timer();



    private void openWindow() {
        final JFrame frame = new JFrame("Simple Line Graph");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        final JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLUE);
        mainPanel.setLayout(new BorderLayout());

        final LineGraph lineGraph = new LineGraph();

        mainPanel.add(lineGraph);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(10, 30, 60, 100, 150));

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    private boolean toggle;
                    private int count = 0;
                    @Override
                    public void run() {
                        if (count == 8) {
                            lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(10, 170, 60, 100, 150));
                        } else if (count == 14 || count == 15 || count == 16) {
                            lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(40, 33, 41, 40, 30));
                        } else if (count == 17 || count == 18) {
                            lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(45, 40, 40, 40, 45));
                        } else if (toggle) {
                            lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(10, 130, 60, 100, 150));
                        } else {
                            lineGraph.setData(Arrays.asList(15, 20, 30, 40, 50), Arrays.asList(10, 40, 60, 100, 150));
                        }
                        toggle = !toggle;
                        count += 1;
                    }
                },
                1_000, 1_000
        );
    }

    public static void createGraph(List<Double> xs, List<Double> ys) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final JFrame frame = new JFrame("Simple Line Graph");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(1200, 800);

                final JPanel mainPanel = new JPanel();
                mainPanel.setBackground(Color.BLUE);
                mainPanel.setLayout(new BorderLayout());

                final LineGraph lineGraph = new LineGraph();

                mainPanel.add(lineGraph);

                frame.getContentPane().add(mainPanel);
                frame.setVisible(true);
                lineGraph.setData(xs, ys);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(new Main()::openWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
