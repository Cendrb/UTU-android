package cz.cendrb.utu.utucomponents;

import android.util.Log;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Event {
	
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String LOCATION = "location";
	static final String PRICE = "price";
	static final String START = "eventStart";
	static final String END = "eventEnd";
	static final String PAY_DATE = "payDate";
	
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
	}
	
	public HashMap<String, String> getRecord()
	{
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

}
