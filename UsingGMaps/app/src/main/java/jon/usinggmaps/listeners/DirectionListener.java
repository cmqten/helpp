package jon.usinggmaps.listeners;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;

import jon.usinggmaps.BasicCharity;
import jon.usinggmaps.ListingsAdapter;
public class DirectionListener implements DirectionCallback {
    private ListingsAdapter.ViewHolder holder;
    private BasicCharity basicCharity;
    public DirectionListener(ListingsAdapter.ViewHolder holder, BasicCharity basicCharity){
        this.holder = holder;
        this.basicCharity = basicCharity;
    }
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);
            Leg leg = route.getLegList().get(0);
            Info duration = leg.getDuration();
            basicCharity.setTravelTime(duration.getText());
            holder.travelView.setText("Transit Time: " + basicCharity.getTravelTime());
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }
}
