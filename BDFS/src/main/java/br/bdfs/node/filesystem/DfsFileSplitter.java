package br.bdfs.node.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class DfsFileSplitter 
{
    public static List<byte[]> split(byte[] data, int dataSplitCount)
    {
        int dataLength = data.length;
        int dataSplitLength = Math.round((dataLength / (float)dataSplitCount));
        
        int offset = 0;
        List<byte[]> dataList = new ArrayList<>();
        
        for(int i = 0; i < dataSplitCount; i++)
        {
            byte[] dataSplit;
            
            if((offset + dataSplitLength) < dataLength)
            {
                dataSplit = new byte[dataSplitLength];
            }
            else
            {
                dataSplit = new byte[dataLength - offset];
            }
            
            for(int j = 0; j < dataSplit.length; j++)
            {
                dataSplit[j] = data[offset++];
            }
            
            dataList.add(dataSplit);
        }
        
        return dataList;
    }
    
    public static byte[] merge(List<byte[]> dataList)
    {
        List<Byte> byteList = new ArrayList<>();
        
        for(byte[] dataSplit : dataList)
        {
            for(int i = 0; i < dataSplit.length; i++)
            {
                byteList.add(dataSplit[i]);
            }
        }
        
        byte[] data = new byte[byteList.size()];
        
        for(int i = 0; i < data.length; i++)
        {
            data[i] = byteList.get(i);
        }
        
        return data;
    }
}
