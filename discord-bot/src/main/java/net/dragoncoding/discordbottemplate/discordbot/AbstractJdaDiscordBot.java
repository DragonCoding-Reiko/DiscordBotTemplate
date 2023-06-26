package net.dragoncoding.discordbottemplate.discordbot;

import net.dragoncoding.discordbottemplate.core.utils.TimeUtils;
import net.dragoncoding.discordbottemplate.discordbot.interfaces.IDiscordBot;
import net.dragoncoding.discordbottemplate.discordbot.interfaces.IDiscordEventListener;
import net.dragoncoding.discordbottemplate.discordbot.utils.DiscordBotMessages;
import net.dragoncoding.discordbottemplate.discordbot.utils.TokenHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.EnumSet;

import static net.dragoncoding.discordbottemplate.discordbot.utils.DiscordBotMessages.*;

public abstract class AbstractJdaDiscordBot implements IDiscordBot {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJdaDiscordBot.class);
    private ShardManager shardManager;

    /**
     * Starts the bot as Spring bean after Spring has loaded its context
     */
    public void afterPropertiesSet() {
        var startTime = System.currentTimeMillis();

        shardManager = createShardManager(); //open the connection to discord
        awaitJdaReady(); //Wait until all Shards are ready
        initSlashCommands(); //Add Slash-Commands to the JDAs

        onBotReady();

        shardManager.setStatus(OnlineStatus.ONLINE);
        shardManager.setActivity(getActivity());

        var durationInMs = System.currentTimeMillis() - startTime;
        var startupMessage = DiscordBotMessages.format(BOT_STARTUP_MESSAGE, TimeUtils.formatMsToDurationInSeconds(durationInMs));
        LOG.info(startupMessage);
    }

    /**
     * Stops the bot once Spring shuts down
     */
    public void destroy() {
        var startTime = System.currentTimeMillis();

        onBotShutdown();

        shardManager.setStatus(OnlineStatus.OFFLINE);
        shardManager.shutdown();

        var durationInMs = System.currentTimeMillis() - startTime;
        var shutdownMessage = DiscordBotMessages.format(BOT_SHUTDOWN_MESSAGE, TimeUtils.formatMsToDurationInSeconds(durationInMs));
        LOG.info(shutdownMessage);
    }

    /**
     * Creates the ShardManager with the default implementation making it possible to
     * state {@link GatewayIntent}s, {@link CacheFlag}s and register EventListeners.
     *
     * @return the created {@link ShardManager}
     *
     * @see TokenHandler#getDiscordToken()
     */
    @NotNull
    protected ShardManager createShardManager() {
        var builder = DefaultShardManagerBuilder.create(
                TokenHandler.getDiscordToken(),
                getGatewayIntents());

        builder.disableCache(getCacheFlagsToDisable());

        //Showing Startup-message and Idle Status til the bot starts up
        builder.setStatus(OnlineStatus.IDLE);
        builder.setActivity(Activity.playing(BOT_STARTUP_ACTIVITY_MESSAGE));

        builder.addEventListeners(getEventListeners());

        return builder.build();
    }

    /**
     * Blocks until the {@link ShardManager}'s {@link net.dv8tion.jda.api.JDA}s have connected.
     */
    private void awaitJdaReady() {
        shardManager.getShards().forEach(jda -> {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    /**
     * Updates all global commands for this bot.
     * So far only SlashCommands are allowed.
     * 
     * @see net.dv8tion.jda.api.interactions.commands.build.Commands#slash(String, String)
     */
    protected void initSlashCommands() {
        shardManager.getShards().forEach(jda -> {
            var commands = jda.updateCommands();

            commands.addCommands(getCommands()).queue();
        });
    }

    /**
     * Provides all global commands this bot should have
     * @return a collection of {@link CommandData}
     *
     * @see AbstractJdaDiscordBot#initSlashCommands()
     * @see JDA#updateCommands() 
     * @see net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction#addCommands(Collection)
     */
    protected abstract Collection<CommandData> getCommands();

    /**
     * Provides the required {@link GatewayIntent}s for this bot
     * @return an {@link EnumSet} containing the {@link GatewayIntent}s
     * 
     * @see DefaultShardManagerBuilder#create(String, Collection)
     */
    protected abstract EnumSet<GatewayIntent> getGatewayIntents();

    /**
     * Provides the {@link CacheFlag}s that should be disabled on the bot
     * @return an {@link Collection} containing the {@link CacheFlag}s
     *
     * @see DefaultShardManagerBuilder#disableCache(Collection)
     */
    protected abstract Collection<CacheFlag> getCacheFlagsToDisable();

    /**
     * Provides the EventListeners that should be registered for this bot
     * @return an {@link Collection} containing the {@link IDiscordEventListener}s
     *
     *  @see DefaultShardManagerBuilder#addEventListeners(Collection)
     */
    protected abstract Collection<IDiscordEventListener> getEventListeners();

    /**
     * Provides the activity the bot should show once being initialized and online
     * @return the {@link Activity} to show
     *
     * @see ShardManager#setActivity(Activity) 
     * @see DefaultShardManagerBuilder#setActivity(Activity)
     */
    protected abstract Activity getActivity();

    /**
     * Gets called when the bot has initialized and is connected to JDA.
     * Will be called before the bots {@link OnlineStatus} switches to {@link OnlineStatus#ONLINE}
     *
     * @see AbstractJdaDiscordBot#afterPropertiesSet()
     */
    protected void onBotReady() { }

    /**
     * Gets called before the bots {@link ShardManager} gets shut down
     *
     * @see AbstractJdaDiscordBot#destroy()
     * @see ShardManager#shutdown()
     */
    protected void onBotShutdown() { }
}
