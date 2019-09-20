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
