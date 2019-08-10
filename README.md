# Pillars of the Guardians

## Running the UI (requires Processing)

This will compile the code when it runs.

    $ bin/potg-ui

## Running headless

You have to compile the code separately:

    $ ant

Then run it:

    $ bin/potg-headless

## Triggering a channel via OSC/Python

    import time
    from pythonosc import udp_client

    client = udp_client.SimpleUDPClient("127.0.0.1", 3030)

    while True:
        client.send_message("/lx/channel/1/enabled", True)
        time.sleep(1)
        client.send_message("/lx/channel/1/enabled", False)
        time.sleep(1)
