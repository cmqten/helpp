package jon.usinggmaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class financeActivity extends AppCompatActivity implements Observer {
    private String year;
    HashMap<String, String> myMap;
    Context activity;
    ArrayList<String> dates;
    Observer obs;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        dates = new ArrayList<>();
         year = "2018";
         obs = this;
        ProgressDialog myDiag = new ProgressDialog(this);
        FinancialAsync myAsync = new FinancialAsync("101676864RR0001",myDiag,this,null);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(year!="2018"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(0));}
                year = "2018";
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(year!="2017"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(1));}
                year = "2017";
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(year!="2016"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(2));}
                year = "2016";
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(year!="2015"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(3));}
                year = "2015";

            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(year!="2014"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(4));}
                year = "2014";
            }
        });
        findViewById(R.id.activities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.pie_chart,null);
                //TextView myText = (TextView)vi.findViewById(R.id.myText);
                //myText.setText(myMap.get("ongoingPrograms"));
                AlertDialog ad = new AlertDialog.Builder(financeActivity.this).setMessage("Ongoing Programs: "+myMap.get("ongoingPrograms")).create();
                //.setView(myPie)
                ad.show();
                Display display =((WindowManager)getSystemService(financeActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height=display.getHeight();
                ad.getWindow().setLayout(width*9/10,height/2);

            }
        });
        findViewById(R.id.expenses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is expenses

                LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.pie_chart,null);
                PieChart myPie = (PieChart)vi.findViewById(R.id.myPie);
                List<PieEntry> entries = new ArrayList<>();
                Float expTot = Float.parseFloat(myMap.get("expenses_total"));
                Float expMngmntAd = Float.parseFloat(myMap.get("expenses_management_and_admin"));

                Float expFnd = Float.parseFloat(myMap.get("expenses_fundraising"));

                Float expOthr = Float.parseFloat(myMap.get("expenses_other"));

                Float expChrtPrg = Float.parseFloat(myMap.get("expenses_charitable_program"));

                entries.add(new PieEntry(expMngmntAd / expTot, "Management and Admin"));
                entries.add(new PieEntry(expFnd / expTot, "Fundraising"));
                entries.add(new PieEntry(expOthr / expTot, "Other"));
                entries.add(new PieEntry(expChrtPrg / expTot, "Charitable Programs"));
                PieDataSet set = new PieDataSet(entries, "");
                int mycolors[] = {Color.parseColor("#68E861"),Color.parseColor("#61ABE8"),Color.parseColor("#E261E8")
                ,Color.parseColor("#E89F61")};
                set.setColors(mycolors);
                set.setDrawValues(false);
                PieData data = new PieData(set);
                myPie.setLayoutParams(new LinearLayout.LayoutParams(500,600));
                myPie.setData(data);
                myPie.setExtraBottomOffset(40f);
                myPie.setDrawEntryLabels(false);
                Description description = new Description();
                description.setText("Expenses for year "+ year);
                myPie.setDescription(description);
                Legend legend = myPie.getLegend();
                //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                //legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setWordWrapEnabled(true);
                legend.setDrawInside(false);
                legend.getCalculatedLineSizes();

                //myPie.setExtraTopOffset(10f);
                myPie.invalidate();

                //myPie.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                AlertDialog ad = new AlertDialog.Builder(financeActivity.this).setView(vi).create();
                        //.setView(myPie)

                ad.show();
                Display display =((WindowManager)getSystemService(financeActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height=display.getHeight();
                ad.getWindow().setLayout(width*9/10,height/2);

            }
        });
        findViewById(R.id.compensation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is compensation
            }
        });
        findViewById(R.id.revenue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Goes here");
                LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.pie_chart,null);
                PieChart myPie = (PieChart)vi.findViewById(R.id.myPie);
                List<PieEntry> entries = new ArrayList<>();
                Float revTot = Float.parseFloat(myMap.get("revenue_total"));
                System.out.println(revTot);
                Float revGvnmtFnd = Float.parseFloat(myMap.get("revenue_government_funding"));
                Float revNonRecDon = Float.parseFloat(myMap.get("revenue_non_receipted_donations"));
                Float revRecDon = Float.parseFloat(myMap.get("revenue_receipted_donations"));
                Float revOther = Float.parseFloat(myMap.get("revenue_other"));
                entries.add(new PieEntry(revGvnmtFnd / revTot, "Government Funding"));
                entries.add(new PieEntry(revNonRecDon / revTot, "Non Receipted Donations"));
                entries.add(new PieEntry(revRecDon / revTot, "Receipted Donations"));
                entries.add(new PieEntry(revOther / revTot, "Other"));
                PieDataSet set = new PieDataSet(entries, "");
                int mycolors[] = {Color.parseColor("#68E861"),Color.parseColor("#61ABE8"),Color.parseColor("#E261E8")
                        ,Color.parseColor("#E89F61")};
                set.setColors(mycolors);
                set.setDrawValues(false);

                PieData data = new PieData(set);
                myPie.setLayoutParams(new LinearLayout.LayoutParams(500,600));
                myPie.setData(data);
                myPie.setExtraBottomOffset(40f);
                myPie.setDrawEntryLabels(false);
                Description description = new Description();
                description.setText("Revenues for year "+ year);
                myPie.setDescription(description);
                Legend legend = myPie.getLegend();
                //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                //legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setWordWrapEnabled(true);
                legend.setDrawInside(false);
                legend.getCalculatedLineSizes();

                //myPie.setExtraTopOffset(10f);
                myPie.invalidate();

                //myPie.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                AlertDialog ad = new AlertDialog.Builder(financeActivity.this).setView(vi).create();
                //.setView(myPie)

                ad.show();
                Display display =((WindowManager)getSystemService(financeActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height=display.getHeight();
                ad.getWindow().setLayout(width*9/10,height/2);
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        myMap = (HashMap<String,String>)o;

            String mydates = myMap.get("financialDates");
            mydates = mydates.substring(1,mydates.length()-1);
            String[] dates = mydates.split(", ");
            for(int i = 0 ; i< dates.length;i++){
                this.dates.add(dates[i].substring(1,dates[i].length()-1));
            }
        
    }
}

