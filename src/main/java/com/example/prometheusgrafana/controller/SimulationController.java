package com.example.prometheusgrafana.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/simulations")
public class SimulationController {

    @GetMapping("/slow")
    public ResponseEntity<String> slow() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ResponseEntity.ok("Simulated slow processing (2s)");
    }

    @GetMapping("/unstable")
    public ResponseEntity<String> unstable() {
        double r = Math.random();
        if (r < 0.3) {
            throw new RuntimeException("Simulated internal error");
        } else if (r < 0.5) {
            return ResponseEntity.badRequest().body("Simulated invalid request");
        } else {
            return ResponseEntity.ok("Success");
        }
    }

    @GetMapping("/consumeMemory")
    public ResponseEntity<String> consumeMemory() {
        try {
            List<byte[]> list = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                list.add(new byte[1_000_000]); // ~1MB
            }
            list.clear();
            System.gc();
            return ResponseEntity.ok("Simulated memory allocation and release");
        } catch (OutOfMemoryError oom) {
            return ResponseEntity.status(500).body("Simulated OutOfMemoryError");
        }
    }
}
