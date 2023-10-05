package net.tntchina.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

public class MyFakePlayer extends ServerPlayer {

    public String nameContent = "";
    public PlayerTeam playerTeam;

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
        this.playerTeam = this.getScoreboard().addPlayerTeam("FakePlayer");
        this.playerTeam.setColor(ChatFormatting.AQUA);
        this.playerTeam.setPlayerPrefix(CraftChatMessage.fromStringOrNull(Utils.getPrefix()));
        this.playerTeam.setNameTagVisibility(Team.Visibility.ALWAYS);
        this.setAllowsListing();
        //this.playerTeam.setDisplayName(CraftChatMessage.fromStringOrNull("我他妈真的好6啊"));
       /*

        ObjectiveCriteria.RenderType renderType = ObjectiveCriteria.RenderType.INTEGER;
        Objective o = this.getScoreboard().addObjective("test", ObjectiveCriteria.DUMMY, CraftChatMessage.fromStringOrNull(ChatColor.GREEN + "TEST"), renderType);

        //o.setRenderType(ObjectiveCriteria.RenderType.INTEGER);
        int slotNumber = Scoreboard.DISPLAY_SLOT_BELOW_NAME;
        this.getScoreboard().setDisplayObjective(slotNumber, o);

        */

        this.getScoreboard().addPlayerToTeam(this.nameContent, this.playerTeam);
    }

    public MyFakePlayer(MinecraftServer server, ServerLevel world, String nameContent) {
        this(server, world, new GameProfile(UUID.randomUUID(), nameContent));
        this.nameContent = nameContent;
    }

    /*@Override
    public boolean isSleeping() {
        return true;
    }*/

    public void setAllowsListing() {
        try {
            Field f = ServerPlayer.class.getDeclaredField("cU");

            if (f != null) {
                f.setAccessible(true);
                f.setBoolean(this, true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean attackable() {
        return false;
    }

    /*
    @Override
    public boolean allowsListing() {
        return true;
    }*/

    @Deprecated
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

    @Override
    public Team getTeam() {
        return this.playerTeam == null ? super.getTeam() : this.playerTeam;
    }
}
