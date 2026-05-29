package com.service.grpc.client;

/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.service.grpc.eqd.*;
import com.service.grpc.hello.*;
import com.service.grpc.server.EQDServer;
import com.service.grpc.server.HelloWorldServer;
import io.grpc.*;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a from the {@link EQDServer}.
 */
public class EQDServiceClient {
    private static final Logger logger = Logger.getLogger(EQDServiceClient.class.getName());

    private final ServiceAppGrpc.ServiceAppBlockingStub blockingStub;

    /** Construct client for accessing EQDServer server using the existing channel. */
    public EQDServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = ServiceAppGrpc.newBlockingStub(channel);
    }

    /** Say hello to server. */
    public void getHealth(String name) {
        logger.info("Will try to getHealth " + name + " ...");
        HealthRequest request = HealthRequest.newBuilder().setName(name).build();
        HealthReply response;
        try {
            response = blockingStub.getHealth(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Health: " + response.getMessage());
    }

    /** Say hello to server. */
    public void getService1(String name, int number) {
        logger.info("Will try to getService1 " + name + " ...");
        Service1Request request = Service1Request.newBuilder()
                .setName(name)
                .setNumber(number)
                .build();
        Iterator<Service1Reply> response;
        try {
            response = blockingStub.getService1(request);
            while(response.hasNext()){
                Service1Reply reply = response.next();
                logger.info("Printing: " + reply.getMessage() +"|" + reply.getLine1() + "|" + reply.getLine2());
            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStackTrace());
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        String user = "world!!!! KUMAR";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]]");
                System.err.println("");
                System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.exit(1);
            }
            user = args[0];
        }
        if (args.length > 1) {
            target = args[1];
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        try {
            EQDServiceClient client = new EQDServiceClient(channel);
            client.getHealth(user);
            client.getService1(user, 2);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
