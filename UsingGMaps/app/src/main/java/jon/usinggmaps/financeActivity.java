package jon.usinggmaps;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class financeActivity extends AppCompatActivity {
    static class Points{
        float y;
        BarEntry myEntry;
        public Points(float[] y, int count){
            float j=0f;
            for(int i =0; i<y.length;i++){
                j+=y[i];
            }
            this.y = j;
            myEntry = new BarEntry(count,this.y,y);
        }

        public float getY(){
            return this.y;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        BarChart myBarChart = (findViewById(R.id.chart));
        ArrayList<financeActivity.Points> myData = new ArrayList<financeActivity.Points>();
        ArrayList<financeActivity.Points> myData2 = new ArrayList<financeActivity.Points>();
        ArrayList<financeActivity.Points> myData3 = new ArrayList<financeActivity.Points>();

        float year1R[] = {12f,15f,10f,23f,12f};
        float year2R[] = {10f,11f,6f,19f,16f};
        float year3R[] = {12f,14f,7f,19f,17f};

        float year1E[] = {17f,12f,11f,12f,12f};
        float year2E[] = {12f,15f,10f,23f,12f};
        float year3E[] = {16f,18f,17f,24f,9f};

        myData.add(new financeActivity.Points(year1R,0));
        myData.add(new financeActivity.Points(year2R,1));
        myData.add(new financeActivity.Points(year3R,2));

        myData2.add(new financeActivity.Points(year1E,0));
        myData2.add(new financeActivity.Points(year2E,1));
        myData2.add(new financeActivity.Points(year3E,2));



        List<BarEntry> revenue = new ArrayList<>();
        List<BarEntry> expenses = new ArrayList<>();


        for( int i =0; i<myData.size();i++) {
            revenue.add(myData.get(i).myEntry);
            expenses.add(myData2.get(i).myEntry);

        }
        BarDataSet set1 = new BarDataSet(revenue, "Revenue");
        BarDataSet set2 = new BarDataSet(expenses, "Expenses");
        set1.setColor(Color.parseColor("#f45642"));
        set2.setColor(Color.parseColor("#fcce6c"));
        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset

        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth); // set the width of each bar
        myBarChart.setData(data);
        myBarChart.groupBars(2015f, groupSpace, barSpace); // perform the "explicit" grouping
        XAxis xAxis = myBarChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(2015f);
        xAxis.setAxisMaximum(2018f);
        xAxis.setLabelCount(3);
        final ArrayList<String> xLabel = new ArrayList<>();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                DecimalFormat mFormat = new DecimalFormat("####");
                return mFormat.format(value);
            }
        });

        myBarChart.setDoubleTapToZoomEnabled(false);
        myBarChart.invalidate();


        myBarChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e,Highlight h) {
                //fire up event

            }

            @Override
            public void onNothingSelected() {

            }
        });
        //----------------------------------------------------------------

    }

}

