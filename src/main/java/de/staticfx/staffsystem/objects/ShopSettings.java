package de.staticfx.staffsystem.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ShopSettings
        implements Listener
{
    private File file = new File("plugins/InvShop", "settings.yml");
    private FileConfiguration cfg = YamlConfiguration.loadConfiguration(this.file);
    private File fileLang = new File("plugins/InvShop", "language.yml");
    private FileConfiguration cfgLang = YamlConfiguration.loadConfiguration(this.fileLang);
    private Inventory inv;
    private Shop shop;
    private List<String> viewing = new ArrayList();
    private Map<String, Integer> languages = new HashMap();
    ItemStack back = new ItemStack(Material.IRON_DOOR);
    ItemStack stackEndScrolling;
    ItemStack stackShopColor;
    ItemStack stackAnimatedScrolling;
    ItemStack stackLanguage;

    public ShopSettings(Player p, Shop shop, Plugin plugin)
    {
        this.inv = Bukkit.createInventory(null, 36, "Shop-Settings");
        this.shop = shop;

        this.languages.put("en", Integer.valueOf(1));
        this.languages.put("de", Integer.valueOf(2));
        this.languages.put("fr", Integer.valueOf(3));
        if (!this.cfg.contains(p.getName()))
        {
            this.cfg.set(p.getName() + ".endScrolling", Boolean.valueOf(false));
            this.cfg.set(p.getName() + ".color", Integer.valueOf(15));
            this.cfg.set(p.getName() + ".animatedScrolling", Boolean.valueOf(false));
            this.cfg.set(p.getName() + ".language", "de");
        }
        try
        {
            this.cfg.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, plugin);

        this.cfgLang.set("en.settings", "Options");
        this.cfgLang.set("de.settings", "Einstellungen");
        this.cfgLang.set("fr.settings", "Param�tres");

        this.cfgLang.set("en.credits", "Credits");
        this.cfgLang.set("de.credits", "Credits");
        this.cfgLang.set("fr.credits", "Cr�dits G�n�rique");

        this.cfgLang.set("en.infinite_scrolling", "Infinite Scrolling");
        this.cfgLang.set("de.infinite_scrolling", "Unendliches Scrollen");
        this.cfgLang.set("fr.infinite_scrolling", "Faire D�filer Infinite");

        this.cfgLang.set("en.color", "Change Shopcolor");
        this.cfgLang.set("de.color", "Shopfarbe �ndern");
        this.cfgLang.set("fr.color", "Changer Le Couleur");

        this.cfgLang.set("en.animated_scrolling", "Animated Scrolling");
        this.cfgLang.set("de.animated_scrolling", "Animiertes Scrollen");
        this.cfgLang.set("fr.animated_scrolling", "Faire D�filer Anim�");

        this.cfgLang.set("en.language", "Change Language");
        this.cfgLang.set("de.language", "Sprache �ndern");
        this.cfgLang.set("fr.language", "Changer La Langue");

        this.cfgLang.set("en.back", "Back");
        this.cfgLang.set("de.back", "Zur�ck");
        this.cfgLang.set("fr.back", "En Arri�re");

        this.cfgLang.set("en.on", "ON");
        this.cfgLang.set("de.on", "AN");
        this.cfgLang.set("fr.on", "ALLUM�");

        this.cfgLang.set("en.off", "OFF");
        this.cfgLang.set("de.off", "AN");
        this.cfgLang.set("fr.off", "�TEINT");
        try
        {
            this.cfgLang.save(this.fileLang);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void openShopSettings(Player p)
    {
        p.closeInventory();

        createSettings(p);
        setItems();

        this.viewing.add(p.getName());
        p.openInventory(this.inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        Player p = (Player)e.getWhoClicked();
        if (getViewers().contains(p))
        {
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + getText(p, "infinite_scrolling"))) {
                changeScrollSettings(p);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + getText(p, "color"))) {
                changeColor(p);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + getText(p, "animated_scrolling"))) {
                changeAnimatedScrollSettings(p);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + getText(p, "language"))) {
                changeLanguage(p);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + getText(p, "back"))) {
                back(p);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if (this.viewing.contains(e.getPlayer().getName())) {
            this.viewing.remove(e.getPlayer().getName());
        }
    }

    public boolean isEndScrolling(Player p)
    {
        return this.cfg.getBoolean(p.getName() + ".endScrolling");
    }

    public void setEndScrolling(Player p, boolean b)
    {
        this.cfg.set(p.getName() + ".endScrolling", Boolean.valueOf(b));
        try
        {
            this.cfg.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void returnToShop(Player p)
    {
        this.shop.openShop(p);
    }

    public void createSettings(Player p)
    {
        List<String> endScrollingLore = new ArrayList();
        List<String> shopColorLore = new ArrayList();
        List<String> animatedScrollingLore = new ArrayList();
        List<String> languageLore = new ArrayList();

        ItemMeta backMeta = this.back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + getText(p, "back"));
        this.back.setItemMeta(backMeta);
        if (isEndScrolling(p))
        {
            this.stackEndScrolling = new ItemStack(Material.GREEN_STAINED_GLASS, 1, (short)13);
            endScrollingLore.add(ChatColor.GREEN + getText(p, "on"));
        }
        else
        {
            this.stackEndScrolling = new ItemStack(Material.RED_STAINED_GLASS, 1, (short)14);
            endScrollingLore.add(ChatColor.RED + getText(p, "off"));
        }
        ItemMeta endScrollingMeta = this.stackEndScrolling.getItemMeta();
        endScrollingMeta.setDisplayName(ChatColor.GOLD + getText(p, "infinite_scrolling"));
        endScrollingMeta.setLore(endScrollingLore);
        this.stackEndScrolling.setItemMeta(endScrollingMeta);

        this.stackShopColor = new ItemStack(Material.BLACK_STAINED_GLASS, 1, (byte)getColorCode(p));
        shopColorLore.add(ChatColor.WHITE + getColorName(p, getColorCode(p)));

        ItemMeta shopColorMeta = this.stackShopColor.getItemMeta();
        shopColorMeta.setDisplayName(ChatColor.GOLD + getText(p, "color"));
        shopColorMeta.setLore(shopColorLore);
        this.stackShopColor.setItemMeta(shopColorMeta);
        if (isAnimatedScrolling(p))
        {
            this.stackAnimatedScrolling = new ItemStack(Material.GREEN_STAINED_GLASS, 1, (short)13);
            animatedScrollingLore.add(ChatColor.GREEN + getText(p, "on"));
        }
        else
        {
            this.stackAnimatedScrolling = new ItemStack(Material.RED_STAINED_GLASS, 1, (short)14);
            animatedScrollingLore.add(ChatColor.RED + getText(p, "off"));
        }
        ItemMeta animatedScrollingMeta = this.stackAnimatedScrolling.getItemMeta();
        animatedScrollingMeta.setDisplayName(ChatColor.GOLD + getText(p, "animated_scrolling"));
        animatedScrollingMeta.setLore(animatedScrollingLore);
        this.stackAnimatedScrolling.setItemMeta(animatedScrollingMeta);

        this.stackLanguage = new ItemStack(Material.GOLD_BLOCK);
        languageLore.add(ChatColor.WHITE + getLanguage(p, false));

        ItemMeta languageMeta = this.stackLanguage.getItemMeta();
        languageMeta.setLore(languageLore);
        languageMeta.setDisplayName(ChatColor.GOLD + getText(p, "language"));
        this.stackLanguage.setItemMeta(languageMeta);
    }

    public void setItems()
    {
        this.inv.setItem(0, this.stackEndScrolling);
        this.inv.setItem(1, this.stackShopColor);
        this.inv.setItem(2, this.stackAnimatedScrolling);
        this.inv.setItem(3, this.stackLanguage);

        this.inv.setItem(35, this.back);
    }

    public void updateSettings(Player p)
    {
        for (int i = 0; i < this.inv.getSize() - 1; i++) {
            this.inv.setItem(i, new ItemStack(Material.AIR));
        }
        createSettings(p);
        setItems();
    }

    public void changeScrollSettings(Player p)
    {
        if (isEndScrolling(p))
        {
            setEndScrolling(p, false);
        }
        else
        {
            setEndScrolling(p, true);
            if (isAnimatedScrolling(p)) {
                setAnimatedScrolling(p, false);
            }
        }
        updateSettings(p);
    }

    public void changeAnimatedScrollSettings(Player p)
    {
        if (isAnimatedScrolling(p))
        {
            setAnimatedScrolling(p, false);
        }
        else
        {
            setAnimatedScrolling(p, true);
            if (isEndScrolling(p)) {
                setEndScrolling(p, false);
            }
        }
        updateSettings(p);
    }

    public void back(Player p)
    {
        this.shop.openShop(p);
    }

    public int getColorCode(Player p)
    {
        return this.cfg.getInt(p.getName() + ".color");
    }

    public String getColorName(Player p, int i)
    {
        if (getLanguage(p, true).equalsIgnoreCase("en"))
        {
            switch (i)
            {
                case 0:
                    return "White";
                case 1:
                    return "Orange";
                case 2:
                    return "Magenta";
                case 3:
                    return "Light Blue";
                case 4:
                    return "Yellow";
                case 5:
                    return "Lime Green";
                case 6:
                    return "Pink";
                case 7:
                    return "Gray";
                case 8:
                    return "Light Gray";
                case 9:
                    return "Cyan";
                case 10:
                    return "Purple";
                case 11:
                    return "Blue";
                case 12:
                    return "Brown";
                case 13:
                    return "Green";
                case 14:
                    return "Red";
                case 15:
                    return "Black";
            }
            return "Black";
        }
        if (getLanguage(p, true).equalsIgnoreCase("de"))
        {
            switch (i)
            {
                case 0:
                    return "Wei�";
                case 1:
                    return "Orange";
                case 2:
                    return "Magenta";
                case 3:
                    return "Hellblau";
                case 4:
                    return "Gelb";
                case 5:
                    return "Hellgr�n";
                case 6:
                    return "Rosa";
                case 7:
                    return "Grau";
                case 8:
                    return "Hellgrau";
                case 9:
                    return "T�rkis";
                case 10:
                    return "Lila";
                case 11:
                    return "Blau";
                case 12:
                    return "Braun";
                case 13:
                    return "Gr�n";
                case 14:
                    return "Rot";
                case 15:
                    return "Schwarz";
            }
            return "Schwarz";
        }
        return "-";
    }

    private void setColor(int i, Player p)
    {
        this.cfg.set(p.getName() + ".color", Integer.valueOf(i));
        try
        {
            this.cfg.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void changeColor(Player p)
    {
        int currentColor = getColorCode(p);
        if (currentColor < 15)
        {
            currentColor++;
            setColor(currentColor, p);
        }
        else
        {
            currentColor = 0;
            setColor(currentColor, p);
        }
        updateSettings(p);
    }

    public boolean isAnimatedScrolling(Player p)
    {
        return this.cfg.getBoolean(p.getName() + ".animatedScrolling");
    }

    public void setAnimatedScrolling(Player p, boolean b)
    {
        this.cfg.set(p.getName() + ".animatedScrolling", Boolean.valueOf(b));
        try
        {
            this.cfg.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<Player> getViewers()
    {
        List<Player> viewers = new ArrayList();
        for (String s : this.viewing) {
            viewers.add(Bukkit.getPlayer(s));
        }
        return viewers;
    }

    public String getText(Player p, String param)
    {
        String lang = getLanguage(p, true);

        return this.cfgLang.getString(lang + "." + param);
    }

    public String getLanguage(Player p, boolean b)
    {
        String s = this.cfg.getString(p.getName() + ".language");
        if (b) {
            return s;
        }
        String str1;
        switch ((str1 = s).hashCode())
        {
            case 3201:
                if (str1.equals("de")) {
                    break;
                }
                break;
            case 3276:
                if (!str1.equals("fr"))
                {

                    return "Deutsch";
                }
                else
                {
                    return "Fran�ais";
                }
        }
        label106:
        return "English";
    }

    public void changeLanguage(Player p)
    {
        int i = ((Integer)this.languages.get(getLanguage(p, true))).intValue();
        if (i < this.languages.size())
        {
            i++;
            for (String lang : this.languages.keySet()) {
                if (((Integer)this.languages.get(lang)).intValue() == i)
                {
                    this.cfg.set(p.getName() + ".language", lang);
                    break;
                }
            }
        }
        else
        {
            i = 1;
            for (String lang : this.languages.keySet()) {
                if (((Integer)this.languages.get(lang)).intValue() == i)
                {
                    this.cfg.set(p.getName() + ".language", lang);
                    break;
                }
            }
        }
        try
        {
            this.cfg.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        updateSettings(p);
    }
}
