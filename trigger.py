import time
from pythonosc import udp_client

client = udp_client.SimpleUDPClient("127.0.0.1", 3030)

while True:
    client.send_message("/lx/channel/2/enabled", True)
    time.sleep(1)
    client.send_message("/lx/channel/2/enabled", False)
    time.sleep(1)
