Kenisis Router
==============

This will route data `POST`ed to the `/data` endpoint to the specified AWS Kinesis queue

The default port is 8080

Running
-------

    java -Dratpack.kinesis.stream=yolo \
         -Dratpack.kinesis.region=us-west-1 \
         -jar build/libs/kinesis-router-all.jar

Checking that it is running

    http POST localhost:8080/health

Posting data
------------

    http POST localhost:8080/data < yourfile.dat

Filtering data
--------------

The following filter will send 1/4 requests if the time is between 8am and 8pm every day

    and:
      rate: 0.25
      datetime: '8:00:00,12h'

