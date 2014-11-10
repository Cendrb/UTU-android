package cz.cendrb.utu.utucomponents;

import android.util.Log;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Event {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LOCATION = "location";
    public static final String PRICE = "price";
    public static final String START = "eventStart";
    public static final String END = "eventEnd";
    public static final String PAY_DATE = "payDate";
    public static final String ID = "id";

    int id;
    String title;
    String description;
    String location;
    double price;
    Date start;
    Date end;
    Date pay;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

    public Event(Element data) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        title = data.getAttribute(TITLE);
        description = data.getAttribute(DESCRIPTION);
        location = data.getAttribute(LOCATION);
        price = Double.parseDouble(data.getAttribute(PRICE));
        try {
            start = df.parse(data.getAttribute(START));
            end = df.parse(data.getAttribute(END));
            pay = df.parse(data.getAttribute(PAY_DATE));
        } catch (ParseException e) {
            Log.e("Event", "Unknown format date" + e.getMessage());
            start = new Date();
            end = new Date();
            pay = new Date();
            e.printStackTrace();
        }
        id = Integer.parseInt(data.getAttribute(ID));
    }

    public HashMap<String, String> getRecord() {
        HashMap<String, String> record = new HashMap<String, String>();
        record.put(TITLE, title);
        record.put(DESCRIPTION, description);
        record.put(LOCATION, location);
        record.put(PRICE, String.valueOf(price));
        record.put(START, dateFormat.format(start));
        record.put(END, dateFormat.format(end));
        record.put(PAY_DATE, dateFormat.format(pay));
        return record;
    }

    public Date getPay() {
        return pay;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
