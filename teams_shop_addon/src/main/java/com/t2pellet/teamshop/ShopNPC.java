package com.t2pellet.teamshop;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.core.ModTeam;

public class ShopNPC extends PathfinderMob {
    public ShopNPC(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            boolean isOp = serverPlayer.hasPermissions(2);

            // Проверка команды в моде Teams
            ModTeam team = TeamDB.getInstance().getTeamForPlayer(serverPlayer.getUUID());
            if (team == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cВы должны состоять в команде, чтобы открыть магазин!"));
                return InteractionResult.SUCCESS;
            }

            int playerRank = RankManager.getPlayerRank(serverPlayer.getUUID());
            int requiredRank = 1; // Например, требуемый ранг для покупки

            if (isOp && serverPlayer.isCrouching()) {
                // Оператор настраивает магазин
                serverPlayer.sendSystemMessage(Component.literal("§a[Настройка Магазина] Измените товары и ранги через /teamrank set <player> <rank>"));
            } else {
                if (playerRank < requiredRank) {
                    serverPlayer.sendSystemMessage(Component.literal("§cУ вас недостаточный ранг для покупок! Требуемый ранг: " + requiredRank + ", ваш ранг: " + playerRank));
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("§a[Магазин Команды] Добро пожаловать! Ваш ранг: " + playerRank));
                    // Открытие GUI магазина
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
