from bokeh.io import show, output_file
from bokeh.models import ColumnDataSource, FactorRange
from bokeh.plotting import figure, curdoc
from bokeh.transform import factor_cmap
from bokeh.palettes import Spectral6
import random
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

#electrodes = ['F1', 'F2', 'O1', 'O2']
electrodes = ["AF3","F7","F3","FC5","T7","P7","O1","O2","P8","T8","FC6","F4","F8","AF4"]
bands = ['Theta', 'Alpha', 'LowBeta', 'HighBeta', 'Gamma']


data = {'electrodes' : electrodes,
        'Theta'   : [random.random() for elec in electrodes],
        'Alpha'   : [random.random() for elec in electrodes],
        'LowBeta'    : [random.random() for elec in electrodes],
        'HighBeta'    : [random.random() for elec in electrodes],
        'Gamma'   : [random.random() for elec in electrodes]}

# this creates [ ("Apples", "2015"), ("Apples", "2016"), ("Apples", "2017"), ("Pears", "2015), ... ]
x = [ (electrode, band) for electrode in electrodes for band in bands ]
power = sum(zip(data['Theta'], data['Alpha'], data['LowBeta'], data['HighBeta'], data['Gamma']), ()) # like an hstack

print(x)
print(power)


source = ColumnDataSource(data=dict(x=x, power=power))

p = figure(x_range=FactorRange(*x), plot_height=250, title="Band Power by Electrode",
           tools="ywheel_zoom,reset", y_range=[0, 10],sizing_mode='scale_width')

thebar = p.vbar(x='x', top='power', width=0.9, source=source,
       fill_color=factor_cmap('x', palette=Spectral6, factors=bands, start=1, end=2))

p.y_range.start = 0
p.x_range.range_padding = 0.1
p.xaxis.major_label_orientation = 1
p.xgrid.grid_line_color = None

#show(p)


def update():
    #print('pulling sample...')
    sample, timestamp = inlet.pull_sample()
    #print(timestamp, sample)

    theta = sample[0:len(sample):5]
    alpha = sample[1:len(sample):5]
    lowbeta = sample[2:len(sample):5]
    highbeta = sample[3:len(sample):5]
    gamma = sample[4:len(sample):5]

    thebar.data_source.data["power"] = sum(zip(theta,alpha,lowbeta,highbeta,gamma), ())



# open a session to keep our local document in sync with server
#session = push_session(curdoc())
curdoc().add_root(p)
curdoc().add_periodic_callback(update, 10)  # period in ms