import logging
import datetime
import os
import time

def getLogger():
  logger = logging.getLogger('vision3656')
  logdir = os.path.join('home', 'pi', 'logs')
  if not os.path.exists(logdir):
    os.makedirs(logdir)
  hdlr = logging.FileHandler(os.path.join(logdir, f'{datetime.datetime.now().isoformat()}'))
  formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')

  hdlr.setFormatter(formatter)
  logger.addHandler(hdlr)
  logger.setLevel(10) # DEBUG level

  return logger
