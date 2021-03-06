// Bukkit Plugin "FAQBox" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.faqbox;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

public class Global
{
    public static FAQPlugin _plugin;
    public static Logger _log = Logger.getLogger("Minecraft");
    
    public static boolean hasPermission(CommandSender sender, String permission)
    {
        return (sender instanceof ConsoleCommandSender) ? true : sender.hasPermission(permission);
    }
    
    public static void sendMessage(CommandSender sender, String message)
    {
        sendMessage(sender, message, ChatColor.WHITE);
    }
    
    public static void sendMessage(CommandSender sender, String message, ChatColor prefix)
    {
        sendMessage(sender, message, prefix.toString());
    }
    
    public static void sendMessage(CommandSender sender, String message, String prefix)
    {
        if((sender == null) || (message == null))
        {
            return;
        }
        if(prefix == null)
        {
            prefix = "";
        }
        int offset = 0;
        int xpos = 0;
        int pos = 0;
        String part;
        while(true)
        {
            if(offset + 60 >= message.length())
            {
                sender.sendMessage(prefix + message.substring(offset).trim());
                break;
            }
            part = message.substring(offset, offset + 60);
            xpos = part.lastIndexOf(" ");
            pos = xpos < 0 ? 60 : xpos;
            part = message.substring(offset, offset + pos).trim();
            sender.sendMessage(prefix + part);
            offset += pos + (xpos < 0 ? 0 : 1);
        }
    }
    
    public static void warn()
    {
        _log.warning("[FAQBox] AN ERROR OCCURED! PLEASE SEND THE MESSAGE BELOW TO THE DEVELOPER!");
    }
}