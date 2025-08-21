package dev.cypdashuhn.rooster.util.infix_gate

import dev.cypdashuhn.rooster.util.*

/** Returns true if both this and other are true. */
infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean {
    return and(this, other)
}

/** Returns true if either this or other is true. */
infix fun <T : Any> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean {
    return or(this, other)
}

/** Returns the negation (not) of this. */
fun <T> ((T) -> Boolean).negate(): (T) -> Boolean {
    return negate(this)
}

/** Returns true if this and other are different. */
infix fun <T> ((T) -> Boolean).xor(other: (T) -> Boolean): (T) -> Boolean {
    return xor(this, other)
}

/** Returns true if this and other are not both true. */
infix fun <T> ((T) -> Boolean).nand(other: (T) -> Boolean): (T) -> Boolean {
    return nand(this, other)
}

/** Returns true if both this and other are false. */
infix fun <T> ((T) -> Boolean).nor(other: (T) -> Boolean): (T) -> Boolean {
    return nor(this, other)
}

/**
 * Returns true if this is false or other is true. Only false when this is
 * true and other is false.
 */
infix fun <T> ((T) -> Boolean).implies(other: (T) -> Boolean): (T) -> Boolean {
    return implies(this, other)
}

/** Returns true if this is true and other is false. */
infix fun <T> ((T) -> Boolean).andNot(other: (T) -> Boolean): (T) -> Boolean {
    return andNot(this, other)
}

