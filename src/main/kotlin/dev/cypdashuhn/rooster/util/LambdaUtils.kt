package dev.cypdashuhn.rooster.util

typealias PredicateCombinator<T> = ((T) -> Boolean, (T) -> Boolean) -> ((T) -> Boolean)

/** Returns true if both first and second are true. */
fun <T> and(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) && second(t) }
}

/** Returns true if either first or second is true. */
fun <T : Any> or(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) || second(t) }
}

/** Returns the negation (not) of the input. */
fun <T> negate(condition: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !condition(t) }
}

/** Returns true if first and second are different. */
fun <T> xor(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) xor second(t) }
}

/** Returns true if first and second are not both true. */
fun <T> nand(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !(first(t) && second(t)) }
}

/** Returns true if both first and second are false. */
fun <T> nor(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !(first(t) || second(t)) }
}

/**
 * Returns true if first is false or second is true. Only false when first
 * is true and second is false.
 */
fun <T> implies(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !first(t) || second(t) }
}

/** Returns true if first is true and second is false. */
fun <T> andNot(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) && !second(t) }
}