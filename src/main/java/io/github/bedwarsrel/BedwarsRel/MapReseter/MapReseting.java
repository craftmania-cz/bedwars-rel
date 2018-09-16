package io.github.bedwarsrel.BedwarsRel.MapReseter;

import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;

public class MapReseting {

    public static void saveWorld(World world){
        if (world != null){
            world.save();
            File worldFolder = new File("plugins/BedwarsRel/WorldSaves/" + world.getName());
            File srcWorldFolder = new File(world.getName());
            if (worldFolder.exists()) {
                deleteFolder(worldFolder);
            }
            copyWorldFolder(srcWorldFolder, worldFolder);
            FileConfiguration settings = YamlConfiguration.loadConfiguration(new File(worldFolder, "WorldSettings.yml"));
            settings.set("World.Seed", Long.valueOf(world.getSeed()));
            settings.set("World.Environment", world.getEnvironment().toString());
            settings.set("World.Structures", Boolean.valueOf(world.canGenerateStructures()));
            settings.set("World.Generator", getChunkGeneratorAsName(world));
            settings.set("World.Type", world.getWorldType().toString());
            try{
                settings.save(new File(worldFolder, "WorldSettings.yml"));
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("[WorldReset] Couldn't create a WorldSettings file!");
            }
        }
    }

    public static void saveWorld(String worldName){
        World world = Bukkit.getWorld(worldName);
        saveWorld(world);
    }

    public static Boolean worldSaved(String worldName){
        File worldFolder = new File("plugins/BedwarsRel/WorldSaves/" + worldName);
        if (worldFolder.exists()) {
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }

    public static void resetWorld(World world){
        File srcWorldFolder = new File("plugins/BedwarsRel/WorldSaves/" + world.getName());
        File worldFolder = new File(world.getName());
        if ((srcWorldFolder.exists()) &&
                (worldFolder.exists())) {
            if (world.getName().equals("world")){
                System.out.println("[WorldReset] The world 'world' is the main world and can't be resetted!");
            }
            else{
                String worldName = world.getName();
                Boolean saveSett = Boolean.valueOf(false);
                Long seed = null;
                World.Environment environment = null;
                Boolean structures = null;
                String generator = null;
                WorldType worldType = null;
                File settingsFile = new File(srcWorldFolder, "WorldSettings.yml");
                FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
                if ((settingsFile.exists()) && (settings.get("World.Seed") != null))
                {
                    seed = Long.valueOf(settings.getLong("World.Seed"));
                    environment = World.Environment.valueOf(settings.getString("World.Environment"));
                    structures = Boolean.valueOf(settings.getBoolean("World.Structures"));
                    generator = settings.getString("World.Generator");
                    worldType = WorldType.valueOf(settings.getString("World.Type"));
                }
                else
                {
                    seed = Long.valueOf(world.getSeed());
                    environment = world.getEnvironment();
                    structures = Boolean.valueOf(world.canGenerateStructures());
                    generator = getChunkGeneratorAsName(world);
                    worldType = world.getWorldType();
                    settings.set("World.Seed", Long.valueOf(world.getSeed()));
                    settings.set("World.Environment", world.getEnvironment().toString());
                    settings.set("World.Structures", Boolean.valueOf(world.canGenerateStructures()));
                    settings.set("World.Generator", getChunkGeneratorAsName(world));
                    settings.set("World.Type", world.getWorldType().toString());
                    saveSett = Boolean.valueOf(true);
                }
                Bukkit.getServer().unloadWorld(world, true);

                WorldCreator w = new WorldCreator(world.getName());
                deleteFolder(worldFolder);
                copyWorldFolder(srcWorldFolder, worldFolder);
                if (saveSett.booleanValue()) {
                    try
                    {
                        settings.save(settingsFile);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("[WorldReset] Couldn't save the WorldSettings file!");
                    }
                }
                w.seed(seed.longValue());
                w.environment(environment);
                w.generateStructures(structures.booleanValue());
                w.generator(generator);
                w.type(worldType);
                Bukkit.getServer().createWorld(w);
            }
        }
    }

    private static void deleteFolder(File folder){
        File[] files = folder.listFiles();
        if (files != null){
            File[] arrayOfFile1;
            int j = (arrayOfFile1 = files).length;
            for (int i = 0; i < j; i++){
                File file = arrayOfFile1[i];
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    private static String getChunkGeneratorAsName(World world){
        String generator = null;
        Plugin[] arrayOfPlugin;
        int j = (arrayOfPlugin = Bukkit.getPluginManager().getPlugins()).length;
        for (int i = 0; i < j; i++)
        {
            Plugin plugin = arrayOfPlugin[i];
            WorldCreator wc = new WorldCreator("ThisMapWillNeverBeCreated");
            wc.generator(plugin.getName());
            if ((wc.generator() != null) && (world.getGenerator() != null) && (wc.generator().getClass().getName().equals(world.getGenerator().getClass().getName()))) {
                generator = plugin.getName();
            }
        }
        return generator;
    }

    private static void copyWorldFolder(File from, File to){
        try{
            ArrayList<String> ignore = new ArrayList();
            ignore.add("session.dat");
            ignore.add("session.lock");
            ignore.add("WorldSettings.yml");
            if (!ignore.contains(from.getName())) {
                if (from.isDirectory()){
                    if (!to.exists()) {
                        to.mkdirs();
                    }
                    String[] files = from.list();
                    String[] arrayOfString1;
                    int j = (arrayOfString1 = files).length;
                    for (int i = 0; i < j; i++)
                    {
                        String file = arrayOfString1[i];
                        File srcFile = new File(from, file);
                        File destFile = new File(to, file);
                        copyWorldFolder(srcFile, destFile);
                    }
                }
                else{
                    Files.copy(from, to);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
