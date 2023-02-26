package sectasy.rocket

import org.bukkit.Bukkit.*
import org.bukkit.plugin.Plugin

object RCommandExecutor {

    private val notPermittedCommands: Array<String> = arrayOf("raload", "shutdown", "restart", "stop")
    private lateinit var plugin: Plugin

    fun execute(command: String): RPacket {
        try {
            if (!isValidCommand(command)) {
                return RPacket(0, RPacketType.SERVER_RESP_FAILURE, "invalid command")
            }

            plugin = getPluginManager().getPlugin("rocket") ?: return RPacket(0, RPacketType.SERVER_RESP_FAILURE, "unexpected")

            val commandSender = RCommandSender()
            var commandFailed = false

            getServer().scheduler.scheduleSyncDelayedTask(plugin) {
                try {
                    getServer().dispatchCommand(commandSender, command)
                } catch (e: Exception) {
                    commandFailed = true
                }
            }

            Thread.sleep(100L)

            return if (commandFailed)
                RPacket(0, RPacketType.SERVER_RESP_FAILURE, "incorrect command format")
            else
                RPacket(0, RPacketType.SERVER_RESP_SUCCESS, commandSender.getOutput)
        } catch (e: Exception) {
            return RPacket(0, RPacketType.SERVER_RESP_FAILURE, "unexpected")
        }
    }

    private fun isValidCommand(command: String): Boolean {
        return when {
            command.startsWith("/") -> false
            notPermittedCommands.any { command.startsWith(it) } -> false
            else -> true
        }
    }
}