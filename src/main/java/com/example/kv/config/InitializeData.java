package com.example.kv.config;

import com.example.kv.repository.KvRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InitializeData {
    public static void main(String[] args) throws Exception {
        try (TarantoolConfig config = new TarantoolConfig()) {

            var client = config.getClient();

            int total = 5_000_000;

            List<CompletableFuture<?>> futures = new ArrayList<>();

            long start = System.currentTimeMillis();

            for (int i = 0; i < total; i++) {

                futures.add(
                        client.space("kv")
                                .replace(Arrays.asList(
                                        "key" + i,
                                        ("value" + i).getBytes()
                                ))
                );

                if (futures.size() == 1000) {
                    futures.forEach(CompletableFuture::join);
                    futures.clear();
                }
            }

            futures.forEach(CompletableFuture::join);

            long end = System.currentTimeMillis();

            System.out.println("Done!");
            System.out.println("Time: " + (end - start) + " ms");
        }
    }
}