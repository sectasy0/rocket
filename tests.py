import unittest
from unittest.mock import Mock, patch
from cryptography.fernet import Fernet
import mock

import rocket
from packet import RPacket, RPacketType
from rocket import Rocket


class RocketPacketTests(unittest.TestCase):

    def test_packet_from_socket(self):
        socket_data: str = "5234;5;say hello"
        packet: RPacket = RPacket.from_socket(socket_data)

        self.assertTrue(packet.request_id == 5234)
        self.assertTrue(packet.p_type == RPacketType.CLIENT_REQUEST)
        self.assertTrue(packet.body == "say hello")

    def test_packet_to_socket(self):
        packet: RPacket = RPacket(5543, RPacketType.CLIENT_REQUEST, "say hello_world")
        socket_data: bytes = packet.to_socket()

        self.assertTrue(socket_data == b"5543;5;say hello_world")


class RocketClientTests(unittest.TestCase):

    def setUp(self):
        self.rocket: Rocket = Rocket('localhost', 5543, "rUc_HJItDCwdkZ0esm8uKUh4yIc8GsYSTNFQleUcmI0=")
        self.rocket_second: Rocket = Rocket('localhost', 5543, "Yx7vlFkJ5Sl8BG4ANAk3ZrvLAkhq6S299JXwZB5MCAQ=")

    @patch('rocket.socket')
    def test_send_valid_command_not_encrypted(self, mock_socket):
        mock_connection = Mock()
        mock_socket.return_value = mock_connection

        mock_connection.recv.return_value = b"2134;0;"

        with self.assertRaises(rocket.RocketSecretFailure):
            result: RPacket = self.rocket.execute_command("say hello")

        mock_connection.connect.assert_called_once_with(('localhost', 5543))

    @patch('rocket.socket')
    def test_send_valid_command_encrypted(self, mock_socket):
        mock_connection = Mock()
        mock_socket.return_value = mock_connection

        encrypted_data: bytes = self.rocket.fernet.encrypt(b"2134;0;")
        mock_connection.recv.return_value = encrypted_data

        result: RPacket = self.rocket.execute_command("say hello")

        mock_connection.connect.assert_called_once_with(('localhost', 5543))

        self.assertTrue(isinstance(result, RPacket))

    @patch('rocket.socket')
    def test_send_valid_command_auth_failed(self, mock_socket):
        mock_connection = Mock()
        mock_socket.return_value = mock_connection

        encrypted_data: bytes = self.rocket_second.fernet.encrypt(b"2134;3;")
        mock_connection.recv.return_value = encrypted_data

        result: RPacket = self.rocket_second.execute_command("say hello")

        mock_connection.connect.assert_called_once_with(('localhost', 5543))

        self.assertTrue(isinstance(result, RPacket))
        self.assertTrue(result.p_type == RPacketType.SERVER_AUTH_FAILURE)

    @patch('rocket.socket')
    def test_send_valid_command_malformed(self, mock_socket):
        mock_connection = Mock()
        mock_socket.return_value = mock_connection

        encrypted_data: bytes = self.rocket_second.fernet.encrypt(b"2134;2;malformed")
        mock_connection.recv.return_value = encrypted_data

        result: RPacket = self.rocket_second.execute_command("say heello;world;everyone")

        mock_connection.connect.assert_called_once_with(('localhost', 5543))

        self.assertTrue(isinstance(result, RPacket))
        self.assertTrue(result.p_type == RPacketType.SERVER_RESP_FAILURE)
        self.assertTrue(result.body == 'malformed')

    @patch('rocket.socket')
    def test_send_valid_command_invalid_command(self, mock_socket):
        mock_connection = Mock()
        mock_socket.return_value = mock_connection

        encrypted_data: bytes = self.rocket_second.fernet.encrypt(b"2134;2;invalid command")
        mock_connection.recv.return_value = encrypted_data

        result: RPacket = self.rocket_second.execute_command("asdfasdgsgsdg sd gasdgasdgsagasdg")

        mock_connection.connect.assert_called_once_with(('localhost', 5543))

        self.assertTrue(isinstance(result, RPacket))
        self.assertTrue(result.p_type == RPacketType.SERVER_RESP_FAILURE)
        self.assertTrue(result.body == 'invalid command')