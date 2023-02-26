package sectasy.rocket

import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import java.util.logging.Logger

class RClientHandler(
    private val client: Socket,
    private val crypto: RCrypto,
    private val logger: Logger
): Runnable {

    companion object {
        private const val BUFFER_SIZE = 1024
    }

    override fun run() {
        try {
            while (true) {
                val payload = ByteArray(BUFFER_SIZE)
                val length: Int = client.inputStream.read(payload)
                if (length == -1) break

                val actualPayload: ByteArray = payload.copyOfRange(0, length)
                val payloadString = String(actualPayload, Charsets.UTF_8).substringBefore("\n")

                try {
                    val decryptedPayload: String = crypto.decrypt(payloadString)
                    val packet = RPacket.fromSocket(decryptedPayload)

                    if (decryptedPayload.count { it == ';' } > 2) {
                        val response = RPacket(packet.requestId, RPacketType.SERVER_RESP_FAILURE, "malformed")
                        logger.info("[rocket] Client ${client.inetAddress.hostAddress} send malformed packet, rejecting")
                        this.sendResponse(response)
                        continue
                    }

                    handleValidPacket(packet)

                } catch (e: Exception) {
                    logger.info(e.toString())
                    val packet = RPacket(-1, RPacketType.SERVER_AUTH_FAILURE, "")

                    this.sendResponse(packet)

                    logger.info("[rocket] Client ${client.inetAddress.hostAddress} tried to access with invalid secred_key, access denied.")
                }
            }
        } catch (e: IOException) {
            logger.info("[rocket] Error occurred while handling request from client ${client.inetAddress.hostAddress}: $e")
        } finally {
            if (!client.isClosed) client.close()
        }
    }

    private fun handleValidPacket(packet: RPacket) {
        logger.info("[rocket] Received packet from ${client.inetAddress.hostAddress} | ${packet.debug()}")
        val result = RCommandExecutor.execute(packet.body)
        result.requestId = packet.requestId
        this.sendResponse(result)
    }

    private fun sendResponse(packet: RPacket) {
        val outputStream = DataOutputStream(client.getOutputStream())
        val encrypted: ByteArray = crypto.encrypt(packet)
        outputStream.write(encrypted)
    }
}