# Pillars of the Guardians

## Running the UI (requires Processing)

This will compile the code when it runs.

    $ bin/lights-ui

## Running headless

You have to compile the code separately:

    $ ant

Then run it:

    $ bin/lights-headless

## Controlling the show

External control is done via OSC. There is a Python wrapper script which sends the correct messages to accomplish various tasks:

### Activate/deactivate a pillar

`PILLAR` is a number from 1 to 10. If omitted, all pillars will be triggered.

    $ bin/command {activate,deactivate} PILLAR

### Turn the head on or off

When a pillar is dormant, its altar head is generally lit. When the head is lifted, it should no longer be lit.

Conversely, when a pillar is active, its pillar head is generally lit. When the head is lifted, it should no longer be lit.

To turn a head on:

    $ bin/command head PILLAR on

To turn a head off:

    $ bin/command head PILLAR off

## Configuring the Pi

There are various config files in `config/`. To install:

1. Copy them to the Pi with `bin/upload`
2. Run `bin/install-configs`

This installs several systemd services. To enable and start them:

    $ sudo systemctl enable lights.service sensors.service pulseaudio.service
    $ sudo systemctl start lights.service sensors.service pulseaudio.service

To see logs:

    $ sudo journalctl -f -u lights.service -u sensors.service
