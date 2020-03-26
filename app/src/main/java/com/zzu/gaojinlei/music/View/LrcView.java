/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zzu.gaojinlei.music.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("DrawAllocation")
public class LrcView extends View {
	private List<String> mLrcs = new ArrayList<String>(); // 存放歌词
	private List<Long> mTimes = new ArrayList<Long>(); // 存放时间

	public int colors[]=new int[]{Color.RED,Color.YELLOW,Color.BLUE,Color.GREEN,0xFF03A9F4,Color.YELLOW};
	public boolean randomColor=false;
	private long mNextTime =0L; // 保存下一句开始的时间
	private int refreshTime=200;//外部的刷新的时间间隔单位ms
	private int mViewWidth; // view的宽度
	private int mLrcHeight; // lrc界面的高度
	private int mRows=5;      // 多少行

	private boolean proofread=false;
	private int mCurrentLine = 1; // 当前行

	private float mTextSize=50.0f; // 字体
	private float mDividerHeight=15.0f; // 行间距

	private Paint mNormalPaint; // 常规的字体
	private Paint mCurrentPaint; // 当前歌词的大小
	public int currentTextColor=0xffffffff;
	public int normalTextColor=0xffffffff;
	private Bitmap mBackground = null;
	private AttributeSet attributeSet;
	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(attrs);
	}

	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initViews(attrs);
	}

	// 初始化操作
	private void initViews(AttributeSet attrs) {
		attributeSet=attrs;
		// <begin>
		// 解析自定义属性
		TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.Lrc);
		mTextSize = ta.getDimension(R.styleable.Lrc_textSize, mTextSize);
		mRows = ta.getInteger(R.styleable.Lrc_rows, mRows);
		mDividerHeight = ta.getDimension(R.styleable.Lrc_dividerHeight, mDividerHeight);

		normalTextColor = ta.getColor(R.styleable.Lrc_normalTextColor,
				normalTextColor);

		currentTextColor = ta.getColor(R.styleable.Lrc_currentTextColor,
				currentTextColor);

		ta.recycle();
		// </end>

		// 计算lrc面板的高度
		mLrcHeight = (int) (mTextSize + mDividerHeight) * mRows + 50;

		mNormalPaint = new Paint();
		mCurrentPaint = new Paint();

		// 初始化paint
		mNormalPaint.setTextSize(mTextSize-9);
		mNormalPaint.setColor(normalTextColor);
		mCurrentPaint.setTextSize(mTextSize+10);
		mCurrentPaint.setColor(currentTextColor);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 获取view宽度
		mViewWidth = getMeasuredWidth();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 重新设置view的高度
		int measuredHeight = MeasureSpec.makeMeasureSpec(mLrcHeight, MeasureSpec.AT_MOST);
		setMeasuredDimension(widthMeasureSpec, measuredHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLrcs.isEmpty() || mTimes.isEmpty()) {
			return;
		}
		if (randomColor){
			currentTextColor=colors[(int)( colors.length* Math.random())];
			mCurrentPaint.setColor(currentTextColor);
		}
		canvas.save();
		canvas.clipRect(0, 0, mViewWidth, mLrcHeight);

		if (null != mBackground) {
			canvas.drawBitmap(Bitmap.createScaledBitmap(mBackground, mViewWidth, mLrcHeight, true),
					new android.graphics.Matrix(), null);
		}

		// 将画布上移
		canvas.translate(0, -((mCurrentLine - (mRows+1)/2) * (mTextSize + mDividerHeight)));

		for (int i = mCurrentLine - 1; i >= 0; i--) {
			String lrc = mLrcs.get(i);
			float x = (mViewWidth - mNormalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, x, (mTextSize + mDividerHeight) * i,mNormalPaint);
		}

		String currentLrc = mLrcs.get(mCurrentLine);
		float currentX = (mViewWidth - mCurrentPaint.measureText(currentLrc)) / 2;
		canvas.drawText(currentLrc, currentX, (mTextSize + mDividerHeight)* mCurrentLine, mCurrentPaint);

		for (int i = mCurrentLine + 1; i < mLrcs.size(); i++) {
			String lrc = mLrcs.get(i);
			float x = (mViewWidth - mNormalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, x, (mTextSize + mDividerHeight) * i, mNormalPaint);
		}

		canvas.restore();
	}
	// 外部提供方法
	// 解析时间
	private Long parseTime(String time) {
		// 03:02.12
		String[] min = time.split(":");
		String[] sec = min[1].split("\\.");

		long minInt = Long.parseLong(min[0].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		long secInt = Long.parseLong(sec[0].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		long milInt = Long.parseLong(sec[1].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		
		return minInt * 60 * 1000 + secInt * 1000 + milInt * 10;
	}
	// 外部提供方法
	// 解析每行
	private String[] parseLine(String line) {
		Matcher matcher = Pattern.compile("\\[.+\\].+").matcher(line);
		// 如果形如：[xxx]后面啥也没有的，则return空
		if (!matcher.matches()) {
			System.out.println("throws " + line);
			return null;
		}

		line = line.replaceAll("\\[", "");
		String[] result = line.split("\\]");
		result[0] = String.valueOf(parseTime(result[0]));

		return result;
	}

	public void changeProcess(){
		mNextTime=0;
		mCurrentLine=1;
	}
	// 传入当前播放时间
	public synchronized void changeCurrent(long time) {
		//下一句还没开始
		if (mNextTime > time) {
			return;
		}
		// 每次进来都遍历存放的时间
		for (int i = 0; i < mTimes.size(); i++) {
			// 发现这个时间大于传进来的时间
			// 那么现在就应该显示这个时间前面的对应的那一行
			// 每次都重新显示，是不是要判断：现在正在显示就不刷新了
			if (mTimes.get(i) > time && i >= mCurrentLine + 1) {
				mNextTime = mTimes.get(i);
				mCurrentLine = i <= 1 ? 0 : i - 1;
				postInvalidate();
				break;
			}
		}
	}

	/**加载歌词文本内容
	 *
	 * @param data
	 */
	public void setLrcString(String data){
		mLrcs.clear();
		mTimes.clear();
		mNextTime=1;
		mCurrentLine=1;
		String[] lin;
		String[] ds=data.split("\n");
		for (String d : ds) {
			lin=parseLine(d);
			if (lin!=null) {
				mTimes.add(Long.parseLong(lin[0]));
				mLrcs.add(lin[1]);
			}
		}


	}
	// 设置lrc的路径
	public void setLrcPath(String path) throws Exception {
		mLrcs.clear();
		mTimes.clear();
		mNextTime=1;
		mCurrentLine=1;
		File file = new File(path);
		if (!file.exists()) {
			throw new Exception("lrc not found...");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));

		String line = "";
		String[] arr;
		while (null != (line = reader.readLine())) {
			arr = parseLine(line);
			if (null == arr) {
				continue;
			}

			// 如果解析出来只有一个
			if (1 == arr.length) {
				String last = mLrcs.remove(mLrcs.size() - 1);
				mLrcs.add(last + arr[0]);
				continue;
			}
			mTimes.add(Long.parseLong(arr[0]));
			mLrcs.add(arr[1]);
		}

		reader.close();
	}

	// 外部提供方法
	// 设置背景图片
	public void setBackground(Bitmap bmp) {
		mBackground = bmp;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getmLrcHeight() {
		return mLrcHeight;
	}

	public void setmLrcHeight(int mLrcHeight) {
		this.mLrcHeight = mLrcHeight;
	}

	public int getmRows() {
		return mRows;
	}

	public void setmRows(int mRows) {
		this.mRows = mRows;
		initViews(attributeSet);
	}

	public void setmTextSize(float mTextSize) {
		this.mTextSize = mTextSize;
		initViews(attributeSet);
	}

	public void setmDividerHeight(float mDividerHeight) {
		this.mDividerHeight = mDividerHeight;
	}
}
