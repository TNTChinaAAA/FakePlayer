package net.tntchina.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MyFakePlayer extends ServerPlayer {
    public MyFakePlayer(MinecraftServer server, ServerLevel world, GameProfile profile) {
        super(server, world, profile);
    }

    @Override
    public boolean attackable() {
        return false;
    }
}
