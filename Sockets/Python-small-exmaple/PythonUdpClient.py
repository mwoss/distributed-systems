import socket

serverIP = "127.0.0.1"
serverPort = 9008
msg = "Python Client"
msg2 = "złota gęś"

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg2, 'utf-8'), (serverIP, serverPort))




