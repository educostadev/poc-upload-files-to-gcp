package dev.educosta.gcpfileuploader.storage;

import com.google.cloud.storage.Storage.BlobField;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  Map<BlobField, String> metadata(String filename);

  void store(MultipartFile file);

  Stream<Path> loadAllURL(String path);

  byte[] load(String filename);

  URL loadAsURL(String filename);

  void delete(String filename);

}
