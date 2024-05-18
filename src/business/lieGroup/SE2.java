package business.lieGroup;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import util.MyMatrixUtils;

public class SE2 {
    private double t1, t2, alpha;
    private RealMatrix g;

    public SE2(double t1, double t2, double alpha) {
        this.alpha = alpha;
        this.t1 = t1;
        this.t2 = t2;
        g = MatrixUtils.createRealMatrix(new double[][]{
                {Math.cos(alpha), -Math.sin(alpha), t1,},
                {Math.sin(alpha), Math.cos(alpha), t2,},
                {0, 0, 1,}});
    }

    public SE2(RealMatrix g) {
        this.g = g;
        t1 = g.getEntry(0,2);
        t2 = g.getEntry(1,2);
        alpha = Math.atan2(g.getEntry(1,0),g.getEntry(0,0));
    }

    public SE2(RealVector vector) {
        this.t1 = vector.getEntry(0);
        this.t2 = vector.getEntry(1);
        this.alpha = vector.getEntry(2);
        g = MatrixUtils.createRealMatrix(new double[][]{
                {Math.cos(alpha), -Math.sin(alpha), t1,},
                {Math.sin(alpha), Math.cos(alpha), t2,},
                {0, 0, 1,}});
    }

    public void set(double t1, double t2, double alpha) {
        this.alpha = alpha;
        this.t1 = t1;
        this.t2 = t2;
        g = MatrixUtils.createRealMatrix(new double [][] {
                { Math.cos(alpha), -Math.sin(alpha), t1, },
                { Math.sin(alpha),  Math.cos(alpha), t2, },
                { 0              ,  0              , 1, }});
    }

    public RealMatrix getG() {
        return g;
    }

    public double getT1() {
        return t1;
    }

    public double getT2() {
        return t2;
    }

    public double getAlpha() {
        return alpha;
    }

    public RealMatrix inverse() {
        return MatrixUtils.inverse(g);
    }

    public SE2 multiply(SE2 a) {
        return new SE2(g.multiply(a.getG()));
    }

    public SE2 preMultiply(SE2 a) {
        return new SE2(g.preMultiply(a.getG()));
    }

    public SE2 preMultiplyInverse(SE2 a) {
        return new SE2(g.preMultiply(a.inverse()));
    }

    public RealMatrix inverse2() {
        return MatrixUtils.createRealMatrix(new double [][] {
                { Math.cos(alpha),  Math.sin(alpha), -t1*Math.cos(alpha) - t2*Math.sin(alpha) },
                { -Math.sin(alpha),  Math.cos(alpha), t1*Math.sin(alpha) - t2*Math.cos(alpha) },
                { 0              ,  0              , 1 }});
    }

    public Se2 logm() {
        return new Se2(MyMatrixUtils.logm(g));
    }

    public SE2 Ad(SE2 other) {
        return new SE2(g.multiply(other.getG()).multiply(MatrixUtils.inverse(g)));
    }

    public RealVector vee() {
        return MatrixUtils.createRealVector(new double [] {t1, t2, alpha});
    }

    @Override
    public String toString() {
        return "SE2{" +
                "t1=" + t1 +
                ", t2=" + t2 +
                ", alpha=" + alpha +
                ", g=" + g +
                '}';
    }

    public String toMatlabString() {
        return "["+t1+" "+t2+" "+alpha+"];";
    }
}
