package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {

    Objects.requireNonNull(klass);

    ArrayList<Method> profiledAnnotatedMethods = new ArrayList<>();
    for(Method method: klass.getDeclaredMethods()){
      if (method.isAnnotationPresent(Profiled.class)){
        profiledAnnotatedMethods.add(method);
      }
    }

    if(profiledAnnotatedMethods.isEmpty()){
      throw new IllegalArgumentException(klass.getName() + " does not contain any Profiled annotated method");
    }

    ProfilingMethodInterceptor methodInterceptor = new ProfilingMethodInterceptor(clock, state, delegate);

    Object proxyInstance = Proxy.newProxyInstance(ProfilerImpl.class.getClassLoader(),
                   new Class<?>[]{klass},
                   methodInterceptor);

    return (T) proxyInstance;
  }

  @Override
  public void writeData(Path path) {

    Objects.requireNonNull(path);
    try(Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
      writeData(writer);
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
