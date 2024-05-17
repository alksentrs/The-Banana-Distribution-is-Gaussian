package business.lieGroup;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

public class Statistics {

    public SE2 mean(Vector<SE2> samples) {
        SE2 a = samples.get(0);
        RealVector average = MatrixUtils.createRealVector(new double[a.vee().getDimension()]);
        for (int i=0; i<samples.size(); i++) {
            average = average.add(samples.get(i).vee());
        }
        average.mapDivideToSelf(samples.size());
        return new SE2(average);
    }

    public SE2 meanExponential(Vector<SE2> samples, SE2 mean) {
        // equation (26)
        SE2 mu = null != mean ? mean: new SE2(0,0,0);
        double magError = Double.MAX_VALUE;
        int count = 0;
        while (magError > 0.00000000001 && count < 512) {
            Se2 sum = new Se2(0, 0, 0);
            for (int i = 0; i < samples.size(); i++) {
                SE2 g = samples.get(i);
                sum.add(g.preMultiplyInverse(mu).logm());
            }
            sum.scale(1. / samples.size());
            SE2 mu2 = sum.exp().preMultiply(mu);
            RealVector error = mu.vee().subtract(mu2.vee());
            magError = error.getNorm();
            count++;
            mu = mu2;
        }
        return mu;
    }

    public RealMatrix covariance(Vector<SE2> samples, SE2 mu) {
        SE2 a = samples.get(0);
        RealMatrix cov = MatrixUtils.createRealMatrix(a.vee().getDimension(),a.vee().getDimension());
        for (int i=0; i<samples.size(); i++) {
            RealVector yy = mu.vee().subtract(samples.get(i).vee());
            cov = cov.add(yy.outerProduct(yy));
        }
        cov = cov.scalarMultiply(1./samples.size());
        return cov;
    }

    public RealMatrix covarianceExponential(Vector<SE2> samples, SE2 mu) {
        // equation (27)
        SE2 a = samples.get(0);
        RealMatrix cov = MatrixUtils.createRealMatrix(a.vee().getDimension(), a.vee().getDimension());
        for (int i = 0; i < samples.size(); i++) {
            SE2 g = samples.get(i);
            RealVector yy = g.preMultiplyInverse(mu).logm().vee();
            cov = cov.add(yy.outerProduct(yy));
        }
        cov = cov.scalarMultiply(1./samples.size());
        return cov;
    }

