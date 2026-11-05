package com.t2pellet.teamshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {
    private static final Map<UUID, Integer> playerRanks = new HashMap<>();

    public static int getPlayerRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, 0);
    }

    public static void setPlayerRank(UUID uuid, int rank) {
        playerRanks.put(uuid, rank);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("teamrank")
            .requires(source -> source.hasPermission(2)) // Только для оператора сервера (OP level 2+)
            .then(Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("rank", IntegerArgumentType.integer(0))
                .executes(context -> {
                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                    int rank = IntegerArgumentType.getInteger(context, "rank");
                    setPlayerRank(target.getUUID(), rank);
                    context.getSource().sendSuccess(Component.literal("§aРанг игрока " + target.getName().getString() + " установлен на " + rank), true);
                    return 1;
                }))))
            .then(Commands.literal("get")
                .then(Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                    int rank = getPlayerRank(target.getUUID());
                    context.getSource().sendSuccess(Component.literal("§eРанг игрока " + target.getName().getString() + ": " + rank), false);
                    return 1;
                }))));
    }
}
