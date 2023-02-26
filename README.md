# rocket protocol
> rocket it's the safer and text-based twin brother of RCON protocol targeted mainly for executing commands on game servers

## What is the Rocket protocol?
Rocket protocol works on the same principle as RCON, that is, it allows remote execution of commands on the server. The communication between the client and the server is text-based, using the TCP protocol. Unlike RCON, the Rocket protocol encrypts all communication between the client and the server using an encryption key that is generated when this protocol is configured on the server (the client version must encrypt its packets with the same key as the server version).

## Supported games servers
Further implementations of the protocol of various games will be available on other branches, such as implementation/minecraft
- Implementation for minecraft servers on branch `server/minecraft`

> **_NOTE:_**  main branch contains only client implementation written in python, for other implementation see branches.

## Client installation
```sh
pip3 install rocket-client
```

## What does the Rocket packet look like?
A Rocket packet has three parts, which are:

```sh
<request_id>;<p_type>;<body>
```

- `request_id` is a unique identifier for the request, and it can be used to identify the response.
- `packet_type` is the type of the packet.
- `body` is the actual content of the packet.

It is important to note that the server and the client should validate the count of `;` in payloads, and there should be a maximum of two.

Here are the types of packets supported by Rocket Protocol:
```py
class RPacketType:
    SERVER_RESP_SUCCESS: int = 0
    SERVER_RESP_FAILURE: int = 2

    SERVER_AUTH_FAILURE: int = 3

    CLIENT_REQUEST: int = 5
```

## How to use rocket python client
Below is a simple example of how to send commands to the server.
```py
from rocket import Rocket
from rocket.packet import RPacketType

if __name__ == "__main__":
    rocket: Rocket = Rocket('localhost', 5543, "rUc_HJItDCwdkZ0esm8uKUh4yIc8GsYSTNFQleUcmI0=")

    response: RPacket = rocket.execute_command("say hello")
    if response.p_type == RPacketType.SERVER_RESP_SUCCESS:
        print("SERVER SUCCESS RESPONSE")

    command: str = "give @a potion{Potion:\"minecraft:night_vision\"}"
    response: RPacket = rocket.execute_command(command)
    print(response)
```

> **_NOTE:_**  The key that encrypts the connection is not a random string, to generate the key you need to use the command::
```sh
python -c "\n\nimport base64, os\nprint(base64.urlsafe_b64encode(os.urandom(32)).decode('utf-8'))"
```

## Advantages of using the Rocket protocol
The Rocket protocol offers several advantages over other remote execution protocols like RCON:

1. __Enhanced Security__: The Rocket protocol encrypts all communication between the client and the server, making it more secure than RCON, which sends all data in plain text. The encryption key is generated when the protocol is configured on the server, and the client must use the same key to encrypt its packets. This ensures that only authorized clients can communicate with the server.

2. __Text-based Communication__: Rocket protocol uses text-based communication, which makes it easier to read and debug than binary protocols. Text-based communication also means that the protocol can be used with any programming language that supports TCP sockets.

3. __Multiple Language Compatibility__: The Rocket protocol is compatible with multiple programming languages, making it easy to use with different types of clients and servers. This means that developers can use their preferred programming language to create clients that can communicate with Rocket-enabled servers, and vice versa.

4. __Flexible Packet Structure__: The packet structure of the Rocket protocol is simple and flexible. It consists of three parts, and the packet type can be easily extended to support new features. This makes it easy to add new functionality to the protocol without breaking compatibility with existing clients and servers.

Overall, the Rocket protocol offers a secure, flexible, and easy-to-use alternative to other remote execution protocols like RCON.



