package com.example.kv.service;

import com.example.kv.*;
import com.example.kv.repository.KvRepository;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;


import java.util.List;

public class KvGrpcService extends KvServiceGrpc.KvServiceImplBase {
    private final KvRepository kvRepository;

    public KvGrpcService(KvRepository kvRepository) {
        this.kvRepository = kvRepository;
    }

    @Override
    public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
        try {
            byte[] value = request.getValue().toByteArray(); //!!!!
            kvRepository.put(request.getKey(), value);

            responseObserver.onNext(PutResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        try {
            byte[] value = kvRepository.get(request.getKey());
            GetResponse.Builder builder = GetResponse.newBuilder();
            if (value != null) {
                builder.setValue(ByteString.copyFrom(value));
                builder.setFound(true);
            } else {
                builder.setFound(false);
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        try {
            kvRepository.delete(request.getKey());
            responseObserver.onNext(DeleteResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void count(CountRequest request, StreamObserver<CountResponse> responseObserver) {
        try {
            long count = kvRepository.count();
            responseObserver.onNext(CountResponse.newBuilder().setCount(count).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void range(RangeRequest request, StreamObserver<RangeResponse> responseObserver) {
        try {
            List<List<Object>> results = kvRepository.range(
                    request.getKeyFrom(),
                    request.getKeyTo()
            );

            for (List<Object> tuple : results) {
                if (tuple.isEmpty()) continue;

                String key = (String) tuple.get(0);

                byte[] value = null;
                if (tuple.size() > 1 && tuple.get(1) != null) {
                    value = (byte[]) tuple.get(1);
                }

                RangeResponse.Builder builder = RangeResponse.newBuilder()
                        .setKey(key);

                if (value != null) {
                    builder.setValue(ByteString.copyFrom(value));
                }

                responseObserver.onNext(builder.build());
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }
}