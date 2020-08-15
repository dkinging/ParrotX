package org.serverct.parrot.parrotx.command.subcommands;

import org.serverct.parrot.parrotx.PPlugin;
import org.serverct.parrot.parrotx.command.BaseCommand;
import org.serverct.parrot.parrotx.command.CommandHandler;
import org.serverct.parrot.parrotx.command.PCommand;
import org.serverct.parrot.parrotx.utils.I18n;

import java.util.Collections;
import java.util.Map;

public class HelpCommand extends BaseCommand {

    public HelpCommand(PPlugin plugin) {
        super(plugin, "help", 0);
        describe("查看插件或指定子指令的帮助信息");

        addChain(CommandChain.builder()
                .name("帮助")
                .description("查询插件命令帮助信息")
                .params(Collections.singletonList(CommandParam.builder()
                        .name("子命令")
                        .description("插件的其他子命令")
                        .optional(true)
                        .position(0)
                        .suggest(() -> plugin.getCmdHandler().getCommands().keySet().toArray(new String[0]))
                        .build()))
                .run((args, chain) -> {
                    CommandHandler handler = plugin.getCmdHandler();
                    Map<String, PCommand> subCommands = handler.getCommands();

                    if (args.length <= 0) {
                        // plugin.lang.getHelp(plugin.localeKey).forEach(sender::sendMessage);
                        handler.formatHelp().forEach(sender::sendMessage);
                    } else {
                        if (subCommands.containsKey(args[0]))
                            for (String help : subCommands.get(args[0]).getHelp()) sender.sendMessage(I18n.color(help));
                        else
                            sender.sendMessage(plugin.lang.build(plugin.localeKey, I18n.Type.WARN, "未知子命令, 输入 &d/" + plugin.getCmdHandler().mainCmd + " help &7获取插件帮助."));
                    }
                })
                .build());
    }

    @Override
    protected void call(String[] args) {
    }
}
