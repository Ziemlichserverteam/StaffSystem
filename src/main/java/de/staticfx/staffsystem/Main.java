package de.staticfx.staffsystem;

import de.staticfx.staffsystem.commands.*;
import de.staticfx.staffsystem.db.DataBaseConnection;
import de.staticfx.staffsystem.events.*;
import de.staticfx.staffsystem.filemanagment.ConfigManagment;
import de.staticfx.staffsystem.filemanagment.IDManagment;
import de.staticfx.staffsystem.objects.ChatLog;
import de.staticfx.staffsystem.objects.Shop;
import de.staticfx.staffsystem.objects.ShopItem;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Main extends JavaPlugin {

    public static Permission perms = null;
    private static Main instance;
    public static String prefix;
    public static String banPrefix;
    public static String reportprefix;
    public static String votePrefix;
    public static ArrayList<Player> logedIn = new ArrayList<>();
    public static ArrayList<Player> teamChatUser = new ArrayList<>();
    public static ArrayList<Player> banNotifycations = new ArrayList<>();
    public static ArrayList<String> reasons = new ArrayList<String>(Arrays.asList("Beleidigung","Spam","Werbung","Hacking","Griefing","Bannumgehung","Bauwerk","Bugusing","Name","Skin"));
    public static ArrayList<String> mutereasons = new ArrayList<String>(Arrays.asList("Beleidigung","Spam","Werbung"));
    public static HashMap<UUID, ChatLog> playerMessageHashMap = new HashMap<>();
    public static ArrayList<Player> team = new ArrayList<>();
    public static HashMap<Player, Integer> playerPageHashMap = new HashMap<>();
    public static Economy eco;
    public static boolean updaterStarted = false;
    public static Chat chat;
    public static boolean vote = false;
    public static int voteYes;
    public static int voteNo;
    public static boolean canVote;
    public static List<Player> voted = new ArrayList<>();
    public static boolean ban;
    public static LuckPerms api;
    public static String premiumPrefix;
    public static String headPrefix;
    public static String shopPrefix;
    public static HashMap<Player, Location> reportLastLocation = new HashMap<>();
    public static boolean globalChat = true;


    @Override
    public void onEnable() {
        System.out.println("Enabling Plugin");
        instance = this;

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("sts.team")) team.add(p);
            playerMessageHashMap.put(p.getUniqueId(), new ChatLog(new ArrayList<>(), new ArrayList<>()));
        }




        IDManagment.INSTANCE.loadFile();
        ConfigManagment.INSTANCE.loadFile();


        getCommand("password").setExecutor(new PasswordCommandExecutor());
        getCommand("TC").setExecutor(new TCCommandExecutor());
        getCommand("login").setExecutor(new LoginCommandExecutor());
        getCommand("punish").setExecutor(new PunishCommandExecutor());
        getCommand("id").setExecutor(new IDCommandExecutor());
        getCommand("check").setExecutor(new CheckCommandExecutor());
        getCommand("history").setExecutor(new HistoryCommandExecutor());
        getCommand("kick").setExecutor(new KickCommandExecutor());
        getCommand("report").setExecutor(new ReportCommandExecutor());
        getCommand("reports").setExecutor(new ReportsCommandExecutor());
        getCommand("log").setExecutor(new LogCommandExecutor());
        getCommand("vote").setExecutor(new VoteCommandExecutor());
        getCommand("v").setExecutor(new VCommandExecutor());
        getCommand("premium").setExecutor(new PremiumCommandExecutor());
        getCommand("head").setExecutor(new HeadCommandExecutor());
        getCommand("shop").setExecutor(new ShopCommandExecutor());
        getCommand("globalchat").setExecutor(new GlobalChat());
        getCommand("spec").setExecutor(new SpecComanndExecutor());

        setupEconomy();
        setupChat();
        setupPermissions();
        loadPerms();

        getServer().getPluginManager().registerEvents(new LoginEvent(),this);
        getServer().getPluginManager().registerEvents(new LogOutEvent(),this);
        getServer().getPluginManager().registerEvents(new MessageEvent(),this);
        getServer().getPluginManager().registerEvents(new InventoryClickEvent(),this);
        getServer().getPluginManager().registerEvents(new PlayerPortalEvent(),this);
        getServer().getPluginManager().registerEvents(new CommandBlocker(),this);


        loadTableStaff();
        loadTableBanSystem();
        loadTableMuteSystem();
        loadUnbannableTable();
        loadTableReportSystem();
        loadTableLogs();
        loadTableVote();
        loadPremiumVote();
        loadHeadTable();

        prefix = getRawString("StaffPrefix").replaceAll("&","§");
        banPrefix = getRawString("BanPrefix").replaceAll("&","§");
        reportprefix = getRawString("ReportPrefix").replaceAll("&","§");
        votePrefix = getRawString("VotePrefix").replaceAll("&","§");
        premiumPrefix = getRawString("PremiumPrefix").replaceAll("&","§");
        headPrefix = getRawString("HeadPrefix").replaceAll("&","§");
        shopPrefix = getRawString("ShopPrefix").replaceAll("&","§");
    }

    public void loadTableStaff() {
        System.out.println("here");
        try {
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            PreparedStatement ps = con.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS accounts(UUID VARCHAR(36) PRIMARY KEY, Password VARCHAR(100), Salt VARCHAR(20), Rank VARCHAR(16), Grouppower INT(5))");
            ps.executeUpdate();
            ps.close();
            con.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTableBanSystem() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS bans(BanID INT(10) PRIMARY KEY, UUID VARCHAR(36), Reason VARCHAR(50), endTime LONG, Punisher VARCHAR(16),  timeStamp VARCHAR(50), Type VARCHAR(5), Permanent BOOLEAN, Unbanned VARCHAR(50), Active BOOLEAN)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTableMuteSystem() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS mutes(BanID INT(10) PRIMARY KEY, UUID VARCHAR(36), Reason VARCHAR(50), endTime LONG, Punisher VARCHAR(16),  timeStamp VARCHAR(50), Type VARCHAR(5), Permanent BOOLEAN, Unbanned VARCHAR(50), Active BOOLEAN)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadUnbannableTable() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS unbannable(UUID VARCHAR(36) PRIMARY KEY)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTableReportSystem() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS reports(reportedPlayer VARCHAR(36), reportingPlayer VARCHAR(36), editingPlayer VARCHAR(36), reason VARCHAR(100), ID int(50) PRIMARY KEY, timeStamp LONG,amount int(50), view BOOLEAN)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTableLogs() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS logs(UUID VARCHAR(36), timeStamp LONG, message NVARCHAR, logID LONG)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void loadTableVote() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS votes(UUID VARCHAR(36), timeStamp LONG, ban boolean)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPremiumVote() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS premium(UUID VARCHAR(36) PRIMARY KEY, timeStamp LONG)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadHeadTable() {
        try{
            DataBaseConnection con = DataBaseConnection.INSTANCE;
            con.openConnection();
            con.executeUpdate("CREATE TABLE IF NOT EXISTS heads(UUID VARCHAR(36) PRIMARY KEY, timeStamp LONG)");
            con.closeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }

    }

    public boolean validTime(String time) {
        if (time.endsWith("s")) {
            return true;
        }
        if (time.endsWith("min")) {
            return true;
        }
        if (time.endsWith("h")) {
            return true;
        }
        if (time.endsWith("d")) {
            return true;
        }
        if (time.endsWith("m")) {
            return true;
        }
        if (!time.endsWith("p")) return false;
        if (!time.endsWith("permanent")) return false;
        return true;
    }

    public Long timeToMilliSeconds(String time) {
        String edit;
        Long result = 0L;
        if (time.endsWith("s")) {
            edit = time.substring(0, time.length() - 1);
            result = Long.parseLong(edit) * 1000L;
        }
        if (time.endsWith("min")) {
            edit = time.substring(0, time.length() - 3);
            result = Long.parseLong(edit) * 60L * 1000L;
        }
        if (time.endsWith("h")) {
            edit = time.substring(0, time.length() - 1);
            result = Long.parseLong(edit) * 60L * 60L * 1000L;
        }
        if (time.endsWith("d")) {
            edit = time.substring(0, time.length() - 1);
            result = Long.parseLong(edit) * 24L * 60L * 60L * 1000L;
        }
        if (!time.endsWith("m")) return result;
        edit = time.substring(0, time.length() - 1);
        return Long.parseLong(edit) * 31L * 24L * 60L * 60L * 1000L;
    }

    public static Main getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return prefix;
    }

    public String getConfigString(String configString) {
            return (ConfigManagment.INSTANCE.getString(configString).replaceAll("&","§"));
    }

    public String getConfigString(String configString,String prefix) {
        return (ConfigManagment.INSTANCE.getString(prefix).replaceAll("&","§") + ConfigManagment.INSTANCE.getString(configString).replaceAll("&","§"));
    }

    public String getIdFormat() {
        return ConfigManagment.INSTANCE.getString("IDFormat").replaceAll("&","§");
    }

    public String getNotifyFormat() {
        return ConfigManagment.INSTANCE.getString("BanNotify").replaceAll("&","§");
    }

    public String getReportFormat() {
        return ConfigManagment.INSTANCE.getString("ReportNotify").replaceAll("&","§");
    }

    public String getBanScreen() {
        return ConfigManagment.INSTANCE.getString("BanScreen").replaceAll("&","§");
    }

    public String getTeamChatFormat() {
        return ConfigManagment.INSTANCE.getString("TeamChatFormat").replaceAll("&","§");
    }

    public String getMuteMessage() {
        return ConfigManagment.INSTANCE.getString("MuteMessage").replaceAll("&","§");
    }

    public String getRawString(String string) {
        return ConfigManagment.INSTANCE.getString(string);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("Verbindung zu Vault fehlgeschlagen!");
            return false;
        }
        System.out.println("Verbindung zu Vault erfolgreich hergestellt");
        eco = rsp.getProvider();
        return eco != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public void loadPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }
    }


    public static Shop getShop() {

        Shop shop = new Shop("§aShop",5,Main.getInstance());


        shop.addItem(new ShopItem(Material.GRASS_BLOCK,"§aOverworldshop",0,0,1));
        shop.addItem(new ShopItem(Material.DIAMOND_ORE,"§aErzshop",0,0,1));
        shop.addItem(new ShopItem(Material.COOKED_BEEF,"§aEssenshop",0,0,1));
        shop.addItem(new ShopItem(Material.WATER_BUCKET,"§aMeeresshop",0,0,1));
        shop.addItem(new ShopItem(Material.NETHER_BRICK,"§aNethershop",0,0,1));
        shop.addItem(new ShopItem(Material.END_STONE,"§aEndshop",0,0,1));
        shop.addItem(new ShopItem(Material.STRING,"§aDropshop",0,0,1));
        shop.addItem(new ShopItem(Material.WHITE_WOOL,"§aWolleshop",0,0,1));
        shop.addItem(new ShopItem(Material.BEACON,"§aAdmin shop",0,0,1));



        return shop;
    }

    public static Shop getAdminShop() {
        Shop shop = new Shop("Admin shop",4,Main.getInstance());

        shop.addItem(new ShopItem(Material.ENDER_CHEST,"§8Enderchest",2500,0,1));
        shop.addItem(new ShopItem(Material.DRAGON_EGG,"§8Drachenei",150000,0,1));
        shop.addItem(new ShopItem(Material.SADDLE,"§8Sattel",3500,0,1));
        shop.addItem(new ShopItem(Material.DIAMOND_HORSE_ARMOR,"§8Dia Pferderüstung",2500,0,1));
        shop.addItem(new ShopItem(Material.IRON_HORSE_ARMOR,"§8Eisen Pferderüstung",1500,0,1));
        shop.addItem(new ShopItem(Material.GOLDEN_HORSE_ARMOR,"§8Gold Pferderüstung",1250,0,1));
        shop.addItem(new ShopItem(Material.CONDUIT,"§8Aquisator",8000,0,1));
        shop.addItem(new ShopItem(Material.EXPERIENCE_BOTTLE,"§8XP Bottle",1000,0,64));
        shop.addItem(new ShopItem(Material.NAME_TAG,"§8Nametag",1500,0,1));
        shop.addItem(new ShopItem(Material.TURTLE_HELMET,"§8Schiltkrötenpanzer",3000,0,1));
        shop.addItem(new ShopItem(Material.TRIDENT,"§8Dreizack",4000,0,1));
        shop.addItem(new ShopItem(Material.TOTEM_OF_UNDYING,"§8Totem der Unsterblichkeit",5000,0,1));
        shop.addItem(new ShopItem(Material.DRAGON_BREATH,"§8Drachenatem",6000,0,64));
        shop.addItem(new ShopItem(Material.ELYTRA,"§8Elytra",50000,0,1));
        shop.addItem(new ShopItem(Material.COBWEB,"§8Spinnennetz",2500,0,64));
        shop.addItem(new ShopItem(Material.FIREWORK_ROCKET,"§8Rakete",500,0,64));
        shop.addItem(new ShopItem(Material.BEACON,"§8Beacon",50000,0,1));
        shop.addItem(new ShopItem(Material.ENCHANTED_GOLDEN_APPLE,"§b5 Plots",1200000,0,1));
        shop.addItem(new ShopItem(Material.EXPERIENCE_BOTTLE,"§bDoppelte XP",600000,0,1));
        shop.addItem(new ShopItem(Material.WOLF_SPAWN_EGG,"§bWold",15000,0,1));
        shop.addItem(new ShopItem(Material.PANDA_SPAWN_EGG,"§bPanda",10000,0,1));
        shop.addItem(new ShopItem(Material.HORSE_SPAWN_EGG,"§bHorse",10000,0,1));
        shop.addItem(new ShopItem(Material.PARROT_SPAWN_EGG,"§bPapagei",12000,0,1));
        shop.addItem(new ShopItem(Material.CAT_SPAWN_EGG,"§bKatze",12000,0,1));




        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));


        return shop;
    }

    public static Shop getEndShop() {
        Shop shop = new Shop("End shop",4,Main.getInstance());
        shop.addItem(new ShopItem(Material.PURPUR_BLOCK,"§8Purpurblock",800,0,64));
        shop.addItem(new ShopItem(Material.DRAGON_HEAD,"§8Drachenkopf",2500,200,1));
        shop.addItem(new ShopItem(Material.SHULKER_BOX,"§8Shulkerbox",10000,500,1));
        shop.addItem(new ShopItem(Material.END_ROD,"§8Endstab",2500,300,64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

    public static Shop getSeaShop() {
        Shop shop = new Shop("Meeres shop",4,Main.getInstance());

        shop.addItem(new ShopItem(Material.PRISMARINE,"§8Prisamrin",800,180,64));
        shop.addItem(new ShopItem(Material.DARK_PRISMARINE,"§8Dunkler Prisamrin",1500,150,64));
        shop.addItem(new ShopItem(Material.SEA_LANTERN,"§8See laterne",1500,300,64));
        shop.addItem(new ShopItem(Material.DRIED_KELP_BLOCK,"§8Seetangblock",600,70,64));
        shop.addItem(new ShopItem(Material.TURTLE_EGG,"§8Schildkrötenei",1500,300,64));
        shop.addItem(new ShopItem(Material.TUBE_CORAL_BLOCK,"§8Korallenblock",1000,150,64));
        shop.addItem(new ShopItem(Material.BRAIN_CORAL_BLOCK,"§8Korallenblock2",1000,150,64));
        shop.addItem(new ShopItem(Material.BUBBLE_CORAL_BLOCK,"§8Korallenblock3",1000,150,64));
        shop.addItem(new ShopItem(Material.FIRE_CORAL_BLOCK,"§8Korallenblock4",1000,150,64));
        shop.addItem(new ShopItem(Material.HORN_CORAL_BLOCK,"§8Korallenblock",1000,200,64));
        shop.addItem(new ShopItem(Material.NAUTILUS_SHELL,"§8Nautilusschale",600,100,1));
        shop.addItem(new ShopItem(Material.HEART_OF_THE_SEA,"§8Herz des Meeres",5000,400,1));
        shop.addItem(new ShopItem(Material.SPONGE,"§8Schwamm",5000,500,64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));


        return shop;

    }

    public static Shop getDropShop() {
        Shop shop = new Shop("Drop shop",4,Main.getInstance());

        shop.addItem(new ShopItem(Material.LEATHER,"§8Leather",400,50,64));
        shop.addItem(new ShopItem(Material.FEATHER,"§8Feather",400,50,64));
        shop.addItem(new ShopItem(Material.STRING, "§8Faden", 200, 10, 64));
        shop.addItem(new ShopItem(Material.SLIME_BALL, "§8Schleimball", 800, 150, 64));
        shop.addItem(new ShopItem(Material.INK_SAC, "§8Tintenbeutel", 250, 50, 64));
        shop.addItem(new ShopItem(Material.ROTTEN_FLESH, "§8Erotisches Fleisch", 250, 25, 64));
        shop.addItem(new ShopItem(Material.ZOMBIE_HEAD, "§8Zombie kopf", 12000, 600, 64));
        shop.addItem(new ShopItem(Material.CREEPER_HEAD, "§8Creeper kopf", 12000, 600, 64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

    public static Shop getNetherShop() {
        Shop shop = new Shop("Nether shop",4,Main.getInstance());
        shop.addItem(new ShopItem(Material.NETHER_BRICK,"§8Netherziegel",300,60,64));
        shop.addItem(new ShopItem(Material.QUARTZ_BLOCK,"§8Quartzblock",1500,250,64));
        shop.addItem(new ShopItem(Material.GLOWSTONE,"§8Glowstone",2000,0,64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

    public static Shop getFoodShop() {
        Shop shop = new Shop("Food shop",4,Main.getInstance());
        shop.addItem(new ShopItem(Material.COOKED_BEEF,"§8Steak",400,0,64));
        shop.addItem(new ShopItem(Material.COOKED_SALMON,"§8Lachs",800,50,64));
        shop.addItem(new ShopItem(Material.COOKED_COD,"§8Fich",800,50,64));
        shop.addItem(new ShopItem(Material.TROPICAL_FISH,"§8Fisch",800,50,64));
        shop.addItem(new ShopItem(Material.HONEY_BLOCK,"§8Honigblock",1000,250,64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

    public static Shop getOverworldShop() {
        Shop shop = new Shop("Overworld shop",4,Main.getInstance());
        shop.addItem(new ShopItem(Material.DIRT,"§8Dirt",50,0,64));
        shop.addItem(new ShopItem(Material.SAND,"§8Sand",100,20,64));
        shop.addItem(new ShopItem(Material.RED_SAND,"§8Roter Sand",120,25,64));
        shop.addItem(new ShopItem(Material.OAK_LOG,"§8Eiche",500,100,64));
        shop.addItem(new ShopItem(Material.BIRCH_LOG,"§8Birke",500,100,64));
        shop.addItem(new ShopItem(Material.SPRUCE_LOG,"§8Fichte",500,100,64));
        shop.addItem(new ShopItem(Material.JUNGLE_LOG,"§8Jungel",500,100,64));
        shop.addItem(new ShopItem(Material.ACACIA_LOG,"§8Akazie",500,100,64));
        shop.addItem(new ShopItem(Material.DARK_OAK_LOG,"§8Schwarzeiche",600,125,64));
        shop.addItem(new ShopItem(Material.GRAVEL, "§8Kies", 200, 40, 64));
        shop.addItem(new ShopItem(Material.SANDSTONE, "§8Sandstein", 200, 30, 64));
        shop.addItem(new ShopItem(Material.RED_SANDSTONE, "§8Roter Sandstein", 250, 35, 64));
        shop.addItem(new ShopItem(Material.BRICK, "§8Ziegelstein", 600, 150, 64));
        shop.addItem(new ShopItem(Material.COBBLESTONE, "§8Bruchstein", 100, 0, 64));
        shop.addItem(new ShopItem(Material.GRANITE, "§8Granit", 30, 0, 64));
        shop.addItem(new ShopItem(Material.DIORITE, "§8Diorit", 30, 0, 64));
        shop.addItem(new ShopItem(Material.ANDESITE, "§8Andesit", 30, 0, 64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));


        return shop;
    }

    public static Shop getOreShop() {
        Shop shop = new Shop("Erze shop",4,Main.getInstance());
        shop.addItem(new ShopItem(Material.REDSTONE,"§8Redstone",500,100,64));
        shop.addItem(new ShopItem(Material.COAL,"§8Kohle",600,120,64));
        shop.addItem(new ShopItem(Material.IRON_INGOT,"§8Eisen",100,50,64));
        shop.addItem(new ShopItem(Material.GOLD_INGOT,"§8Gold",1200,250,64));
        shop.addItem(new ShopItem(Material.LAPIS_LAZULI,"§8Lapis",800,100,64));
        shop.addItem(new ShopItem(Material.DIAMOND,"§8Diamant",5000,450,64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

    public static Shop getWoolShop() {
        Shop shop = new Shop("Wolle shop",4,Main.getInstance());

        shop.addItem(new ShopItem(Material.WHITE_WOOL,"§8Weiße wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.BLACK_WOOL,"§8Schwarze wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.RED_WOOL,"§8Rote wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.ORANGE_WOOL,"§8Orangene wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.GREEN_WOOL,"§8Grüne wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.BLUE_WOOL,"§8Blaue wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.LIME_WOOL,"§8Hellgrüne wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.YELLOW_WOOL,"§8Gelbe wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.CYAN_WOOL,"§8Türkise wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.MAGENTA_WOOL,"§8Magenta wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.BROWN_WOOL,"§8Braune wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.PURPLE_WOOL,"§8Lila wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.GRAY_WOOL,"§8Graue wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.LIGHT_BLUE_WOOL,"§8Hellblaue wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.LIGHT_GRAY_WOOL,"§8Hellgraue wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.PINK_WOOL,"§8Pink wolle", 200, 60, 64));
        shop.addItem(new ShopItem(Material.BARRIER,"§cBack",0,0,1));

        return shop;
    }

}
