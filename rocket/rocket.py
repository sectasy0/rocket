from __future__ import annotations
from typing import Union
from socket import socket, AF_INET, SOCK_STREAM
from random import randint
from cryptography.fernet import Fernet, InvalidToken

from .packet import RPacket


class RocketSecretFailure(Exception):
    pass


class Rocket:

    def __init__(self, host: str, port: int, secret: str) -> None:
        self.host: str = host
        self.port: int = port
        self.secret: str = secret

        self.fernet: Fernet = Fernet(self.secret)

        self.socket: Union[socket, None] = None

    def execute_command(self, command: str) -> RPacket:
        if self.socket is None:
            self._create_connection()

        request_id: int = randint(10000, 99999)
        packet: RPacket = RPacket(request_id, 5, command)

        self._send_packet(packet)
        received_packet: RPacket = self._recv_packet()
        return received_packet

    def _send_packet(self, packet: RPacket) -> None:
        payload: bytes = packet.to_socket()
        encrypted_payload: str = self.fernet.encrypt(payload).decode('utf-8')
        self.socket.send(f"{encrypted_payload}\n".encode('utf-8'))

    def _recv_packet(self) -> RPacket:
        try:
            payload: bytes = self.socket.recv(1024)
            payload: str = self.fernet.decrypt(payload).decode('utf-8')

            return RPacket.from_socket(payload)
        except InvalidToken:
            raise RocketSecretFailure('your secret is invalid') from None
        except ConnectionAbortedError:
            self.socket.close()
            self.socket = None

    def _create_connection(self):
        self.socket = socket(AF_INET, SOCK_STREAM)
        self.socket.settimeout(5)
        self.socket.connect((self.host, self.port))