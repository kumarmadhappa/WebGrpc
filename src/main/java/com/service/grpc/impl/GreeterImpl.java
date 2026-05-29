package com.service.grpc.impl;

import com.service.grpc.*;
import io.grpc.stub.StreamObserver;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder()
              .setMessage("Hello " + req.getName())
              .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAndGreet(HelloAndGreetRequest request, StreamObserver<HelloAndGreetReply> responseObserver) {
        for (int i = 1; i <= request.getTimes(); i++) {
            HelloAndGreetReply reply = HelloAndGreetReply.newBuilder()
                    .setMessage("Hello "+ request.getName())
                    .setEofMessage("!Over")
                    .build();

            // Intermittently stream data packets onto the wire live
            responseObserver.onNext(reply);

            // Artificial low-latency simulation delay
            try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        }

        // Inform the client connection that the entire data payload has finished streaming
        responseObserver.onCompleted();

    }
}