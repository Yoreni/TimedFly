import yoreni.glattfly.util.Util;
import yoreni.glattfly.util.Validation;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println(stringToTicks("0"));
        System.out.println(stringToTicks("1"));
        System.out.println(stringToTicks("1s"));
        System.out.println(stringToTicks("2s"));
        System.out.println(stringToTicks("15t"));
        System.out.println(stringToTicks("1m"));
        System.out.println(stringToTicks("100m"));
        System.out.println(stringToTicks("3h"));
        System.out.println(stringToTicks("2d"));
    }

    public static int stringToTicks(String ticks)
    {
        if(validInt(ticks))
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

    public static boolean validInt(String string)
    {
        try
        {
            int number = Integer.parseInt(string);
            return true;
        }
        catch(NumberFormatException exception)
        {
            return false;
        }
    }
}
