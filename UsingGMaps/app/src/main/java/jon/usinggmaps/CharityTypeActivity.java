package jon.usinggmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CharityTypeActivity extends AppCompatActivity {

    private boolean Community;
    private boolean Education;
    private boolean Health;
    private boolean Religion;
    private boolean Welfare;
    private boolean All;

    private View nextButton;
    private Button CommunityButton;
    private Button EducationButton;
    private Button ReligionButton;
    private Button HealthButton;
    private Button WelfareButton;
    private Button AllButton;

    Button[] buttons;
    boolean[] checked;
    int[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_type);
        nextButton = findViewById(R.id.nextButton);
        CommunityButton = findViewById(R.id.Community);
        EducationButton = findViewById(R.id.Education);
        HealthButton = findViewById(R.id.Health);
        ReligionButton = findViewById(R.id.Religion);
        WelfareButton = findViewById(R.id.Welfare);
        AllButton  = findViewById(R.id.All);
        nextButton.setVisibility(View.INVISIBLE);
        Community = false;
        Education = false;
        Health = false;
        Religion = false;
        Welfare = false;

    }
    public void onCommunity(View view){
        Community = !Community;
        if(Community){
            CommunityButton.setText("✔");
        }
        else{
            CommunityButton.setText(R.string.Community);
        }
        turnOnNext();

    }
    public void onEducation(View view){
        Education = !Education;
        if(Education){
            EducationButton.setText("✔");
        }
        else{
            EducationButton.setText(R.string.Education);
        }
        turnOnNext();
    }
    public void onHealth(View view){
        Health = !Health;
        if(Health){
            HealthButton.setText("✔");
        }
        else{
            HealthButton.setText(R.string.Health);
        }
        turnOnNext();
    }
    public void onReligion(View view){
        Religion = !Religion;
        if(Religion){
            ReligionButton.setText("✔");
        }
        else{
            ReligionButton.setText(R.string.Religion);
        }
        turnOnNext();


    }
    public void onWelfare(View view){
        Welfare = !Welfare;
        if(Welfare){
            WelfareButton.setText("✔");
        }
        else{
            WelfareButton.setText(R.string.Welfare);
        }
        turnOnNext();


    }
    public void onAll(View view){
        All = !All;
        if(All){
            Community = true;
            Education = true;
            Health = true;
            Religion = true;
            Welfare = true;

            CommunityButton.setText("✔");
            EducationButton.setText("✔");
            HealthButton.setText("✔");
            ReligionButton.setText("✔");
            WelfareButton.setText("✔");

            AllButton.setText(R.string.Clear);
        }
        else{
            Community = false;
            Education = false;
            Health = false;
            Religion = false;
            Welfare = false;

            CommunityButton.setText(R.string.Community);
            EducationButton.setText(R.string.Education);
            HealthButton.setText(R.string.Health);
            ReligionButton.setText(R.string.Religion);
            WelfareButton.setText(R.string.Welfare);

            AllButton.setText(R.string.All);
        }
        turnOnNext();

    }

    private void turnOnNext(){
        if(Community || Education || Health || Religion || Welfare){
            nextButton.setVisibility(View.VISIBLE);
        }else{
            nextButton.setVisibility(View.INVISIBLE);
        }
    }

    public void onNext(View view){
        Intent fromTypeActivity = getIntent();
        Intent goMaps = new Intent(this, MapsActivity.class);
        goMaps.putExtra("Community", Community);
        goMaps.putExtra("Education", Education);
        goMaps.putExtra("Health", Health);
        goMaps.putExtra("Religion", Religion);
        goMaps.putExtra("Welfare",Welfare);
        goMaps.putExtra("charitiesSelected", fromTypeActivity.getBooleanExtra("charitiesSelected",true));
        goMaps.putExtra("eventsSelected", fromTypeActivity.getBooleanExtra("eventsSelected",true));
        startActivity(goMaps);
    }


}
