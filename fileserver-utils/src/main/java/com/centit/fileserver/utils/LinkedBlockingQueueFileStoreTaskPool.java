package com.centit.fileserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueFileStoreTaskPool implements FileStoreTaskPool {
    private static final Logger logger = LoggerFactory.getLogger(LinkedBlockingQueueFileStoreTaskPool.class);

    private File taskFile;
    private LinkedBlockingQueue<FileStoreTaskInfo> taskQueue;

    public LinkedBlockingQueueFileStoreTaskPool(String taskFileRoot) throws Exception {
        if (taskFileRoot.endsWith(String.valueOf(File.separatorChar))) {
            taskFile = new File(taskFileRoot + "task.dat");
        } else {
            taskFile = new File(taskFileRoot + File.separatorChar +  "task.dat");
        }

        if (taskFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(taskFile));
            taskQueue = (LinkedBlockingQueue) ois.readObject();
            ois.close();
        } else {
            taskQueue = new LinkedBlockingQueue();
        }
    }

    @Override
    public boolean add(FileStoreTaskInfo task) {
        taskQueue.offer(task);
        saveTasksToDisk();
        return true;
    }

    @Override
    public FileStoreTaskInfo get() {
        FileStoreTaskInfo task = taskQueue.poll();
        if (null != task) {
            saveTasksToDisk();
        }
        return task;
    }

    private void saveTasksToDisk() {
        logger.info("持久化任务, 任务总数: " + taskQueue.size());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(taskFile))) {
            oos.writeObject(taskQueue);
        } catch (IOException e) {
            logger.error("持久化文件存储任务失败: " + e.getMessage());
        }
    }
}
