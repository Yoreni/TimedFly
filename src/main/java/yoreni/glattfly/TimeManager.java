package yoreni.glattfly;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import yoreni.glattfly.util.Yml;

import java.util.HashMap;
import java.util.UUID;

public class TimeManager implements Listener
{
    private HashMap<UUID, Integer> times = new HashMap<UUID, Integer>();
    private Yml data;

    public TimeManager()
    {
        data = new Yml(Glattfly.getInstance(),"times");
        Bukkit.getPluginManager().registerEvents(this, Glattfly.getInstance());
    }

    /**
     * gets the time of a player
     * returns -1 if the player isnt in the list
     *
     * @param player
     * @return the time in ticks
     */
    public int getTime(Player player)
    {
        if(times.containsKey(player.getUniqueId()))
        {
            return times.get(player.getUniqueId());
        }
        return -1;
    }

    /**
     * adds to a players time
     * (put in a negitive number if you want to take away)
     *
     * @param player
     * @param ticks
     */
    public void changeTime(Player player, int ticks)
    {
        if(times.containsKey(player.getUniqueId()))
        {
            final int currentTime = getTime(player);
            times.put(player.getUniqueId(), currentTime + ticks);

            //if its less than 0 we will set it back to 0
            if(getTime(player) < 0)
            {
                times.put(player.getUniqueId(), 0);
            }
        }
    }

    /**
     * set a players time
     *
     * @param player
     * @param ticks
     */
    public void setTime(Player player, int ticks)
    {
        if(times.containsKey(player.getUniqueId()))
        {
            times.put(player.getUniqueId(), ticks);

            //if its less than 0 we will set it back to 0
            if(getTime(player) < 0)
            {
                times.put(player.getUniqueId(), 0);
            }
        }
    }

    /**
     * Save the players flight times to the config
     */
    public void saveTimes()
    {
        for(UUID uuid : times.keySet())
        {
            int time = times.get(uuid);
            data.set(uuid.toString(), time);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        //if the player is not in the hashmap we will load it in from the file
        if(!hasPlayer(player))
        {
            UUID uuid = player.getUniqueId();
            if(data.isSet(uuid.toString()))
            {
                times.put(uuid, data.getInt(uuid.toString()));
            }
            else
            {
                //if they are not in the file we start them off with no flytime
                times.put(uuid, 0);
            }
        }
    }

    private boolean hasPlayer(Player player)
    {
        return times.containsKey(player.getUniqueId());
    }
}
