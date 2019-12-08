package br.bdfs.event.message;

import br.bdfs.config.DfsConfig;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.ObjectHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author ltosc
 */
public class DfsEventMessage 
{
    private final String eventName;
    private final HashMap<String, String> eventParamList;
    
    public DfsEventMessage(String eventName, HashMap<String, String> eventParamList)
    {
        this.eventName = eventName;
        
        if (ObjectHelper.isNull(eventParamList)) 
        {
            this.eventParamList = new HashMap<>();
        }
        else
        {
            this.eventParamList = eventParamList;
        }
    }
    
    public static DfsEventMessage fromString(String msg) throws InvalidMessageException
    {
        if(ObjectHelper.strIsNullOrEmpty(msg))
        {
            throw new InvalidMessageException();
        }
        
        String[] msgSplit = msg.split(DfsConfig.MSG_NAME_AND_PARAM_SEPARATOR);
        
        String eventName = msgSplit[0].toUpperCase().trim();
        HashMap<String, String> paramList = null;
        
        if(msgSplit.length > 1)
        {
            paramList = new HashMap<>();
            String[] msgParamSplit = msgSplit[1].split(DfsConfig.MSG_PARAM_SEPARATOR);
            
            for(String param : msgParamSplit)
            {
                String[] paramSplit = param.split("=");
                
                if (paramSplit.length != 2) 
                {
                    throw new InvalidMessageException();
                }
                
                paramList.put(paramSplit[0].toUpperCase().trim(), paramSplit[1]);
            }
        }
        
        return new DfsEventMessage(eventName, paramList);
    }
    
    @Override
    public String toString()
    {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getEventName().toUpperCase().trim());
        strBuilder.append(DfsConfig.MSG_NAME_AND_PARAM_SEPARATOR);

        Iterator<Map.Entry<String, String>> eventParamIterator = getEventParamList().entrySet().iterator();
        Map.Entry<String, String> eventParamEntry;

        while (eventParamIterator.hasNext())
        {
            eventParamEntry = eventParamIterator.next();

            strBuilder.append(String.format("%s=%s", eventParamEntry.getKey().toUpperCase().trim(), eventParamEntry.getValue()));
            strBuilder.append(DfsConfig.MSG_PARAM_SEPARATOR);
        }

        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append('\n');
        
        return strBuilder.toString();
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @return the eventParamList
     */
    public HashMap<String, String> getEventParamList() {
        return eventParamList;
    }
}
