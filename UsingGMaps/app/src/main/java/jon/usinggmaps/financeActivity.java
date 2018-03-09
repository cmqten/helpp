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
                AlertDialog ad = new AlertDialog.Builder(financeActivity.this).setMessage("Ongoing Programs: "+myMap.get("ongoingPrograms")).create();
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
                String[] portions = {"expenses_total","expenses_management_and_admin",
                        "expenses_fundraising","expenses_other","expenses_charitable_program"};
                String[] labels = {"Management and Admin","Fundraising","Other","Charitable Programs","Expenses for year "};
                DrawPie(portions,labels);

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
                String[] portions= {"revenue_total","revenue_government_funding",
                        "revenue_non_receipted_donations","revenue_receipted_donations"
                ,"revenue_other"};
                String [] labels = {"Government Funding","Non Receipted Donations",
                        "Receipted Donations","Other","Revenues for year "};
               DrawPie(portions,labels);
            }
        });
    }
    public void DrawPie(String[] portions, String[] labels){
        /*Inflates the layout so we can use the PieChart*/
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.pie_chart,null);
        PieChart myPie = (PieChart)vi.findViewById(R.id.myPie);
        /*------------------------------------------------------------*/
        /*Generates the portions of the pie chart and sets the data*/
        List<PieEntry> entries = new ArrayList<>();
        Float revTot = Float.parseFloat(myMap.get(portions[0]));
        System.out.println(revTot);
        Float revGvnmtFnd = Float.parseFloat(myMap.get(portions[1]));
        Float revNonRecDon = Float.parseFloat(myMap.get(portions[2]));
        Float revRecDon = Float.parseFloat(myMap.get(portions[3]));
        Float revOther = Float.parseFloat(myMap.get(portions[4]));
        entries.add(new PieEntry(revGvnmtFnd / revTot,labels[0]));
        entries.add(new PieEntry(revNonRecDon / revTot,labels[1]));
        entries.add(new PieEntry(revRecDon / revTot,labels[2]));
        entries.add(new PieEntry(revOther / revTot, labels[3]));
        PieDataSet set = new PieDataSet(entries, "");
        int mycolors[] = {Color.parseColor("#68E861"),Color.parseColor("#61ABE8"),Color.parseColor("#E261E8")
                ,Color.parseColor("#E89F61")};
        set.setColors(mycolors);
        set.setDrawValues(false);
        PieData data = new PieData(set);
        /*-------------------------------------------------------------*/
        /*Sets up graph options---------------------------------------*/
        myPie.setLayoutParams(new LinearLayout.LayoutParams(500,600));
        myPie.setData(data);
        myPie.setExtraBottomOffset(40f);
        myPie.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText(labels[4]+ year);
        myPie.setDescription(description);
        /*----------------------------------------------------------------*/
        /*Sets up the legend------------------------------------*/
        Legend legend = myPie.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.getCalculatedLineSizes();
        /*-----------------------------------------------------------*/
        //Updates the graph
        myPie.invalidate();
        /*Puts the graph into an alert dialog---------------------------*/
        AlertDialog ad = new AlertDialog.Builder(financeActivity.this).setView(vi).create();
        ad.show();
        Display display =((WindowManager)getSystemService(financeActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height=display.getHeight();
        ad.getWindow().setLayout(width*9/10,height/2);
        /*-------------------------------------------------------------*/
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

