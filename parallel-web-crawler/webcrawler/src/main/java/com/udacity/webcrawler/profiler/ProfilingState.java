package com.udacity.webcrawler.profiler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Helper class that records method performance data from the method interceptor.
 */
final class ProfilingState {
  //private final Map<String, Duration> data = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ProfileData> data = new ConcurrentHashMap<>();

  /**
   * Records the given method invocation data.
   *
   * @param callingClass the Java class of the object that called the method.
   * @param method       the method that was called.
   * @param elapsed      the amount of time that passed while the method was called.
   */
  void record(Class<?> callingClass, Method method, Duration elapsed, Long threadId) {
    Objects.requireNonNull(callingClass);
    Objects.requireNonNull(method);
    Objects.requireNonNull(elapsed);
    if (elapsed.isNegative()) {
      throw new IllegalArgumentException("negative elapsed time");
    }
    String key = formatMethodCall(callingClass, method);

    data.compute(key, (k,v) ->
            {
              if (v == null)
              {
               v = new ProfileData();
              }
              v.addDuration(elapsed);
              v.addThreadId(threadId);
              return v;
            });

  }

  /**
   * Writes the method invocation data to the given {@link Writer}.
   *
   * <p>Recorded data is aggregated across calls to the same method. For example, suppose
   * {@link #record(Class, Method, Duration, Long) record} is called three times for the same method
   * {@code M()}, with each invocation taking 1 second. The total {@link Duration} reported by
   * this {@code write()} method for {@code M()} should be 3 seconds.
   */
  void write(Writer writer) throws IOException {
    List<String> entries =
        data.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + " took " + formatProfileData(e.getValue(), e.getKey()) + System.lineSeparator())
            .collect(Collectors.toList());

    // We have to use a for-loop here instead of a Stream API method because Writer#write() can
    // throw an IOException, and lambdas are not allowed to throw checked exceptions.
    for (String entry : entries) {
      writer.write(entry);
    }
  }

  /**
   * Formats the given method call for writing to a text file.
   *
   * @param callingClass the Java class of the object whose method was invoked.
   * @param method       the Java method that was invoked.
   * @return a string representation of the method call.
   */
  private static String formatMethodCall(Class<?> callingClass, Method method) {
    return String.format("%s#%s", callingClass.getName(), method.getName());
  }

  /**
   * Formats the given {@link Duration} for writing to a text file.
   */
  private static String formatDuration(Duration duration) {
    return String.format("%sm %ss %sms", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart());
  }
  private static  String formatMethodCallCount(String methodName, int methodCount){
    return String.format("The method %s is called %s time(s).", methodName, methodCount);
  }

  private static String formatThreadIdCountMap(Map<Long, Integer> threadIdCountMap){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Thread Ids and count of threads that invoke the method: ");
    stringBuilder.append(" { ");
    for(Map.Entry<Long, Integer> entry: threadIdCountMap.entrySet()){
      stringBuilder.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" ,");
    }
    if (stringBuilder.length()>1){
      stringBuilder.setLength(stringBuilder.length()-2);
    }
    stringBuilder.append(" } ");

    return stringBuilder.toString();
  }

  private static String formatProfileData(ProfileData profileDate, String methodName){
    Duration duration = profileDate.getDuration();
    int methodCallCount = profileDate.getMethodCallCount();
    Map<Long, Integer> threadIdCountMap = profileDate.getThreadIdCountMap();

    return formatDuration(duration) + System.lineSeparator() + formatMethodCallCount(methodName, methodCallCount)
            + System.lineSeparator() + formatThreadIdCountMap(threadIdCountMap) + System.lineSeparator();
  }

  private static class ProfileData{
    private Duration duration;
    private AtomicInteger methodCallCount;
    private ConcurrentHashMap<Long, Integer> threadIdCountMap;

    private ProfileData(){
      this.duration = Duration.ZERO;
      this.methodCallCount = new AtomicInteger(0);
      this.threadIdCountMap = new ConcurrentHashMap<>();
    }

    private void addDuration(Duration currentDuration){
        duration = duration.plus(currentDuration);
        methodCallCount.incrementAndGet();
    }
    private Duration getDuration(){
      return duration;
    }
    private void addThreadId(Long threadId){
      threadIdCountMap.merge(threadId, 1, Integer::sum);
    }
    private int getMethodCallCount(){
      return methodCallCount.get();
    }

    private ConcurrentHashMap<Long, Integer> getThreadIdCountMap(){
      return threadIdCountMap;
    }
  }
}
