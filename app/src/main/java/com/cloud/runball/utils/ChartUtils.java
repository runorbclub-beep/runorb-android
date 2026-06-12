package com.cloud.runball.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.cloud.runball.basecomm.utils.TimeUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.cloud.runball.R;
import com.cloud.runball.widget.DataMarkerView;

import java.util.Calendar;
import java.util.List;

public class ChartUtils {
    public static int dayValue = 0;
    public static int weekValue = 1;
    public static int monthValue = 2;
    public static int labelsValue = 3;


    public static LineChart initChart(Context context,LineChart chart) {
        return initChart(context,chart,false,0);
    }
    /**
     * 初始化图表
     *
     * @param chart 原始图表
     * @return 初始化后的图表
     */
    public static LineChart initChart(Context context,LineChart chart,boolean enableDashedLine,int limitLine) {
        // 不显示数据描述
        chart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        chart.setNoDataText("暂无数据");
        // 不显示表格颜色
        chart.setDrawGridBackground(false);
        // 不可以缩放
        chart.setScaleEnabled(false);
        // 不显示y轴右边的值
        chart.getAxisRight().setEnabled(false);
        // 不显示图例
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        // 触摸
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        // 拖拽
        //chart.setDragEnabled(true);
        // 缩放
        //chart.setScaleEnabled(true);
        //chart.setPinchZoom(false);
        //能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        chart.setHighlightPerDragEnabled(true);
        // 向左偏移15dp，抵消y轴向右偏移的30dp
        //chart.setExtraLeftOffset(-15);

        // create marker to display box when values are selected
        DataMarkerView mv = new DataMarkerView(context, R.layout.marker_view);
        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        XAxis xAxis = chart.getXAxis();
        //绘制轴的标签
        xAxis.setDrawLabels(true);
        // 不显示x轴
        xAxis.setDrawAxisLine(false);
        //去掉左右边线
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawAxisLine(false);

        // 设置x轴数据的位置
        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTextColor(Color.WHITE);
        xAxis.setTextColor(Color.parseColor("#FF767779"));
        xAxis.setTextSize(14);
        //xAxis.setGridColor(Color.parseColor("#30FFFFFF"));
        xAxis.setDrawGridLines(false);//不绘制格网线
        //xAxis.setGridColor(Color.TRANSPARENT);
        // 设置x轴数据偏移量
        xAxis.setYOffset(5);
        xAxis.setDrawLabels(true);
        //xAxis.setEnabled(false);
        //设置legend 和X轴之间间距
        chart.setExtraBottomOffset(10f);

        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        //xAxis.setGridColor(Color.parseColor("#FFFDE833"));
        //设置竖线的显示样式为虚线
        //lineLength控制虚线段的长度
        //spaceLength控制线之间的空间
        //xAxis.enableGridDashedLine(10f, 10f, 0f);


        YAxis yAxis = chart.getAxisLeft();

        // 不显示y轴
        yAxis.setDrawAxisLine(false);
        // 设置y轴数据的位置
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 不从y轴发出横向直线
        yAxis.setDrawGridLines(false);
        //yAxis.setTextColor(Color.WHITE);
        yAxis.setTextColor(Color.parseColor("#FF767779"));

        yAxis.setTextSize(12);
        // 设置y轴数据偏移量
        yAxis.setXOffset(30);
        yAxis.setYOffset(3);   //-3
        yAxis.setAxisMinimum(0);


        //设置基线
        if(enableDashedLine){
            yAxis.setEnabled(true);
            yAxis.setAxisMaximum(9000f);
            yAxis.setAxisMinimum(0f);
            LimitLine hightLimit = new LimitLine(limitLine, String.valueOf(limitLine));
            hightLimit.setLineWidth(1f);  //设置线宽
            hightLimit.setTextSize(12f);   //设置限制线上label字体大小
            hightLimit.setLineColor(Color.parseColor("#C7B390")); //设置线的颜色
            hightLimit.setTextColor(Color.parseColor("#F0CF9F"));  //设置限制线上label字体的颜色
            hightLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);//标签位置
            hightLimit.enableDashedLine(5f,3f,0);  //设置虚线
            //hightLimit.disableDashedLine();
            yAxis.setDrawLimitLinesBehindData(true);  //这个很神奇开始看源码注释我有点懵逼，啥意思？看下文解释吧
            yAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
            yAxis.addLimitLine(hightLimit);
        }else{
            yAxis.setEnabled(false);
        }



