package com.test.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;


@State(Scope.Thread)
public class LambdaExpressionsBenchmarks {


    private int _SizeMap = 1000;
    private int _SizeList = 100;

    private HashMap<String, ArrayList<String>> _hashMap = new HashMap();

    @Setup(Level.Iteration)
    public void setup() {
        for (int i = 0; i < _SizeMap; i++) {
            ArrayList<String> aList = new ArrayList<>();
            for (int j = 0; j < _SizeList; j++)
                aList.add("Value_" + i + "_" + j);
            _hashMap.put(String.valueOf(i), aList);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void browseMapWithExplicitIterator(Blackhole bh) throws InterruptedException {
        String key = "";
        List<String> values;
        Iterator<Map.Entry<String, ArrayList<String>>> mapIter = _hashMap.entrySet().iterator();
        while (mapIter.hasNext()) {
            Map.Entry<String, ArrayList<String>> mapEntry = mapIter.next();
            key = mapEntry.getKey();
            bh.consume(key);
            values = mapEntry.getValue();
            for (String value : values) {
                bh.consume(value);
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void browseMapWithoutExplicitIterator(Blackhole bh) throws InterruptedException {
        String key = "";
        List<String> values;
        for (Map.Entry<String, ArrayList<String>> entry : _hashMap.entrySet()) {
            key = entry.getKey();
            bh.consume(key);
            values = entry.getValue();
            for (String value : values) {
                bh.consume(value);
            }
        }

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void browseMapWithExplicitBiConsumer(Blackhole bh) throws InterruptedException {
        BiConsumer<String, ArrayList<String>> bc = (key, values) -> {
            bh.consume(key);
            values.forEach(value -> bh.consume(value));
        };
        _hashMap.forEach(bc);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void browseMapWithoutExplicitBiConsumer(Blackhole bh) throws InterruptedException {
        _hashMap.forEach((key, values) -> {
            bh.consume(key);
            values.forEach(value -> bh.consume(value));
        });

    }


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(".*" + LambdaExpressionsBenchmarks.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(25)
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
