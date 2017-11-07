package com.security.fragment.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.security.fragment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/4/18 0018.
 */
public class AutoWrapTextView extends View {

    public static final String TAG = "AutoWrapTextView";
    private int paddingLeft,paddingRight,paddingTop,paddingBottom;
    private int textSize;
    private int lineSpace;
    private int textColor;
    private TextPaint textPaint;
    private char[] textCharArray;

    //单行文本的最大宽度;
    private int singleTextWidth;

    /**
     * 拆分后的文本集合
     */
    private List<String> splitTextList;

    /**
     * 分割后的文本rect数组
     */
    private Rect[] splitTextRectArray = null;

    public AutoWrapTextView(Context context) {
        this(context, null);
    }

    public AutoWrapTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoWrapTextView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.AutoWrapTextView);

        paddingLeft=array.getDimensionPixelSize(R.styleable.AutoWrapTextView_paddingLeft,0);
        paddingRight=array.getDimensionPixelSize(R.styleable.AutoWrapTextView_paddingRight,0);
        paddingTop=array.getDimensionPixelSize(R.styleable.AutoWrapTextView_paddingTop,0);
        paddingBottom=array.getDimensionPixelSize(R.styleable.AutoWrapTextView_paddingBottom,0);

        textColor=array.getColor(R.styleable.AutoWrapTextView_textColor, Color.BLACK);
        textSize=array.getDimensionPixelSize(R.styleable.AutoWrapTextView_textSize,50);
        lineSpace=array.getInteger(R.styleable.AutoWrapTextView_lineSpace,8);
        array.recycle();
        
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * 设置文本
     * @param text
     */
    public void setText(String text){
        if(TextUtils.isEmpty(text)){
            return;
        }
        textCharArray = text.toCharArray();
        requestLayout();
    }

    /**
     * 得到单个char的宽度
     * @param textChar:单个字符;
     * @return :字符的宽度;
     */
    private float getSingleCharWidth(char textChar) {
        Rect rect=new Rect();
        textPaint.getTextBounds(new char[] {textChar},0,1,rect);
        int widths=rect.width();

        //textPaint.getTextBounds();
        float[] width = new float[1];
        textPaint.getTextWidths(new char[] {textChar}, 0, 1, width);
        Log.i(TAG,widths+":"+width[0]);
        return width[0];
    }

    /***
     * 控件的测量;
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        splitText(MeasureSpec.getMode(heightMeasureSpec));
    }

    /**
     * 拆分文本;
     */
    private void splitText(int heightMode){
        if(textCharArray==null) return ;
        splitTextList=new ArrayList<>();
        singleTextWidth=getMeasuredWidth()-paddingLeft-paddingRight;
        int currentSingleTextWidth=0;//当前当行文本的宽度;
        int length=textCharArray.length;//字符组合的长度;
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            char textChar=textCharArray[i];
            currentSingleTextWidth+=getSingleCharWidth(textChar);
            if(currentSingleTextWidth>singleTextWidth){//当前累加的字符的长度大于最大宽度
                splitTextList.add(sb.toString());
                //重新初始化StringBuffer;
                sb=new StringBuffer();
                currentSingleTextWidth=0;
                i--;
            }else{
                sb.append(textChar);
                if(i==length-1){
                    splitTextList.add(sb.toString());
                }
            }
        }

        int textHeight=0;
        splitTextRectArray = new Rect[splitTextList.size()];
        int lineCount=splitTextList.size();
        for(int m=0;m<lineCount;m++){
            String lineText=splitTextList.get(m);
            Rect lineTextRect = new Rect();
            textPaint.getTextBounds(lineText,0,lineText.length(),lineTextRect);
            if(heightMode==MeasureSpec.AT_MOST){//设置高度为Wrap_content;
                textHeight+=(lineTextRect.height()+lineSpace);
                if(m==length-1){
                    textHeight=textHeight+paddingTop+paddingBottom;
                }
            }else{
                if(textHeight==0){
                    textHeight=getMeasuredHeight();
                }
            }
            splitTextRectArray[m]=lineTextRect;
        }
        setMeasuredDimension(getMeasuredWidth(),textHeight);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawText(canvas);
    }

    /**
     * 绘制文本;
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        if(splitTextList==null||splitTextList.size()==0) return;
        int marginTop = getTopTextMarginTop();
        for (int m = 0, length = splitTextList.size(); m < length; m++) {
            String lineText = splitTextList.get(m);
            canvas.drawText(lineText, paddingLeft, marginTop, textPaint);
            marginTop += (splitTextRectArray[m].height() + lineSpace);
        }
    }

    private int getTopTextMarginTop() {
        return splitTextRectArray[0].height() / 2 + paddingTop + getFontSpace();
    }

    private int getFontSpace() {
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        return (fontMetrics.descent- fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

}
