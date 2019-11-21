# Pillars of the Guardians

This is the code for controlling an interactive LED art installation which was built for [Burning Seed](http://burningseed.com/) 2019.

For more technical details, [read this article](https://www.jonathanleighton.com/articles/2019/building-pillars-of-the-guardians/).

## Lights

Lighting is controlled by [LX Studio](https://lx.studio/). The code is in the `lights/` directory.

### Running the UI (requires Processing)

This is used for development. It will compile the code automatically.

    $ bin/lights-ui

Load the `lights/test.lxp` project file to test the lights/wiring.

Load the `lights/static.lxp` project file to run a static light show with no interactivity.

### Running headless

This is used for running on the Pi without a graphical environment.

You have to compile the code separately:

    $ ant

Then run it:

    $ bin/lights-headless

### Controlling the lights

The LX Studio project is set up in a very specific way (see `lights/Project.java`). To produce various changes to the light show, there is a Python script at `bin/lights-control`. It sends [OSC](https://en.wikipedia.org/wiki/Open_Sound_Control) messages to LX.

To see usage information:

    $ bin/lights-control --help

This script also plays sound files which are located in `sounds/`. They are no checked into the git repository, so you need to source them separately.

## Sensors

Magnetic sensors are monitored via the Raspberry Pi’s [GPIO](https://www.raspberrypi.org/documentation/usage/gpio/). The code is in the `sensors/` directory and is written in Ruby.

### Testing sensors

This can only be done on the Pi:

    $ bin/sensors-test

It will continuously print out which of the sensors are currently firing.

### Running the sensor monitor

This can only be done on the Pi:

    $ bin/sensors

Logging information is printed to stdout.

### Developing the state logic

You can run the tests for the state management code off-Pi:

    $ ruby sensors/pillar_circle_test.rb

The code in `sensors/lights_listener.rb` is where the sensor code talks to the `bin/lights-control` script, which then talks to LX.

## Uploading to the Pi

When changes have been made, the Pi can be updated like so:

    $ bin/upload [PI_IP_ADDRESS]

## Configuring the Pi

There are various config files in `config/`. To install:

1. Copy them to the Pi with `bin/upload`
2. Run `bin/configure-pi`

This installs several systemd services. To enable and start them:

    $ sudo systemctl enable lights.service sensors.service pulseaudio.service
    $ sudo systemctl start lights.service sensors.service pulseaudio.service

To see logs:

    $ sudo journalctl -f -u lights.service -u sensors.service

The Pi config in this repository is not currently exhaustive. There are some things that were done manually, such as installing dependencies.
