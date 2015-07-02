Kenisis Router
--------------

This will route data `POST`ed to the `/data` endpoint to the specified AWS Kinesis queue

The default port is 8080

Running

    java -Dratpack.kinesis.stream=yolo \
         -Dratpack.kinesis.region=us-west-1 \
         -jar build/libs/kinesis-router-all.jar

Checking that it is running

    http POST localhost:8080/health

Posting data

    http POST localhost:8080/data < yourfile.dat

