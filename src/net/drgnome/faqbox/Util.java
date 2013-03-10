// Bukkit Plugin "FAQBox" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.faqbox;

import java.io.*;
import java.net.*;
import java.lang.reflect.Array;

public class Util
{
    public static boolean hasUpdate(String name, String version)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection)(new URL("http://dev.drgnome.net/version.php?t=" + name)).openConnection();            
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; JVM)");                        
            con.setRequestProperty("Pragma", "no-cache");
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            StringBuilder stringb = new StringBuilder();
            if((line = reader.readLine()) != null)
            {
                stringb.append(line);
            }
            String vdigits[] = version.toLowerCase().split("\\.");
            String cdigits[] = stringb.toString().toLowerCase().split("\\.");
            int max = vdigits.length > cdigits.length ? cdigits.length : vdigits.length;
            int a = 0;
            int b = 0;
            for(int i = 0; i < max; i++)
            {
                try
                {
                    a = Integer.parseInt(cdigits[i]);
                }
                catch(Throwable t1)
                {
                    char c[] = cdigits[i].toCharArray();
                    for(int j = 0; j < c.length; j++)
                    {
                        a += (c[j] << ((c.length - (j + 1)) * 8));
                    }
                }
                try
                {
                    b = Integer.parseInt(vdigits[i]);
                }
                catch(Throwable t1)
                {
                    char c[] = vdigits[i].toCharArray();
                    for(int j = 0; j < c.length; j++)
                    {
                        b += (c[j] << ((c.length - (j + 1)) * 8));
                    }
                }
                if(a > b)
                {
                    return true;
                }
                else if(a < b)
                {
                    return false;
                }
                else if((i == max - 1) && (cdigits.length > vdigits.length))
                {
                    return true;
                }
            }
        }
        catch(Throwable t)
        {
        }
        return false;
    }
    
    public static <T> T[] cut(T[] objects, int start)
    {
        return cut(objects, start, objects.length);
    }
    
    public static <T> T[] cut(T[] objects, int start, int offset)
    {
        T[] array = createGenericArray((Class<T>)objects.getClass().getComponentType(), offset - start);
        for(int i = start; i < offset; i++)
        {
            array[i - start] = objects[i];
        }
        return array;
    }
    
    public static String implode(String glue, String... parts)
    {
        if((glue == null) || (parts.length <= 0))
        {
            return "";
        }
        String string = parts[0];
        for(int i = 1; i < parts.length; i++)
        {
            string += glue + parts[i];
        }
        return string;
    }
    
    public static <T> T[] createGenericArray(Class<T> clazz)
    {
        return createGenericArray(clazz, 0);
    }
    
    public static <T> T[] createGenericArray(Class<T> clazz, int... size)
    {
        for(int i = 0; i < size.length; i++)
        {
            if(size[i] < 0)
            {
                size[i] = 0;
            }
        }
        try
        {
            return (T[])(Array.newInstance(clazz, size));
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            return (T[])null;
        }
    }
}