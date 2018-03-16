package jon.usinggmaps;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class TestingActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    Tab_fragment tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    private void setupViewPager(ViewPager viewPager) {


        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        tf = new Tab_fragment();
        adapter.addFragment(tf, "TAB1");
        adapter.addFragment(new Tab_fragment(), "TAB2");
        adapter.addFragment(new Tab_fragment(), "TAB3");

        viewPager.setAdapter(adapter);
    }

}
