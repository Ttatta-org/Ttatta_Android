package com.umc.design.character

class AccessorySet private constructor(
    accessories: Iterable<Accessory>
) {
    val values: Set<Accessory>

    companion object {
        fun create(accessories: Iterable<Accessory>) = AccessorySet(accessories)
        fun create(vararg accessories: Accessory) = AccessorySet(accessories.asIterable())
    }

    init {
        accessories.forEach { accessory ->
            val count = accessories.map {
                accessory.characterType == it.characterType && accessory.bodyPart == it.bodyPart
            }.count { it }

            if (count > 1) {
                throw IllegalArgumentException(
                    "AccessorySet must contain " +
                            "only one accessory of " +
                            "the same character type and body part"
                )
            }
        }
        values = accessories.toSet()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AccessorySet) return false
        return values == other.values
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }
}