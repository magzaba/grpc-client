package mzaba.grpc.client;

import com.github.javafaker.Faker;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import mzaba.grpc.stubs.pingpong.PingPongServiceGrpc;
import mzaba.grpc.stubs.pingpong.PingRequest;
import mzaba.grpc.stubs.pingpong.PongResponse;

public class GrpcJavaClient {

    private PingPongServiceGrpc.PingPongServiceBlockingStub pingPongServiceBlockingStub;
    private static final Faker FAKER = new Faker();

    public GrpcJavaClient(Channel channel) {
        pingPongServiceBlockingStub = PingPongServiceGrpc.newBlockingStub(channel);
    }

    private PingRequest sendMessage(){
        return PingRequest.newBuilder()
                .setMessage("'PING' from Java GRPC Client")
                .setUsername(FAKER.name().fullName())
                .build();
    }

    private PongResponse getResponse(PingRequest pingRequest){
        return pingPongServiceBlockingStub.playPingPong(pingRequest);
    }

    private void stopClient(ManagedChannel channel){
        if (channel!=null) {
            channel.shutdown();
        }
    }

    public static void main(String[] args) {
        var channel = ManagedChannelBuilder
                .forTarget("localhost:8080")
                .usePlaintext()
                .build();
        var grpcClient = new GrpcJavaClient(channel);
        var request = grpcClient.sendMessage();
        var response = grpcClient.getResponse(request);
        System.out.println("Received the response from Server: '" + response.getMessage() + "'");
        grpcClient.stopClient(channel);
    }
}
