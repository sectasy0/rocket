package sectasy.rocket

import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*


class RCommandSender : CommandSender {

    private val output = ByteArrayOutputStream()
    private val printStream = PrintStream(output)

    override fun sendMessage(message: String) {
        printStream.println(message)
    }

    override fun sendMessage(messages: Array<String>) {
        messages.forEach { sendMessage(it) }
    }

    override fun sendMessage(sender: UUID?, message: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(sender: UUID?, vararg messages: String) {
        TODO("Not yet implemented")
    }

    override fun isPermissionSet(name: String) = true

    override fun isPermissionSet(perm: Permission) = true

    override fun hasPermission(name: String) = true

    override fun hasPermission(perm: Permission) = true

    override fun addAttachment(plugin: Plugin) = PermissionAttachment(plugin, this)
    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        TODO("Not yet implemented")
    }

    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        TODO("Not yet implemented")
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean) = PermissionAttachment(plugin, this)

    override fun removeAttachment(attachment: PermissionAttachment) = Unit

    override fun recalculatePermissions() = Unit

    override fun getEffectivePermissions() = emptySet<PermissionAttachmentInfo>()

    override fun getServer() = TODO("Not yet implemented")

    override fun getName(): String = "RCommandSender"

    override fun spigot() = TODO("Not yet implemented")

    override fun name() = TODO("Not yet implemented")

    override fun isOp() = true

    override fun setOp(value: Boolean) = Unit

    val getOutput: String get() = output.toString()
}