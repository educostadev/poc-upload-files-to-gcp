package dev.educosta.gcpfileuploader.storage;

import static java.util.Objects.requireNonNull;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GoogleStorageService implements StorageService {

  private Storage storage;

  private static final String BUCKET_NAME = "bucket-poc-upload-files";


  @Override
  public void store(MultipartFile file) {
    String filename = StringUtils.cleanPath(requireNonNull(file.getOriginalFilename()));
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + filename);
      }
      BlobId blobId = BlobId.of(BUCKET_NAME, filename);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
      storage.create(blobInfo, file.getBytes());
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + filename, e);
    }
  }


  @Override
  public void delete(String filename) {
    String filenameCleaned = StringUtils.cleanPath(filename);
    BlobId blobId = BlobId.of(BUCKET_NAME, filenameCleaned);
    boolean deleted = storage.delete(blobId);
    if (!deleted) {
      throw new StorageException("File not deleted: " + filename);
    }
  }


  @PostConstruct
  public void init() {
    try {
      storage = StorageOptions.getDefaultInstance().getService();
    } catch (Exception e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public Stream<Path> loadAllURL(String path) {
    return Stream.empty();
  }


  @Override
  public byte[] load(String filename) {
    Blob blob = storage.get(BlobId.of(BUCKET_NAME, filename));
    return blob.getContent();
  }

  /**
   * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
   * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
   * you could also pass a service account signer to StorageOptions, i.e.
   * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
   * neither of these options are used, which means the following code only works when the
   * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
   * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
   * details.
   */
  @Override
  public URL loadAsURL(String filename) {
    Blob blob = storage.get(BlobId.of(BUCKET_NAME, filename));
    return blob.signUrl(15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
  }


  /**
   * Select all fields Fields can be selected individually e.g. Storage.BlobField.CACHE_CONTROL
   *
   * @param filename - the file name with path
   * @return A map of fields and values
   */
  @Override
  public Map<BlobField, String> metadata(String filename) {
    Blob blob = storage
        .get(BUCKET_NAME, filename, BlobGetOption.fields(Storage.BlobField.values()));
    return this.createFiledMapFrom(blob);
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

}
