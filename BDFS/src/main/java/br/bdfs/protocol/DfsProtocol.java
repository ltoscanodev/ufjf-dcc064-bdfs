package br.bdfs.protocol;

import br.bdfs.event.dispatcher.DfsEventService;
import java.util.HashMap;
import br.bdfs.event.dispatcher.DfsNotifyEvent;

/**
 *
 * @author ltosc
 */
public abstract class DfsProtocol implements DfsNotifyEvent
{
    private final DfsEventService eventService;
    
    public DfsProtocol(HashMap<String, Class> eventList, DfsEventService eventService)
    {
        this.eventService = eventService;
        
        for(String eventName : eventList.keySet())
        {
            eventService.registerEvent(eventName, this);
        }
    }

    /**
     * @return the eventService
     */
    public DfsEventService getEventService() {
        return eventService;
    }
}
