package yoreni.glattfly;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyWGFlagHandler extends Handler
{
    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<FlyWGFlagHandler>
    {
        @Override
        public FlyWGFlagHandler create(Session session)
        {
            return new FlyWGFlagHandler(session);
        }
    }

    public FlyWGFlagHandler(Session session)
    {
        super(session);
    }

    @Override
    public void tick(LocalPlayer lplayer, ApplicableRegionSet set)
    {
        Player player = Bukkit.getPlayer(lplayer.getName());
        State state = set.queryState(lplayer, Glattfly.FLY_WG_FLAG);

        if(player.getAllowFlight() == true && state == State.DENY)
        {
            player.setAllowFlight(false);
        }
    }
}
