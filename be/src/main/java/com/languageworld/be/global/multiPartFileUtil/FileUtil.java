package com.languageworld.be.global.multiPartFileUtil;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${file.upload-dir.term}")
    private Path termPath;

    @Value("${file.upload-dir.post}")
    private Path postPath;

    @Value("${file.upload-dir.profile}")
    private Path profilePath;

        public String uploadFile(MultipartFile requestedFile, FilePurposeCode requestedPurpose) {

            // 파일 공백 확인
            fileEmptyCheck(requestedFile);

            // 파일 확장자 추출
            String extension = extractExtension(requestedFile.getContentType());

            // 확장장 검증
            Path uploadPath = fileContentTypeCheckByPurpose(requestedFile, requestedPurpose);

            // 파일 이름
            String fileName = UUID.randomUUID() + "." + extension;

            // 파일 업로드 경로
            Path fileUploadedPath = uploadPath.resolve(fileName);

            // 확장자에 따른 처리
            switch(extension) {
                case("pdf"):

                    // 업로드 폴더 생성
                    createFileUploadDir(uploadPath, LogEventReasonCode.CREATE_PDF_DIR_EXCEPTION);

                    // 파일 이름 + 경로
                    saveFile(requestedFile, fileUploadedPath, LogEventReasonCode.SAVE_PDF_EXCEPTION);

                    break;
                case("jpeg"):

                    createFileUploadDir(uploadPath, LogEventReasonCode.CREATE_JPEG_DIR_EXCEPTION);
                    saveFile(requestedFile, fileUploadedPath, LogEventReasonCode.SAVE_JPEG_EXCEPTION);

                    break;
                case("png"):

                    createFileUploadDir(uploadPath, LogEventReasonCode.CREATE_PNG_DIR_EXCEPTION);
                    saveFile(requestedFile, fileUploadedPath, LogEventReasonCode.SAVE_PNG_EXCEPTION);

                    break;
                default:
                    CustomLogger.warn(
                            LogEventCode.MULTIPART_FILE,
                            "FAIL",
                            LogEventReasonCode.UNSUPPORTED_CONTENT_TYPE,
                            LogEventReasonCode.UNSUPPORTED_CONTENT_TYPE.getMessage() + " - File extension: " + extension,
                            null
                    );

                    throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
            }

            // 성공 로그
            CustomLogger.info(LogEventCode.MULTIPART_FILE,
                    "SUCCESS",
                    SuccessCode.FILE_UPLOAD_SUCCESS.getMessage(),
                    null);

            return fileUploadedPath.toString();
        }

        private void createFileUploadDir(Path uploadPath, LogEventReasonCode logEventReasonCode) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {

                CustomLogger.warn(
                        LogEventCode.MULTIPART_FILE,
                        "FAIL",
                        logEventReasonCode,
                        logEventReasonCode.getMessage() + " - IOException while creating file upload directory",
                        null
                );

                throw new CustomException(CustomExceptionCode.CREATE_DIR_EXCEPTION);
            }
        }

        private void saveFile(MultipartFile multipartFile, Path fileUploadedPath, LogEventReasonCode logEventReasonCode) {

            try {
                multipartFile.transferTo(fileUploadedPath);
            } catch (IOException e) {

                CustomLogger.warn(
                        LogEventCode.MULTIPART_FILE,
                        "FAIL",
                        logEventReasonCode,
                        logEventReasonCode.getMessage() + " - IOException while saving file",
                        null
                );

                throw new CustomException(CustomExceptionCode.SAVE_FILE_EXCEPTION);
            }
        }

        private void fileEmptyCheck(MultipartFile multipartFile) {

            if(multipartFile == null || multipartFile.isEmpty()) {
                CustomLogger.warn(
                        LogEventCode.MULTIPART_FILE,
                        "FAIL",
                        LogEventReasonCode.MULTIPART_FILE_REQUIRED,
                        LogEventReasonCode.MULTIPART_FILE_REQUIRED.getMessage() + " - File is null or empty",
                        null
                );

                throw new CustomException(CustomExceptionCode.MULTIPART_FILE_REQUIRED);
            }
        }

        // 경로 결정, 타입 검사
        private Path fileContentTypeCheckByPurpose(MultipartFile multipartFile, FilePurposeCode filePurpose) {

            String requestedContentType = multipartFile.getContentType();

            if(requestedContentType == null) {
                CustomLogger.warn(
                        LogEventCode.MULTIPART_FILE,
                        "FAIL",
                        LogEventReasonCode.CONTENT_TYPE_NULL,
                        LogEventReasonCode.CONTENT_TYPE_NULL.getMessage() + " - MultipartFile.contentType is null",
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
            }
            else if(filePurpose == FilePurposeCode.TERM) {

                if (!requestedContentType.equals(FileTypeCode.PDF.getContentType())) {
                    CustomLogger.warn(
                            LogEventCode.MULTIPART_FILE,
                            "FAIL",
                            LogEventReasonCode.PDF_REQUIRED,
                            LogEventReasonCode.PDF_REQUIRED.getMessage() + " - File extension is not pdf",
                            null
                    );

                    throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
                }

                return termPath;
            }

            else if(filePurpose == FilePurposeCode.POST || filePurpose == FilePurposeCode.PROFILE) {

                if(!requestedContentType.equals(FileTypeCode.JPEG.getContentType()) && !requestedContentType.equals(FileTypeCode.PNG.getContentType())) {

                    CustomLogger.warn(
                            LogEventCode.MULTIPART_FILE,
                            "FAIL",
                            LogEventReasonCode.INVALID_IMAGE_TYPE,
                            LogEventReasonCode.INVALID_IMAGE_TYPE.getMessage() + " - File extension is not image: jpeg, jpg,png required",
                            null
                    );

                    throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
                }

                if(filePurpose == FilePurposeCode.POST)
                    return postPath;
                else
                    return profilePath;
            }

            else {

                CustomLogger.warn(
                        LogEventCode.MULTIPART_FILE,
                        "FAIL",
                        LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST,
                        LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST.getMessage() + " - " + filePurpose.name() + "is unsupported purpose: TERM, POST, PROFILE only supported",
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
            }

        }

        // 확장자 반환
        private String extractExtension(String contentType) {

            String extension = contentType.substring(contentType.indexOf("/") + 1);

            return extension;
        }
}
