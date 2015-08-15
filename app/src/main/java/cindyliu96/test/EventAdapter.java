package cindyliu96.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tyczj.extendedcalendarview.Event;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by yunjie on 8/3/2015.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;

    public EventAdapter(Context context, int textViewResourceId, ArrayList<Event> eventList) {
        super(context, textViewResourceId, eventList);
        this.events = eventList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.calendar_event, null);


        Event e = events.get(position);

        TextView userName = (TextView) v.findViewById(R.id.userName);
        TextView eventName = (TextView) v.findViewById(R.id.eventTitle);
        TextView eventDescription = (TextView) v.findViewById(R.id.eventDescription);
        TextView time = (TextView) v.findViewById(R.id.time);
        TextView secondTime = (TextView) v.findViewById(R.id.secondTime);

//        if (username != null) {
//            username.setText("Name");
//        }

        System.out.println("The user who made this event is: " + e.getUser());

        userName.setText(e.getUser());
        if (eventName != null) {
            eventName.setText(e.getTitle());
        }
        if (eventDescription != null) {
            eventDescription.setText(e.getDescription());
        }
        if (time != null) {
            time.setText(e.getStartDate("h:mm a"));
        }

        if (secondTime != null) {
            secondTime.setText(" - " + e.getEndDate("h:mm a"));
        }
        return v;
    }



}


