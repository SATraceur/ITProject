"""Example program to show how to read a multi-channel time series from LSL."""

from pylsl import StreamInlet, resolve_stream, StreamInfo

# first resolve an EEG stream on the lab network
print("looking for an FreqBand stream...")
streams = resolve_stream('type', 'FreqBand')

# create a new inlet to read from the stream
inlet = StreamInlet(streams[0])
info = inlet.info()
print(info.as_xml())

print("The channel labels are as follows:")
ch = info.desc().child("channels").child("channel")
for k in range(info.channel_count()):
    print("  " + ch.child_value("label"))
    ch = ch.next_sibling()

while True:
    # get a new sample (you can also omit the timestamp part if you're not
    # interested in it)
    print 'pulling sample...',
    sample, timestamp = inlet.pull_sample()
    print(timestamp, sample)
