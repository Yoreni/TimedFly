package yoreni.glattfly;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import yoreni.glattfly.command.GFlyCommand;
import yoreni.glattfly.util.MessageHandler;
import yoreni.glattfly.util.Yml;

public final class Glattfly extends JavaPlugin
{
    private static Glattfly instance;
    private static TimeManager timeManager;
    private static MessageHandler messageHandler;
    private static Yml config;

    //a custom world graud how ever The type is "Object" just in case world graud isnt installed
    public static StateFlag FLY_WG_FLAG;

    @Override
    public void onLoad()
    {
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
        {
            registerFlags();
        }
    }

    @Override
    public void onEnable()
    {
        instance = this;
        timeManager = new TimeManager();
        messageHandler = new MessageHandler(this);

        config = new Yml(this,"config");
        config.setDefaultsFromJar();

        final long updateInterval = config.getLong("update-interval");
        BukkitTask updateFlight = new FightUpdater(updateInterval)
                .runTaskTimer(this, updateInterval, updateInterval);

        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        // second param allows for ordering of handlers - see the JavaDocs
        sessionManager.registerHandler(FlyWGFlagHandler.FACTORY, null);

        this.getCommand("gfly").setExecutor(new GFlyCommand(this));

    }

    @Override
    public void onDisable()
    {
        timeManager.saveTimes();
    }

    public static Yml config()
    {
        return config;
    }

    public static TimeManager getTimeManager()
    {
        return timeManager;
    }

    public static MessageHandler getMessageHandler()
    {
        return messageHandler;
    }

    public static Glattfly getInstance()
    {
        return instance;
    }

    private void registerFlags()
    {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try
        {
            StateFlag flag = new StateFlag("fly", true);
            registry.register(flag);
            FLY_WG_FLAG = flag;
        }
        catch (FlagConflictException e)
        {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("fly");
            if (existing instanceof StateFlag)
            {
                FLY_WG_FLAG = (StateFlag) existing;
            }
            else
            {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
            }
        }
    }
}
