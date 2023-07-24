package net.tntchina.fakeplayer;

import com.mojang.authlib.GameProfile;
import io.papermc.paper.adventure.AdventureComponent;
import net.minecraft.core.Rotations;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec2;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class MyFakePlayer extends ServerPlayer {

    public String nameContent = "";

    public MyFakePlayer(MinecraftServer server, ServerLevel world, GameProfile profile) {
        super(server, world, profile);
        this.affectsSpawning = true;
        this.isRealPlayer = false;
        this.visibleByDefault = true;
        this.joining = false;
        this.connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND), this);
        this.setGameMode(GameType.CREATIVE);
        //this.onGround = false; //TODO: let fakePlayer flies in the server.
        this.setInvisible(false);
        this.setNoGravity(true);
        this.nameContent = profile.getName();
    }

    public MyFakePlayer(MinecraftServer server, ServerLevel world, String nameContent) {
        this(server, world, new GameProfile(UUID.randomUUID(), nameContent));
        this.nameContent = nameContent;
    }

    /*@Override
    public boolean isSleeping() {
        return true;
    }*/


    @Override
    public boolean attackable() {
        return false;
    }

    public void sendJoinPacket() {
        this.send((new ClientboundAddPlayerPacket(this)));
    }

    public void setRotation(ServerPlayer player) {
        this.setXRot(player.getXRot());
        this.setYRot(player.getYRot()); //TODO: 让npc视角和玩家一模一样
    }

    public void setLocation(Player player) {
        Location location = player.getLocation();
        this.setPos(location.getX(), location.getY(), location.getZ());
    }

    public String getNameContent() {
        return this.nameContent;
    }

    public void send(Packet<?> packet) {
        if (this.connection != null) {
            this.connection.send(packet);
        }
    }
}
