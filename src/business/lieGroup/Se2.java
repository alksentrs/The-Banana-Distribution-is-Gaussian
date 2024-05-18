package business.lieGroup;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import util.MyMatrixUtils;

public class Se2 {

    private double v1, v2, alpha;
    private RealMatrix X;

    public Se2(RealMatrix X) {
        this.X = X;
        alpha = X.getEntry(1,0);
        v1 = X.getEntry(0,2);
        v2 = X.getEntry(1,2);
    }

    public Se2(double v1, double v2, double alpha) {
        this.alpha = alpha;
        this.v1 = v1;
        this.v2 = v2;
        X = MatrixUtils.createRealMatrix(new double [][] {  { 0,     -alpha, v1},
                { alpha,  0,     v2},
                { 0,      0,     0}});
    }

    public Se2(RealVector vector) {
        this.v1 = vector.getEntry(0);;
        this.v2 = vector.getEntry(1);;
        this.alpha = vector.getEntry(2);
        X = MatrixUtils.createRealMatrix(new double [][] {  { 0,     -alpha, v1},
                { alpha,  0,     v2},
                { 0,      0,     0}});
    }

    public void set(double v1, double v2, double alpha) {
        this.alpha = alpha;
        this.v1 = v1;
        this.v2 = v2;
        X = MatrixUtils.createRealMatrix(new double [][] {  { 0,     -alpha, v1},
                { alpha,  0,     v2},
                { 0,      0,     0}});
    }

    public RealMatrix getX() {
        return X;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getV1() {
        return v1;
    }

    public double getV2() {
        return v2;
    }

    public SE2 exp() {
        return new SE2(MyMatrixUtils.expm(X));
    }

    public SE2 exp2() {
        double t1 = (v2*(-1 + Math.cos(alpha)) + v1*Math.sin(alpha))/alpha;
        double t2 = (v1*(1 -Math.cos(alpha)) + v2*Math.sin(alpha))/alpha;
        return new SE2(t1,t2,alpha);
    }

    public Se2 bracket(Se2 other) {
        RealMatrix Y = other.getX();
        RealMatrix B = X.multiply(Y).subtract(Y.multiply(X));
        return new Se2(B);
    }

    public RealVector vee() {
        return MatrixUtils.createRealVector(new double [] {v1, v2, alpha});
    }

    @Override
    public String toString() {
        return "Se2{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                ", alpha=" + alpha +
                ", X=" + X +
                '}';
    }

    public void add(Se2 other) {
        this.v1 += other.getV1();
        this.v2 += other.getV2();
        this.alpha += other.getAlpha();
        X = MatrixUtils.createRealMatrix(new double [][] {  { 0,     -alpha, v1},
                { alpha,  0,     v2},
                { 0,      0,     0}});
    }

    public void scale(double s) {
        this.v1 *= s;
        this.v2 *= s;
        this.alpha *= s;
        X = MatrixUtils.createRealMatrix(new double [][] {  { 0,     -alpha, v1},
                { alpha,  0,     v2},
                { 0,      0,     0}});
    }
}
