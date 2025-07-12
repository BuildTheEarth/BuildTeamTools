package net.buildtheearth.modules.backup.components;

import com.jcraft.jsch.*;
import jdk.internal.net.http.common.Pair;
import lombok.Getter;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.utils.ChatHelper;
import org.apache.commons.net.ntp.TimeStamp;
import org.bukkit.Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Queue;

public class FileUploadComponent extends ModuleComponent {

    private static final String BACKUP_HOST = "your-storagebox.de";
    private static final int BACKUP_PORT = 21;

    private Session session;
    private ChannelSftp sftp;

    private final FileTrackerComponent fileTrackerComponent;

    @Getter
    private final Queue<File> queue = new LinkedList<>();

    public FileUploadComponent(FileTrackerComponent fileTrackerComponent) {
        super("FileUpload");
        this.fileTrackerComponent = fileTrackerComponent;
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
        disconnect();
    }

    public void processQueue() throws Exception {
        File fileToProcess = queue.poll();
        if (fileToProcess == null) return;

        uploadFile(fileToProcess);
        fileTrackerComponent.markUploaded(fileToProcess.getName(), TimeStamp.getCurrentTime().getTime());
    }

    public void enqueueByChunk(Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Convert chunk coordinates to region coordinates
        int regionX = chunkX >> 5; // 32 chunks per region (2^5)
        int regionZ = chunkZ >> 5;

        String regionFileName = "r." + regionX + "." + regionZ + ".mca";
        File regionFile = new File(FileTrackerComponent.getREGION_FOLDER(), regionFileName);

        if (!queue.contains(regionFile)) {
            queue.add(regionFile);
        }
    }

    private synchronized void connect() throws JSchException {
        if (session != null && session.isConnected()) return;

        Pair<String, String> authenticationDetails = getAuthenticationDetails();
        if (authenticationDetails == null) {
            ChatHelper.logError("Failed to get authentication details for backup service.");
            return;
        }

        JSch jsch = new JSch();
        session = jsch.getSession(authenticationDetails.first, BACKUP_HOST, BACKUP_PORT);
        session.setPassword(authenticationDetails.second);

        session.setConfig("StrictHostKeyChecking", "no"); // disable known_hosts check
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    private synchronized void disconnect() {
        if (sftp != null && sftp.isConnected()) sftp.disconnect();
        if (session != null && session.isConnected()) session.disconnect();
    }

    private synchronized void uploadFile(File localFile) throws Exception {
        connect();

        try (FileInputStream fis = new FileInputStream(localFile)) {
            String remotePath = "/" + localFile.getName();
            sftp.put(fis, remotePath);
        }
    }

    private Pair<String, String> getAuthenticationDetails() {
        // TODO
        return null;
    }
}
