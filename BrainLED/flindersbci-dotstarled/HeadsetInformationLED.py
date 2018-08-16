from pylsl import StreamInlet, resolve_stream, StreamInfo
import sys
import os
import platform
import time
import ctypes
from dotstar import Adafruit_DotStar
import atexit
from select import select
	
from ctypes import *
from fileinput import close




# -------------------------------------------------------------------------

def kbhit():
	''' Returns True if keyboard character was hit, False otherwise.
	'''
	dr,dw,de = select([sys.stdin], [], [], 0)
	return dr != []

# -------------------------------------------------------------------------

userID       = c_uint(0)
user         = pointer(userID)
ready        = 0
state        = c_int(0)
systemUpTime = c_float(0.0)

batteryLevel     = c_long(0)
batteryLevelP    = pointer(batteryLevel)
maxBatteryLevel  = c_int(0)
maxBatteryLevelP = pointer(maxBatteryLevel)

systemUpTime     = c_float(0.0)
wirelessStrength = c_int(0)

# first resolve an ContactQuality stream on the lab network
print("looking for a ContactQuality stream...")
streams = resolve_stream('type', 'ContactQuality')

# LED Strip Setup
numpixels = 72 # Number of LEDs in strip
strip    = Adafruit_DotStar(numpixels,125000,order='bgr')

print 'strip object created'

strip.begin()           # Initialize pins for output
strip.setBrightness(8) # Limit brightness to ~1/4 duty cycle

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
    print ",  " + ch.child_value("label"),
    ch = ch.next_sibling()
print " "

# flash for LSL connection
for ledi in range(0, numpixels):
       strip.setPixelColor(ledi,0x00FF00)
strip.show()
time.sleep(0.25)
for ledi in range(0, numpixels):
       strip.setPixelColor(ledi,0)
strip.show()

contact_color = [0xFF0000, 0xFFFF00, 0xBBFF00, 0x77FF00, 0x00FF00]

#elec2led = [-1 -1 -1 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14]
elec2led = [1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29]

print "Press any key to stop logging...\n"


while True:    
	
        if kbhit():
                break
        sample, timestamp = inlet.pull_sample()
        vali = 0
        for val in sample:
           strip.setPixelColor(elec2led[vali],contact_color[val])
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

