// Bukkit Plugin "FAQBox" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.faqbox;

import java.io.*;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import static net.drgnome.faqbox.Global.*;

public class FAQPlugin extends JavaPlugin implements Runnable, Listener
{
    private static final String _version = "1.0.0";
    private static final String _color = "" + ChatColor.COLOR_CHAR;
    
    private HashMap<String, String> _messages = new HashMap<String, String>();
    private boolean _update = false;
    private int _upTick = 72000;
    
    public FAQPlugin()
    {
        super();
        _plugin = this;
    }
    
    public void onEnable()
    {
        _log.info("Enabling FAQBox v" + _version);
        Config.reload();
        saveConfig();
        loadMessages();
        if(Config.bool("check-update"))
        {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, this, 0L, 1L);
            getServer().getPluginManager().registerEvents(this, this);
        }
    }
    
    public void onDisable()
    {
        _log.info("Disabling FAQBox v" + _version);
    }
    
    public void reloadConfig()
    {
        super.reloadConfig();
        Config.reload();
        saveConfig();
    }
    
    private void loadMessages()
    {
        _messages = new HashMap<String, String>();
        try
        {
            File file = new File(getDataFolder(), "messages.ini");
            if(!file.exists())
            {
                getDataFolder().mkdirs();
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null)
            {
                String[] parts = line.split("=", 2);
                if(parts.length == 2)
                {
                    _messages.put(parts[0], parts[1]);
                }
            }
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    private void saveMessages()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getDataFolder(), "messages.ini")));
            for(Map.Entry<String, String> entry : _messages.entrySet())
            {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
            writer.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPermission("faqbox.update"))
        {
            sendMessage(player, "There is an update available for FAQBox.", ChatColor.YELLOW);
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!hasPermission(sender, "faqbox.manage") && !hasPermission(sender, "faqbox.send"))
        {
            sendMessage(sender, "You're not allowed to use this command.", ChatColor.RED);
        }
        else if((args.length == 0) || args[0].equalsIgnoreCase("help"))
        {
            sendMessage(sender, "----- FAQBox Help -----", ChatColor.GREEN);
            if(hasPermission(sender, "faqbox.manage"))
            {
                sendMessage(sender, "/faq add <id> <message> - Add a new message", ChatColor.AQUA);
                sendMessage(sender, "/faq update <id> <message> - Update a message", ChatColor.AQUA);
                sendMessage(sender, "/faq remove <id> - Remove a message", ChatColor.AQUA);
                sendMessage(sender, "/faq reload - Reload the messages", ChatColor.AQUA);
            }
            if(hasPermission(sender, "faqbox.send"))
            {
                sendMessage(sender, "/faq list - List all available message IDs", ChatColor.AQUA);
                sendMessage(sender, "/faq send <player1> <messageID> - Send a message to a player", ChatColor.AQUA);
                sendMessage(sender, "/faq send <player1> <player2> ... <message1[,message2[,message3]]> - Send one or more message to one or more players", ChatColor.AQUA);
            }
        }
        else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("remove"))
        {
            if(!hasPermission(sender, "faqbox.manage"))
            {
                sendMessage(sender, "You're not allowed to use this command.", ChatColor.RED);
            }
            else if(args[0].equalsIgnoreCase("reload"))
            {
                loadMessages();
                sendMessage(sender, "Messages reloaded.", ChatColor.GREEN);
            }
            else if(args[0].equalsIgnoreCase("add"))
            {
                if(args.length < 3)
                {
                    sendMessage(sender, "Too few arguments.", ChatColor.RED);
                }
                else if(_messages.containsKey(args[1]))
                {
                    sendMessage(sender, "This message ID exist already.", ChatColor.RED);
                }
                else if(args[1].contains(","))
                {
                    sendMessage(sender, "Message IDs must not contain commas.", ChatColor.RED);
                }
                else
                {
                    _messages.put(args[1], Util.implode(" ", Util.cut(args, 2)));
                    saveMessages();
                    sendMessage(sender, "Added message.", ChatColor.GREEN);
                }
            }
            else if(args[0].equalsIgnoreCase("update"))
            {
                if(args.length < 3)
                {
                    sendMessage(sender, "Too few arguments.", ChatColor.RED);
                }
                else if(!_messages.containsKey(args[1]))
                {
                    sendMessage(sender, "This message ID doesn't exist.", ChatColor.RED);
                }
                else
                {
                    _messages.put(args[1], Util.implode(" ", Util.cut(args, 2)));
                    saveMessages();
                    sendMessage(sender, "Updated message.", ChatColor.GREEN);
                }
            }
            else if(args[0].equalsIgnoreCase("remove"))
            {
                if(args.length < 2)
                {
                    sendMessage(sender, "Too few arguments.", ChatColor.RED);
                }
                else if(!_messages.containsKey(args[1]))
                {
                    sendMessage(sender, "This message ID doesn't exist.", ChatColor.RED);
                }
                else
                {
                    _messages.remove(args[1]);
                    saveMessages();
                    sendMessage(sender, "Removed message.", ChatColor.GREEN);
                }
            }
        }
        else if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("send"))
        {
            if(!hasPermission(sender, "faqbox.send"))
            {
                sendMessage(sender, "You're not allowed to use this command.", ChatColor.RED);
            }
            else if(args[0].equalsIgnoreCase("list"))
            {
                for(String key : _messages.keySet().toArray(new String[0]))
                {
                    sendMessage(sender, key, ChatColor.AQUA);
                }
            }
            else if(args[0].equalsIgnoreCase("send"))
            {
                if(args.length < 3)
                {
                    sendMessage(sender, "Too few arguments.", ChatColor.RED);
                }
                else
                {
                    ArrayList<String> list = new ArrayList<String>();
                    for(String key : args[args.length - 1].split(","))
                    {
                        if(!_messages.containsKey(key))
                        {
                            sendMessage(sender, "Unknown message ID: " + key, ChatColor.RED);
                        }
                        else
                        {
                            list.add(_messages.get(key).replace("&", _color).replace(_color + _color, "&"));
                        }
                    }
                    String[] msgs = list.toArray(new String[0]);
                    ArrayList<String> playernames = new ArrayList<String>();
                    for(int i = 1; i < args.length - 1; i++)
                    {
                        Player player = Bukkit.getPlayer(args[i]);
                        if(player == null)
                        {
                            sendMessage(sender, "Can't find player " + args[i], ChatColor.RED);
                        }
                        else
                        {
                            playernames.add(player.getName());
                            for(String msg : msgs)
                            {
                                sendMessage(player, msg.replace("%player", player.getName()));
                            }
                        }
                    }
                    String info = "[FAQBox] " + sender.getName() + " sent messages " + Util.implode(", ", Util.cut(msgs, 0, msgs.length - 1)) + " and " + msgs[msgs.length - 1] + " to players " + Util.implode(", ", Util.cut(args, 1, args.length - 2)) + " and " + args[args.length - 2] + ".";
                    for(Player player : Bukkit.getOnlinePlayers())
                    {
                        if(player.hasPermission("faqbox.notify") && !player.getName().equals(sender.getName()))
                        {
                            sendMessage(player, info, ChatColor.YELLOW);
                        }
                    }
                    sendMessage(sender, "Messages sent.", ChatColor.GREEN);
                }
            }
        }
        return true;
    }
    
    public void run()
    {
        tick();
    }
    
    public void tick()
    {
        if(!_update)
        {
            _upTick++;
            if(_upTick >= 72000)
            {
                checkUpdate();
            }
        }
    }
    
    public boolean checkUpdate()
    {
        _update = Util.hasUpdate("faqbox", _version);
        _upTick = 0;
        return _update;
    }
}