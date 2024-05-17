package business;

import business.lieGroup.SE2;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Random;

public class Robot {

    private enum MODE {STRAIGHT, ARC}

    private double D = 7; //1,4,7
    private MODE mode = MODE.ARC;
    private double r = 0.033;  // radius
    private double L = 0.2;    // wheel base
    private double arc = 1;    // arc radius
    private double dalpha = Math.PI / 2; // rate
    private double omega1;
    private double omega2;
    private double x, y, theta;
    private SE2 g;

    public Robot() {
        switch (mode) {
            case ARC:
                omega1 = dalpha * (arc + L / 2) / r;
                omega2 = dalpha * (arc - L / 2) / r;
                break;
            case STRAIGHT:
                omega1 = 1. / r;
                omega2 = 1. / r;
                break;
        }

        x = 0;
        y = 0;
        theta = 0;
        g = new SE2(x, y, theta);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public SE2 getSE2() {
        return g;
    }

    public void update(double dt) {
        Random random = new Random();
        double Winc1 = Math.sqrt(dt) * random.nextGaussian();
        double Winc2 = Math.sqrt(dt) * random.nextGaussian();
        //equation(2)
        double dx = 0.5 * r * (omega1 + omega2) * Math.cos(theta) * dt + 0.5 * r * Math.sqrt(D) * Math.cos(theta) * (Winc1 + Winc2);
        double dy = 0.5 * r * (omega1 + omega2) * Math.sin(theta) * dt + 0.5 * r * Math.sqrt(D) * Math.sin(theta) * (Winc1 + Winc2);
        double dtheta = dt * r * (omega1 - omega2) / L + Math.sqrt(D) * r * (Winc1 - Winc2) / L;
        x += dx;
        y += dy;
        theta += dtheta;
        g.set(x, y, theta);
    }

    public SE2 mean(double t) {
        switch (mode) {
            case ARC:
                return new SE2(MatrixUtils.createRealMatrix(new double[][]{
                        {Math.cos(dalpha * t), -Math.sin(dalpha * t), arc * Math.sin(dalpha * t)},
                        {Math.sin(dalpha * t), Math.cos(dalpha * t), arc * (1 - Math.cos(dalpha * t))},
                        {0, 0, 1}}));
            case STRAIGHT:
                return new SE2(MatrixUtils.createRealMatrix(new double[][]{
                        {1, 0, r * omega1 * t},
                        {0, 1, 0},
                        {0, 0, 1}}));
        }
        return null;
    }

    public RealMatrix covExp(double t) {
        double arc2 = arc * arc;
        double L2 = L * L;
        double r2 = r * r;
        double c = (D * r2) / (dalpha * L2);

        double sigma11;
        double sigma12;
        double sigma13;
        double sigma22;
        double sigma23;
        double sigma33;
        RealMatrix cov_exp = null;
        switch (mode) {
            case ARC:
                sigma11 = c * ((4 * arc2 + L2) * (2 * dalpha * t + Math.sin(2 * dalpha * t)) + 16 * arc2 * (dalpha * t - 2 * Math.sin(dalpha * t))) / 8;
                sigma12 = -0.5 * c * (4 * arc2 * (-1 + Math.cos(dalpha * t)) + L2) * Math.pow(Math.sin(0.5 * dalpha * t), 2);
                sigma13 = 2 * c * arc * (dalpha * t - Math.sin(dalpha * t));
                sigma22 = -c * (4 * arc2 + L2) * (-2 * dalpha * t + Math.sin(2 * dalpha * t)) / 8;
                sigma23 = -2 * c * arc * (-1 + Math.cos(dalpha * t));
                sigma33 = 2 * c * dalpha;
                cov_exp = MatrixUtils.createRealMatrix(new double[][]{{sigma11, sigma12, sigma13}, {sigma12, sigma22, sigma23}, {sigma13, sigma23, sigma33}});
                break;
            case STRAIGHT:
                sigma11 = 0.5 * D * t * r2;
                sigma22 = (2 * D * (omega1 * omega1) * (r2 * r2) * (t * t * t)) / (3 * (L2));
                sigma23 = D * omega1 * r2 * r * t * t / L2;
                sigma33 = 2 * D * r2 * t / (L2);
                cov_exp = MatrixUtils.createRealMatrix(new double[][]{{sigma11, 0, 0}, {0, sigma22, sigma23}, {0, sigma23, sigma33}});
                break;
        }
        return cov_exp;
    }
}
