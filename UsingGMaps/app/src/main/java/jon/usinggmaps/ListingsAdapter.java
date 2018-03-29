package jon.usinggmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import jon.usinggmaps.listeners.DirectionListener;

import static android.content.ContentValues.TAG;
import static jon.usinggmaps.MapsActivity.location;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {


    private ViewGroup parent;
    private Bitmap defaultIcon;
    private String charityName;

    private HashMap<String, Bitmap> logoMap = new HashMap<>();

    private ArrayList<BasicCharity> basicCharities;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public ListingsAdapter(Context context, ArrayList<BasicCharity> basicCharities) {
        this.basicCharities = basicCharities;
        this.mClickListener = (ItemClickListener)context;
        this.mInflater = LayoutInflater.from(context);


    }

    // inflates the row layout from xml when needed
    @Override
    public ListingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // render default icon
        defaultIcon = BitmapFactory.decodeResource(parent.getResources(),
                R.drawable.charity_icon);

        this.parent = parent;
        return new ViewHolder(mInflater.inflate(R.layout.basic_charity_card, null,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.v(TAG, basicCharities.get(position).getName() + " is in? " +logoMap.containsKey(basicCharities.get(position).getName()));
        if(!logoMap.containsKey(basicCharities.get(position).getName())){
            holder.myImageView.setImageBitmap(defaultIcon);
            new stupidOse().execute(position);
        }else{
            holder.myImageView.setImageBitmap(basicCharities.get(position).getLogo());
            Log.v(TAG, "Setting " + basicCharities.get(position).getName());
        }

        holder.nameView.setText(basicCharities.get(position).getName());
        holder.travelView.setText("Transit Time: " + basicCharities.get(position).getTravelTime());

        if(location != null && basicCharities.get(position).getTravelTime().equals("N/A")) {
            GoogleDirection.withServerKey("AIzaSyBaqjL31XMR4F6BW2KcCmRsBa4E_MkYA74")
                    .from(new LatLng(location.getLatitude(), location.getLongitude()))
                    .to(basicCharities.get(position).getLatLng())
                    .transportMode(TransportMode.TRANSIT)
                    .unit(Unit.METRIC)
                    .execute(new DirectionListener(holder, basicCharities.get(position)));
        }



    }

    @Override
    public int getItemCount() {
        return basicCharities.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameView;
        public ImageView myImageView;
        public TextView travelView;
        public ViewHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.Logo);
            nameView = itemView.findViewById(R.id.charity_name);
            travelView = itemView.findViewById(R.id.travel_time);
            itemView.setOnClickListener(this);
        }
//view, getAdapterPosition(),
                        //basicCharities.get(getAdapterPosition()).getId(),
                        //basicCharities.get(getAdapterPosition()).getName()
        @Override
        public void onClick(View view) {
            if (mClickListener != null){
                mClickListener.onItemClick(view,basicCharities.get(getAdapterPosition()));

            }
        }
    }

    // parent activity will implement this method to respond to click events
    //int position, String id, String name
    public interface ItemClickListener {
        void onItemClick(View view,BasicCharity bC );
    }

    private class stupidOse extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... arguments) {

            try {
                Log.v(TAG, basicCharities.get(arguments[0]).getName().trim());
                String encodedCharity = URLEncoder.encode(basicCharities.get(arguments[0]).getName().trim(), "UTF-8");
                //encodedCharity = encodedCharity.replace("+", "%20");
                String myUrl = "http://72.139.72.18:4000/getData/" +
                        basicCharities.get(arguments[0]).getId() + "/" + encodedCharity;
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = httpclient.execute(new HttpGet(myUrl));
                org.apache.http.StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);

                    // handle output from server
                    String rawData = out.toString();

                    // get json out of summary
                    JSONObject data = new JSONObject(rawData);

                    // set result default
                    String result = "None";

                    // set logo if error is empty
                    if (data.getString("error").equals("")) {
                        result = data.getString("Image");
                        Log.v(TAG, basicCharities.get(arguments[0]).getName().trim() + ", Link: "+result);
                    }

                    // check if logo is chimp
                    // don't show chimp links
                    if (result.contains("chimp.net")){
                        result = "";
                    }

                    if (result.equals("None")) {
                        // server error
                        Log.v(TAG, "Server error, Could not get logo for charity");
                    } else {
                        // get img from link

                        // check if its a facebook link, before we add size param
                        if (!result.toLowerCase().contains("scontent")){
                            result = result + "?size=500";
                        }
                        URL url = new URL(result);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        logoMap.put(basicCharities.get(arguments[0]).getName().trim(), bmp);
                        Log.v(TAG, "past here");
                        basicCharities.get(arguments[0]).setLogo(bmp);
                    }
                    out.close();

                } else {
                    //Closes the connection.
                    Log.v(TAG, "Could not connect to server, Could not get charity");
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }

            return "";
        }
    }

}

