package sectasy.rocket

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin


class Rocket : JavaPlugin() {

    private var config: FileConfiguration = getConfig()

    override fun onEnable() {
        config.addDefault("port", 5543)
        config.addDefault("secret_key", "")
        config.addDefault("thread_pool_size", 5)
        config.options().copyDefaults(true)
        saveConfig()

        if (config.getString("secret_key").isNullOrEmpty()) {
            logger.severe("secret key must be present")
            server.pluginManager.disablePlugin(this)
            return
        }

        if (!config.isInt("port")) {
            logger.warning("port must be present and valid int.. using default")
            config.set("port", 5543)
            saveConfig()
        }

        if (!config.isInt("thread_pool_size")) {
            logger.warning("thread_pool_size must be present and valid int.. using default")
            config.set("thread_pool_size", 5)
            saveConfig()
        }

        val propertiesAddress: String = Bukkit.getServer().ip
        if (propertiesAddress.isEmpty()) {
            logger.warning("server.ip section in server.properties file is empty, please fill this parameter")
        }

        val server = RServer(propertiesAddress, config)
        server.listen()
    }

    override fun onDisable() { }
}