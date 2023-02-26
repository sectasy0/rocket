package sectasy.rocket

import org.bukkit.Bukkit
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger
import com.macasaet.fernet.Key
import org.bukkit.configuration.file.FileConfiguration

class RServer(propertiesAddress: String, config: FileConfiguration) {

    private val logger: Logger = Bukkit.getLogger()

    private val host: InetAddress = InetAddress.getByName(propertiesAddress)
    private val port: Int = config.getInt("port")

    private lateinit var serverSocket: ServerSocket
    private lateinit var secretKey: Key
    private lateinit var crypto: RCrypto

    private val executorService: ExecutorService = Executors.newFixedThreadPool(
                                                    config.getInt("thread_pool_size", 5))

    init {
        try {
            serverSocket = ServerSocket(port, 5, host)
            secretKey = Key(config.getString("secret_key"))
            crypto = RCrypto(secretKey)
        } catch (e: Exception) {
            logger.severe(e.toString())
        }
    }

    fun listen() {
        executorService.submit(listenThread())
    }

    private fun listenThread(): Runnable {
        return Runnable {
            logger.info("[rocket] Listener started on $host:$port")
            try {
                while (true) {
                    val client = serverSocket.accept()
                    executorService.submit(RClientHandler(client, crypto, logger))
                }
            } catch (e: Exception) {
                logger.fine("[rocket] An error occurred while accepting client connection: $e")
            }
        }
    }

}
