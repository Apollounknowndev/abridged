package dev.worldgen.abridged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ConfigHandler {
    private static final ConfigCodec DEFAULT_CONFIG = new ConfigCodec(1.0F);
    private static ConfigCodec CONFIG;

    public static void load(Path path) {
        if (!Files.isRegularFile(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try(BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(gson.toJson(ConfigCodec.CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT_CONFIG).result().get()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonElement json = JsonParser.parseReader(reader);
            Optional<ConfigCodec> result = ConfigCodec.CODEC.parse(JsonOps.INSTANCE, json).result();
            if (result.isEmpty()) {
                result = Optional.of(DEFAULT_CONFIG);
            }
            CONFIG = result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(ConfigCodec.CODEC.encodeStart(JsonOps.INSTANCE, CONFIG).result().get()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigCodec getConfig() {
        return CONFIG;
    }
}