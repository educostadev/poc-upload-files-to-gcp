package dev.educosta.gcpfileuploader.storage;

import com.google.cloud.storage.Storage.BlobField;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  Map<BlobField, String> metadata(String filename);

  void store(MultipartFile file);

  Stream<Path> loadAll();

  Path load(String filename);

  Resource loadAsResource(String filename);

  void delete(String filename);

}
