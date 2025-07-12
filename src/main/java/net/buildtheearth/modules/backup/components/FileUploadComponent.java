package net.buildtheearth.modules.backup.components;

import com.jcraft.jsch.*;
import jdk.internal.net.http.common.Pair;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.utils.ChatHelper;

import java.io.File;
import java.io.FileInputStream;

public class FileUploadComponent extends ModuleComponent {

    private static final String BACKUP_HOST = "your-storagebox.de";
    private static final int BACKUP_PORT = 21;

    private Session session;
    private ChannelSftp sftp;

    public FileUploadComponent() {
        super("FileUpload");
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
    }

    public synchronized void connect() throws JSchException {
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

    public synchronized void disconnect() {
        if (sftp != null && sftp.isConnected()) sftp.disconnect();
        if (session != null && session.isConnected()) session.disconnect();
    }

    public synchronized void uploadFile(File localFile) throws Exception {
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
