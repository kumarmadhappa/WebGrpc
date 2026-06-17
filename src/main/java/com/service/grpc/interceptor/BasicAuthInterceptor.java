package com.service.grpc.interceptor;

import com.service.grpc.server.EQDServer;
import io.grpc.*;

import java.util.logging.Logger;

public class BasicAuthInterceptor implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(BasicAuthInterceptor.class.getName());

    static final Metadata.Key<String> AUTH_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String authHeader =  headers.get(AUTH_KEY);
        logger.info("Header received from client "+ headers);
        System.out.println("Header received from client "+ headers);

        if(validated()){
            return next.startCall(call, headers);
        }

        call.close(Status.UNAUTHENTICATED.withDescription("Invalid Credentials"), new Metadata());
        return new ServerCall.Listener<ReqT>(){};
    }

    private boolean validated() {
        return true;
    }
}
