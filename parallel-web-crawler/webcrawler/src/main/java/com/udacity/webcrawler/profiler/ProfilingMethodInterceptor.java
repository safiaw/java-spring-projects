package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private Clock clock;
  private ProfilingState state;
  private Object targetObject;

  ProfilingMethodInterceptor(Clock clock, ProfilingState state, Object targetObject) {
    this.clock = Objects.requireNonNull(clock);
    this.state = state;
    this.targetObject = targetObject;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

      Object result;
      Instant startTime = null;
      Long threadId = null;
      if(method.isAnnotationPresent(Profiled.class)) {
          startTime = clock.instant();
          threadId = Thread.currentThread().getId();
      }

      try {
        if (method.getName().equals("equals") && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(Object.class)) {
            result = targetObject.equals(args[0]);
        }
        else if (method.getName().equals("hashCode") && method.getParameterCount() == 0) {
            result = targetObject.hashCode();
            
        }
        else if (method.getName().equals("toString") && method.getParameterCount() == 0) {
            result = targetObject.toString();
        }
        else {
            result = method.invoke(targetObject, args);
        }
        }
      catch(InvocationTargetException e){
         throw e.getTargetException();
       }
      catch(IllegalAccessException e){
         throw new RuntimeException(e);
       }
      finally {
          if (startTime != null && threadId != null) {
              Duration elapsed = Duration.between(startTime, clock.instant());
              state.record(targetObject.getClass(), method, elapsed, threadId);
          }
        }
    return result;
  }
}
