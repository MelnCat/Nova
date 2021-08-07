package xyz.xenondevs.nova.serialization.cbf

import io.netty.buffer.ByteBuf
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.serialization.cbf.element.other.ItemStackElement
import xyz.xenondevs.nova.serialization.cbf.element.other.LocationElement
import xyz.xenondevs.nova.serialization.cbf.element.other.NamespacedKeyElement
import xyz.xenondevs.nova.serialization.cbf.element.other.UUIDElement
import xyz.xenondevs.nova.serialization.cbf.element.primitive.*
import xyz.xenondevs.nova.util.ReflectionRegistry.CB_CRAFT_ITEM_STACK_CLASS
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

interface Element {
    
    fun getTypeId(): Int
    
    fun write(buf: ByteBuf)
    
}

interface BackedElement<T> : Element {
    val value: T
    
    companion object BackedElementRegistry {
        
        val TYPE_TO_ELEMENT: Map<KClass<*>, KFunction<BackedElement<*>>> = mapOf(
            Boolean::class to ::BooleanElement,
            Byte::class to ::ByteElement,
            Int::class to ::IntElement,
            Char::class to ::CharElement,
            Float::class to ::FloatElement,
            Long::class to ::LongElement,
            Double::class to ::DoubleElement,
            String::class to ::StringElement,
            BooleanArray::class to ::BooleanArrayElement,
            IntArray::class to ::IntArrayElement,
            CharArray::class to ::CharArrayElement,
            FloatArray::class to ::FloatArrayElement,
            LongArray::class to ::LongArrayElement,
            DoubleArray::class to ::DoubleArrayElement,
            Array<String>::class to ::StringArrayElement,
            UUID::class to ::UUIDElement,
            ItemStack::class to ::ItemStackElement,
            CB_CRAFT_ITEM_STACK_CLASS.kotlin to ::ItemStackElement,
            Location::class to ::LocationElement,
            NamespacedKey::class to ::NamespacedKeyElement
        )
        
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> createElement(value: T): BackedElement<T> {
            if (value is Enum<*>) {
                return StringElement(value.name) as BackedElement<T>
            }
            val constructor = TYPE_TO_ELEMENT[value::class]
                ?: throw IllegalArgumentException("Couldn't find BackedElement type for " + value::class)
            return constructor.call(value) as BackedElement<T>
        }
        
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> createElement(clazz: KClass<T>, value: Any): BackedElement<T> {
            if (value is Enum<*>) {
                return StringElement(value.name) as BackedElement<T>
            }
            val constructor = TYPE_TO_ELEMENT[clazz]
                ?: throw IllegalArgumentException("Couldn't find BackedElement type for $clazz")
            return constructor.call(value) as BackedElement<T>
        }
        
    }
    
}