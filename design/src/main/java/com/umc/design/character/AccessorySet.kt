package com.umc.design.character

class AccessorySet private constructor(
    accessories: Iterable<Accessory>
): Collection<Accessory> {
    private val values: Set<Accessory>
    override val size: Int get() = values.size

    companion object {
        fun create(accessories: Iterable<Accessory>) = AccessorySet(accessories)
        fun create(vararg accessories: Accessory) = AccessorySet(accessories.asIterable())
    }

    init {
        val set = mutableSetOf<Accessory>()
        accessories.forEach { accessory ->
            if (set.conflict(accessory))
                throw IllegalArgumentException("AccessorySet conflicted")
            else
                set += accessory
        }
        values = set.toSet()
    }

    fun plusReplacingConflict(accessory: Accessory): AccessorySet {
        val set = mutableSetOf(accessory)
        values.forEach { prevAccessory ->
            if (!set.conflict(prevAccessory))
                set += prevAccessory
        }
        return AccessorySet(set)
    }

    override fun containsAll(elements: Collection<Accessory>): Boolean {
        return values.containsAll(elements)
    }

    override fun contains(element: Accessory): Boolean {
        return values.contains(element)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AccessorySet) return false
        return values == other.values
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    override fun isEmpty(): Boolean {
        return values.isEmpty()
    }

    override fun iterator(): Iterator<Accessory> {
        return values.iterator()
    }

    operator fun plus(accessory: Accessory): AccessorySet {
        return AccessorySet(values + accessory)
    }

    operator fun minus(accessory: Accessory): AccessorySet {
        return AccessorySet(values - accessory)
    }

    override fun toString(): String {
        return "AccessorySet(values=$values)"
    }
}

fun Collection<Accessory>.conflict(accessory: Accessory): Boolean {
    return this.any {
        it.characterType == accessory.characterType && it.bodyPart == accessory.bodyPart
    }
}