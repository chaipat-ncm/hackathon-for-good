FROM debian:stretch

# install debian packages
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update -qq \
 && apt-get install --no-install-recommends -y \
    # install essentials
    build-essential \
    wget \
    g++ \
    git \
    openssh-client \
    # install python 3
    python3 \
    python3-dev \
    python3-pip \
    python3-setuptools \
    python3-virtualenv \
    python3-wheel \
    pkg-config \
    # requirements for numpy
    libopenblas-base \
    python3-numpy \
    python3-scipy \
    # requirements for keras
    python3-h5py \
    python3-yaml \
    python3-pydot \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*


# manually update numpy
RUN pip3 --no-cache-dir install -U numpy==1.13.3

ARG TENSORFLOW_VERSION=1.12.0
ARG TENSORFLOW_DEVICE=cpu
ARG TENSORFLOW_APPEND=
RUN pip3 --no-cache-dir install https://storage.googleapis.com/tensorflow/linux/${TENSORFLOW_DEVICE}/tensorflow${TENSORFLOW_APPEND}-${TENSORFLOW_VERSION}-cp35-cp35m-linux_x86_64.whl

ARG KERAS_VERSION=2.2.4
ENV KERAS_BACKEND=tensorflow
RUN pip3 --no-cache-dir install --no-dependencies git+https://github.com/fchollet/keras.git@${KERAS_VERSION}

# install dependencies from debian packages
RUN apt-get update -qq \
 && apt-get install --no-install-recommends -y \
    python-matplotlib \
    python-pillow \
    tesseract-ocr

# install dependencies from python packages
RUN pip3 --no-cache-dir install \
    pandas \
    scikit-learn \
    statsmodels \
    scipy \
    pytesseract \
    nltk \
    np_utils \
    pandas \
    flask

RUN mkdir -p /tmp/test_script/images/nato_bad_propaganda

ARG PA_BASE_PATH=/usr/local/propaganda_assessment
ARG PA_MODEL_PATH=${BASE_PATH}/models

# install your app

ENV PA_BASE_PATH=${PA_BASE_PATH}
ENV PA_TEMPLATE_PATH=${PA_BASE_PATH}/templates
ENV PROPAGANDA_CNN_MODEL=${PA_MODEL_PATH}/propaganda_cnn_model.json
ENV PROPAGANDA_CNN_MODEL_WEIGHTS=${PA_MODEL_PATH}/propaganda_cnn_model_weights.h5
ENV PROPAGANDA_NLP_MODEL=${PA_MODEL_PATH}/propaganda_nlp_model.csv

ADD images/ ${PA_BASE_PATH}/images
ADD models/ ${PA_MODEL_PATH}
ADD *.py ${PA_BASE_PATH}/
ADD templates/ ${PA_TEMPLATE_PATH}

RUN chmod +x ${PA_BASE_PATH}/*.py
RUN chmod 777 ${PA_TEMPLATE_PATH}/*

WORKDIR ${PA_BASE_PATH}

EXPOSE 8080

# default command
CMD python3 app.py

