package com.pocan.pticket;

import com.pocan.pticket.buttons.ButtonManager;
import com.pocan.pticket.commands.CommandManager;
import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.listeners.EventListener;
import com.pocan.pticket.modals.ModalManager;
import com.pocan.pticket.selectmenu.SelectMenuManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

public class pTicket {

    private final ShardManager shardManager;
    private final Dotenv config;
    private static pTicket instance;

    public static pTicket getInstance() {
        return instance;
    }

    public pTicket() throws LoginException {
        instance = this;
        config = Dotenv.configure().load();

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("Token"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setActivity(Activity.watching(config.get("WatchingMessage")));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY);
        shardManager = builder.build();

        // Registering the listeners
        shardManager.addEventListener(new EventListener(), new CommandManager(), new ButtonManager(), new ModalManager(), new SelectMenuManager());
    }

    public ShardManager getShardManager() {
        return shardManager;
    }
    public Dotenv getConfig() {
        return config;
    }

    public static String getSimpleDateFormat(String argument) {
        SimpleDateFormat date = new SimpleDateFormat(argument);
        return date.format(new Date());
    }

    public static void sendLogToConsole(String log) {
        System.out.println(log);
    }


    public static void main(String[] args) {
        try {
            pTicket pTicket = new pTicket();
            DatabaseOperation databaseOperation = new DatabaseOperation();
        } catch (LoginException e) {
            String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " ERROR]: " + " Token hatalı.";
            pTicket.sendLogToConsole(logger);
        }
    }



}
