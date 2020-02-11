package dev.educosta.gcpfileuploader;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class FileUploadController {

  // The name of the bucket to access
  String bucketName = "bucket-poc-upload-files";

  @GetMapping(value = "/file/readMetadata")
  public Map<BlobField, String> readMetadata(@RequestParam(value = "name") String fileName) {
    return readMetadataTo(fileName);
  }

  @PostMapping(value = "/file/upload")
  public Map<BlobField, String> upload() throws IOException {

    return uploadTo(
        MediaType.IMAGE_PNG_VALUE,
        System.nanoTime()+".png",
        FileUploadController.class.getResourceAsStream("/icon.png").readAllBytes()
    );
  }


  public Map<BlobField, String> uploadTo(String MEDIA_TYPE_VALUE, String fileName, byte[] bytes) {
    Storage storage = StorageOptions.getDefaultInstance().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(MEDIA_TYPE_VALUE).build();
    Blob blob = storage.create(blobInfo, bytes);
    Map<BlobField, String> fields = this.createFiledMapFrom(blob);
    return fields;

  }


  /**
   * Select all fields Fields can be selected individually e.g. Storage.BlobField.CACHE_CONTROL
   *
   * @param blobName
   * @return
   */
  public Map<BlobField, String> readMetadataTo(String blobName) {
    Storage storage = StorageOptions.getDefaultInstance().getService();
    Blob blob = storage.get(bucketName, blobName, BlobGetOption.fields(Storage.BlobField.values()));
    Map<BlobField, String> fields = this.createFiledMapFrom(blob);
    return fields;
  }

  private Map<BlobField, String> createFiledMapFrom(Blob blob) {
    Map<BlobField, String> fieldValues = new EnumMap<>(BlobField.class);
    fieldValues.put(BlobField.BUCKET, blob.getBucket());
    fieldValues.put(BlobField.CACHE_CONTROL, blob.getCacheControl());
    fieldValues.put(BlobField.COMPONENT_COUNT, String.valueOf(blob.getComponentCount()));
    fieldValues.put(BlobField.CONTENT_DISPOSITION, blob.getContentDisposition());
    fieldValues.put(BlobField.CONTENT_ENCODING, blob.getContentEncoding());
    fieldValues.put(BlobField.CONTENT_LANGUAGE, blob.getContentLanguage());
    fieldValues.put(BlobField.CONTENT_TYPE, blob.getContentType());
    fieldValues.put(BlobField.CRC32C, blob.getCrc32c());
    fieldValues.put(BlobField.ETAG, blob.getEtag());
    fieldValues.put(BlobField.GENERATION, String.valueOf(blob.getGeneration()));
    fieldValues.put(BlobField.ID, String.valueOf(blob.getBlobId()));
    fieldValues.put(BlobField.KMS_KEY_NAME, blob.getKmsKeyName());
    fieldValues.put(BlobField.MD5HASH, blob.getMd5());
    fieldValues.put(BlobField.MEDIA_LINK, blob.getMediaLink());
    fieldValues.put(BlobField.METAGENERATION, String.valueOf(blob.getMetageneration()));
    fieldValues.put(BlobField.NAME, blob.getName());
    fieldValues.put(BlobField.SIZE, String.valueOf(blob.getSize()));
    fieldValues.put(BlobField.STORAGE_CLASS, String.valueOf(blob.getStorageClass()));
    fieldValues.put(BlobField.TIME_CREATED, new Date(blob.getCreateTime()).toString());
    fieldValues.put(BlobField.UPDATED, new Date(blob.getUpdateTime()).toString());
    boolean temporaryHoldIsEnabled = (blob.getTemporaryHold() != null && blob.getTemporaryHold());
    fieldValues.put(BlobField.TEMPORARY_HOLD, (temporaryHoldIsEnabled ? "enabled" : "disabled"));
    boolean eventBasedHoldIsEnabled =
        (blob.getEventBasedHold() != null && blob.getEventBasedHold());
    fieldValues.put(BlobField.EVENT_BASED_HOLD, (eventBasedHoldIsEnabled ? "enabled" : "disabled"));
    if (blob.getRetentionExpirationTime() != null) {
      fieldValues
          .put(BlobField.RETENTION_EXPIRATION_TIME,
              new Date(blob.getRetentionExpirationTime()).toString());
    }
    return fieldValues;
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
