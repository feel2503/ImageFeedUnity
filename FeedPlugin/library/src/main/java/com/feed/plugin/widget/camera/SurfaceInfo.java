package com.feed.plugin.widget.camera;

import android.graphics.SurfaceTexture;


/**
 * Stores information about the {@link SurfaceTexture} showing camera preview.
 */
class SurfaceInfo {

    SurfaceTexture surface;
    int width;
    int height;

    void configure(SurfaceTexture s, int w, int h) {
        surface = s;
        width = w;
        height = h;
    }

}
