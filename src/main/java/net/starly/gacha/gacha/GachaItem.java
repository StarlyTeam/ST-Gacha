package net.starly.gacha.gacha;

import lombok.Getter;
import lombok.Setter;
import net.starly.gacha.util.EncodeUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class GachaItem extends ItemStack {

    static {
        ConfigurationSerialization.registerClass(ItemStack.class, "ItemStack");
    }

    @Getter
    @Setter
    private double percentage = 0d;

    public GachaItem(ItemStack stack, double d) throws IllegalArgumentException {
        Validate.notNull(stack, "Cannot copy null stack");
        setType(stack.getType());
        setAmount(stack.getAmount());
        setDurability(stack.getDurability());
        setData(stack.getData());
        if (stack.hasItemMeta()) {
            setItemMeta(stack.getItemMeta());
        }
        percentage = d;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap();
        result.put("type", this.getType().name());
        if (this.getDurability() != 0) {
            result.put("damage", this.getDurability());
        }

        if (this.getAmount() != 1) {
            result.put("amount", this.getAmount());
        }

        ItemMeta meta = this.getItemMeta();
        if (!Bukkit.getItemFactory().equals(meta, (ItemMeta)null)) {
            result.put("meta", meta);
        }

        result.put("percentage", percentage);

        return result;
    }

    public static GachaItem deserialize(Map<String, Object> args) {
        Material type = Material.getMaterial((String)args.get("type"));
        short damage = 0;
        int amount = 1;
        if (args.containsKey("damage")) {
            damage = ((Number)args.get("damage")).shortValue();
        }

        if (args.containsKey("amount")) {
            amount = ((Number)args.get("amount")).intValue();
        }

        ItemStack result = new ItemStack(type, amount, damage);
        Object raw;
        if (args.containsKey("enchantments")) {
            raw = args.get("enchantments");
            if (raw instanceof Map) {
                Map<?, ?> map = (Map)raw;
                Iterator var8 = map.entrySet().iterator();

                while(var8.hasNext()) {
                    Map.Entry<?, ?> entry = (Map.Entry)var8.next();
                    Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());
                    if (enchantment != null && entry.getValue() instanceof Integer) {
                        result.addUnsafeEnchantment(enchantment, (Integer)entry.getValue());
                    }
                }
            }
        } else if (args.containsKey("meta")) {
            raw = args.get("meta");
            if (raw instanceof ItemMeta) {
                result.setItemMeta((ItemMeta)raw);
            }
        }

        return new GachaItem(result, (Double) args.get("percentage"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        ItemStack stack = this.clone();

        result.put("stack", EncodeUtil.encode(stack));
        result.put("percentage", percentage);
        return result;
    }

    public static GachaItem fromMap(Map<String, Object> map) {
        try {

            System.out.println((String) map.get("stack"));

            ItemStack stack = EncodeUtil.decode((String) map.get("stack"), ItemStack.class);

            return new GachaItem(stack, (Double) map.get("percentage"));

        } catch (Exception exception) {
            return null;
        }
    }
}
