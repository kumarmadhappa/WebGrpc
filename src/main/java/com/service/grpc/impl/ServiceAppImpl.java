package com.service.grpc.impl;

import com.service.grpc.eqd.*;
import io.grpc.stub.StreamObserver;

public class ServiceAppImpl extends ServiceAppGrpc.ServiceAppImplBase {

    @Override
    public void getHealth(HealthRequest req, StreamObserver<HealthReply> responseObserver) {
        HealthReply reply = HealthReply.newBuilder()
              .setMessage("Hello " + req.getName())
              .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void getService1(Service1Request request, StreamObserver<Service1Reply> responseObserver) {
        for (int i = 1; i <= request.getNumber(); i++) {
            Service1Reply reply = Service1Reply.newBuilder()
                    .setMessage("Hello "+ request.getName())
                    .setLine1("This is Line 1 of "+ i)
                    .setLine2("This is Line 2 of "+ i)
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