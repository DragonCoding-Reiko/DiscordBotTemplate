package net.dragoncoding.discordbottemplate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@SpringBootApplication(scanBasePackages = {"net.dragoncoding.discordbottemplate.app", "net.dragoncoding.discordbottemplate.discordbot"})
public class Application {

    private static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(3);
    private static final ArrayList<ScheduledFuture<?>> taskList = new ArrayList<>();

    public static void main(String[] args) {
        ((ScheduledThreadPoolExecutor) timer).setRemoveOnCancelPolicy(true);

        SpringApplication.run(Application.class, args);

        shutdown();
    }

    private static void shutdown() {
        for (ScheduledFuture<?> task : taskList) {
            task.cancel(false);
        }

        timer.shutdown();
    }
}
