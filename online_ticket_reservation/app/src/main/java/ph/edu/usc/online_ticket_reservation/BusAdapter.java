package ph.edu.usc.online_ticket_reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

public class BusAdapter extends BaseAdapter {
    private Context context;
    private List<Bus> busList;

    public BusAdapter(Context context, List<Bus> busList) {
        this.context = context;
        this.busList = busList;
    }

    @Override
    public int getCount() { return busList.size(); }
    @Override
    public Object getItem(int position) { return busList.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bus, parent, false);
        }

        Bus bus = busList.get(position);
        ((TextView) convertView.findViewById(R.id.textViewBus)).setText(bus.getName());

        return convertView;
    }
}