package jon.usinggmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class TypeActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private boolean charitiesSelected;
    private boolean eventsSelected;
    private View nextButton;
    private Button charityButton;
    private Button eventsButton;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        charitiesSelected = false;
        eventsSelected = false;

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);


        MobileAds.initialize(this, "ca-app-pub-2650389847656790~2722040847");
        charityButton =  findViewById(R.id.charitiesButton);
        eventsButton =  findViewById(R.id.eventsButton);
        nextButton = findViewById(R.id.nextButton);


        turnOnNext();
    }

    public void onCharities(View view){
        charitiesSelected = !charitiesSelected;
        if(charitiesSelected){
            charityButton.setText("✔");
        }
        else{
            charityButton.setText(R.string.Charities);
        }
        turnOnNext();

    }

    public void onEvents(View view){
        eventsSelected = !eventsSelected;
        if(eventsSelected){
            eventsButton.setText("✔");
        }
        else{
            eventsButton.setText(R.string.Events);
        }
        turnOnNext();

    }
    public void onNext(View view){
        Intent goTypes = new Intent(this, CharityTypeActivity.class);
        goTypes.putExtra("charitiesSelected", charitiesSelected);
        goTypes.putExtra("eventsSelected",eventsSelected);
        startActivity(goTypes);
    }




    public void onWatchAds(View view){
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        mRewardedVideoAd.loadAd("ca-app-pub-2650389847656790~2722040847",
                new AdRequest.Builder()
                        .addTestDevice("1D8CDD67796A84D4192A2C11A2AD9E11")  // An example device ID
                        .build());

    }

    private void turnOnNext(){
        if(charitiesSelected || eventsSelected){
            nextButton.setVisibility(View.VISIBLE);
        }else{
            nextButton.setVisibility(View.GONE);
        }


    }


    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
