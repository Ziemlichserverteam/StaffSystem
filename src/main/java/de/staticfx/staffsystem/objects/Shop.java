package de.staticfx.staffsystem.objects;

import de.staticfx.staffsystem.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class Shop implements Listener
{
    Permission perms = Main.getInstance().perms;
    Economy eco = Main.getInstance().eco;
    private ShopSettings shopSettings;
    private int size;
    private int pages;
    private int currentPage;
    private int currentSlot;
    private List<ShopItem> items = new ArrayList();
    private Inventory inv;
    private String name;
    private ItemStack page = new ItemStack(Material.BOOK, 1);
    private ItemMeta pageMeta = this.page.getItemMeta();
    private List<String> viewing = new ArrayList();
    private Plugin plugin;


    public Shop(String name, int size, Plugin plugin)
    {
        this.items.removeAll(this.items);

        this.size = (size * 9);
        this.name = name;
        if (size > 45) {
            this.size = 45;
        }
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, plugin);

        this.plugin = plugin;
    }

    public void addItem(ShopItem item)
    {
        if (!this.items.contains(item)) {
            this.items.add(item);
        }
    }

    public Shop openShop(Player p)
    {
        this.shopSettings = new ShopSettings(p, this, this.plugin);

        this.currentPage = 1;
        this.currentSlot = 1;

        p.openInventory(getInventory(p));
        this.viewing.add(p.getName());
        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 1F);

        return this;
    }

    public void scrollUp(Player p)
    {
        if (!this.shopSettings.isEndScrolling(p))
        {
            if (this.currentPage < this.pages)
            {
                this.currentPage += 1;

                clearInventory();
                for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
                {
                    if (i - this.size * (this.currentPage - 1) >= this.size) {
                        break;
                    }
                    this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
                }
                this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
                this.page.setItemMeta(this.pageMeta);

                this.inv.setItem(this.size + 4, this.page);
            }
            else
            {
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 1.0F);
            }
        }
        else if (this.currentPage < this.pages)
        {
            this.currentPage += 1;

            clearInventory();
            for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
            {
                if (i - this.size * (this.currentPage - 1) >= this.size) {
                    break;
                }
                this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
            }
            this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
            this.page.setItemMeta(this.pageMeta);

            this.inv.setItem(this.size + 4, this.page);
        }
        else
        {
            this.currentPage = 1;

            clearInventory();
            for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
            {
                if (i - this.size * (this.currentPage - 1) >= this.size) {
                    break;
                }
                this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
            }
            this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
            this.page.setItemMeta(this.pageMeta);

            this.inv.setItem(this.size + 4, this.page);
        }
    }

    public void scrollDown(Player p)
    {
        if (!this.shopSettings.isEndScrolling(p))
        {
            if (this.currentPage > 1)
            {
                this.currentPage -= 1;

                clearInventory();
                for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
                {
                    if (i - this.size * (this.currentPage - 1) >= this.size) {
                        break;
                    }
                    this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
                }
                this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
                this.page.setItemMeta(this.pageMeta);

                this.inv.setItem(this.size + 4, this.page);
            }
            else
            {
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 1.0F);
            }
        }
        else if (this.currentPage > 1)
        {
            this.currentPage -= 1;

            clearInventory();
            for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
            {
                if (i - this.size * (this.currentPage - 1) >= this.size) {
                    break;
                }
                this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
            }
            this.pageMeta.setDisplayName("§6"+ this.currentPage + "/" + this.pages);
            this.page.setItemMeta(this.pageMeta);

            this.inv.setItem(this.size + 4, this.page);
        }
        else
        {
            this.currentPage = this.pages;

            clearInventory();
            for (int i = this.size * (this.currentPage - 1); i < this.items.size(); i++)
            {
                if (i - this.size * (this.currentPage - 1) >= this.size) {
                    break;
                }
                this.inv.setItem(i - this.size * (this.currentPage - 1), ((ShopItem)this.items.get(i)).getItem());
            }
            this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
            this.page.setItemMeta(this.pageMeta);

            this.inv.setItem(this.size + 4, this.page);
        }
    }

    public void animatedScrollUp(Player p)
    {
        int slots = this.pages * 4;
        if (this.currentSlot + 3 < slots)
        {
            this.currentSlot += 1;

            clearInventory();
            for (int i = (this.currentSlot - 1) * 9; i < this.items.size(); i++)
            {
                if (i - (this.currentSlot - 1) * 9 >= this.size) {
                    break;
                }
                this.inv.setItem(i - (this.currentSlot - 1) * 9, ((ShopItem)this.items.get(i)).getItem());
            }
        }
        else
        {
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 1.0F);
        }
    }

    public void animatedScrollDown(Player p)
    {
        if (this.currentSlot > 1)
        {
            this.currentSlot -= 1;

            clearInventory();
            for (int i = (this.currentSlot - 1) * 9; i < this.items.size(); i++)
            {
                if (i - (this.currentSlot - 1) * 9 >= this.size) {
                    break;
                }
                this.inv.setItem(i - (this.currentSlot - 1) * 9, ((ShopItem)this.items.get(i)).getItem());
            }
        }
        else
        {
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0F, 1.0F);
        }
    }

    private void clearInventory()
    {
        ItemStack empty = new ItemStack(Material.AIR);
        for (int i = 0; i < this.size; i++) {
            this.inv.setItem(i, empty);
        }
    }

    public void buyItem(ShopItem item, Player p)
    {


        int amount = item.getAmount();
        int price = item.getBuyPrice();


        //schauen ob er das Geld hat
        if (this.eco.getBalance(p) < price)
        {
            System.out.println(this.eco.getBalance(p));
            p.sendMessage(Main.getInstance().getConfigString("NotEnoughMoney","ShopPrefix"));
            return;
        }


        if(item.getMaterial() == Material.AIR)
            return;


        if(item.getMaterial() == Material.BLACK_STAINED_GLASS_PANE)
            return;



        if(item.getMaterial() == Material.WHITE_WOOL && item.getAmount() == 1) {
            Main.getWoolShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.DIAMOND_ORE && item.getAmount() == 1) {
            Main.getOreShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.COOKED_BEEF && item.getAmount() == 1) {
            Main.getFoodShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.NETHER_BRICK && item.getAmount() == 1) {
            Main.getNetherShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.END_STONE && item.getAmount() == 1) {
            Main.getEndShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.STRING && item.getAmount() == 1) {
            Main.getDropShop().openShop(p);
            return;
        }


        if(item.getMaterial() == Material.GRASS_BLOCK && item.getAmount() == 1) {
            Main.getOverworldShop().openShop(p);
            return;
        }

        if(item.getMaterial() == Material.WATER_BUCKET && item.getAmount() == 1) {
            Main.getSeaShop().openShop(p);
            return;
        }

        if(item.getName().equals("§aAdmin shop")) {
            Main.getAdminShop().openShop(p);
            return;
        }

        if(item.getName().equalsIgnoreCase("§cBack")) {
            Main.getShop().openShop(p);
            return;
        }


        if(item.getName().equalsIgnoreCase("§b5 Plots")) {
            if(p.hasPermission("plots.plot.5")) {
                p.sendMessage(Main.getInstance().getConfigString("AlreadyHaveItem","ShopPrefix"));
                return;
            }else{
                if (this.eco.getBalance(p) < price)
                {
                    System.out.println(this.eco.getBalance(p));
                    p.sendMessage(ChatColor.RED + "Du hast nicht genug Geld um dieses Item zu kaufen");
                    return;
                }
                Main.perms.playerAdd(null,p,"plots.plot.5");
                p.sendMessage(Main.getInstance().getConfigString("BoughtItem","ShopPrefix"));
                eco.withdrawPlayer(p, price);
                return;
            }
        }

        if(item.getName().equalsIgnoreCase("§bDoppelte XP")) {
            if(p.hasPermission("perks.doublexp")) {
                p.sendMessage(Main.getInstance().getConfigString("AlreadyHaveItem","ShopPrefix"));
                return;
            }else{
                if (this.eco.getBalance(p) < price)
                {
                    System.out.println(this.eco.getBalance(p));
                    p.sendMessage(ChatColor.RED + "Du hast nicht genug Geld um dieses Item zu kaufen");
                    return;
                }
                Main.perms.playerAdd(null,p,"perks.doublexp");
                eco.withdrawPlayer(p, price);
                p.sendMessage(Main.getInstance().getConfigString("BoughtItem","ShopPrefix"));
                p.sendMessage("Please rejoin to activate your perk.");
                return;
            }
        }

        ItemStack stack = new ItemStack(item.getMaterial(), amount);
        p.getInventory().addItem(new ItemStack[] { stack });
        eco.withdrawPlayer(p, price);
    }

    public void sellItem(ShopItem item, Player p)
    {
        int amount = item.getAmount();
        int price = item.getSellPrice();

        if(item.getMaterial() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        if(item.getMaterial() == Material.AIR) {
            return;
        }
        if(item.getMaterial() == Material.BARRIER) {
            return;
        }

        if(item.getSellPrice() == 0) return;


        ItemStack stack = new ItemStack(item.getMaterial(), amount);
        if (p.getInventory().containsAtLeast(stack, amount))
        {
            p.getInventory().removeItem(new ItemStack[] { stack });
            eco.depositPlayer(p, price);
        }
        else
        {
            p.sendMessage(Main.getInstance().getConfigString("NeedItemToSell","ShopPrefix"));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        Player p = (Player)e.getWhoClicked();
        if (getViewers().contains(p))

        {
            e.setCancelled(true);

             if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null) return;


            if ((e.getCurrentItem().getType() == Material.PAPER) && (
                    (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("→")) || (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("←")) ||
                            (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("↑")) || (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("↓"))))
            {
                if (!getSettings().isAnimatedScrolling(p))
                {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("→")) {
                        scrollUp(p);
                    }
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("←")) {
                        scrollDown(p);
                    }

                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("↓")) {
                    animatedScrollUp(p);
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("↑")) {
                    animatedScrollDown(p);
                }
            }
            else if ((e.getCurrentItem().getType() == Material.COMPASS) && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + getSettings().getText(p, "settings"))))
            {
                getSettings().openShopSettings(p);
            }
            else if ((e.getCurrentItem().getType() == Material.OAK_SIGN) && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + getSettings().getText(p, "credits"))))
            {
                p.closeInventory();
                p.sendMessage(ChatColor.YELLOW + "");
                p.sendMessage(ChatColor.DARK_RED + "Main-Programmer: " + ChatColor.BLUE + "Main Developer: Unknown");
                p.sendMessage(ChatColor.BLUE + "Edited by: StaticRed ");
                p.sendMessage(ChatColor.YELLOW + "");
            }
            else
            {
                for (ShopItem item : getItems()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(item.getName()))
                    {
                        if (e.getAction() == InventoryAction.PICKUP_ALL)
                        {
                            buyItem(item, p);
                            break;
                        }
                        if (e.getAction() == InventoryAction.PICKUP_HALF)
                        {
                            sellItem(item, p);
                            break;
                        }
                    }
                }
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


    public Inventory getInventory(Player p)
    {
        this.inv = Bukkit.createInventory(p, this.size + 9, this.name);

        ItemStack forward = new ItemStack(Material.PAPER);
        ItemMeta forwardMeta = forward.getItemMeta();

        ItemStack backward = new ItemStack(Material.PAPER);
        ItemMeta backwardMeta = forward.getItemMeta();
        if (!getSettings().isAnimatedScrolling(p))
        {
            forwardMeta.setDisplayName("→");
            backwardMeta.setDisplayName("←");
        }
        else
        {
            forwardMeta.setDisplayName("↓");
            backwardMeta.setDisplayName("↑");
        }
        forward.setItemMeta(forwardMeta);
        backward.setItemMeta(backwardMeta);

        ItemStack empty = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1, (short)getSettings().getColorCode(p));
        ItemMeta emptyMeta = forward.getItemMeta();
        emptyMeta.setDisplayName(" ");
        empty.setItemMeta(emptyMeta);

        ItemStack settings = new ItemStack(Material.COMPASS);
        ItemMeta settingsMeta = settings.getItemMeta();
        settingsMeta.setDisplayName(ChatColor.DARK_RED + getSettings().getText(p, "settings"));
        settings.setItemMeta(settingsMeta);

        ItemStack credits = new ItemStack(Material.OAK_SIGN);
        ItemMeta creditsMeta = settings.getItemMeta();
        creditsMeta.setDisplayName(ChatColor.DARK_RED + getSettings().getText(p, "credits"));
        credits.setItemMeta(creditsMeta);

        int slot = this.size;

        this.inv.setItem(slot, empty);

        this.inv.setItem(slot + 2, empty);
        this.inv.setItem(slot + 3, backward);

        this.inv.setItem(slot + 5, forward);
        this.inv.setItem(slot + 6, empty);
        this.inv.setItem(slot + 7, credits);
        this.inv.setItem(slot + 8, empty);

        this.currentPage = 1;
        this.pages = ((this.items.size() - 1) / this.size + 1);
        for (int i = 0; i < this.items.size(); i++)
        {
            if (i >= this.size) {
                break;
            }
            this.inv.setItem(i, ((ShopItem)this.items.get(i)).getItem());
        }
        if (!getSettings().isAnimatedScrolling(p))
        {
            this.pageMeta.setDisplayName("§6" + this.currentPage + "/" + this.pages);
            this.page.setItemMeta(this.pageMeta);
            this.inv.setItem(slot + 1, settings);
            this.inv.setItem(this.size + 4, this.page);
        }
        else
        {
            this.inv.setItem(slot + 4, settings);
            this.inv.setItem(this.size + 1, empty);
        }
        return this.inv;
    }

    public String getName()
    {
        return this.name;
    }

    public List<ShopItem> getItems()
    {
        return this.items;
    }

    public ShopSettings getSettings()
    {
        return this.shopSettings;
    }

    public int getSize()
    {
        return this.size;
    }

    public List<Player> getViewers()
    {
        List<Player> viewers = new ArrayList();
        for (String s : this.viewing) {
            viewers.add(Bukkit.getPlayer(s));
        }
        return viewers;
    }
}
