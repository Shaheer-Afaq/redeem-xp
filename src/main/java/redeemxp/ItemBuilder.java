package redeemxp;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static redeemxp.Manager.getWorld;

public class ItemBuilder {
    private final ItemStack stack;

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public <T> ItemBuilder setComponent(ComponentType<T> type, T value) {
        stack.set(type, value);
        return this;
    }

    public ItemBuilder setName(String name, Formatting color) {
        return setComponent(DataComponentTypes.CUSTOM_NAME, Text.literal(name).formatted(color));
    }

    public ItemBuilder setLore(String text, Formatting color, boolean replace) {
        List<Text> lines = new ArrayList<>();
        if (!replace){
            lines = stack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        }
        lines.add(Text.literal(text).formatted(color));
        return setComponent(DataComponentTypes.LORE, new LoreComponent(lines));
    }

    public ItemBuilder setNbt(Consumer<NbtCompound> nbtModifier) {
        NbtCompound nbt = new NbtCompound();
        nbtModifier.accept(nbt);
        return setComponent(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public ItemBuilder setMaxDura(int amount) {
        return setComponent(DataComponentTypes.MAX_DAMAGE, amount);
    }
    public ItemBuilder setDura(int amount) {
        return setComponent(DataComponentTypes.DAMAGE, amount);
    }

    public ItemBuilder setStackSize(int max_stack_size){
        return setComponent(DataComponentTypes.MAX_STACK_SIZE, max_stack_size);
    }

    public ItemBuilder setEntityData(EntityType<?> type, Consumer<NbtCompound> nbtModifier) {
        NbtCompound nbt = new NbtCompound();
        nbtModifier.accept(nbt);
        TypedEntityData<EntityType<?>> typedData = TypedEntityData.create(type, NbtComponent.of(nbt).copyNbt());

        return setComponent(DataComponentTypes.ENTITY_DATA, typedData);
    }

    public ItemBuilder addEnchant(RegistryKey<Enchantment> enchantment, int level) {
        Registry<Enchantment> registry = getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        stack.addEnchantment(registry.getOrThrow(enchantment), level);
        return this;
    }

    public ItemBuilder addAttribute(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation operation, AttributeModifierSlot slot) {
        AttributeModifiersComponent current = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        EntityAttributeModifier modifier = new EntityAttributeModifier(
                Identifier.of("chaos",
                        attribute.getKey().map(key -> key.getValue().getPath())
                                .orElse("unknown")),
                amount, operation);

        return setComponent(DataComponentTypes.ATTRIBUTE_MODIFIERS, current.with(attribute, modifier, slot));
    }

    public ItemBuilder setTrim(RegistryKey<ArmorTrimPattern> patternKey, RegistryKey<ArmorTrimMaterial> materialKey) {
        var rm = getWorld().getRegistryManager();

        ArmorTrim trim = new ArmorTrim(
                rm.getOrThrow(RegistryKeys.TRIM_MATERIAL).getOrThrow(materialKey),
                rm.getOrThrow(RegistryKeys.TRIM_PATTERN).getOrThrow(patternKey)
        );

        return setComponent(DataComponentTypes.TRIM, trim);
    }

    public ItemStack build() {
        return this.stack;
    }
}