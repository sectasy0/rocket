from __future__ import annotations
from dataclasses import dataclass


class RPacketType:
    SERVER_RESP_SUCCESS: int = 0
    SERVER_RESP_FAILURE: int = 2

    SERVER_AUTH_FAILURE: int = 3

    CLIENT_REQUEST: int = 5


@dataclass
# Packet format
# [int: request_id];[int: p_type];[bytes: body]
class RPacket:
    request_id: int
    p_type: int
    body: str

    def to_socket(self) -> bytes:
        return f"{self.request_id};{self.p_type};{self.body}".encode('utf-8')

    @staticmethod
    def from_socket(payload: str) -> RPacket:
        chunks: list = [int(c) if c.isdigit() else c for c in payload.split(';')]
        return RPacket(*chunks)
