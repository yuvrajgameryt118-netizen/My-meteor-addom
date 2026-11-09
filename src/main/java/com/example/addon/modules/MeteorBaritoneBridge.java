package com.example.addon.modules;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.util.math.BlockPos;

public class MeteorBaritoneBridge extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> targetX = sgGeneral.add(new IntSetting.Builder()
        .name("target-x")
        .description("The target X coordinate for automated pathfinding.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> targetZ = sgGeneral.add(new IntSetting.Builder()
        .name("target-z")
        .description("The target Z coordinate for automated pathfinding.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Boolean> autoY = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-surface-y")
        .description("Automatically find the highest Y level at the destination.")
        .defaultValue(true)
        .build()
    );

    public MeteorBaritoneBridge() {
        super("meteor-baritone-bridge", "Fuses Meteor Client GUI and logic directly with Baritone pathfinding.");
    }

    @Override
    public void onActivate() {
        if (mc.world == null || mc.player == null) {
            toggle();
            return;
        }

        int x = targetX.get();
        int z = targetZ.get();
        int y = autoY.get() ? mc.world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, x, z) : (int) mc.player.getY();

        BlockPos destination = new BlockPos(x, y, z);

        // Direct API hook into Baritone to execute pathfinding
        BaritoneAPI.getProvider().getPrimaryProcess().getCustomGoalProcess().setGoalAndPath(new GoalBlock(destination));
        warning("Baritone pathfinder engaged towards: X: %d, Y: %d, Z: %d", x, y, z);
    }

    @Override
    public void onDeactivate() {
        // Safely cancel Baritone when the module is toggled off from the Meteor GUI
        if (BaritoneAPI.getProvider().getPrimaryProcess().isActive()) {
            BaritoneAPI.getProvider().getPrimaryProcess().cancel();
            info("Baritone pathfinding aborted.");
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        // Auto-disable module once Baritone reaches the destination successfully
        if (!BaritoneAPI.getProvider().getPrimaryProcess().isActive()) {
            info("Destination reached! Disabling bridge module.");
            toggle();
        }
    }
  }
