package gg.uhc.heavyarmour;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ArmourCraftListener implements Listener {

    protected final AttributeModifierApplier applier;

    protected final Map<Material, AttributeInformation> mapping;

    protected static AttributeInformation getNewAttributeForMaterial(Material material, double percentReduction) {
        return new AttributeInformation(UUID.randomUUID(), material.name() + " Weight Reduction", 1, -(percentReduction / 100D));
    }

    public ArmourCraftListener(AttributeModifierApplier applier, double percentPer) {
        this.applier = applier;
        mapping = Maps.newHashMap();

        Map<Material, Integer> armourPoints = Maps.newHashMap();

        armourPoints.put(Material.LEATHER_BOOTS, 1);
        armourPoints.put(Material.LEATHER_LEGGINGS, 2);
        armourPoints.put(Material.LEATHER_CHESTPLATE, 3);
        armourPoints.put(Material.LEATHER_HELMET, 1);

        armourPoints.put(Material.GOLD_BOOTS, 1);
        armourPoints.put(Material.GOLD_LEGGINGS, 3);
        armourPoints.put(Material.GOLD_CHESTPLATE, 5);
        armourPoints.put(Material.GOLD_HELMET, 2);

        armourPoints.put(Material.CHAINMAIL_BOOTS, 1);
        armourPoints.put(Material.CHAINMAIL_LEGGINGS, 4);
        armourPoints.put(Material.CHAINMAIL_CHESTPLATE, 5);
        armourPoints.put(Material.CHAINMAIL_HELMET, 2);

        armourPoints.put(Material.IRON_BOOTS, 2);
        armourPoints.put(Material.IRON_LEGGINGS, 5);
        armourPoints.put(Material.IRON_CHESTPLATE, 6);
        armourPoints.put(Material.IRON_HELMET, 2);

        armourPoints.put(Material.DIAMOND_BOOTS, 3);
        armourPoints.put(Material.DIAMOND_LEGGINGS, 6);
        armourPoints.put(Material.DIAMOND_CHESTPLATE, 8);
        armourPoints.put(Material.DIAMOND_HELMET, 3);

        // set actual attributes
        for (Map.Entry<Material, Integer> points : armourPoints.entrySet()) {
            mapping.put(points.getKey(), getNewAttributeForMaterial(points.getKey(), points.getValue() * percentPer));
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();

        if (result == null) return;

        AttributeInformation info = mapping.get(result.getType());

        if (info == null) return;

        event.getInventory().setResult(applier.applyAttribute(result, info));
    }
}
