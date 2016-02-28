# trackthread
Java instrumentation agent which enables to track and intercept thread lifecycle events

# Introduction

Trackthread is a java instrumentation agent which enables to track and intercept thread lifecycle events. It enables applications to register custom thread listeners at runtime, which will be notified of thread related events. Trackthread distinguishes the following thread lifecycle events: 
* ThreadStarting: Called from within thread's start method before the new thread is actually started. This event is triggered in the context of the parent thread which is executing `Thread.start()`.
* ThreadRunning: Called from within thread's run method just after thread code has started executing. This event is triggered in the context of the child thread.
* ThreadStopping: Called just before thread termination. This event is triggered in the context of the child thread. It is triggered from within child thread's run method just before exiting from it. It is called in the try/finally fashion to intercept all exit paths from the run method.
* ThreadRename: Called when the thread is about to be renamed. This event is triggered in the context of the thread which is calling `Thread.setName()`. 

Applications can provide their own implementations of `ThreadListener` and register it globally just after application startup using `ThreadListeners.addListener()`. 

# Build
Requirements:
- JRE 1.6+
- Maven 3.3.1

```
mvn install
```

# Usage

1. Enable instrumenation agent in java command line: e.g. `-javaagent:lib/trackthread-1.0.jar`. Additionally you may specify fully class name of the listener to be automatically registered during application startup: e.g. `-javaagent:lib/trackthread-1.0.jar=org.app.CustomThreadListener`. The class CustomThreadListener must be available from boot class loader (you may need to add `-Xbootclasspath/a:<path>/<custom.jar>` to the command line).

2. Implement the interface `ThreadListener` and register the implementing class during application startup using `ThreadListeners.addListener(listener)`. 

# Example

```java
import org.krzogr.trackthread.ThreadListener;
import org.krzogr.trackthread.ThreadListeners;

public class ThreadListenerExample {
  public static void main(final String[] args) throws Exception {
    ThreadListeners.addListener(new ThreadListener() {
      @Override
      public void onThreadStarting(final Thread thread) {
        System.out.println("*** Starting thread: " + thread.getName());
      }
      
      @Override
      public void onThreadRunning(final Thread thread) {
        System.out.println("*** Running thread: " + thread.getName());
      }

      @Override
      public void onThreadStopping(final Thread thread) {
        System.out.println("*** Stopping thread: " + thread.getName());
      }
      
      @Override
      public void onThreadRename(final Thread thread, String newName) {
        System.out.println("*** Renaming thread: " + thread.getName());
      }
    });
    
    System.out.println("Before thread");
    
    Thread t = new Thread() {
      @Override
      public void run() {
        System.out.println("Running thread");
        setName("new name");
        System.out.println("Completing thread");
      }
    };
    
    t.start();
    t.join();
    
    System.out.println("After thread");
  }
}
```