        //Matrix matrix = new Matrix();
        // x轴缩放1.5倍
        //matrix.postScale(1.5f, 1f);
        // 在图表动画显示之前进行缩放
        //chart.getViewPortHandler().refresh(matrix, chart, false);
        // x轴执行动画
        //chart.animateX(2000);
        chart.invalidate();
        return chart;
    }

    /**
     * 设置图表数据
     *
     * @param mContext
     * @param chart
     * @param fillDrawable
     * @param lineColor
     * @param values
     */
    public static void setChartData(Context mContext, LineChart chart, int fillDrawable, int lineColor, boolean enMarkerView,List<Entry> values) {
        LineDataSet lineDataSet;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            //绘制轴的标签+//设置自定义格式，在绘制之前动态调整x的值。
           /* XAxis xAxis = chart.getXAxis();
            xAxis.setDrawLabels(true);
            xAxis.setLabelCount(values.size());
            ValueFormatter valueFormatter = new ValueFormatter() {
                private final String[] xLableList = new String[]{"110\n报警", "120\n报警", "119\n报警", "110\n报警", "120\n报警",
                        "119\n报警", "110\n报警", "120\n报警", "119\n报警"};

                @Override
                public String getFormattedValue(float value) {
                        return "";
                }
            };
            xAxis.setValueFormatter(valueFormatter);*/


            lineDataSet = new LineDataSet(values, "");
            // 设置曲线颜色
            lineDataSet.setColor(Color.parseColor("#FFFFFF"));
            // 设置平滑曲线
            //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
            //设置折线图填充
            lineDataSet.setDrawFilled(true);
            //设置线的颜色
            lineDataSet.setColors(lineColor);
            //设置数据点圆形的颜色
            //lineDataSet.setCircleColor(lineColor);
            //设置填充圆形中间的颜色
            //lineDataSet.setCircleColorHole(ColorAndImgUtils.ONE_COLOR);
            //设置是否在数据点中间显示一个孔
            //lineDataSet.setDrawCircleHole(true);
            //lineDataSet.setCircleHoleColor(Color.BLACK);
            //设置折线宽度
            lineDataSet.setLineWidth(1f);
            //设置折现点圆点半径
            //lineDataSet.setCircleRadius(4f);
            // 不显示坐标点的小圆点
            lineDataSet.setDrawCircles(false);
            // 不显示坐标点的数据
            lineDataSet.setDrawValues(false);

            if(enMarkerView){
                // 显示定位线  设置显示十字线，必须显示十字线，否则MarkerView不生效
                lineDataSet.setHighlightEnabled(true);
                //设置十字线颜色
                lineDataSet.setHighLightColor(lineColor);
                lineDataSet.setHighlightLineWidth(0.5f);
                lineDataSet.setDrawHighlightIndicators(false);
                //点击x轴不显示高亮
                //lineDataSet.setDrawHorizontalHighlightIndicator(false);
            }else{
                lineDataSet.setHighlightEnabled(false);
            }


            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(mContext, fillDrawable);
                lineDataSet.setFillDrawable(drawable);
            } else {
                lineDataSet.setFillColor(Color.BLACK);
            }
            LineData data = new LineData(lineDataSet);
            chart.setData(data);
            chart.invalidate();
        }
    }

    /**
     * 更新图表
     *
     * @param mContext
     * @param chart
     * @param fillDrawable
     * @param lineColor
     * @param values
     * @param valueType
     */
    public static void notifyDataSetChanged(Context mContext, LineChart chart, int fillDrawable, int lineColor, boolean enMarkerView, List<Entry> values,
                                            final int valueType) {
        //x轴自定义格式
        chart.getXAxis().setValueFormatter(new ValueFormatter() {

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xValuesProcess(valueType)[(int) value];
            }
            /**
             @Override public String getFormattedValue(float value, AxisBase axis) {
             super.getFormattedValue(value, axis);
             return xValuesProcess(valueType)[(int) value];
             }
             **/
        });

        chart.invalidate();
        setChartData(mContext, chart, fillDrawable, lineColor, enMarkerView,values);
    }

    /**
     * 更新图表
     *
     * @param mContext
     * @param chart
     * @param fillDrawable
     * @param lineColor
     * @param values
     * @param labels
     */
    public static void notifyDataSetChanged(Context mContext, LineChart chart, int fillDrawable, int lineColor, boolean enMarkerView, List<Entry> values,
                                            List<String> labels) {
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int) value % labels.size());
            }
        });

        //设置x轴的显示位置
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.invalidate();
        setChartData(mContext, chart, fillDrawable, lineColor, enMarkerView,values);
    }



    /**
     * x轴数据处理
     *
     * @param valueType 数据类型
     * @return x轴数据
     */
    private static String[] xValuesProcess(int valueType) {
        String[] week = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        if (valueType == dayValue) { // 今日
            String[] dayValues = new String[7];
            long currentTime = System.currentTimeMillis();
            for (int i = 6; i >= 0; i--) {
                dayValues[i] = TimeUtils.dateToString(currentTime, TimeUtils.dateFormat_day);
                currentTime -= (3 * 60 * 60 * 1000);
            }
            return dayValues;

        } else if (valueType == weekValue) { // 本周
            String[] weekValues = new String[7];
            Calendar calendar = Calendar.getInstance();
            int currentWeek = calendar.get(Calendar.DAY_OF_WEEK);

            for (int i = 6; i >= 0; i--) {
                weekValues[i] = week[currentWeek - 1];
                if (currentWeek == 1) {
                    currentWeek = 7;
                } else {
                    currentWeek -= 1;
                }
            }
            return weekValues;

        } else if (valueType == monthValue) { // 本月
            String[] monthValues = new String[7];
            long currentTime = System.currentTimeMillis();
            for (int i = 6; i >= 0; i--) {
                monthValues[i] = TimeUtils.dateToString(currentTime, TimeUtils.dateFormat_month);
                currentTime -= (4 * 24 * 60 * 60 * 1000);
            }
            return monthValues;
        } else if (valueType == labelsValue) {  //文本标签

        }
        return new String[]{};
    }
}
