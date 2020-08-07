from __future__ import print_function
import math
import keras
from keras.datasets import mnist
from numpy import random as rnd
from keras.models import Sequential
from keras.layers import Dense, Dropout
from keras.optimizers import RMSprop
import tensorflow as tf
import matplotlib.pyplot as plt
import matplotlib.pyplot as pltim
from keras import backend as K
import zipfile as zf
import gc
import os, random
from PIL import Image
import glob
import numpy as np
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.utils import np_utils
import tensorflow as tf
import pandas as pd
import cv2
from keras.models import load_model
batch_size = 5

lst = []
cwd = os.getcwd();
imgPath = cwd+'/FingerImages/';
fpPath = cwd+'/FingerPrints/';
candidates = ['Abhishek','Gautam','Harshitha','Patanjali','Rahul','Rohit','SaiKrishna','Surbhi','SaiRahul','Vivek','Kashyap'];
fingers = ['LT','LI','LM','LR','LS','RT','RI','RM','RR','RS'];
#cases = ['gn','sp'];
cases = ['gn'];
for i in range (0,len(candidates)):
    level1 = candidates[i]+'/';
    for j in range (0,len(fingers)):
        level2 = level1+fingers[j]+'/';
        x = []; y = []; #as x and y are deleted, it needs to be redeclared each time
        for k in range (0,len(cases)):
            level3 = level2+cases[k]+'/';
            for filetypes in ('*.jpg','*.JPG'):
                for filename in glob.glob(imgPath+level3+filetypes): #assuming jpg
                    for fpName in os.listdir(fpPath+level2):
                        lst.append([filename, fpPath+level2+fpName])

df = pd.DataFrame(lst, columns=['X','Y'])
df.head()
df.shape

dfTrain = df[:(int)(len(df)*0.8)]
dfTest = df[(int)(len(df)*0.8):]

def generator(df, batch_size):
    while 1:
        sub = df.sample(batch_size)
        x_train = []
        for im in sub.X.values:
            img = pltim.imread(im)
            img = cv2.resize(img, (1000,1000))
            x_train.append(img);
        x_train = np.array(x_train)
       
        
        y_train = []
        for im in sub.Y.values:
            
            img = pltim.imread(im)
            img = cv2.resize(img, (0,0), fx=.5, fy=.5, interpolation=cv2.INTER_LANCZOS4)
            y_train.append(img);
        y_train = np.array(y_train)
        

        
        x_train = x_train.astype('float32')
        x_train = x_train/255

        #reshape y
        y_train = y_train.reshape(len(y_train), 62500)
        y_train = y_train.astype('float32')
        y_train = y_train/255
        
        
        yield x_train, y_train
        #restart counter to yeild data in the next epoch as well


def baseline_model():
	# create model
	model = Sequential()
	model.add(Conv2D(16, (3,3), input_shape=(1000,1000,3), activation='relu'))
	model.add(MaxPooling2D())
	model.add(Conv2D(32, (3,3)))
	model.add(MaxPooling2D())
	model.add(Conv2D(32, (3,3)))
	model.add(MaxPooling2D())
	model.add(Conv2D(16, (3,3)))
	model.add(MaxPooling2D())
	model.add(Flatten())
	model.add(Dense(units = 512, activation = 'relu'))
	model.add(Dense(62500, activation='softmax'))
	# Compile model
	model.compile(loss='RMS', optimizer='adam', metrics=['accuracy'])
	return model

gc.enable()
config = tf.ConfigProto(log_device_placement=True, allow_soft_placement=True)
config.gpu_options.allow_growth=True
config.gpu_options.per_process_gpu_memory_fraction=0.8
config.gpu_options.allocator_type = 'BFC'

with tf.device('/gpu:0'):
    session = tf.Session(config=config)
    K.set_session(session)

            
    #print(x_train.shape[0], 'train samples')
    #print(x_test.shape[0], 'test samples')
    #model = Sequential()
    #model.add(keras.layers.Conv2D(5, kernel_size=(3,3), padding='same', input_shape = input_shape))
    #model.add(keras.layers.MaxPool2D(pool_size=(3,3)))
    #model.add(keras.layers.Conv2D(5, kernel_size=(3,3), padding='same'))
    #model.add(keras.layers.MaxPool2D())
    #model.add(keras.layers.Conv2D(5, kernel_size=(3,3), padding='same'))
    #model.add(keras.layers.MaxPool2D())
    #model.add(keras.layers.Conv2D(5, kernel_size=(3,3), padding='same'))
    #model.add(keras.layers.MaxPool2D())
    #model.add(keras.layers.Flatten())
    #model.add(Dense(73728, activation='relu'))
    #model.add(Dense(20,activation='relu',input_shape=(3145728,)))
    #model.add(Dropout(0.2))
    #model.add(Dense(512, activation='relu'))
    #model.add(Dropout(0.2))
    #model.add(Dense(73728, activation='softmax'))
    
    # build the model
    model = baseline_model()
    model.summary()
    # Fit the model
    #model.fit(X_train, y_train, validation_data=(X_test, y_test), epochs=10, batch_size=200, verbose=2)
    model.fit_generator(generator(dfTrain, batch_size), epochs=10,
                        steps_per_epoch = len(dfTrain)/batch_size,
                        validation_data=generator(dfTest, batch_size),
                        validation_steps=len(dfTest)/batch_size);

    #model.fit_generator(generator(x_train, y_train, batch_size), samples_per_epoch=10, nb_epoch=10)

    

    #history = model.fit(x_train, y_train, batch_size=batch_size, epochs=epochs, verbose=1,
    #validation_data=(x_test, y_test))

    score = model.evaluate_generator(generator(dfTest, batch_size),steps= len(dfTest)/batch_size)
    print('Test loss:', score[0])
    print('Test accuracy:', score[1])

model.save('cnn_model_060718.h5')  # creates a HDF5 file 'my_model.h5'

keras.models.load_model('cnn_model_060718.h5')
img = pltim.imread('/home/csgautam92/FingerImages/Abhishek/LI/gn/IMG_5278.JPG')
img = cv2.resize(img, (1000,1000))
img=np.array(img)
img = img.astype('float32')
img = img/255
img = np.expand_dims(img, axis=0)
output = model.predict(img)
output = output*255
out_img=output.reshape(250,250)
print(out_img.shape)
pltim.imshow(out_img,cmap='gist_gray',interpolation='nearest')
plt.imsave('Output.jpg',out_img,cmap='gray')
