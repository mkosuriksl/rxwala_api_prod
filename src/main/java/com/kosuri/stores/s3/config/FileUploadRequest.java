package com.kosuri.stores.s3.config;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.apache.commons.codec.binary.Base64;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

	@NotNull(message = "NotNull.fileUploadRequest.fileType")
	private FileType fileType;

	private String base64Data;

	private MultipartFile file;

	@NotBlank(message = "NotBlank.fileUploadRequest.fileName")
	private String fileName;

	@Builder.Default
	private FileAccess fileAccess = FileAccess.PUBLIC;

	public static final String ALLOWED_IMAGE_SIZE_IN_MB = "10 MB";

	public static final String ALLOWED_VIDEO_SIZE_IN_MB = "10 MB";

	public static final String ALLOWED_FILE_SIZE_IN_MB = "10 MB";

	public static final String ALLOWED_AUDIO_SIZE_IN_MB = "10 MB";

	public static final Long ALLOWED_IMAGE_SIZE = (long) (1024 * 1024
			* Integer.parseInt(ALLOWED_IMAGE_SIZE_IN_MB.split(" ")[0]));

	public static final Long ALLOWED_VIDEO_SIZE = (long) (1024 * 1024
			* Integer.parseInt(ALLOWED_VIDEO_SIZE_IN_MB.split(" ")[0]));

	public static final Long ALLOWED_FILE_SIZE = (long) (1024 * 1024
			* Integer.parseInt(ALLOWED_FILE_SIZE_IN_MB.split(" ")[0]));

	public static final Long ALLOWED_AUDIO_SIZE = (long) (1024 * 1024
			* Integer.parseInt(ALLOWED_AUDIO_SIZE_IN_MB.split(" ")[0]));

	public static final String ALLOWED_IMAGE_FORMATS = "jpg, jpeg, gif, png";

	public static final String ALLOWED_VIDEO_FORMATS = "mp4, avi, wmv, mov, flv, mkv, webm, ogv, ogg, mpg, mpeg, 3gp, 3g2";

	public static final String ALLOWED_AUDIO_FORMATS = "mp3, m4p, mpc, msv, vox, wav, wma, webm, aac, au, dvf, oga, ogg, 3gp, mpeg, mpeg3";

	public static final Set<String> VALID_IMAGE_FORMATS = stream(ALLOWED_IMAGE_FORMATS.split(", ")).collect(toSet());

	public static final Set<String> VALID_VIDEO_FORMATS = stream(ALLOWED_VIDEO_FORMATS.split(", ")).collect(toSet());

	public static final Set<String> VALID_AUDIO_FORMATS = stream(ALLOWED_AUDIO_FORMATS.split(", ")).collect(toSet());

	@JsonIgnore
	@AssertTrue(message = "NotEmpty.fileUploadRequest.data")
	public boolean isFileDataProvided() {
		return isNotBlank(base64Data) || (Objects.nonNull(file) && file.getSize() > 0);
	}

	@JsonIgnore
	@AssertTrue(message = "NotValid.fileUploadRequest.imageFile(::)" + ALLOWED_IMAGE_FORMATS)
	public boolean isValidImage() {
		String contentType = getContentType();
		if (fileType != null && FileType.IMAGE.equals(fileType) && isNotBlank(contentType)) {
			return VALID_IMAGE_FORMATS.contains(contentType.toLowerCase().split("/")[1]);
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "NotValid.fileUploadRequest.videoFile(::)" + ALLOWED_VIDEO_FORMATS)
	public boolean isValidVideo() {
		String contentType = getContentType();
		if (fileType != null && FileType.VIDEO.equals(fileType) && isNotBlank(contentType)) {
			return VALID_VIDEO_FORMATS.contains(contentType.toLowerCase().split("/")[1]);
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "NotValid.fileUploadRequest.audioFile(::)" + ALLOWED_AUDIO_FORMATS)
	public boolean isValidAudio() {
		String contentType = getContentType();
		if (fileType != null && FileType.AUDIO.equals(fileType) && isNotBlank(contentType)) {
			return VALID_AUDIO_FORMATS.contains(contentType.toLowerCase().split("/")[1]);
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "NotValid.fileUploadRequest.base64Data")
	public boolean isFileDataNotValid() {
		if (isNotBlank(base64Data)) {
			try {
				convertBase64DataIntoByteArray(base64Data);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.imageFile(::)" + ALLOWED_IMAGE_SIZE_IN_MB)
	public boolean isImageLarge() {
		if (fileType != null && FileType.IMAGE.equals(fileType) && isNotBlank(base64Data)) {
			try {
				return convertBase64DataIntoByteArray(base64Data).length <= ALLOWED_IMAGE_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.imageFile(::)" + ALLOWED_IMAGE_SIZE_IN_MB)
	public boolean isBinaryImageLarge() {
		if (fileType != null && FileType.IMAGE.equals(fileType) && Objects.nonNull(file)) {
			try {
				return file.getSize() <= ALLOWED_IMAGE_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.videoFile(::)" + ALLOWED_VIDEO_SIZE_IN_MB)
	public boolean isVideoLarge() {
		if (fileType != null && FileType.VIDEO.equals(fileType) && isNotBlank(base64Data)) {
			try {
				return convertBase64DataIntoByteArray(base64Data).length <= ALLOWED_VIDEO_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.videoFile(::)" + ALLOWED_VIDEO_SIZE_IN_MB)
	public boolean isBinaryVideoLarge() {
		if (fileType != null && FileType.VIDEO.equals(fileType) && Objects.nonNull(file)) {
			try {
				return file.getSize() <= ALLOWED_VIDEO_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.audioFile(::)" + ALLOWED_AUDIO_SIZE_IN_MB)
	public boolean isDocLarge() {
		if (fileType != null && FileType.AUDIO.equals(fileType) && isNotBlank(base64Data)) {
			try {
				return convertBase64DataIntoByteArray(base64Data).length <= ALLOWED_AUDIO_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	@JsonIgnore
	@AssertTrue(message = "Large.fileUploadRequest.audioFile(::)" + ALLOWED_AUDIO_SIZE_IN_MB)
	public boolean isBinaryAudioLarge() {
		if (fileType != null && FileType.AUDIO.equals(fileType) && Objects.nonNull(file)) {
			try {
				return file.getSize() <= ALLOWED_AUDIO_SIZE;
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}

	public static byte[] convertBase64DataIntoByteArray(String base64Data) {
		try {
			// Decode the Base64 string into a byte array
			byte[] decodedBytes = Base64.getDecoder().decode(base64Data.getBytes(StandardCharsets.UTF_8));
			return decodedBytes;
		} catch (IllegalArgumentException e) {
			// Handle invalid Base64 data
			e.printStackTrace(); // or log the error
			return null;
		}
	}

	private String getContentType() {
		if (StringUtils.isNotBlank(base64Data)) {
			return getContentTypeFromBase64(base64Data);
		}
		if (Objects.nonNull(file)) {
			return file.getContentType();
		}
		return "";
	}

	public static String getContentTypeFromBase64(String base64Data) {
		// Decode the Base64 data
		byte[] decodedData = org.apache.commons.codec.binary.Base64.decodeBase64(base64Data);

		// Check the first few bytes to identify the content type
		if (decodedData.length >= 2) {
			// Check for PDF
			if (decodedData[0] == 0x25 && decodedData[1] == 0x50) {
				return "application/pdf";
			}
		}

		// If the content type is not identified, return a generic binary type
		return "application/octet-stream";
	}

	public enum FileType {
		IMAGE, AUDIO, VIDEO, DOCUMENT;
	}

	public enum FileAccess {

		PUBLIC, PRIVATE;

	}

}
