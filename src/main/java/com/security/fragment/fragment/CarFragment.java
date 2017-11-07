package com.security.fragment.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.security.fragment.R;
import com.security.fragment.view.AutoWrapTextView;

import java.util.ArrayList;

/**
 * 展示自定义的展示中英文混排的TextView;
 */
public class CarFragment extends BaseFragment {

    private PieChart chart;
    private Typeface mTf;

    public static CarFragment newInstance(String title) {
        CarFragment fragment = new CarFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_car,container,false);
        chart = (PieChart) view.findViewById(R.id.pieChart);
        AutoWrapTextView textView= (AutoWrapTextView) view.findViewById(R.id.tv);
        textView.setText("密码:afsfADFJSDLFJSDLFJSDLFSDLFJSDLFJDOSFSDLFdflsdjfsdjflsdfs");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // apply styling
        chart.getDescription().setEnabled(false);
        chart.setHoleRadius(52f);
        chart.setTransparentCircleRadius(57f);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextTypeface(mTf);
        chart.setCenterTextSize(9f);
        chart.setUsePercentValues(true);
        chart.setExtraOffsets(5, 10,20, 10);

        PieData mChartData=generateDataPie();
        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.WHITE);
        // set data
        chart.setData(mChartData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0f);
        l.setYOffset(5f);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateY(900);

        /*mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        chart.setDescription("android厂商2016年手机占有率");
        chart.setHoleRadius(52f);//最内层的圆的半径
        chart.setTransparentCircleRadius(60f);//包裹内层圆的半径
        chart.setCenterText("Android\n厂商占比");
        chart.setCenterTextTypeface(mTf);
        chart.setCenterTextSize(18f);
        //是否使用百分比。true:各部分的百分比的和是100%。
        chart.setUsePercentValues(true);

        PieData pieData = generateDataPie();
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTypeface(mTf);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.parseColor("#FF6A6A"));
        // set data
        chart.setData(pieData);

        //获取右上角的描述结构的对象
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setYEntrySpace(10f);//相邻的entry在y轴上的间距
        l.setYOffset(30f);//第一个entry距离最顶端的间距

        // do not forget to refresh the chart
        // pieChart.invalidate();
        chart.animateXY(900, 900);*/

        /*// apply styling
        chart.getDescription().setEnabled(false);
        chart.setHoleRadius(52f);
        chart.setTransparentCircleRadius(57f);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextTypeface(mTf);
        chart.setCenterTextSize(9f);
        chart.setUsePercentValues(true);
        chart.setExtraOffsets(5, 10, 50, 10);

        PieData mChartData=generateDataPie();

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.RED);
        // set data
        chart.setData((PieData) mChartData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateY(900);*/
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("MPAndroidChart\ncreated by\nPhilipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.6f), 0, 14, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0);
        s.setSpan(new RelativeSizeSpan(.9f), 14, 25, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, 25, 0);
        s.setSpan(new RelativeSizeSpan(1.4f), 25, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 25, s.length(), 0);
        return s;
    }

    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries,"饼状图");
        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(d);
        return cd;
    }

}
