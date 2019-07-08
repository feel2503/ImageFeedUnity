package com.feed.plugin.widget.cookicrop;

import android.graphics.Matrix;

public class MatrixParams {

    private float x;
    private float y;
    private float scaleWidth;
    private float scaleHeight;

    public static MatrixParams fromMatrix(Matrix matrix) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);

        MatrixParams matrixParams = new MatrixParams();
        matrixParams.x = matrixValues[2];
        matrixParams.y = matrixValues[5];
        matrixParams.scaleWidth = matrixValues[0];
        matrixParams.scaleHeight = matrixValues[4];

        return matrixParams;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }
}
