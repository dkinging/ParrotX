package org.serverct.parrot.parrotx;

import lombok.Getter;
import lombok.NonNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.serverct.parrot.parrotx.command.CommandHandler;
import org.serverct.parrot.parrotx.config.PConfig;
import org.serverct.parrot.parrotx.data.PConfiguration;
import org.serverct.parrot.parrotx.hooks.BaseExpansion;
import org.serverct.parrot.parrotx.utils.i18n.I18n;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class PPlugin extends JavaPlugin {

    public static final int PARROTX_ID = 9515;
    public final String PARROTX_VERSION = "1.4.7-Alpha (Build 5)";
    private final List<PConfiguration> configs = new ArrayList<>();
    private final List<BaseExpansion> expansions = new ArrayList<>();
    public String localeKey = "Chinese";
    protected PConfig pConfig;
    @Getter
    protected I18n lang;
    private Consumer<PluginManager> listenerRegister = null;
    private final String versionLog = "本插件基于 ParrotX {0}, 感谢使用.";
    private String timeLog = "插件加载完成, 共耗时&a{0}ms&r.";
    @Getter
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        final long timestamp = System.currentTimeMillis();

        try {
            beforeInit();

            init();

            afterInit();

            if (Objects.nonNull(listenerRegister)) {
                listenerRegister.accept(Bukkit.getPluginManager());
                lang.log.info("已注册监听器.");
            }

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && !this.expansions.isEmpty()) {
                this.expansions.forEach(PlaceholderExpansion::register);
                lang.log.info("已注册 PlaceholderAPI 扩展包.");
            }

            if (getConfig().getBoolean("bStats", true) && !Bukkit.getPluginManager().isPluginEnabled("ParrotX")) {
                final Metrics metrics = new Metrics(this, PARROTX_ID);
                metrics.addCustomChart(new Metrics.SingleLineChart("plugins_using_parrotx", () -> 1));
                if (!Bukkit.getPluginManager().isPluginEnabled("ParrotX")) {
                    metrics.addCustomChart(new Metrics.SimplePie("integration_method", () -> "Compile"));
                }
                lang.log.info("已启用 bStats 数据统计.");
                lang.log.info("若您需要禁用此功能, 一般情况下可于配置文件 config.yml 中编辑或新增 \"bStats: false\" 关闭此功能.");
            } else {
                lang.log.warn("bStats 数据统计已被禁用.");
            }

            if (Objects.nonNull(timeLog)) {
                final long time = System.currentTimeMillis() - timestamp;
                lang.log.info(timeLog, time);
            }
            if (Objects.nonNull(versionLog)) {
                lang.log.info(versionLog, PARROTX_VERSION);
            }
        } catch (Throwable e) {
            lang.log.error(I18n.INIT, "插件", e, null);
            this.setEnabled(false);
        }
    }

    public void init() {
        lang = new I18n(this, localeKey);

        preload();

        if (Objects.nonNull(pConfig)) {
            pConfig.init();
            localeKey = pConfig.getConfig().getString("Language");
        }

        this.configs.forEach(PConfiguration::init);

        load();
    }

    protected void beforeInit() {
    }

    protected void preload() {
    }

    protected void load() {
    }

    protected void afterInit() {
    }

    protected void listen(Consumer<PluginManager> register) {
        this.listenerRegister = register;
    }

    protected void setTimeLog(final String format) {
        this.timeLog = format;
    }

    protected <T extends BaseExpansion> void registerExpansion(final T expansions) {
        this.expansions.add(expansions);
    }

    protected void registerCommand(@NonNull CommandHandler handler) {
        PluginCommand command = Bukkit.getPluginCommand(handler.mainCmd);
        if (command != null) {
            this.commandHandler = handler;
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        } else {
            lang.log.error(I18n.REGISTER, "命令", "无法获取插件主命令.");
        }
    }

    public void registerConfiguration(final PConfiguration configuration) {
        this.configs.add(configuration);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.configs.forEach(config -> {
            if (!config.isReadOnly()) {
                config.save();
            }
        });
        preDisable();
    }

    public void preDisable() {
        getServer().getScheduler().cancelTasks(this);
        this.configs.clear();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        if (Objects.isNull(this.pConfig) || Objects.isNull(this.pConfig.getConfig())) {
            return super.getConfig();
        }
        return this.pConfig.getConfig();
    }

    public File getFile(final String path) {
        return new File(getDataFolder(), path);
    }
}
