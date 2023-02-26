package sectasy.rocket

enum class RPacketType(val value: Int) {
    SERVER_RESP_SUCCESS(0),
    SERVER_RESP_FAILURE(2),

    SERVER_AUTH_FAILURE(3),

    CLIENT_REQUEST(5),
}

data class RPacket(var requestId: Int, val type: RPacketType?, val body: String) {

    fun toSocket(): ByteArray {
        return "$requestId;${type?.value};$body".toByteArray(Charsets.UTF_8)
    }

    fun debug(): String {
        return "RPacket<requestId=$requestId, type=$type, body='$body'>"
    }

    companion object {
        fun fromSocket(payload: String): RPacket {
            val splitted: List<String> = payload.split(';')
            val valueToEnum = RPacketType.values().associateBy { it.value }
            val type = valueToEnum[splitted[1].toInt()]
            return RPacket(splitted[0].toInt(), type!!, splitted[2])
        }
    }
}