package gg.uhc.heavyarmour;

import java.util.UUID;

public class AttributeInformation {
    protected final UUID uuid;
    protected final String name;
    protected final int type;
    protected final double amount;

    public AttributeInformation(UUID uuid, String name, int type, double amount) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.amount = amount;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}
