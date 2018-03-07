package jon.usinggmaps;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import jon.usinggmaps.listeners.DirectionListener;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {


    private ArrayList<BasicCharity> basicCharities;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Location location;
    public ListingsAdapter(Context context, ArrayList<BasicCharity> basicCharities) {
        this.basicCharities = basicCharities;
        this.mClickListener = (ItemClickListener)context;
        this.mInflater = LayoutInflater.from(context);


    }

    // inflates the row layout from xml when needed
    @Override
    public ListingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.basic_charity_card, null,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameView.setText(basicCharities.get(position).getName());
        holder.travelView.setText("Transit Time: " + basicCharities.get(position).getTravelTime());
        if(location != null) {
            GoogleDirection.withServerKey("AIzaSyBaqjL31XMR4F6BW2KcCmRsBa4E_MkYA74")
                    .from(new LatLng(location.getLatitude(), location.getLongitude()))
                    .to(basicCharities.get(position).getLatLng())
                    .transportMode(TransportMode.TRANSIT)
                    .unit(Unit.METRIC)
                    .execute(new DirectionListener(holder, basicCharities.get(position)));




        }
    }

    public void setLocation(Location location){
        this.location = location;
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

        @Override
        public void onClick(View view) {
            if (mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}

