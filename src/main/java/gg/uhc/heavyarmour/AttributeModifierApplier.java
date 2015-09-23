package gg.uhc.heavyarmour;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AttributeModifierApplier {

    // static methods on CraftItemStack
    protected final Method asNMSCopyMethod;
    protected final Method asBukkitCopyMethod;

    // methods on ItemStack
    protected final Method getTagMethod;
    protected final Method setTagMethod;

    // methods on NBTTagCompound
    protected final Method getListMethod;
    protected final Method setMethod;
    protected final Method tagSetMethod;

    // methods on NBTTagList
    protected final Method addMethod;

    // constuctors for new objects
    protected final Constructor<?> nbtTagCompoundConstructor;
    protected final Constructor<?> nbtTagStringConstructor;
    protected final Constructor<?> nbtTagLongConstructor;
    protected final Constructor<?> nbtTagIntConstructor;
    protected final Constructor<?> nbtTagDoubleConstructor;

    public AttributeModifierApplier(Plugin plugin) throws ClassNotFoundException, NoSuchMethodException {
        String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1);

        Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        Class<?> nmsItemStackClass = Class.forName("net.minecraft.server." + version + ".ItemStack");
        Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
        Class<?> nbtBaseClass = Class.forName("net.minecraft.server." + version + ".NBTBase");
        Class<?> nbtTagListClass = Class.forName("net.minecraft.server." + version + ".NBTTagList");

        asNMSCopyMethod = craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
        asBukkitCopyMethod = craftItemStackClass.getDeclaredMethod("asBukkitCopy", nmsItemStackClass);

        getTagMethod = nmsItemStackClass.getDeclaredMethod("getTag");
        setTagMethod = nmsItemStackClass.getDeclaredMethod("setTag", nbtTagCompoundClass);

        getListMethod = nbtTagCompoundClass.getDeclaredMethod("getList", String.class, int.class);
        setMethod = nbtTagCompoundClass.getDeclaredMethod("set", String.class, nbtBaseClass);
        tagSetMethod = nbtTagCompoundClass.getDeclaredMethod("set", String.class, nbtBaseClass);

        addMethod = nbtTagListClass.getDeclaredMethod("add", nbtBaseClass);

        nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
        nbtTagStringConstructor = Class.forName("net.minecraft.server." + version + ".NBTTagString").getConstructor(String.class);
        nbtTagLongConstructor = Class.forName("net.minecraft.server." + version + ".NBTTagLong").getConstructor(long.class);
        nbtTagIntConstructor = Class.forName("net.minecraft.server." + version + ".NBTTagInt").getConstructor(int.class);
        nbtTagDoubleConstructor = Class.forName("net.minecraft.server." + version + ".NBTTagDouble").getConstructor(double.class);
    }

    public ItemStack applyAttribute(ItemStack stack, AttributeInformation attribute) {
        try {
            Object nmsStack = asNMSCopyMethod.invoke(null, stack);

            Object tag = getTagMethod.invoke(nmsStack);

            if (tag == null) {
                tag = nbtTagCompoundConstructor.newInstance();
                setTagMethod.invoke(nmsStack, tag);
            }

            Object nbtTagList = getListMethod.invoke(tag, "AttributeModifiers", 10);

            Object modifier = nbtTagCompoundConstructor.newInstance();
            setMethod.invoke(modifier, "Name", nbtTagStringConstructor.newInstance(attribute.getName()));
            setMethod.invoke(modifier, "UUIDLeast", nbtTagLongConstructor.newInstance(attribute.getUUID().getLeastSignificantBits()));
            setMethod.invoke(modifier, "UUIDMost", nbtTagLongConstructor.newInstance(attribute.getUUID().getMostSignificantBits()));
            setMethod.invoke(modifier, "AttributeName", nbtTagStringConstructor.newInstance("generic.movementSpeed"));
            setMethod.invoke(modifier, "Operation", nbtTagIntConstructor.newInstance(attribute.getType()));
            setMethod.invoke(modifier, "Amount", nbtTagDoubleConstructor.newInstance(attribute.getAmount()));

            addMethod.invoke(nbtTagList, modifier);

            tagSetMethod.invoke(tag, "AttributeModifiers", nbtTagList);

            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsStack);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return stack;
    }

    /*
     * THIS IS THE NMS VERSION OF THE ABOVE FOR READABILITY/REFERENCE
     */
//    public ItemStack applyAttributeNMS(ItemStack stack, AttributeInformation attribute) {
//        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
//
//        NBTTagCompound tag = nms.getTag();
//
//        if (tag == null) {
//            tag = new NBTTagCompound();
//            nms.setTag(tag);
//        }
//
//        NBTTagList attributes = tag.getList("AttributeModifiers", 10);
//
//        NBTTagCompound modifier = new NBTTagCompound();
//
//        modifier.set("Name", new NBTTagString(attribute.getName()));
//        modifier.set("UUIDLeast", new NBTTagLong(attribute.getUUID().getLeastSignificantBits()));
//        modifier.set("UUIDMost", new NBTTagLong(attribute.getUUID().getMostSignificantBits()));
//        modifier.set("AttributeName", new NBTTagString("generic.movementSpee"));
//        modifier.set("Operation", new NBTTagInt(attribute.getType()));
//        modifier.set("Amount", new NBTTagDouble(attribute.getAmount()));
//
//        attributes.add(modifier);
//
//        tag.set("AttributeModifiers", attributes);
//        return CraftItemStack.asBukkitCopy(nms);
//    }
}
