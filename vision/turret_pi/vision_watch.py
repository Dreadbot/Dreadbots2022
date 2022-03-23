#!/usr/bin/env python3
import os
from time import sleep
import logging
from networktables import NetworkTables

target = 'main.py'

logging.basicConfig(level=logging.DEBUG)

ip = "10.36.56.2"

NetworkTables.initialize(server=ip)

def connection_listener(connected, info):
    print(info, "; connected=%s" % connected)

NetworkTables.addConnectionListener(connection_listener, immediateNotify=True)

table = NetworkTables.getTable('SmartDashboard')

while True:
    found_proc = None

    os.system("ps ax|grep python >> out.txt")
    with open('out.txt', 'r') as f:
        os_output = f.read().split("\n")

    for process in os_output:
        if target in process:
            found_proc = process

    if found_proc is None:
        table.putBoolean("TargetFoundInFrame", False)
    else:
        pass

    with open('out.txt', 'w+') as f:
        f.write('')

    sleep(0.1)
