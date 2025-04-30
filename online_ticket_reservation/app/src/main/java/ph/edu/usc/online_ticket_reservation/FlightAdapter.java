package ph.edu.usc.online_ticket_reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

public class FlightAdapter extends BaseAdapter {
    private Context context;
    private List<Flight> flightList;
    private OnFlightSelectListener listener;

    public interface OnFlightSelectListener {
        void onFlightSelected(Flight flight);
    }

    public FlightAdapter(Context context, List<Flight> flightList) {
        this.context = context;
        this.flightList = flightList;

    }

    @Override
    public int getCount() { return flightList.size(); }
    @Override
    public Object getItem(int position) { return flightList.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_flight, parent, false);
        }

        Flight flight = flightList.get(position);

        TextView textViewFlight = convertView.findViewById(R.id.textViewFlight);
        TextView textViewTimes = convertView.findViewById(R.id.textViewTimes);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        Button buttonBook = convertView.findViewById(R.id.buttonBook);

        textViewFlight.setText(flight.getAirline() + " - " + flight.getFlightNumber());
        textViewTimes.setText(flight.getDepartureTime() + " - " + flight.getArrivalTime() + " (" + flight.getDuration() + ")");
        textViewPrice.setText(flight.getPrice());

        buttonBook.setOnClickListener(v -> listener.onFlightSelected(flight));

        return convertView;
    }
}
