from pylsl import StreamInfo, StreamOutlet
import time
import random
from array import *

def getRandomPowerData():
    return random.uniform(0, 1000) #Change random range here
    


print("Creating LSL outelt...", end='')
info = StreamInfo('EPOCH', 'FreqBand', 14*5, 2, 'float32', 'uniqueid')

channelList = array('I', [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16])
channelListLabel = ["AF3", "F7", "F3", "FC5", "T7", "P7", "O1", "O2", "P8", "T8", "FC6", "F4", "F8", "AF4"]
info.desc().append_child_value("manufacturer", "Emotiv")
channels = info.desc().append_child("channels")
for c in channelListLabel:
    channels.append_child("channel") \
        .append_child_value("label", c + "_theta") \
        .append_child_value("unit", "uV^2/Hz") \
        .append_child_value("type", "frequency")
    channels.append_child("channel") \
        .append_child_value("label", c + "_alpha") \
        .append_child_value("unit", "uV^2/Hz") \
        .append_child_value("type", "frequency")
    channels.append_child("channel") \
        .append_child_value("label", c + "_lowbeta") \
        .append_child_value("unit", "uV^2/Hz") \
        .append_child_value("type", "frequency")
    channels.append_child("channel") \
        .append_child_value("label", c + "_highbeta") \
        .append_child_value("unit", "uV^2/Hz") \
        .append_child_value("type", "frequency")
    channels.append_child("channel") \
        .append_child_value("label", c + "_gamma") \
        .append_child_value("unit", "uV^2/Hz") \
        .append_child_value("type", "frequency")

# next make an outlet
outlet = StreamOutlet(info)

print("done!")

while (1):
    lsldata = []
    for i in channelList:
        power_data = [1, getRandomPowerData(), getRandomPowerData(), getRandomPowerData(), getRandomPowerData()] #pass random values here
        #print(power_data)
        lsldata.extend(power_data)
    outlet.push_sample(lsldata)
    time.sleep(0.1)
