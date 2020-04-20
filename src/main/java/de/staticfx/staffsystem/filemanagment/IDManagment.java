package de.staticfx.staffsystem.filemanagment;

import de.staticfx.staffsystem.Main;
import de.staticfx.staffsystem.objects.ID;
import de.staticfx.staffsystem.objects.Type;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class IDManagment {

    public static final IDManagment INSTANCE = new IDManagment();

    public IDManagment getInstance() {
        return INSTANCE;
    }

    File file = new File(Main.getInstance().getDataFolder().getAbsolutePath(),"ids.yml");
    FileConfiguration conf;

    public void loadFile() {

        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        conf = YamlConfiguration.loadConfiguration(file);

    }

    public void saveFile() {
        try {
            conf.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveID(ID id) {
        conf.set(id.getId() + ".reason", id.getReason());
        conf.set(id.getId() + ".time", id.getTime());
        conf.set(id.getId() + ".type", id.getType().toString());
        conf.set(id.getId() + ".permanent", id.isPermanent());
        saveFile();
    }

    public ID getID(int id) {
        return new ID(id, conf.getString(id + ".reason"), Type.valueOf(conf.getString(id + ".type")), conf.getString( id + ".time"), conf.getBoolean(id + ".permanent"));
    }

    public List<ID> getAllIDs() {
        List<ID> list = new ArrayList<>();
        for(String string : conf.getKeys(false)) {
            int i = Integer.parseInt(string);
            list.add(getID(i));
        }
        return list;
    }

    public boolean doesIDExist(int id) {
        return conf.contains(Integer.toString(id));
    }


}
