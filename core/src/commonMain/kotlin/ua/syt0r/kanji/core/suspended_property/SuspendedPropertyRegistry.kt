package ua.syt0r.kanji.core.suspended_property

/***
 * Each backup-able repository should extend registry, use Koin's bind to this interface for
 * repository component to be searchable by properties backup manager and use registerProperty
 * method to create properties
 * For non backup-able properties use provider to create properties directly
 */
interface SuspendedPropertyRegistry {

    val properties: List<SuspendedProperty<*>>

    fun <T> registerProperty(
        block: SuspendedPropertyProvider.() -> SuspendedProperty<T>
    ): SuspendedProperty<T>

}

class DefaultSuspendedPropertyRegistry(
    private val provider: SuspendedPropertyProvider
) : SuspendedPropertyRegistry {

    private val internalProperties = mutableSetOf<SuspendedProperty<*>>()

    override val properties: List<SuspendedProperty<*>>
        get() {
            return internalProperties.toList()
        }

    override fun <T> registerProperty(
        block: SuspendedPropertyProvider.() -> SuspendedProperty<T>
    ): SuspendedProperty<T> {
        val property = provider.block()
        internalProperties.add(property)
        return property
    }

}