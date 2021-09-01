package yoreni.glattfly.util;

public class Validation
{
    /**
     * this checks if a string can be turned into an int using the Integer.parseInt method
     *
     * @param string
     * @return if the Integer.parseInt will work on the string it returns true.
     */
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
