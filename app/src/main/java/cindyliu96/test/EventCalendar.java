package cindyliu96.test;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.widget.TimePicker;


public class EventCalendar extends ListActivity {
    public Day clickedDay;
    ArrayList itemsList;
    ExtendedCalendarView calendar;
    ListView listViewCalendar;
    Context context = this;
    private EventAdapter eventAdapter;

    Dialog dialog;
    EditText eventTitle;
    EditText eventDescription;
    EditText startTime;
    EditText endTime;
    Button cancelButton;
    Button submitButton;
    private int hour;
    private int minute;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean startTimeSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_calendar);


        System.out.println("August : " + Calendar.AUGUST);

        listViewCalendar = getListView();
        calendar = (ExtendedCalendarView)findViewById(R.id.calendar);
        calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);

        calendar.setOnDayClickListener(new ExtendedCalendarView.OnDayClickListener() {
            int i = 0;
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
                clickedDay = day;
                getScheduleDetails(day);
                eventAdapter = new EventAdapter(context, R.layout.calendar_event, itemsList);
                listViewCalendar.setAdapter(eventAdapter);
                i++;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };

                if (i == 1) {
                    //Single click
                    System.out.println("single click");
                    handler.postDelayed(r, 350);
                } else if (i == 2) {
                    //Double click means that user wants to add a new event
                    System.out.println("you double clicked and the day is: " + clickedDay.getDay());
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.event_dialog);
                    dialog.setTitle("Add Event");
                    dialog.show();
                    startTime = (EditText) dialog.findViewById(R.id.startTime);
                    endTime =  (EditText) dialog.findViewById(R.id.endTime);

                    startTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startTimeSelected = true;
                            setTime();
                        }
                    });

                    endTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startTimeSelected = false;
                            setTime();
                        }
                    });

                    cancelButton = (Button) dialog.findViewById(R.id.cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    submitButton = (Button) dialog.findViewById(R.id.submitEvent);
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("adding new event");
                            eventTitle = (EditText) dialog.findViewById(R.id.title);
                            eventDescription = (EditText) dialog.findViewById(R.id.description);
                            startTime = (EditText) dialog.findViewById(R.id.startTime);
                            endTime = (EditText) dialog.findViewById(R.id.endTime);
                            addEvent(eventTitle.getText().toString(), eventDescription.getText().toString(), clickedDay.getYear(), clickedDay.getMonth(), clickedDay.getDay(),
                                    startHour, startMinute, endHour, endMinute);
                            dialog.dismiss();
                            calendar.refreshCalendar();
                        }
                    });

                    i = 0;
                }
            }
        });
        //Clear database
        //System.out.println("Rows deleted" + getContentResolver().delete(CalendarProvider.CONTENT_URI, null, null));
    }

    public void addEvent(String eventTitle, String eventDescription, int year, int month, int day, int sHour, int sMinute, int eHour, int eMinute) {
        // Adding events
        ContentValues values = new ContentValues();
        values.put(CalendarProvider.COLOR, Event.COLOR_BLUE);
        values.put(CalendarProvider.DESCRIPTION, eventDescription);
        values.put(CalendarProvider.EVENT, eventTitle);

        System.out.println("Event title: " + eventTitle);
        System.out.println("Event description: " + eventDescription);
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);
        System.out.println("Day: " + day);
        System.out.println("Start hour: " + sHour);
        System.out.println("Start minute: " + sMinute);
        System.out.println("End hour: " + eHour);
        System.out.println("End minute: " + eMinute);

        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();

        cal.set(year, month, day, sHour, sMinute);
        int julianDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds((tz.getOffset(cal.getTimeInMillis()))));

        values.put(CalendarProvider.START, cal.getTimeInMillis());
        values.put(CalendarProvider.START_DAY, julianDay);

        cal.set(year, month, day, eHour, eMinute);
        int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(),TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

        values.put(CalendarProvider.END, cal.getTimeInMillis());
        values.put(CalendarProvider.END_DAY, endDayJulian);

        Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI,values);
        System.out.println("Event added");

    }

    public void setTime() {
        Calendar myCurrentTime = Calendar.getInstance();
        hour = myCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = myCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog myTimePicker;
        myTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (startTimeSelected) {
                    startHour = selectedHour;
                    startMinute = selectedMinute;
                    startTime.setText(selectedHour + ":" + selectedMinute);
                } else {
                    endHour = selectedHour;
                    endMinute = selectedMinute;
                    endTime.setText(selectedHour + ":" + selectedMinute);
                }
            }
        }, hour, minute, true);
        myTimePicker.setTitle("Select start time");
        myTimePicker.show();
    }



    private void getScheduleDetails(Day day) {
        itemsList = new ArrayList();
        for (Event e : day.getEvents()) {
            System.out.println("Event description: " + e.getDescription());
            itemsList.add(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
