package yoreni.glattfly.util;

public class Util
{
    /**
     * Takes a number in minecraft ticks and converts that into a human reable one
     * eg.
     * 13762 -> 11m 28s
     * 125000 -> 1h 44m
     * 87965334 -> 50d 21h
     *
     * @param ticks
     * @return
     */
    public static String formatTime(int ticks)
    {
        int s = (int) (ticks / 20) % 60;
        int m = (int) (ticks / 1_200) % 60;
        int h = (int) (ticks / 72_000) % 24;
        int d = (int) (ticks / 1_728_000);

        if(ticks < 1_200)
        {
            return s + "s";
        }
        else if(ticks < 72_000)
        {
            return m + "m " + s + "s";
        }
        else if(ticks < 1_728_000)
        {
            return h + "h " + m + "m";
        }
        else
        {
            return d + "d " + h + "h";
        }
    }

    /**
     * converts a string into a number in ticks
     * if no unit is used to defaults to seconds
     * the following units can be used
     * t - minecraft ticks
     * s - seconds
     * m - minuites
     * h - hours
     * d - days
     *
     * @param ticks the string
     * @return the number of ticks
     */
    public static int stringToTicks(String ticks)
    {
        if(Validation.validInt(ticks))
        {
            return Integer.parseInt(ticks) * 20;
        }
        else if(ticks.matches("[0-9]+(t|s|m|h|d)"))
        {
            int value = Integer.parseInt(ticks.replaceAll("(t|s|m|h|d)",""));
            String unit = ticks.replaceAll("[0-9]+","");

            switch (unit)
            {
                case "t":
                    return value;
                case "s":
                    return value * 20;
                case "m":
                    return value * 1_200;
                case "h":
                    return value * 72_000;
                case "d":
                    return value * 1_728_000;
            }
        }

        //we will return -1 if the string is not valid
        return -1;
    }
}
