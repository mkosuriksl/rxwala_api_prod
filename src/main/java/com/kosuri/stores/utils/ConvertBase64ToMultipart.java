package com.kosuri.stores.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConvertBase64ToMultipart {

	@SuppressWarnings("unchecked")
	public static File saveStringImage(String base64, String fileName) {
		File file = null;
		try {
			file = getImageFromBase64(base64, fileName);
			log.info("Base64 to Converting File Successfully !!");
		} catch (Exception e) {
			log.error("Error while Converting the file " + e);
			throw new RuntimeException("ErrorCode.FILE_UPLOADING_ERROR");
		}
		return file;
	}

	@SuppressWarnings("unchecked")
	public static File getImageFromBase64(String base64String, String fileName) {
		String[] strings = base64String.split(",");
		String extension;
		switch (strings[0]) { // Check MIME type
		case "data:image/jpeg;base64":
			extension = "jpeg";
			break;
		case "data:image/png;base64":
			extension = "png";
			break;
		case "data:application/pdf;base64":
			extension = "pdf";
			break;
		default:
			extension = "bin"; // Use a generic extension if the MIME type is unknown
			break;
		}
		// convert base64 string to binary data
		byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
		String name = "." + extension;
		File file = new File(fileName + name);
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			outputStream.write(data);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("ErrorCode.FILE_UPLOADING_ERROR");
		} 
		// } catch (Exception e) {
		// throw new ServiceException(ErrorCode.FILE_UPLOADING_ERROR, e);
		// }
		return file;
	}

}
