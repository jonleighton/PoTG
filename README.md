# Pillars of the Guardians

## Running the UI (requires Processing)

This will compile the code when it runs.

    $ bin/ui

## Running headless

You have to compile the code separately:

    $ ant

Then run it:

    $ bin/headless

## Controlling the show

External control is done via OSC. There is a Python wrapper script which sends the correct messages to accomplish various tasks:

### Activate/deactivate a pillar

`pillar` is a number from 1 to 10. If omitted, all pillars will be triggered.

    $ bin/command {activate,deactivate} pillar
