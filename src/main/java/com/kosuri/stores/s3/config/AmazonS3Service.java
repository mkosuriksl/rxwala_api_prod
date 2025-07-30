package com.kosuri.stores.s3.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.utils.RandomUtils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
public class AmazonS3Service {

	private String accessKey = new String("AKIA6DE6T6IDDS4YWV4S");

	private String secretKey = new String("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");

	private Region region;

	private String baseUrl;

	AmazonS3 client = null;

	public final static String BUCKET_PREFIX = "rxkolan.in";

	public static final Regions REGIONS = Regions.AP_SOUTH_1;

	private static final Logger logger = Logger.getLogger(AmazonS3Service.class.getCanonicalName());

	ResponseService responseService = new ResponseService();

	@Bean
	public AmazonS3 s3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(REGIONS).build();
		return client;
	}

	public String uploadFile(String folder, MultipartFile file) {
		logger.info("Add image file to S3");
		// String bucket = getBucketName(bucketName);
		// Execute the request to create the folder
		createFolder(client, BUCKET_PREFIX, folder);

		File fileObj = convertMultiPartFileToFile(file);
		String fileName = RandomUtils.generate10RandomDigit();
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("Image");
		try {
			fileName = folder + fileName;
			client.putObject(new PutObjectRequest(BUCKET_PREFIX, fileName, fileObj));
			logger.info("File Name ==> " + fileName + "  Bucket name==> " + BUCKET_PREFIX);
		} catch (Exception e) {
			throw new ResourceNotFoundException("File Uploading Problem");
		} finally {
			// Delete the temporary file object
			if (fileObj != null && fileObj.exists()) {
				fileObj.delete();
			}
		}
		String imageUrl = getImageUrl(BUCKET_PREFIX, fileName);
		logger.info("Uploaded File Url ==> " + imageUrl);
		return imageUrl;
	}

	public String uploadStringFile(String folder, String fileName, File file) {
		logger.info("Add image file to S3");

		// Creating folder if it is not there in AWS s3 bucket
		createFolder(client, BUCKET_PREFIX, folder);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("application/octet-stream");
		try {
			fileName = folder + fileName;
			client.putObject(new PutObjectRequest(BUCKET_PREFIX, fileName, file));
			logger.info("File Name ==> " + fileName + "  Bucket name==> " + BUCKET_PREFIX);
		} catch (AmazonS3Exception e) {
			e.printStackTrace();
			System.err.println("Error uploading object to Amazon S3: " + e.getMessage());
			throw new ResourceNotFoundException("Error AmazonS3Exception uploading object to Amazon S3");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unexpected error occurred: " + e.getMessage());
			throw new ResourceNotFoundException("Error Exception File Uploading Problem Upload String File");
		} finally {
			// Delete the temporary file object
			if (file != null && file.exists()) {
				file.delete();
			}
		}

		String imageUrl = getImageUrl(BUCKET_PREFIX, fileName);
		logger.info("Uploaded File Url ==> " + imageUrl);
		return imageUrl;
	}

	public String getImageUrl(String folder, String fileName) {
		logger.info("get Image url");
		String bucketEndpoint = client.getBucketLocation(BUCKET_PREFIX); // Get the region-specific endpoint
		System.out.println("bucketEndpoint  ==>  " + bucketEndpoint);
		String objectKey = fileName;
		System.out.println("objectKey  ==>  " + objectKey);
		return "https://" + "s3." + bucketEndpoint + ".amazonaws.com" + "/" + BUCKET_PREFIX + "/" + objectKey;

	}

	public String getImageUrl1(String bucket, String name) {
		logger.info("get Image url");
		return client.getUrl(bucket, name).toString();
	}

	private void createBucket(String bucket) {
		logger.info("Create S3 bucket");
		// String bucket = getBucketName(name);

		logger.info("Create bucket now: " + bucket);
		client.createBucket(bucket);
	}

	private void createFolder(String folderName) {
		logger.info("Create S3 folder");
		// AmazonS3 s3=AmazonS3Client.builder().build();
		// PutObjectRequest objectRequest=s3

	}

	public List<S3ObjectDetail> getImagesForBucket(String bucket, String prefix) {
		;
		logger.info("Get list of images:  " + bucket);

		ObjectListing images = null;
		if (prefix == null) {
			images = client.listObjects(bucket);
		} else {
			images = client.listObjects(bucket, prefix);
		}

		List<S3ObjectDetail> s3ObjectDetails = new ArrayList<>();
		for (S3ObjectSummary objectSummary : images.getObjectSummaries()) {
			String url = getImageUrl(bucket, objectSummary.getKey());
			s3ObjectDetails.add(new S3ObjectDetail(url, objectSummary.getKey(), objectSummary.getSize()));
		}
		return s3ObjectDetails;
	}

	public ResponseEntity getImages(String appId, Long shopId, String prefix) {
		logger.info("Get Images for app: " + appId);
		prefix = shopId + "/" + prefix;
		String bucket = getBucketName(appId);
		S3ObjectMaster s3ObjectMaster = new S3ObjectMaster();
		s3ObjectMaster.setAppBucket(new KeyValue(bucket, "Personal"));
		List<KeyValue> genericBucketList = new ArrayList<>();
		HashMap<String, List<S3ObjectDetail>> s3ObjectDetails = new HashMap<>();
		s3ObjectDetails.put(bucket, getImagesForBucket(bucket, prefix));
		for (S3ObjectMaster.GenericBuckets genericBuckets : S3ObjectMaster.GenericBuckets.values()) {
			try {
				String genericBucket = getBucketName(genericBuckets.name());
				// s3ObjectDetails.put(genericBucket, getImagesForBucket(genericBucket));
				genericBucketList.add(new KeyValue(genericBucket, genericBuckets.getDescription()));
			} catch (Exception e) {
				continue;
			}
		}

		s3ObjectMaster.setGenericBuckets(genericBucketList);
		s3ObjectMaster.setS3ObjectDetails(s3ObjectDetails);
		return responseService.getResponseEntity(s3ObjectMaster);
	}

	public ResponseEntity deleteImages1(String appId, Long shopId, String image) {
		logger.info("Delete S3 images");
		String bucket = getBucketName(appId);
		if (!client.doesBucketExistV2(bucket)) {
			logger.severe("S3 bucket doesn't exists");
			return responseService.getResponseEntity("S3 bucket doesn't exists");
		}

		// image = shopId + "/" + image;
		logger.info("Delete S3 image: " + image);
		client.deleteObject(bucket, image);

		return responseService.getResponseEntity();
	}

	public ResponseEntity deleteImages(String folder, String image) {
		logger.info("Delete S3 images");
		String key = folder + image;
		// image = shopId + "/" + image;
		logger.info("Delete S3 image: " + image);
		client.deleteObject(BUCKET_PREFIX, key);

		return ResponseEntity.ok("Delete S3 image");
	}

	public static String getBucketName(String appId) {
		return BUCKET_PREFIX + appId.toLowerCase();
	}

	public static String getS3Url(String appId) {
		String url = "https://" + getBucketName(appId) + ".s3." + REGIONS.getName().toLowerCase() + ".amazonaws.com";
		return url;
	}

	public AmazonS3Service(String accessKey, String secretKey, Region region) {
		super();
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;
	}

	public String getFileName(String fullUrl) {
		URL parsedUrl = null;
		try {
			parsedUrl = new URL(fullUrl);
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		String path = parsedUrl.getPath();
		// Use the File class to extract the filename
		File file = new File(path);
		return file.getName();

	}

	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			logger.info("Error converting multipartFile to file");
		}
		return convertedFile;
	}

	public void createFolder(AmazonS3 amazonS3, String bucketName, String folderName) {
		// Check if the folder already exists
		if (folderExists(amazonS3, bucketName, folderName)) {
			logger.info("Folder already exists: " + folderName);
			System.out.println("Folder already exists: " + folderName);
			return;
		}

		// Create a PutObjectRequest to create an empty object (folder) with the
		// Create an empty InputStream
		ByteArrayInputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);

		// Create metadata for an empty object (folder)
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// Specify the bucket name, key (folder name), and input stream with metadata
		amazonS3.putObject(bucketName, folderName, emptyInputStream, metadata);

		System.out.println("Folder created: " + folderName);
	}

	private boolean folderExists(AmazonS3 amazonS3, String bucketName, String folderName) {
		// Check if the folder exists by listing objects with the specified prefix
		ObjectListing objectListing = amazonS3.listObjects(bucketName, folderName);
		return !objectListing.getObjectSummaries().isEmpty();
	}

	public void downloadFilesAndZip(AmazonS3 amazonS3, String BUCKET_NAME, String[] storeDocFileList,
			String zipFileName) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {
			for (String fileName : storeDocFileList) {
				/*
				 * if (fileName != null && !fileName.isEmpty()) { try
				 * (ResponseInputStream<GetObjectResponse> s3Object = amazonS3
				 * .getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(fileName).build
				 * ())) { ZipEntry zipEntry = new ZipEntry(fileName);
				 * zos.putNextEntry(zipEntry); s3Object.transferTo(zos); zos.closeEntry(); }
				 * catch (AmazonS3Exception e) { throw new
				 * APIException("Error downloading file: " + fileName, e); } }
				 */
			}
		} catch (IOException e) {
			throw new ResourceNotFoundException("Error creating zip file");
		}
	}

}
