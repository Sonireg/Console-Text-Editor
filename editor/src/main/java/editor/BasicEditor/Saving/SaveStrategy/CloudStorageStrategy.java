package editor.BasicEditor.Saving.SaveStrategy;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class CloudStorageStrategy implements StorageStrategy {
    private static final String APPLICATION_NAME = "TextEditor";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive.file");
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private static final String FOLDER_ID = "1qiYbwGdDYG2HmylreOWgsav-ZChkQWGA";

    private Drive service;

    public CloudStorageStrategy() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static com.google.api.client.auth.oauth2.Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        InputStream in = Files.newInputStream(Paths.get(CREDENTIALS_FILE_PATH));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    @Override
    public void save(String fileName, List<StringBuilder> content) throws IOException {
        // Запись содержимого во временный файл
        java.io.File tempFile = java.io.File.createTempFile("temp", null);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (StringBuilder line : content) {
                writer.write(line.toString());
                writer.newLine();
            }
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList("ВАШ_ID_ПАПКИ"));
        FileContent mediaContent = new FileContent("text/plain", tempFile);
        fileMetadata.setParents(Collections.singletonList(FOLDER_ID));
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        System.out.println("Файл загружен в Google Drive с ID: " + file.getId());
    }
}