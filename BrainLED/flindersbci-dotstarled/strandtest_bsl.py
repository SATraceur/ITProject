#!/usr/bin/python

# Simple strand test for Adafruit Dot Star RGB LED strip.
# This is a basic diagnostic tool, NOT a graphics demo...helps confirm
# correct wiring and tests each pixel's ability to display red, green
# and blue and to forward data down the line.  By limiting the number
# and color of LEDs, it's reasonably safe to power a couple meters off
# USB.  DON'T try that with other code!

import time
from dotstar import Adafruit_DotStar

numpixels = 72 # Number of LEDs in strip

# Here's how to control the strip from any two GPIO pins:
# datapin  = 23
# clockpin = 24
# strip    = Adafruit_DotStar(numpixels, datapin, clockpin)
strip    = Adafruit_DotStar(numpixels,125000,order='bgr')
print 'strip object created'

# Alternate ways of declaring strip:
#  Adafruit_DotStar(npix, dat, clk, 1000000) # Bitbang @ ~1 MHz
#  Adafruit_DotStar(npix)                    # Use SPI (pins 10=MOSI, 11=SCLK)
#  Adafruit_DotStar(npix, 32000000)          # SPI @ ~32 MHz
#  Adafruit_DotStar()                        # SPI, No pixel buffer
#  Adafruit_DotStar(32000000)                # 32 MHz SPI, no pixel buf
# See image-pov.py for explanation of no-pixel-buffer use.
# Append "order='gbr'" to declaration for proper colors w/older DotStar strips)

strip.begin()           # Initialize pins for output
#strip.setBrightness(128) # Limit brightness to ~1/4 duty cycle
print 'begun and brightness turned down'

# Runs 10 LEDs at a time along strip, cycling through red, green and blue.
# This requires about 200 mA for all the 'on' pixels + 1 mA per 'off' pixel.

head  = 0               # Index of first 'on' pixel
tail  = -1             # Index of last 'off' pixel
startcolor = 0x123456   # First color
color = startcolor      # 'On' color (starts red)
shift = 1               # how far to shift

while True:                              # Loop forever

	strip.setPixelColor(head, color) # Turn on 'head' pixel
	strip.setPixelColor(tail, 0)     # Turn off 'tail'
	strip.show()                     # Refresh strip
#	time.sleep(1.0 / 5000)             # Pause 20 milliseconds (~50 fps)

	head += 1                        # Advance head position
	if(head >= numpixels):           # Off end of strip?
		head    = 0              # Reset to start
#		color >>= 8              # Red->green->blue->black

	tail += 1                        # Advance tail position
	if(tail >= numpixels): tail = 0  # Off end? Reset

	color = color >> shift | color << ( 24 - shift)
	if(color == 0): color = startcolor # If black, reset to red


