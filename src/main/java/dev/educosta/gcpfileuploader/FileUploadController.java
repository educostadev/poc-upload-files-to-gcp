package dev.educosta.gcpfileuploader;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.StorageOptions;
import dev.educosta.gcpfileuploader.storage.StorageService;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1")
public class FileUploadController {

  // The name of the bucket to access
  String bucketName = "bucket-poc-upload-files";


  private final StorageService storageService;

  @Autowired
  public FileUploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping(value = "/file/metadata")
  public Map<BlobField, String> readMetadata(@RequestParam(value = "name") String filename) {
    return this.storageService.metadata(filename);
  }

  @DeleteMapping(value = "/file")
  public void deleteFile(@RequestParam(value = "name") String filename) {
    this.storageService.delete(filename);
  }

  @PostMapping(value = "/file")
  public void upload(@RequestParam(name = "file") MultipartFile file) {
    storageService.store(file);
  }


  public void download() {

// The name of the remote file to download
    String srcFilename = "file.txt";

// The path to which the file should be downloaded
    Path destFilePath = Paths.get("/local/path/to/file.txt");

// Instantiate a Google Cloud Storage client
    Storage storage = StorageOptions.getDefaultInstance().getService();

// Get specific file from specified bucket
    Blob blob = storage.get(BlobId.of(bucketName, srcFilename));

// Download file to specified path
    blob.downloadTo(destFilePath);
  }


  public void list() {
//    Storage storage = StorageOptions.getDefaultInstance().getService();
//
//    Page<Blob> blobs = bucket.list();
//    for (Blob blob : blobs.iterateAll()) {
//      // do something with the blob
//    }
  }

}
