package me.michqql.bhd.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class InfoFile {

    private final File dataFolder;
    protected File directory, file;

    protected final String folder, name, path;
    protected final boolean useDirectory;
    protected final String extension;

    protected FileConfiguration config;

    public InfoFile(Plugin plugin, String folder, String name) {
        this.dataFolder = plugin.getDataFolder();
        this.folder = folder;
        this.name = name;
        this.useDirectory = (folder != null && !folder.isEmpty());

        this.path = (useDirectory ? folder + "/" : "") + name + ".txt";
        this.extension = "txt";

        preInit(plugin);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void preInit(Plugin plugin) {
        if(!this.dataFolder.exists())
            this.dataFolder.mkdirs();

        if(useDirectory) {
            this.directory = new File(dataFolder, folder);
            if(!this.directory.exists())
                this.directory.mkdirs();

            this.file = new File(directory, name + "." + extension);
        } else {
            this.file = new File(dataFolder, name + "." + extension);
        }

        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
                copy(plugin);
            } catch(IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not create file: " + path);
            }
        }
    }

    protected void copy(Plugin plugin) {
        InputStream in = plugin.getResource(path);
        if(in == null) {
            Bukkit.getLogger().log(Level.WARNING, "Could not find resource to copy: " + path);
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];

            int length;
            while((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();
        } catch(IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to copy resource: " + path);
        }
    }
}
