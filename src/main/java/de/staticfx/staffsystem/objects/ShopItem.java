package de.staticfx.staffsystem.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem
{
    private String name;
    private int buyPrice;
    private int sellPrice;
    private int amount;
    private ItemStack item;
    private Material mat;

    public ShopItem(Material mat, String name, int buyPrice, int sellPrice, int amount)
    {
        this.name = name;

        this.buyPrice = buyPrice;



        this.sellPrice = sellPrice;
        this.amount = amount;
        this.mat = mat;

        List<String> lore = new ArrayList();

        lore.add(ChatColor.DARK_PURPLE + "BUY: " + ChatColor.YELLOW + this.buyPrice);

        if(sellPrice == 0) {
            lore.add(ChatColor.DARK_PURPLE + "SELL: " + ChatColor.YELLOW + "Du kannst dieses Item nicht verkaufen");
        }else{
            lore.add(ChatColor.DARK_PURPLE + "SELL: " + ChatColor.YELLOW + this.sellPrice);
        }

        if(mat == Material.DIAMOND_CHESTPLATE) {
            lore.add("§8-§5/tag");
            lore.add("§8-§5/weather");
            lore.add("§8-§5/spam");
            lore.add("§8-§5/repair");
            lore.add("§8-§5/Gaymode");
            lore.add("§8-§5Item behalten Perk");
        }else{
            lore.add(ChatColor.DARK_PURPLE + "Amount: " + ChatColor.YELLOW + amount);
        }


        this.item = new ItemStack(mat, amount);
        ItemMeta itemMeta = this.item.getItemMeta();

        if(mat == Material.BLACK_STAINED_GLASS_PANE) {
            itemMeta.setDisplayName("§8´");
        }else{
            itemMeta.setDisplayName(name);
        }
        itemMeta.setLore(lore);
        this.item.setItemMeta(itemMeta);
    }

    public String getName()
    {
        return this.name;
    }

    public int getBuyPrice()
    {
        return this.buyPrice;
    }

    public int getSellPrice()
    {
        return this.sellPrice;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    public Material getMaterial()
    {
        return this.mat;
    }
}
