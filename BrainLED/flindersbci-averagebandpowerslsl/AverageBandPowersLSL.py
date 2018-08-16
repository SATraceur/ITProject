import sys
import os
import platform
import time
import ctypes

from array import *
from ctypes import *
#from __builtin__ import exit

# LSL imports
from pylsl import StreamInfo, StreamOutlet

if sys.platform.startswith('win32'):
    import msvcrt
elif sys.platform.startswith('linux'):
    import atexit
    from select import select

from ctypes import *

try:
    if sys.platform.startswith('win32'):
        libEDK = cdll.LoadLibrary("../../bin/win32/edk.dll")
    elif sys.platform.startswith('linux'):
        srcDir = os.getcwd()
        if platform.machine().startswith('arm'):
            libPath = srcDir + "/../bin/armhf/libedk.so"
        else:
            libPath = srcDir + "/../../bin/linux64/libedk.so"
        libEDK = CDLL(libPath)
    else:
        raise Exception('System not supported.')
except Exception as e:
    print('Error: cannot load EDK lib:', e)
    exit()

print("away we go!")


IEE_EmoEngineEventCreate = libEDK.IEE_EmoEngineEventCreate
IEE_EmoEngineEventCreate.restype = c_void_p
eEvent = IEE_EmoEngineEventCreate()

IS_GetTimeFromStart = libEDK.IS_GetTimeFromStart
IS_GetTimeFromStart.argtypes = [ctypes.c_void_p]
IS_GetTimeFromStart.restype = c_float

IEE_EmoEngineEventGetEmoState = libEDK.IEE_EmoEngineEventGetEmoState
IEE_EmoEngineEventGetEmoState.argtypes = [c_void_p, c_void_p]
IEE_EmoEngineEventGetEmoState.restype = c_int

IEE_EmoStateCreate = libEDK.IEE_EmoStateCreate
IEE_EmoStateCreate.restype = c_void_p
eState = IEE_EmoStateCreate()

print("Creating LSL outelt...", end='')
info = StreamInfo('EPOCH', 'FreqBand', 14*5, 2, 'float32', 'uniqueid')

# append some meta-data
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


# -------------------------------------------------------------------------

def kbhit():
    ''' Returns True if keyboard character was hit, False otherwise.
    '''
    if sys.platform.startswith('win32'):
        return msvcrt.kbhit()
    else:
        dr, dw, de = select([sys.stdin], [], [], 0)
        return dr != []


# -------------------------------------------------------------------------


userID = c_uint(0)
user = pointer(userID)
ready = 0
state = c_int(0)

alphaValue = c_double(0)
low_betaValue = c_double(0)
high_betaValue = c_double(0)
gammaValue = c_double(0)
thetaValue = c_double(0)

alpha = pointer(alphaValue)
low_beta = pointer(low_betaValue)
high_beta = pointer(high_betaValue)
gamma = pointer(gammaValue)
theta = pointer(thetaValue)

# -------------------------------------------------------------------------
# print "==================================================================="
# print "Example to get the average band power for a specific channel from" \
# " the latest epoch."
# print "==================================================================="

# -------------------------------------------------------------------------
if libEDK.IEE_EngineConnect(create_string_buffer(b"Emotiv Systems-5")) != 0:
    print("Emotiv Engine start up failed.")
    exit()

# print "Theta, Alpha, Low_beta, High_beta, Gamma \n"
print ("systemuptime, AF3, AF4, O1, O2")
while (1):

    if kbhit():
        break

    state = libEDK.IEE_EngineGetNextEvent(eEvent)
    # print "after next event %d" % (state)

    if state == 0:
        eventType = libEDK.IEE_EmoEngineEventGetType(eEvent)
        libEDK.IEE_EmoEngineEventGetUserId(eEvent, user)
        if eventType == 16:  # libEDK.IEE_Event_enum.IEE_UserAdded
            ready = 1
            libEDK.IEE_FFTSetWindowingType(userID, 1);  # 1: libEDK.IEE_WindowingTypes_enum.IEE_HAMMING
            # print "User added"

        if ready == 1 and eventType == 64:
            libEDK.IEE_EmoEngineEventGetEmoState(eEvent, eState);
            systemUpTime = IS_GetTimeFromStart(eState);
            #print (systemUpTime, ",", end='')
            lsldata = []
            for i in channelList:
                result = c_int(0)
                result = libEDK.IEE_GetAverageBandPowers(userID, i, theta, alpha, low_beta, high_beta, gamma)

                if result == 0:  # EDK_OK
                    #lsldata.append(alphaValue.value)
                    power_data = [thetaValue.value, alphaValue.value,
                                  low_betaValue.value, high_betaValue.value, gammaValue.value]
                    lsldata.extend(power_data)
                    #print ("%.2f" % (alphaValue.value), ",", end='')
                    # print "%.6f, %.6f, %.6f, %.6f, %.6f \n" % (thetaValue.value, alphaValue.value,
                    #                                           low_betaValue.value, high_betaValue.value, gammaValue.value)
                else:
                    #lsldata.append(-1.0)
                    lsldata.extend([0.0, 0.0, 0.0, 0.0, 0.0])
                    #print("XXX", ",", end='')
            #print(" ")
            #print(lsldata)
            outlet.push_sample(lsldata)
    elif state != 0x0600:

        print("Internal error in Emotiv Engine ! ")
    time.sleep(0.1)

# -------------------------------------------------------------------------
libEDK.IEE_EngineDisconnect()
libEDK.IEE_EmoStateFree(eState)
libEDK.IEE_EmoEngineEventFree(eEvent)
