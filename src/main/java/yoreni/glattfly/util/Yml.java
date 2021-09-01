package yoreni.glattfly.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Yml
{
    FileConfiguration config = null;
    File file = null;
    Plugin plugin;

    public Yml(Plugin plugin, String path)
    {
        if (!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdirs();
        }

        //cutting the .yml on the end if there is one
        path = fixFilePath(path);

        //sperating the name and the path
        String name = path;
        if(path.contains("/"))
        {
            name = path.split("/")[path.split("/").length - 1];
            path = "/" + path.substring(0,(path.length() - name.length()) - 1);
        }
        else
        {
            path = "";
        }

        // created all the sub directries if there are any
        file = new File(new File(plugin.getDataFolder() + path),name + ".yml");
        if(!path.equals(""))
        {
            File subDir = new File(plugin.getDataFolder(),path);
            subDir.mkdir();
        }

        //actaully creating the file
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
                InputStream stream =  getClass().getResourceAsStream("/" + name +".yml");
                //copyFile(stream,file);
            }
            catch (Exception Exception)
            {
                Exception.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        this.plugin = plugin;
    }

    public Yml(Plugin plugin, File file)
    {
        this.file = file;
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
                //InputStream stream =  getClass().getResourceAsStream("/" + name +".yml");
                //copyFile(stream,file);
            }
            catch (Exception Exception)
            {
                Exception.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        this.plugin = plugin;
    }

    private void save()
    {
        try
        {
            config.save(file);
        }
        catch (IOException IOException)
        {
            IOException.printStackTrace();
        }
    }

    public void setDefault(String key, Object value)
    {
        if(config.isSet(key))
        {
            config.set(key, value);
            save();
        }
    }

    public String getOrDefault(String key, Object defaultValue)
    {
        if(config.isSet(key))
        {
            return config.get(key).toString();
        }
        else
        {
            return defaultValue.toString();
        }
    }

    public String getOrSetDefault(String key, Object defaultValue)
    {
        if(config.isSet(key))
        {
            return config.get(key).toString();
        }
        else
        {
            config.set(key, defaultValue);
            return defaultValue.toString();
        }
    }

    public void set(String s,Object x)
    {
        config.set(s,x);
        save();
    }

    public void changeDouble(String s,double x)
    {
        config.set(s,config.getDouble(s) + x);
        save();
    }

    public void changeInt(String s,int x)
    {
        config.set(s,config.getInt(s) + x);
        save();
    }

    public void changeLong(String s,long x)
    {
        config.set(s,config.getLong(s) + x);
        save();
    }

    public double getDouble(String s)
    {
        return config.getDouble(s);
    }

    public int getInt(String s)
    {
        return config.getInt(s);
    }

    public long getLong(String s)
    {
        return config.getLong(s);
    }

    public String getString(String s)
    {
        return config.getString(s);
    }

    public boolean getBoolean(String s)
    {
        return config.getBoolean(s);
    }

    public List<String> getStringList(String s)
    {
        return config.getStringList(s);
    }

    public Object get(String s)
    {
        return config.get(s);
    }

    public boolean isSet(String s)
    {
        return config.isSet(s);
    }

    public @NotNull Map<String, Object> getValues(String path)
    {
        return config.getConfigurationSection(path).getValues(true);
    }

    public void reload()
    {
        try
        {
            config.load(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public String getName()
    {
        return file.getName();
    }

    public void setDefaultsFromJar()
    {
        Bukkit.getLogger().info(file.length() + "");
        if(file.length() == 0)
        {
            plugin.saveResource(file.getName(), true);
        }
        reload();
        /*
        // Load default file into input stream
        String fileName = file.getName();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        // Copy to desired location
        try {
            Files.copy(inputStream, new File(file.getPath(), fileName).toPath(), new CopyOption[0]);
        } catch (IOException e) {
            Bukkit.getLogger().info("[ERROR] Could not create new " + fileName + " file...");
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */
    }

    @Deprecated
    public static void copyFile(InputStream in, File out) throws Exception
    {
        // https://bukkit.org/threads/extracting-file-from-jar.16962/
        InputStream fis = in;
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }catch(Exception e) {
            throw e;
        }finally {
            if(fis != null) {
                fis.close();
            }
            if(fos != null) {
                fos.close();
            }
        }
    }

    /**
     * fixes a path if it has an extra .yml
     * eg hello.yml become hello
     * hello.txt.jdh.yml becomes hello.txt.jdh
     *
     * @param path
     * @return
     */
    private String fixFilePath(String path)
    {
        if(!path.contains("."))
        {
            return path;
        }

        String out = "";
        for(int i = 0; i < path.split("//.").length - 1; i++)
        {
            out += path.split("//.")[i];
        }

        return out;
    }
}
