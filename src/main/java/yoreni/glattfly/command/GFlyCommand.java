package yoreni.glattfly.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yoreni.glattfly.Glattfly;
import yoreni.glattfly.util.Placeholder;
import yoreni.glattfly.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GFlyCommand implements CommandExecutor, TabCompleter
{
    Plugin plugin;

    public GFlyCommand(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        if(args.length == 0)
        {
            if(!sender.hasPermission("glattfly.fly"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }

            //if the console if running the command we will just show the help menu
            if(!(sender instanceof Player))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "help-menu");
            }
            else
            {
                Player player = (Player) sender;
                final boolean hasFlightTime = Glattfly.getTimeManager().getTime(player) > 0;
                final boolean hasInfiniteFlight = player.hasPermission("glattfly.infiniteflight");

                if(player.getAllowFlight() == false)
                {
                    if(hasFlightTime || hasInfiniteFlight)
                    {
                        player.setAllowFlight(true);
                        Glattfly.getMessageHandler().sendMessage(player, "flight-enabled");
                    }
                    else
                    {
                        Glattfly.getMessageHandler().sendMessage(player, "out-of-flight-time");
                    }
                }
                else
                {
                    player.setAllowFlight(false);
                    Glattfly.getMessageHandler().sendMessage(player, "flight-disabled");
                }
            }

            return true;
        }

        if(args[0].equalsIgnoreCase("give"))
        {
            if(!sender.hasPermission("glattfly.admin.give"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }
            if(args.length < 3)
            {
                sender.sendMessage("Usage: /gfly give <player> <time>");
                return true;
            }

            Player reciver = Bukkit.getPlayer(args[1]);
            if(reciver == null)
            {
                Glattfly.getMessageHandler().sendMessage(sender, "invalid-player");
            }
            else
            {
                int timeToBeGiven = calcTime(Arrays.copyOfRange(args,2, args.length));
                if(timeToBeGiven >= 0)
                {
                    Glattfly.getTimeManager().changeTime(reciver, timeToBeGiven);

                    Glattfly.getMessageHandler().sendMessage(sender, "given-time-success",
                            new Placeholder("%time%", Util.formatTime(timeToBeGiven)),
                            new Placeholder("%player%", reciver.getName()));
                }
                else
                {
                    Glattfly.getMessageHandler().sendMessage(sender, "invalid-time-amount");
                }
            }

            return true;
        }
        else if(args[0].equalsIgnoreCase("take"))
        {
            if(!sender.hasPermission("glattfly.admin.take"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }
            if(args.length < 3)
            {
                sender.sendMessage("Usage: /gfly take <player> <time>");
                return true;
            }

            Player reciver = Bukkit.getPlayer(args[1]);
            if(reciver == null)
            {
                Glattfly.getMessageHandler().sendMessage(sender, "invalid-player");
            }
            else
            {
                int timeToBeTaken = calcTime(Arrays.copyOfRange(args,2, args.length));
                if(timeToBeTaken >= 0)
                {
                    Glattfly.getTimeManager().changeTime(reciver, -timeToBeTaken);

                    Glattfly.getMessageHandler().sendMessage(sender, "taken-time-success",
                            new Placeholder("%time%", Util.formatTime(Math.abs(timeToBeTaken))),
                            new Placeholder("%player%", reciver.getName()));
                }
                else
                {
                    Glattfly.getMessageHandler().sendMessage(sender, "invalid-time-amount");
                }
            }

            return true;
        }
        else if(args[0].equalsIgnoreCase("set"))
        {
            if(!sender.hasPermission("glattfly.admin.set"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }
            if(args.length < 3)
            {
                sender.sendMessage("Usage: /gfly set <player> <time>");
                return true;
            }

            Player reciver = Bukkit.getPlayer(args[1]);
            if(reciver == null)
            {
                Glattfly.getMessageHandler().sendMessage(sender, "invalid-player");
            }
            else
            {
                int timeToBeSet = calcTime(Arrays.copyOfRange(args,2, args.length));
                if(timeToBeSet >= 0)
                {
                    Glattfly.getTimeManager().setTime(reciver, timeToBeSet);

                    Glattfly.getMessageHandler().sendMessage(sender, "set-time-success",
                            new Placeholder("%time%", Util.formatTime(timeToBeSet)),
                            new Placeholder("%player%", reciver.getName()));
                }
                else
                {
                    Glattfly.getMessageHandler().sendMessage(sender, "invalid-time-amount");
                }
            }
            return true;
        }
        else if(args[0].equalsIgnoreCase("balance"))
        {
            if(!sender.hasPermission("glattfly.balance"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }

            if(args.length == 1)
            {
                if(sender instanceof Player)
                {
                    Player player = (Player) sender;
                    int time = Glattfly.getTimeManager().getTime(player);
                    Glattfly.getMessageHandler().sendMessage(player, "your-balance",
                            new Placeholder("%time%", Util.formatTime(time)));
                }
            }
            else
            {
                Player player = Bukkit.getPlayer(args[1]);
                int time = Glattfly.getTimeManager().getTime(player);
                if(time == -1)
                {
                    Glattfly.getMessageHandler().sendMessage(sender, "invalid-player");
                }
                else
                {
                    Glattfly.getMessageHandler().sendMessage(player, "another-players-balance",
                            new Placeholder("%time%", Util.formatTime(time)),
                            new Placeholder("%player%", player.getName()));
                }
            }
        }
        else if(args[0].equalsIgnoreCase("help"))
        {
            if(!sender.hasPermission("glattfly.help"))
            {
                Glattfly.getMessageHandler().sendMessage(sender, "no-perms");
                return true;
            }

            Glattfly.getMessageHandler().sendMessage(sender, "help-menu");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        List<String> showOnTabComplete = new ArrayList<String>();
        if (args.length == 1)
        {
            List<String> options = new ArrayList<String>();
            if(commandSender.hasPermission("glattfly.admin.set"))
            {
                options.add("set");
            }
            if(commandSender.hasPermission("glattfly.admin.give"))
            {
                options.add("give");
            }
            if(commandSender.hasPermission("glattfly.admin.take"))
            {
                options.add("take");
            }
            if(commandSender.hasPermission("glattfly.balance"))
            {
                options.add("balance");
            }
            if(commandSender.hasPermission("glattfly.help"))
            {
                options.add("help");
            }

            StringUtil.copyPartialMatches(args[0], options, showOnTabComplete);
            return showOnTabComplete;
        }
        if (args.length == 2)
        {
            //returning null displays all the players who are online which is kind of what we want
            //seeing all our commands want a players name in this positon
            return null;
        }
        return new ArrayList<String>();
    }

    /**
     * gets a list of human reable times then convert them all into thier amount in minecraft ticks
     * then it sums them all sum and return that number
     *
     * @param times
     * @return
     */
    private int calcTime(String... times)
    {
        int sum = 0;
        for(String s : times)
        {
            int value = Util.stringToTicks(s);

            if(value == -1)
            {
                return -1;
            }
            else
            {
                sum += value;
            }
        }

        return sum;
    }
}
