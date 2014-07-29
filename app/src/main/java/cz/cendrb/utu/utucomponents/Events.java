package cz.cendrb.utu.utucomponents;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.cendrb.utu.R;

public class Events {
    public List<Event> events;
    public static String[] from;
    public static int[] to;

    public Events(List<Event> Events) {
        this.events = Events;
        setFromAndTo();
    }

    public Events() {
        setFromAndTo();
        events = new ArrayList<Event>();
    }

    public void clearAndLoad(Element inEvents) {
        this.events.clear();
        for (int counter = inEvents.getChildNodes().getLength() - 1; counter > 0; counter--) {
            Node node = inEvents.getChildNodes().item(counter);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Event event = new Event((Element) node);
                this.events.add(event);
            }
        }
    }

    private void setFromAndTo() {
        from = new String[]{Event.TITLE, Event.DESCRIPTION, Event.LOCATION, Event.START, Event.END};
        to = new int[]{R.id.eventTitle, R.id.eventDescription, R.id.eventLocation, R.id.eventFrom, R.id.eventTo};
    }

    public List<HashMap<String, String>> getListForAdapter() {
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        for (Event event : events) {
            data.add(event.getRecord());
        }

        return data;
    }
}
