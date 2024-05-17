package business;

import business.lieGroup.SE2;
import business.lieGroup.Se2;
import business.lieGroup.Statistics;
import org.apache.commons.math3.linear.RealMatrix;
import presentation.MainView;
import presentation.util.JPaintListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Sketch implements JPaintListener {

    private final String windowName = "The Banana Distribution id Gaussian";
    private final int width = 800;
    private final int height = 800;

    private Vector<Robot> robots;
    private SE2 mean, meanExp, meanTeoric;
    private BufferedImage pdfImage, pdfExpImage;

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

        Statistics estatistical = new Statistics();
        Vector<SE2> samples = new Vector<>();
        for (int i=0; i<robots.size(); i++) {
            samples.add(robots.get(i).getSE2());
        }

        mean = estatistical.mean(samples);
        meanExp = estatistical.meanExponential(samples,null);
        meanTeoric = robots.get(0).mean(1);

        RealMatrix cov = estatistical.covariance(samples,mean);
        RealMatrix covExp = estatistical.covarianceExponential(samples,meanExp);
        RealMatrix covTeoric = robots.get(0).covExp(1);

        int M = 100;
        RealMatrix pdf = estatistical.pdf(samples,mean,cov,M,null);
        pdfImage = estatistical.pdfToImage(pdf);
        RealMatrix pdfExp = estatistical.pdfExponential(samples,meanExp,covExp,M,null);
        pdfExpImage = estatistical.pdfToImage(pdfExp);
    }

    @Override
    public void paintComponent(Graphics g) {
        double factor = 100;
        double tx = 100;
        double ty = 100;
        int factor2 = 3;

        int w = pdfExpImage.getWidth();
        int h = pdfExpImage.getHeight();
        BufferedImage after = new BufferedImage(factor2*w, factor2*h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(factor2,factor2);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(pdfImage, after);
        g.drawImage(after, 0, factor2*h, null);

        after = scaleOp.filter(pdfExpImage, after);
        g.drawImage(after, factor2*w, factor2*h, null);

        g.setColor(MainView.abobora);
        for (int i=0; i<robots.size(); i++) {
            Robot robot = robots.get(i);
            g.drawOval((int) (factor*robot.getX()+tx), (int) (factor*robot.getY()+ty),2,2);
        }

        g.setColor(MainView.cyan);
        int delta = 60;
        g.drawString("SAMPLES",delta,delta);
        g.drawString("PDF",delta,delta+factor2*h);
        g.drawString("PDF Exponential",delta+factor2*w,delta+factor2*h);
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
