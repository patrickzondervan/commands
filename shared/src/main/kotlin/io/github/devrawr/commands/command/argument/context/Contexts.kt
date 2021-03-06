package io.github.devrawr.commands.command.argument.context

import io.github.devrawr.commands.command.argument.context.defaults.*
import io.github.devrawr.commands.processor.executor.Executor
import io.github.devrawr.commands.util.ObjectInstanceUtil.getOrCreateInstance
import java.util.*
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.reflect.KClass

object Contexts
{
    val contexts = mutableMapOf<Class<*>, ArgumentContext<*>>(
        Int::class.java to IntegerArgumentContext,
        Long::class.java to LongArgumentContext,
        Double::class.java to DoubleArgumentContext,
        Float::class.java to FloatArgumentContext,
        String::class.java to StringArgumentContext,
        UUID::class.java to UUIDArgumentContext,
    )

    inline fun <reified K, reified V : ArgumentContext<K>> useContext() = useContext(K::class.java, V::class.java)

    fun <K, V : ArgumentContext<K>> useContext(
        keyType: Class<K>,
        valueType: Class<V>
    ): Contexts
    {
        return this.apply {
            this.contexts[keyType] = valueType.kotlin.getOrCreateInstance()
        }
    }

    inline fun <reified T> createContext(noinline body: (String) -> T) = createContext(T::class.java, body)
    inline fun <reified T> createContext(noinline body: (Executor<*>?, String) -> T) = createContext(T::class.java, body)

    fun <T> createContext(
        type: Class<T>,
        body: (String) -> T
    ): Contexts
    {
        return this.apply {
            this.contexts[type] = object : ArgumentContext<T>
            {
                override fun fromString(executor: Executor<*>?, value: String): T?
                {
                    return body.invoke(value)
                }
            }
        }
    }

    fun <T> createContext(
        type: Class<T>,
        body: (Executor<*>?, String) -> T
    ): Contexts
    {
        return this.apply {
            this.contexts[type] = object : ArgumentContext<T>
            {
                override fun fromString(executor: Executor<*>?, value: String): T?
                {
                    return body.invoke(executor, value)
                }
            }
        }
    }

    inline fun <reified T : Any> retrieveContext(): ArgumentContext<T>
    {
        return retrieveContext(
            if (T::class.javaPrimitiveType != null)
            {
                T::class.javaPrimitiveType!!
            } else
            {
                T::class.java
            }
        )
    }


    fun <T> retrieveContext(type: Class<T>): ArgumentContext<T>
    {
        return (this.contexts[type] as ArgumentContext<T>?)!!
    }

    fun <T> retrieveAnyContext(type: Class<T>): ArgumentContext<*>
    {
        return (this.contexts[type] ?: this.contexts[String::class.java]!!)
    }
}