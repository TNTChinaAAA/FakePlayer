package net.tntchina.fakeplayer;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import io.papermc.paper.chunk.system.scheduling.ChunkLoadTask;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {

    public FakePlayer fakePlayer;

    public ConfigManager(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

    public void load() {
        Gson gson = new Gson();
        File file = new File(FakePlayer.getFolder(), "players.json");
        this.createFile(file);
        FileReader fr = null;

        try {
            fr = new FileReader(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (fr != null) {
            BufferedReader reader = new BufferedReader(fr);
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            Map<String, CraftWorld> worlds = Utils.getWorldMaps();

            if (json.has("players")) {
                JsonObject players = (JsonObject) json.get("players");

                Map<String, JsonElement> map = players.asMap();

                for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                    String nameContent = entry.getKey();
                    JsonObject properties = (JsonObject) entry.getValue();
                    UUID uuid = UUID.randomUUID();
                    CraftWorld world = null;
                    float xRot = 0;
                    boolean hasXRot = false;
                    float yRot = 0;
                    boolean hasYRot = false;
                    float headRot = 0;
                    boolean hasHeadRot = false;
                    float bodyRot = 0;
                    boolean hasBodyRot = false;
                    double x = 0;
                    boolean hasX = false;
                    double y = 0;
                    boolean hasY = false;
                    double z = 0;
                    boolean hasZ = false;

                    if (properties.has("uuid")) {
                        uuid = gson.fromJson(properties.get("uuid").getAsString(), UUID.class);
                    }

                    if (properties.has("world")) {
                        CraftWorld world1 = worlds.get(properties.get("world").getAsString());

                        if (world1 != null) {
                            world = world1;
                        }
                    }

                    if (properties.has("location")) {
                        JsonObject location = (JsonObject) properties.get("location");

                        if (location.has("x")) {
                            x = location.get("x").getAsDouble();
                            hasX = true;
                        }

                        if (location.has("y")) {
                            y = location.get("y").getAsDouble();
                            hasY = true;
                        }

                        if (location.has("z")) {
                            z = location.get("z").getAsDouble();
                            hasZ = true;
                        }
                    }

                    if (properties.has("rotation")) {
                        JsonObject rotation = (JsonObject) properties.get("rotation");

                        if (rotation.has("rotX")) {
                            xRot = rotation.get("rotX").getAsFloat();
                            hasXRot = true;
                        }

                        if (rotation.has("rotY")) {
                            yRot = rotation.get("rotY").getAsFloat();
                            hasYRot = true;
                        }

                        if (rotation.has("bodyRot")) {
                            bodyRot = rotation.get("bodyRot").getAsFloat();
                            hasBodyRot = true;
                        }

                        if (rotation.has("headRot")) {
                            headRot = rotation.get("headRot").getAsFloat();
                            hasHeadRot = true;
                        }
                    }

                    if (world != null) {
                        ServerLevel nmsWorld = world.getHandle();
                        MyFakePlayer fk_player = new MyFakePlayer(MinecraftServer.getServer(), world.getHandle(), new GameProfile(uuid, nameContent));
                        fk_player.setLevel(world.getHandle());

                        if (hasXRot && hasYRot) {
                            fk_player.setXRot(xRot);
                            fk_player.setYRot(yRot);
                        }

                        if (hasX && hasY && hasZ) {
                            fk_player.setPos(x, y, z);
                        }

                        if (hasBodyRot) {
                            fk_player.setYBodyRot(bodyRot);
                        }

                        if (hasHeadRot) {
                            fk_player.setYHeadRot(headRot);
                        }

                        fk_player.setHealth(20);
                        fk_player.isRealPlayer = false;
                        Utils.setFakePlayerColorName(nameContent, fk_player);
                        Utils.addSpecificPlayer(fk_player, world);
                        //world.addEntityToWorld(fk_player, CreatureSpawnEvent.SpawnReason.CUSTOM);
                        //fk_player.spawnIn(world.getHandle());
                        /*Chunk craftChunk = world.getChunkAt(fk_player.getBlockX(), fk_player.getBlockZ());
                        craftChunk.setForceLoaded(true);
                        craftChunk.load();
                        LevelChunk levelChunk = nmsWorld.getChunk(fk_player.getBlockX(), fk_player.getBlockZ());
                        levelChunk.setLoaded(true);
                        levelChunk.loadCallback();
                         */
                        //fk_player.isRealPlayer = true;
                        //nmsWorld.getChunkSource().chunkMap.addEntity(fk_player);
                        //nmsWorld.playerChunkLoader.addPlayer(fk_player);
                        //nmsWorld.playerChunkLoader.updatePlayer(fk_player);
                        //nmsWorld.playerChunkLoader.tick();
                        //fk_player.isRealPlayer = false;
                        //FakePlayer.commandExecutor.addPlayerToServerList(fk_player);
                        //FakePlayer.commandExecutor.updateScoreboard(world.getHandle(), fk_player);
                        //FakePlayer.commandExecutor.dedicatedPlayerList.onPlayerJoinFinish(fk_player, world.getHandle(), "local");
                        ServerStatus serverping = MinecraftServer.getServer().getStatus();

                        if (serverping != null) {
                            fk_player.sendServerStatus(serverping);
                        }

                        fk_player.isRealPlayer = false;
                        Utils.fakePlayerList.add(fk_player);
                        Utils.fakePlayerMap.put(fk_player, world);

                        for (ServerPlayer player__ : MinecraftServer.getServer().getPlayerList().getPlayers()) {
                            if (player__.isRealPlayer && !(player__ instanceof MyFakePlayer)) {
                                //server.getPlayerList().placeNewPlayer(player__.connection.connection, npc);
                                Utils.doSending(player__.connection, fk_player);
                            }
                        }
                    }
                }
            }

            try {
                reader.close();
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FakePlayer.getFolder(), "players.json");
        this.createFile(file);
        JsonObject jsonObject = new JsonObject();
        JsonObject fkPlayers = new JsonObject();

        for (Map.Entry<MyFakePlayer, CraftWorld> entry : Utils.getFakePlayerMaps().entrySet()) {
            MyFakePlayer fkPlayer = entry.getKey();
            CraftWorld world = entry.getValue();
            JsonObject element = new JsonObject();
            JsonObject location = new JsonObject();
            JsonObject rotation = new JsonObject();
            element.addProperty("uuid", gson.toJson(fkPlayer.getUUID(), UUID.class));
            element.addProperty("world", world.getName());
            location.addProperty("x", fkPlayer.getX());
            location.addProperty("y", fkPlayer.getY());
            location.addProperty("z", fkPlayer.getZ());
            element.add("location", location);
            rotation.addProperty("rotX", fkPlayer.getXRot());
            rotation.addProperty("rotY", fkPlayer.getYRot());
            rotation.addProperty("headRot", fkPlayer.getYHeadRot());
            rotation.addProperty("bodyRot", fkPlayer.yBodyRot);
            element.add("rotation", rotation);
            fkPlayers.add(fkPlayer.getNameContent(), element);
            Utils.removeSpecificPlayer(fkPlayer, world.getHandle());
        }

        Utils.fakePlayerList = new ArrayList<>();
        Utils.fakePlayerMap = new HashMap<>();
        jsonObject.add("players", fkPlayers);

        try {
            FileWriter wr = new FileWriter(file);
            JsonWriter jr = new JsonWriter(wr);
            gson.toJson(jsonObject, jr);
            jr.close();
            wr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
