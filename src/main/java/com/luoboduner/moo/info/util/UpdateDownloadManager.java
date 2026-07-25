package com.luoboduner.moo.info.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luoboduner.moo.info.ui.UiConsts;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Silent download and install-state management after a new version is found.
 */
@Slf4j
public class UpdateDownloadManager {

    public enum Status {
        IDLE,
        DOWNLOADING,
        READY,
        ERROR
    }

    private static final UpdateDownloadManager INSTANCE = new UpdateDownloadManager();

    private final List<Consumer<UpdateDownloadManager>> listeners = new CopyOnWriteArrayList<>();

    @Getter
    private volatile Status status = Status.IDLE;

    @Getter
    private volatile String version;

    @Getter
    private volatile File downloadedFile;

    @Getter
    private volatile int percent;

    @Getter
    private volatile String releaseNotesHtml;

    private volatile String downloadingVersion;

    public static UpdateDownloadManager getInstance() {
        return INSTANCE;
    }

    private UpdateDownloadManager() {
    }

    public void addListener(Consumer<UpdateDownloadManager> listener) {
        listeners.add(listener);
        listener.accept(this);
    }

    public void removeListener(Consumer<UpdateDownloadManager> listener) {
        listeners.remove(listener);
    }

    /**
     * Start a background silent download when not already ready for the same version.
     */
    public void startSilentDownload(String newVersion, String releaseNotesHtml) {
        if (StringUtils.isBlank(newVersion)) {
            return;
        }
        synchronized (this) {
            if (status == Status.READY
                    && newVersion.equals(version)
                    && downloadedFile != null
                    && downloadedFile.exists()) {
                if (StringUtils.isNotBlank(releaseNotesHtml)) {
                    this.releaseNotesHtml = releaseNotesHtml;
                }
                notifyListeners();
                return;
            }
            if (status == Status.DOWNLOADING && newVersion.equals(downloadingVersion)) {
                if (StringUtils.isNotBlank(releaseNotesHtml)) {
                    this.releaseNotesHtml = releaseNotesHtml;
                }
                return;
            }
            status = Status.DOWNLOADING;
            downloadingVersion = newVersion;
            version = newVersion;
            this.releaseNotesHtml = releaseNotesHtml;
            percent = 0;
            downloadedFile = null;
        }
        notifyListeners();
        ThreadUtil.execute(() -> download(newVersion));
    }

    public void installAndExit() {
        openPackageAndExit(downloadedFile);
    }

    /**
     * Open the downloaded installer, then exit.
     * <p>
     * Do not use {@link java.awt.Desktop#open(File)} followed by an immediate
     * {@link System#exit(int)}: AWT open is asynchronous and a hard JVM exit can
     * interrupt the hand-off so the installer never actually opens.
     */
    public static void openPackageAndExit(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalStateException("update package missing");
        }
        try {
            openPackage(file);
            Thread.sleep(500);
            System.exit(0);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.exit(0);
        }
    }

    static void openPackage(File file) throws IOException {
        String path = file.getAbsolutePath();
        ProcessBuilder pb = new ProcessBuilder(buildOpenCommand(path));
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();
        try {
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (finished && process.exitValue() != 0) {
                throw new IOException("open update package failed, exitCode=" + process.exitValue());
            }
            if (!finished) {
                log.warn("open update package still running after timeout: {}", path);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while opening update package", e);
        }
    }

    static String[] buildOpenCommand(String absolutePath) {
        if (SystemUtil.isMacOs()) {
            return new String[]{"open", absolutePath};
        }
        if (SystemUtil.isWindowsOs()) {
            return new String[]{"cmd", "/c", "start", "", absolutePath};
        }
        return new String[]{"xdg-open", absolutePath};
    }

    private void download(String newVersion) {
        try {
            String fileUrl = resolveDownloadUrl();
            if (StringUtils.isBlank(fileUrl)) {
                throw new IllegalStateException("empty download url");
            }
            String fileName = FileUtil.getName(fileUrl);
            File pendingDir = pendingDir();
            if (!pendingDir.exists() && !pendingDir.mkdirs()) {
                throw new IllegalStateException("cannot create pending-updates dir");
            }
            FileUtil.clean(pendingDir);
            File target = FileUtil.file(pendingDir, fileName);

            HttpUtil.downloadFile(fileUrl, FileUtil.touch(target), new StreamProgress() {
                @Override
                public void start() {
                    percent = 0;
                    notifyListeners();
                }

                @Override
                public void progress(long totalSize, long progressSize) {
                    if (totalSize > 0) {
                        percent = (int) Math.min(100, progressSize * 100 / totalSize);
                    }
                    notifyListeners();
                }

                @Override
                public void finish() {
                    // status updated after downloadFile returns
                }
            });

            synchronized (this) {
                if (!newVersion.equals(downloadingVersion)) {
                    return;
                }
                downloadedFile = target;
                version = newVersion;
                percent = 100;
                status = Status.READY;
            }
            log.info("Silent update download finished: {} -> {}", newVersion, target.getAbsolutePath());
            notifyListeners();
        } catch (Exception e) {
            log.error("Silent update download failed: {}", newVersion, e);
            synchronized (this) {
                if (newVersion.equals(downloadingVersion)) {
                    status = Status.ERROR;
                    percent = 0;
                }
            }
            notifyListeners();
        }
    }

    static String resolveDownloadUrl() {
        String downloadLinkInfo = HttpUtil.get(UiConsts.DOWNLOAD_LINK_INFO_URL);
        if (StringUtils.isEmpty(downloadLinkInfo) || downloadLinkInfo.contains("404: Not Found")) {
            throw new IllegalStateException("download links unavailable");
        }
        JSONObject parse = JSON.parseObject(downloadLinkInfo);
        return DownloadLinkSelector.select(parse);
    }

    static File pendingDir() {
        return new File(SystemUtil.CONFIG_HOME + File.separator + "pending-updates");
    }

    private void notifyListeners() {
        if (SwingUtilities.isEventDispatchThread()) {
            fireListeners();
        } else {
            SwingUtilities.invokeLater(this::fireListeners);
        }
    }

    private void fireListeners() {
        for (Consumer<UpdateDownloadManager> listener : listeners) {
            try {
                listener.accept(this);
            } catch (Exception e) {
                log.warn("Update download listener failed", e);
            }
        }
    }
}
