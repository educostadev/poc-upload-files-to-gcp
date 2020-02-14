package dev.educosta.gcpfileuploader.storage;

import com.google.cloud.storage.Storage.BlobField;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  Map<BlobField, String> metadata(String filename);

  void store(MultipartFile file);

  Set<URL> loadAllURL(String directory);

  byte[] load(String filename);

  URL loadAsURL(String filename);

  void delete(String filename);

}
