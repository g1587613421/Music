/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Manager;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.gplibs.magicsurfaceview.MagicMultiSurface;
import com.gplibs.magicsurfaceview.MagicMultiSurfaceUpdater;
import com.gplibs.magicsurfaceview.MagicSurfaceView;
import com.gplibs.magicsurfaceview.MagicUpdater;
import com.gplibs.magicsurfaceview.MagicUpdaterListener;
import com.zzu.gaojinlei.music.Launcher.Direction;
import com.zzu.gaojinlei.music.Launcher.updater.MultiScrapUpdater;
import com.zzu.gaojinlei.music.R;

public class MultiScrapAnim {
static volatile Object lock=new Object();//一次只允许一种操作正在进行
    public static void show(final View view, final MagicSurfaceView surfaceView) {
        synchronized (lock) {
            MultiScrapUpdater mMultiUpdater = new MultiScrapUpdater(false, (int) (Math.random()*4));
            mMultiUpdater.addListener(new MagicUpdaterListener() {
                @Override
                public void onStart() {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onStop() {
                    view.setVisibility(View.VISIBLE);
                    surfaceView.setVisibility(View.GONE);
                }
            });
            surfaceView.render(new MagicMultiSurface(view, 25, 15).setUpdater(mMultiUpdater));
        }
    }

    public static void hide(final View view, final MagicSurfaceView surfaceView) {
        synchronized (lock) {
            MultiScrapUpdater mMultiUpdater = new MultiScrapUpdater(true, Direction.RIGHT);
            mMultiUpdater.addListener(new MagicUpdaterListener() {
                @Override
                public void onStart() {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onStop() {
                    view.setVisibility(View.INVISIBLE);
                    surfaceView.setVisibility(View.GONE);
                    surfaceView.release();
                }
            });
            surfaceView.render(new MagicMultiSurface(view, 20, 10).setUpdater(mMultiUpdater));
        }
    }
}
