package com.registering;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import com.mainclass.MainClass;

public class WatchingServices {
    //watcher: a watch service that watches registered objects for changes and events.
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchingServices(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        walkAndRegisterDirectories(dir);
    }

    /**
     * Register the given directory with the WatchService; This function will be
     * called by FileVisitor
     */
    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
                ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void walkAndRegisterDirectories(final Path start)
            throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        for (;;) {
            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>) event).context();
                Path child = dir.resolve(name);

                // print out event
                // System.out.format("%s: %s\n", event.kind().name(), child);
                // System.out.println(event.kind().name());
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child)) {
                            walkAndRegisterDirectories(child);
                        }
                    } catch (IOException x) {
                        // do something useful
                    }
                }

                if (kind == ENTRY_CREATE) {
                    // a new file has been created
                    // print the name of the file. To test this, go to the temp
                    // directory
                    // and create a plain text file. name the file a.txt. If you
                    // are on windows, watch what happens!
                    MainClass.countJavaFiles();
                } else if (kind == ENTRY_DELETE) {
                    // the file has been deleted. delete the file. and exit the
                    // loop.
                    // System.out.println("File deleted:" +
                    // watchEvent.context());
                    // if ("a.txt".equals(watchEvent.context().toString())) {
                    // stopPolling = true;
                    // }
                    MainClass.countJavaFiles();
                } else if (kind == ENTRY_MODIFY) {
                    MainClass.counter();
                }

                // reset key and remove from set if directory no longer
                // accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}