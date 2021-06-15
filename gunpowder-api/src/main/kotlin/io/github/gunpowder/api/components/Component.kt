package io.github.gunpowder.api.components

import net.minecraft.nbt.NbtCompound

abstract class Component<T : Any> {
    lateinit var bound: T
    internal fun setBound(it: Any) {
        bound = it as T
    }

    /**
     * Serialization as supported by:
     * - Entity
     * - ServerPlayerEntity (with respawning)
     * - ItemStack
     * Alternatively, these can be stored in a database column as NbtCompound
     */
    open fun writeNbt(tag: NbtCompound) {}
    open fun fromNbt(tag: NbtCompound) {}
}