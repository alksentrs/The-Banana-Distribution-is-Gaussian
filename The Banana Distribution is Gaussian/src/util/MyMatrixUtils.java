package util;

import jeigen.DenseMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MyMatrixUtils {

    static public RealMatrix expm(RealMatrix m) {
        DenseMatrix denseMatrix = new DenseMatrix(m.getData());
        DenseMatrix expDenseMatrix = denseMatrix.mexp();
        RealMatrix expMatrix = MatrixUtils.createRealMatrix(m.getRowDimension(),m.getColumnDimension());
        for (int r=0; r<expMatrix.getRowDimension(); r++) for (int c=0; c<expMatrix.getColumnDimension(); c++) expMatrix.setEntry(r,c,expDenseMatrix.get(r,c));
        return expMatrix;
    }

    static public RealMatrix logm(RealMatrix m) {
        DenseMatrix denseMatrix = new DenseMatrix(m.getData());
        DenseMatrix logDenseMatrix = denseMatrix.mlog();
        RealMatrix logMatrix = MatrixUtils.createRealMatrix(m.getRowDimension(),m.getColumnDimension());
        for (int r=0; r<logMatrix.getRowDimension(); r++) for (int c=0; c<logMatrix.getColumnDimension(); c++) logMatrix.setEntry(r,c,logDenseMatrix.get(r,c));
        return logMatrix;
    }
}
