from xdf import *
import time
from pylsl import StreamInfo, StreamOutlet
from collections import OrderedDict, defaultdict
from array import *

xdfStreamObj = load_xdf('../firstRecording.xdf', verbose=False)
nope = xdfStreamObj[0][0]['info']
nopeDesc = nope['desc']
#not useful for arburtuary .xdf transmission.         xdfnopeChannels = nope['desc']['channels']
#nope = xdfStreamObj[0][0]['time_series'][0] #will read the first set of data from all channels
#nope = xdfStreamObj[0][0]['time_stamps'][0] #will read the first set of data from all channels


#to switch out
#file name
#uniqueID replication
#    change 'uniqueid to recorded uid'


print (nope['name'][0])
print (nope['type'][0])
print (nope['channel_count'][0])
print (float(nope['nominal_srate'][0]))
print (nope['channel_format'][0])


info = StreamInfo('EPOCH', 'FreqBand', 14*5, 2, 'float32', 'uniqueid')
##info = StreamInfo(nope['name'][0], nope['type'][0], nope['channel_count'][0], float(nope['nominal_srate'][0]), nope['channel_format'][0], 'uniqueid')

######################################################################
## I really didn't want this to be schema specific...               ##
## I guess it works. Doesn't care about rate transmission though :( ##
######################################################################

print("Creating LSL outelt...", end='')


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

outlet = StreamOutlet(info)

#DataSource = xdfStreamObj[0][0]['time_series'][0]

print ('@@@')
print (len(xdfStreamObj[0][0]['time_series']))

while (1):
    for i in range(0,(len(xdfStreamObj[0][0]['time_series']))):
        lsldata = []
        DataSource = xdfStreamObj[0][0]['time_series'][i]
        for j in channelList:
            power_data = [DataSource[0+((j-3)*5)], DataSource[1+((j-3)*5)], DataSource[2+((j-3)*5)], DataSource[3+((j-3)*5)], DataSource[4+((j-3)*5)]]
            #power_data = [1,1,1,1,1]
            #print (power_data)
            lsldata.extend(power_data)
        outlet.push_sample(lsldata)
        time.sleep(0.1)
