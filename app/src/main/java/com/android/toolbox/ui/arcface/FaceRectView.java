package com.android.toolbox.ui.arcface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.arcsoft.face.FaceInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用于显示人脸信息的控件
 */
public class FaceRectView extends View {
    private CopyOnWriteArrayList<FaceInfo> drawInfoList = new CopyOnWriteArrayList<>();

    // 画笔，复用
    private Paint paint;

    // 默认人脸框厚度
    private static final int DEFAULT_FACE_RECT_THICKNESS = 6;

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawInfoList != null && drawInfoList.size() > 0) {
            for (int i = 0; i < drawInfoList.size(); i++) {
                //DrawHelper.drawFaceRect(canvas, drawInfoList.get(i), DEFAULT_FACE_RECT_THICKNESS, paint);
                drawFaceRect(canvas, drawInfoList.get(i), DEFAULT_FACE_RECT_THICKNESS, paint);

            }
        }
    }

    public void clearFaceInfo() {
        drawInfoList.clear();
        postInvalidate();
    }

    public void addFaceInfo(FaceInfo faceInfo) {
        drawInfoList.add(faceInfo);
        postInvalidate();
    }

    public void addFaceInfo(List<FaceInfo> faceInfoList) {
        drawInfoList.addAll(faceInfoList);
        postInvalidate();
    }

    public static void drawFaceRect(Canvas canvas, FaceInfo drawInfo,int faceRectThickness, Paint paint) {
        if (canvas == null || drawInfo == null) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(faceRectThickness);
        paint.setColor(Color.parseColor("#42ed45"));
        paint.setAntiAlias(true);

        Path mPath = new Path();
        // 左上
        Rect rect = drawInfo.getRect();
        mPath.moveTo(rect.left, rect.top + rect.height() / 4);
        mPath.lineTo(rect.left, rect.top);
        mPath.lineTo(rect.left + rect.width() / 4, rect.top);
        // 右上
        mPath.moveTo(rect.right - rect.width() / 4, rect.top);
        mPath.lineTo(rect.right, rect.top);
        mPath.lineTo(rect.right, rect.top + rect.height() / 4);
        // 右下
        mPath.moveTo(rect.right, rect.bottom - rect.height() / 4);
        mPath.lineTo(rect.right, rect.bottom);
        mPath.lineTo(rect.right - rect.width() / 4, rect.bottom);
        // 左下
        mPath.moveTo(rect.left + rect.width() / 4, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom - rect.height() / 4);
        canvas.drawPath(mPath, paint);

        // 绘制文字，用最细的即可，避免在某些低像素设备上文字模糊
        paint.setStrokeWidth(1);
    }
}