package yoreni.glattfly;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import sun.jvm.hotspot.ui.ObjectHistogramPanel;
import yoreni.glattfly.util.Placeholder;
import yoreni.glattfly.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FightUpdater extends BukkitRunnable implements Listener
{
    /**
     * A long that countdowns when it will save the players flight times in the file
     */
    private int timeUntilNextSave;

    /**
     * A list of players that it will disable fall damage for
     */
    private List<Player> disableFllDmg = new ArrayList<Player>();

    /**
     * a long so this BukkitRunable knows how often this will be looping
     */
    final private long updateInterval;

    public FightUpdater(long updateInterval)
    {
        Bukkit.getPluginManager().registerEvents(this, Glattfly.getInstance());
        this.updateInterval = updateInterval;
        this.timeUntilNextSave = Glattfly.config().getInt("save-interval") * 20;
    }

    @Override
    public void run()
    {
        //update everyones fly
        for(Player player : Bukkit.getOnlinePlayers())
        {
            final boolean hasInfiniteFly = player.hasPermission("glattfly.infiniteflight");

            if(player.isFlying())
            {
                if(hasInfiniteFly)
                {
                    Glattfly.getMessageHandler().sendActionbar(player, "flight-meter-infinte");
                }
                else
                {
                    Glattfly.getTimeManager().changeTime(player, -(int) updateInterval);

                    Glattfly.getMessageHandler().sendActionbar(player, "flight-meter",
                            new Placeholder("%time%", Util.formatTime(Glattfly.getTimeManager().getTime(player))));

                    //disable the players flight if theyve ran out
                    if(Glattfly.getTimeManager().getTime(player) <= 0)
                    {
                        player.setAllowFlight(false);

                        //we are going to detect if the player will take fall damage if so we will add the
                        //player on the list so the fall damage gets disabled
                        Location location = player.getLocation();
                        World world = location.getWorld();
                        int airBlocks = 0;
                        for(;airBlocks < 5; airBlocks++)
                        {
                            Location lookingAt = new Location(world, location.getBlockX()
                                    , location.getBlockY() - airBlocks, location.getBlockZ());
                            if(world.getBlockAt(lookingAt).getType() != Material.AIR)
                            {
                                break;
                            }
                        }

                        if(airBlocks >= 4 && Glattfly.config().getBoolean("disable-fall-damage"))
                        {
                            //disabling the next fall damage for the player if the player is high up in the air
                            //so they dont die when thier flight time runs out
                            disableFllDmg.add(player);
                        }
                    }
                }
            }
        }

        //save everyones flight if its time
        timeUntilNextSave -= updateInterval;
        if(timeUntilNextSave <= 0)
        {
            timeUntilNextSave = Glattfly.config().getInt("save-interval") * 20;
            Glattfly.getTimeManager().saveTimes();
        }
    }

    @EventHandler
    public void disableFalldamge(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();

            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && disableFllDmg.contains(player))
            {
                event.setCancelled(true);
                disableFllDmg.remove(player);
            }
        }
    }
}
