package jon.usinggmaps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import jon.usinggmaps.listeners.PlaceSelectListener;

import static android.content.ContentValues.TAG;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        ListingsAdapter.ItemClickListener, OnMapReadyCallback {

    public static Location location;
    private GoogleMap mMap;
    private ArrayList<Tab_fragment> charityTypes;
    private String[] charityTypesNames = {"All", "Community", "Education", "Health", "Religion", "Welfare", "Other"};

    private DrawerLayout mDrawerLayout;

    FloatingActionButton myLocation;
    private final int numberOfCharityTypes = 6;
    private int currentPosition = 0;

    SearchView nameSearch;
    LinearLayout autocompleteHolder;
    PlaceAutocompleteFragment autocompleteFragment;
    ToggleButton toggleCharityEvent;

    private boolean isEvents;

    public static final String SEARCH_FILTER_HINT = "Search Charity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        myLocation = (FloatingActionButton) findViewById(R.id.myLocationButton);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView myNavView = findViewById(R.id.nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setDisplayShowTitleEnabled(false);

        ToggleButton toggleAlarm = (ToggleButton) findViewById(R.id.toggle);

        toggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEvents = isChecked;

            }
        });



        myNavView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.createEvent:
                                MapsActivity.this.startActivity(new Intent(MapsActivity.this, createEventActivity.class));
                                break;

                            case R.id.nav_share:
                                MapsActivity.this.startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                                break;
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                }
        );



        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        charityTypes = new ArrayList<Tab_fragment>();
        for(int i = 0; i< numberOfCharityTypes; i ++){
            Tab_fragment temp = new Tab_fragment();
            charityTypes.add(temp);
            temp.setQueryURL(charityTypesNames[i]);
            adapter.addFragment(temp, charityTypesNames[i]);
        }

        ViewPager mViewPager =  findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                currentPosition = position;
                charityTypes.get(position).runSearch();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Toolbar toolbar1 = findViewById(R.id.toolbar);
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED || newState == SlidingUpPanelLayout.PanelState.DRAGGING){
                    toolbar1.setVisibility(View.GONE);
                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    toolbar1.setVisibility(View.VISIBLE);

                }

            }
        });
        prepareSearchLogic();
    }

    /**
     * Use this to grab the filter.
     * @return
     */
    public String getFilterField() {
        return nameSearch.getQuery().toString();
    }

    private void prepareSearchLogic() {
        nameSearch = findViewById(R.id.search_name);
        autocompleteHolder = findViewById(R.id.autocomplete_holder);
        toggleCharityEvent = findViewById(R.id.toggle);
        autocompleteHolder.setVisibility(View.GONE);

        nameSearch.setQueryHint(SEARCH_FILTER_HINT);

        nameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteHolder.setVisibility(View.VISIBLE);
                nameSearch.onActionViewExpanded();
            }
        });
        nameSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    autocompleteHolder.setVisibility(View.GONE);
                } else {
                    autocompleteHolder.setVisibility(View.VISIBLE);
                }
            }
        });
        nameSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                nameSearch.clearFocus();
                nameSearch.setQuery(s, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void redoSearch(View view){
        view.animate().rotationBy(360f);
        searchNearby();
    }

    public void toMyLocation(View view){
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(final GoogleMap Map) {
        mMap = Map;
        mMap.setPadding(40,40,40,200);
        mMap.setMinZoomPreference(15);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                nameSearch.onActionViewCollapsed();
                autocompleteHolder.setVisibility(View.GONE);
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectListener(mMap));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMyLocationButtonClickListener(this);
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location userlocation) {
                            if (userlocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlocation.getLatitude(), userlocation.getLongitude()), 15));
                                location = userlocation;
                                myLocation.setVisibility(View.VISIBLE);
                                searchNearby();
                            }
                            else {
                                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
                                googleApiClient.connect();

                                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create());
                                builder.setAlwaysShow(true);


                                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
                                        .setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                            @Override
                                            public void onResult(LocationSettingsResult result) {
                                                try {
                                                    result.getStatus().startResolutionForResult(MapsActivity.this, 0x1);
                                                } catch (IntentSender.SendIntentException e) {
                                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                                }
                                            }
                                        });
                            }

                        }
                    });
        }

    }



    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    private void searchNearby(){
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        for(Tab_fragment tab_fragment : charityTypes){
            tab_fragment.setMapValues(mMap,
                    Double.toString(bounds.northeast.latitude),
                    Double.toString(bounds.northeast.longitude),
                    Double.toString(bounds.southwest.latitude),
                    Double.toString(bounds.southwest.longitude),
                    isEvents


            );
        }
        charityTypes.get(currentPosition).runSearch();
    }
    @Override
    public void onItemClick(View view, BasicCharity bC) {

        //public void onItemClick(View view, int position, String id, String name) {
        if(isEvents) {
            Intent startNewActivity = new Intent(this, EventInfo.class);
            startNewActivity.putExtra("Id", bC.getId());
            startNewActivity.putExtra("Name", bC.getName());
            startNewActivity.putExtra("imageName", bC.getImageName());
            startNewActivity.putExtra("add", bC.getAdd());
            startNewActivity.putExtra("sDate", bC.getsDate());
            startNewActivity.putExtra("eDate", bC.geteDate());
            startNewActivity.putExtra("sTime", bC.getsTime());
            startNewActivity.putExtra("eTime", bC.geteTime());
            startNewActivity.putExtra("det", bC.getDet());
            startNewActivity.putExtra("eDate", bC.geteDate());
            startNewActivity.putExtra("Email", bC.getEmail());
            startNewActivity.putExtra("pName", bC.getpName());
            startNewActivity.putExtra("Cat", bC.getCat());

            startActivity(startNewActivity);
        }else{
            Intent startNewActivity = new Intent(this, DescriptionsActivity.class);
            startNewActivity.putExtra("Id", bC.getId());
            startNewActivity.putExtra("Name", bC.getName());
            startActivity(startNewActivity);
        }
    }
}

