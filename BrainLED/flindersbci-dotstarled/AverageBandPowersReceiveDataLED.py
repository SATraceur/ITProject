from pylsl import StreamInlet, resolve_stream, StreamInfo
import time
from dotstar import Adafruit_DotStar
from select import select
import sys
import numpy as np


# -------------------------------------------------------------------------

def kbhit():
	''' Returns True if keyboard character was hit, False otherwise.
	'''
	dr,dw,de = select([sys.stdin], [], [], 0)
	return dr != []

# -------------------------------------------------------------------------

numpixels = 72 # Number of LEDs in strip

# first resolve an EEG stream on the lab network
print("looking for an FreqBand stream...")
streams = resolve_stream('type', 'FreqBand')

strip    = Adafruit_DotStar(numpixels,125000,order='bgr')
print 'strip object created'

strip.begin()           # Initialize pins for output
strip.setBrightness(128) # Limit brightness to ~1/4 duty cycle
print 'begun and brightness turned down'

# Runs 10 LEDs at a time along strip, cycling through red, green and blue.
# This requires about 200 mA for all the 'on' pixels + 1 mA per 'off' pixel.

head  = 0               # Index of first 'on' pixel
tail  = -1             # Index of last 'off' pixel
startcolor = 0x123456   # First color
color = startcolor      # 'On' color (starts red)
shift = 1               # how far to shift

# reset strip
for ledi in range(0, numpixels):
       strip.setPixelColor(ledi,0x00FF00)
       strip.show()
       
       strip.setPixelColor(ledi,0)
       strip.show()

# create a new inlet to read from the stream
inlet = StreamInlet(streams[0])
info = inlet.info()
print(info.as_xml())

print("The channel labels are as follows:")
ch = info.desc().child("channels").child("channel")
for k in range(info.channel_count()):
    print("  " + ch.child_value("label"))
    ch = ch.next_sibling()

# flash for LSL connection
for ledi in range(0, numpixels):
       strip.setPixelColor(ledi,0x00FF00)
strip.show()
time.sleep(0.25)
for ledi in range(0, numpixels):
       strip.setPixelColor(ledi,0)
strip.show()

#elec2led = [1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29]
#elec2led = []
#for ei in range(0,14):
#        elec2led.append(ei*4)
#elec2led = [ei*4 for ei in range(0,14)]

elec2led = np.linspace(0,14,14,dtype=int)*4
print(elec2led)

while True:

    if kbhit():
        break
    
    # get a new sample (you can also omit the timestamp part if you're not
    # interested in it)
    #print 'pulling sample...',
    sample, timestamp = inlet.pull_sample()
    #print(timestamp, sample)

    vali = 0
    for val in sample:
        #color = int(val/25 * 255)
        if val < 1:
                color = 0x0000FF
        elif val < 2:
                color = 0x00FFFF
        elif val < 3:
                color = 0x00FF00
        elif val < 4:
                color = 0xFFFF00
        else:
                color = 0xFF0000
        
        strip.setPixelColor(elec2led[vali], color)
        vali = vali + 1
        
    strip.show()
# -------------------------------------------------------------------------
# turn off LEDs
# reset strip
for ledi in range(0,numpixels):
       strip.setPixelColor(numpixels-ledi-1,0x0000FF)
       strip.show()
       
       strip.setPixelColor(numpixels-ledi-1,0)
       strip.show()   
