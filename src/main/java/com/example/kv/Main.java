package com.example.kv;

import com.example.kv.config.TarantoolConfig;
import com.example.kv.repository.KvRepository;
import com.example.kv.service.KvGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class Main {
    public static void main(String[] args) {
        try (TarantoolConfig config = new TarantoolConfig()) {
            KvRepository repository = new KvRepository(config.getClient());

            Server server = ServerBuilder.forPort(9090)
                    .addService(new KvGrpcService(repository))
                    .addService(ProtoReflectionService.newInstance())
                    .build();

            server.start();
            System.out.println("gRPC Server started on port 9090");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stopping gRPC server...");
                server.shutdown();
            }));

            server.awaitTermination();
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
        }
    }
}