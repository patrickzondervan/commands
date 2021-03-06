package io.github.devrawr.commands

import io.github.devrawr.commands.command.WrappedCommand
import io.github.devrawr.commands.processor.CommandProcessor
import io.github.devrawr.commands.processor.executor.ExecutorProcessor
import io.github.devrawr.commands.processor.help.HelpProcessor
import io.github.devrawr.commands.processor.tab.TabCompletionProcessor
import io.github.devrawr.commands.processor.tab.defaults.DefaultTabCompletionProcessor
import io.github.devrawr.commands.util.ObjectInstanceUtil.getOrCreateInstance

abstract class CommandPlatform
{
    abstract val executorProcessor: ExecutorProcessor<*>
    abstract val helpProcessor: HelpProcessor

    open val processor = CommandProcessor()
    open var tabCompletionProcessor: TabCompletionProcessor = DefaultTabCompletionProcessor

    val commands = mutableListOf<WrappedCommand>()

    open fun registerCommand(command: WrappedCommand)
    {
        this.commands.add(command)
    }

    fun registerCommand(command: Any)
    {
        this.wrapCommand(command).forEach {
            this.registerCommand(it)
        }
    }

    inline fun <reified T> registerCommand()
    {
        this.registerCommand(
            T::class.getOrCreateInstance()
        )
    }

    fun wrapCommand(command: Any): List<WrappedCommand>
    {
        return Platforms.wrapCommand(command)
    }
}