    public RealMatrix pdf(Vector<SE2> samples, SE2 mu, RealMatrix cov, int N, BufferedWriter bufferedWriter) {
        double minT1 = Double.MAX_VALUE;
        double minT2 = Double.MAX_VALUE;
        double minAlpha = Double.MAX_VALUE;
        double maxT1 = Double.MIN_VALUE;
        double maxT2 = Double.MIN_VALUE;
        double maxAlpha = Double.MIN_VALUE;
        double stepT1, stepT2, stepAlpha;
        double [][] pdf = new double[N][N];

        RealMatrix covInv = MatrixUtils.inverse(cov);
        LUDecomposition luDecomposition = new LUDecomposition(cov);
        double determinant = luDecomposition.getDeterminant();
        // equation (20)
        double cc = Math.pow(2.*Math.PI,mu.vee().getDimension()/2.)*Math.sqrt(determinant);

        for (int i=0; i<samples.size(); i++) {
            SE2 g = samples.get(i);
            if (g.getT1() > maxT1) maxT1 = g.getT1();
            if (g.getT2() > maxT2) maxT2 = g.getT2();
            if (g.getAlpha() > maxAlpha) maxAlpha = g.getAlpha();
            if (g.getT1() < minT1) minT1 = g.getT1();
            if (g.getT2() < minT2) minT2 = g.getT2();
            if (g.getAlpha() < minAlpha) minAlpha = g.getAlpha();
        }

        double alphaFactor = 2;

        stepT1 = (maxT1-minT1)/N;
        stepT2 = (maxT2-minT2)/N;
        stepAlpha = (maxAlpha-minAlpha)/(N/alphaFactor);

        if (null!=bufferedWriter) {
            try {
                bufferedWriter.write("tt1=[");
                for (int i = 0; i < N; i++) bufferedWriter.write(minT1 + i * stepT1 + " ");
                bufferedWriter.write("];");
                bufferedWriter.newLine();
                bufferedWriter.write("tt2=[");
                for (int i = 0; i < N; i++) bufferedWriter.write(minT2 + i * stepT2 + " ");
                bufferedWriter.write("];");
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i=0; i<N; i++) {
            for (int j=0; j<N; j++) {
                for (int k=0; k<N/alphaFactor; k++) {
                    SE2 g = new SE2(minT1+i*stepT1,minT2+j*stepT2,minAlpha+k*stepAlpha);
                    RealVector yy = g.vee().subtract(mu.vee());
                    // equation (19)
                    pdf[j][i] += 1./cc*Math.exp(-0.5*covInv.preMultiply(yy).dotProduct(yy));
                }
                pdf[j][i] *= (alphaFactor/N);
            }
        }
        return MatrixUtils.createRealMatrix(pdf);
    }

    public RealMatrix pdfExponential(Vector<SE2> samples, SE2 mu, RealMatrix cov, int N, BufferedWriter bufferedWriter) {
        double minT1 = Double.MAX_VALUE;
        double minT2 = Double.MAX_VALUE;
        double minAlpha = Double.MAX_VALUE;
        double maxT1 = Double.MIN_VALUE;
        double maxT2 = Double.MIN_VALUE;
        double maxAlpha = Double.MIN_VALUE;
        double stepT1, stepT2, stepAlpha;
        double [][] pdf = new double[N][N];

        RealMatrix covInv = MatrixUtils.inverse(cov);
        LUDecomposition luDecomposition = new LUDecomposition(cov);
        double determinant = luDecomposition.getDeterminant();
        // equation (20)
        double cc = Math.pow(2.*Math.PI,mu.vee().getDimension()/2.)*Math.sqrt(determinant);

        for (int i=0; i<samples.size(); i++) {
            SE2 g = samples.get(i);
            if (g.getT1() > maxT1) maxT1 = g.getT1();
            if (g.getT2() > maxT2) maxT2 = g.getT2();
            if (g.getAlpha() > maxAlpha) maxAlpha = g.getAlpha();
            if (g.getT1() < minT1) minT1 = g.getT1();
            if (g.getT2() < minT2) minT2 = g.getT2();
            if (g.getAlpha() < minAlpha) minAlpha = g.getAlpha();
        }

        double alphaFactor = 2;

        stepT1 = (maxT1-minT1)/N;
        stepT2 = (maxT2-minT2)/N;
        stepAlpha = (maxAlpha-minAlpha)/(N/alphaFactor);

        if (null!=bufferedWriter) {
            try {
                bufferedWriter.write("tt1Exp=[");
                for (int i = 0; i < N; i++) bufferedWriter.write(minT1 + i * stepT1 + " ");
                bufferedWriter.write("];");
                bufferedWriter.newLine();
                bufferedWriter.write("tt2Exp=[");
                for (int i = 0; i < N; i++) bufferedWriter.write(minT2 + i * stepT2 + " ");
                bufferedWriter.write("];");
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i=0; i<N; i++) {
            for (int j=0; j<N; j++) {
                for (int k=0; k<N/alphaFactor; k++) {
                    SE2 g = new SE2(minT1+i*stepT1,minT2+j*stepT2,minAlpha+k*stepAlpha);
                    RealVector yy = g.preMultiplyInverse(mu).logm().vee();
                    // equation (23)
                    pdf[j][i] += 1./cc*Math.exp(-0.5*covInv.preMultiply(yy).dotProduct(yy));
                }
                pdf[j][i] *= (alphaFactor/N);
            }
        }
        return MatrixUtils.createRealMatrix(pdf);
    }

    public BufferedImage pdfToImage(RealMatrix pdf) {
        int cols = pdf.getColumnDimension();
        int rows = pdf.getRowDimension();
        BufferedImage bufferedImage = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                max = Math.max(pdf.getEntry(j,i),max);
                min = Math.min(pdf.getEntry(j,i),min);
            }
        }
        double len = max-min;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {

                Color c;
                double value = (pdf.getEntry(j,i)-min)/len;
                int rgbValue = (int)(value*200);
                if (value <0.2 ) {
                    c = new Color(0, 0, rgbValue);
                } else {
                    if (value <0.4 ) {
                        c = new Color(0, rgbValue, rgbValue);
                    } else {
                        if (value < 0.6) {
                            c = new Color(0, rgbValue, 0);
                        } else {
                            if (value < 0.9) {
                                c = new Color(rgbValue, rgbValue, 0);
                            } else {
                                c = new Color(rgbValue, 0, 0);
                            }
                        }
                    }
                }
                bufferedImage.setRGB(i, j, c.getRGB());
            }
        }
        return bufferedImage;
    }

}
