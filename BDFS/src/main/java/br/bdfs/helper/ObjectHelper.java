package br.bdfs.helper;

/**
 *
 * @author ltosc
 */
public class ObjectHelper 
{
    public static boolean isNull(Object obj)
    {
        return (obj == null);
    }
    
    public static boolean strIsNullOrEmpty(String str)
    {
        return (isNull(str) || str.isEmpty());
    }
}
