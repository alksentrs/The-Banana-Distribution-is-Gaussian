package business;

import presentation.MainView;
import presentation.util.JPaintListener;

import java.awt.*;
import java.util.Vector;

public class Sketch implements JPaintListener {

    private final String windowName = "The Banana Distribution id Gaussian";
    private final int width = 800;
    private final int height = 800;

    private Vector<Robot> robots;

    public Sketch() {
        robots = new Vector<>();
        int sample = 2000;

        for (int i=0; i<sample; i++) robots.add(new Robot());

        double T=1;
        double N=100;
        double dt=T/N;

        for (int i=0; i<robots.size(); i++) {
            for (int j=0; j<N; j++) {
                robots.get(i).update(dt);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        double factor = 200;

        g.setColor(MainView.abobora);
        for (int i=0; i<robots.size(); i++) {
            Robot robot = robots.get(i);
            g.drawOval((int) (factor*robot.getX()), (int) (factor*robot.getY()),2,2);
        }
    }

    public String getWindowName() {
        return windowName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
