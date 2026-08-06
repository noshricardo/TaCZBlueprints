package com.raiiiden.taczblueprints.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.raiiiden.taczblueprints.attachment.GunUnlocksProvider;
import com.raiiiden.taczblueprints.attachment.IGunUnlocks;
import com.raiiiden.taczblueprints.network.ModNetworking;
import com.raiiiden.taczblueprints.network.SyncUnlockedGunsPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashSet;

public class BlueprintCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("blueprints")
                // List unlocked guns
                .then(Commands.literal("list")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            IGunUnlocks unlocks = GunUnlocksProvider.get(player);
                            if (unlocks.getUnlockedGuns().isEmpty()) {
                                player.sendSystemMessage(Component.literal("§eYou have no unlocked blueprints."));
                            } else {
                                player.sendSystemMessage(Component.literal("§aUnlocked blueprints:"));
                                for (String gunId : unlocks.getUnlockedGuns()) {
                                    player.sendSystemMessage(Component.literal(" - " + gunId));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                // Clear own unlocked guns
                .then(Commands.literal("clear")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            IGunUnlocks unlocks = GunUnlocksProvider.get(player);
                            unlocks.clearAll();
                            ModNetworking.sendToPlayer(player, new SyncUnlockedGunsPacket(unlocks.getUnlockedGuns()));

                            player.sendSystemMessage(Component.literal("§cAll unlocked blueprints cleared."));

                            return Command.SINGLE_SUCCESS;
                        })
                        // Admin: Clear all players
                        .then(Commands.literal("all")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    Collection<ServerPlayer> players = source.getServer().getPlayerList().getPlayers();

                                    int clearedCount = 0;
                                    for (ServerPlayer player : players) {
                                        IGunUnlocks unlocks = GunUnlocksProvider.get(player);
                                        unlocks.clearAll();
                                        ModNetworking.sendToPlayer(player, new SyncUnlockedGunsPacket(new HashSet<>()));
                                        clearedCount++;
                                    }

                                    final int finalClearedCount = clearedCount;
                                    source.sendSuccess(
                                            () -> Component.literal("§aCleared blueprints for " + finalClearedCount + " player(s)"),
                                            true
                                    );

                                    return Command.SINGLE_SUCCESS;
                                }))
                        // Admin: Clear specific player
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");

                                    IGunUnlocks unlocks = GunUnlocksProvider.get(targetPlayer);
                                    unlocks.clearAll();
                                    ModNetworking.sendToPlayer(targetPlayer, new SyncUnlockedGunsPacket(new HashSet<>()));

                                    source.sendSuccess(
                                            () -> Component.literal("§aCleared blueprints for " + targetPlayer.getName().getString()),
                                            true
                                    );

                                    targetPlayer.displayClientMessage(
                                            Component.literal("§eYour gun blueprints have been cleared by an administrator"),
                                            false
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}