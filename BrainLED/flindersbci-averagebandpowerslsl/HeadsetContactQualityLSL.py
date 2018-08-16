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

IEE_EmoEngineEventCreate = libEDK.IEE_EmoEngineEventCreate
IEE_EmoEngineEventCreate.restype = c_void_p
eEvent = IEE_EmoEngineEventCreate()

IS_GetTimeFromStart = libEDK.IS_GetTimeFromStart
IS_GetTimeFromStart.argtypes = [ctypes.c_void_p]
IS_GetTimeFromStart.restype = c_float

IS_GetWirelessSignalStatus = libEDK.IS_GetWirelessSignalStatus
IS_GetWirelessSignalStatus.restype = c_int
IS_GetWirelessSignalStatus.argtypes = [c_void_p]

IS_GetContactQuality = libEDK.IS_GetContactQuality
IS_GetContactQuality.restype = c_int
IS_GetContactQuality.argtypes = [c_void_p, c_int]

IEE_EmoEngineEventGetEmoState = libEDK.IEE_EmoEngineEventGetEmoState
IEE_EmoEngineEventGetEmoState.argtypes = [c_void_p, c_void_p]
IEE_EmoEngineEventGetEmoState.restype = c_int

IEE_EmoStateCreate = libEDK.IEE_EmoStateCreate
IEE_EmoStateCreate.restype = c_void_p
eState = IEE_EmoStateCreate()

print("Creating LSL outelt...")
info = StreamInfo('EPOCH', 'ContactQuality', 14, 2, 'int32', 'uniqueid')

# append some meta-data
channelList = array('I', [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16])
channelListLabel = ["AF3", "F7", "F3", "FC5", "T7", "P7", "O1", "O2", "P8", "T8", "FC6", "F4", "F8", "AF4"]
info.desc().append_child_value("manufacturer", "Emotiv")
channels = info.desc().append_child("channels")
for c in channelListLabel:
    channels.append_child("channel") \
        .append_child_value("label", c) \
        .append_child_value("unit", "unknown") \
        .append_child_value("type", "contactquality")

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
systemUpTime = c_float(0.0)

batteryLevel = c_long(0)
batteryLevelP = pointer(batteryLevel)
maxBatteryLevel = c_int(0)
maxBatteryLevelP = pointer(maxBatteryLevel)

systemUpTime = c_float(0.0)
wirelessStrength = c_int(0)

# -------------------------------------------------------------------------
# print "==================================================================="
# print "Example to get the average band power for a specific channel from" \
# " the latest epoch."
# print "==================================================================="

# -------------------------------------------------------------------------
#libEDK.IEE_EngineConnect(create_string_buffer(b"Emotiv Systems-5"))
#if libEDK.IEE_EngineConnect("Emotiv Systems-5") != 0:
if libEDK.IEE_EngineConnect(create_string_buffer(b"Emotiv Systems-5")) != 0:
    print("Emotiv Engine start up failed.")
    exit();

print("systemuptime")
for chl in channelListLabel:
    print(", " + chl)

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
            print("User added")

        if ready == 1 and eventType == 64:
            libEDK.IEE_EmoEngineEventGetEmoState(eEvent, eState);
            systemUpTime = IS_GetTimeFromStart(eState);

            wirelessStrength = libEDK.IS_GetWirelessSignalStatus(eState);

            if wirelessStrength > 0:

                libEDK.IS_GetBatteryChargeLevel(eState, batteryLevelP, maxBatteryLevelP);

                print(systemUpTime)
                print(", ", wirelessStrength)
                print(", ", batteryLevel.value)

                lsldata = []
                for i in channelList:
                    result = IS_GetContactQuality(eState, i)
                    lsldata.append(result)
                    print("," + str(result))
                print("")
                outlet.push_sample(lsldata)
    elif state != 0x0600:
        print("Internal error in Emotiv Engine ! ")
    time.sleep(0.1)

# -------------------------------------------------------------------------
libEDK.IEE_EngineDisconnect()
libEDK.IEE_EmoStateFree(eState)
libEDK.IEE_EmoEngineEventFree(eEvent)
