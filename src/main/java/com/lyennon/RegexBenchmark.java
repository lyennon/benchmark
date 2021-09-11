package com.lyennon;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(16)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RegexBenchmark {

    @Benchmark
    public void regex() {
        String bulk = "{ \"index\" : { \"_index\" : \"test\", \"_id\" : \"1\" } }\n"
                + "{ \"field1\" : \"value1\" }\n"
                + "{ \"delete\" : { \"_index\" : \"test\", \"_id\" : \"2\" } }\n"
                + "{ \"create\" : { \"_index\" : \"test\", \"_id\" : \"3\" } }\n"
                + "{ \"field1\" : \"value3\" }\n"
                + "{ \"update\" : {\"_id\" : \"1\", \"_index\" : \"test\"} }\n"
                + "{ \"doc\" : {\"field2\" : \"value2\"} }";
        Pattern compile = Pattern.compile("\"_index\"\\s*:\\s*\"(.*?)\"[,|}]");
        Matcher matcher = compile.matcher(bulk);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String originIndex = matcher.group(1);
            String shadowIndex = "shadow_" + originIndex;
            matcher.appendReplacement(sb, matcher.group().replace(originIndex, shadowIndex));
        }
        matcher.appendTail(sb);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(RegexBenchmark.class.getSimpleName())
                .forks(2).build();
        new Runner(options).run();
    }
}


