package net.starly.gacha.util;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class EncodeUtil {

    private EncodeUtil() {}


    public static String encode(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); BukkitObjectOutputStream boos = new BukkitObjectOutputStream(bos)) {
            boos.writeObject(obj);
            byte[] serialized = bos.toByteArray();
            return Base64.getEncoder().encodeToString(serialized);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T decode(String encoded, Class<T> clazz) {
        byte[] serialized = Base64.getDecoder().decode(encoded);

        try (ByteArrayInputStream bis = new ByteArrayInputStream(serialized); BukkitObjectInputStream bois = new BukkitObjectInputStream(bis)) {
            return clazz.cast(bois.readObject());
        } catch (NullPointerException ex) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}