package com.languageworld.be.global.multiPartFileUtil;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.multiPartFileUtil.enumGroup.FilePurposeCode;
import com.languageworld.be.global.multiPartFileUtil.enumGroup.FileTypeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
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

    // ContentType 검증
    String contentType = validateContentType(requestedFile);

    // 확장자 검증
    Path uploadPath = fileContentTypeCheckByPurpose(contentType, requestedPurpose);

    // 파일 확장자 추출
    String extension = contentType.substring(contentType.indexOf("/") + 1);

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
    }

    // 성공 로그
    CustomLogger.info(LogEventCode.MULTIPART_FILE,
            "SUCCESS",
            SuccessCode.FILE_UPLOAD_SUCCESS.getMessage() + " - Purpose: " + requestedPurpose + " - contentType: " + contentType,
            null);

    return fileUploadedPath.toString();
    }

    public void deleteFile(FilePurposeCode filePurposeCode, String requestedFileUrl) {


     if(requestedFileUrl == null || requestedFileUrl.isEmpty()) {

         return;
     }

     // 소셜 계정 프로필 소셜 제공자
     if (filePurposeCode == FilePurposeCode.PROFILE
             && (requestedFileUrl.startsWith("https://") || requestedFileUrl.startsWith("http://")))
         return;

     Path fileUploadedPath = Path.of(requestedFileUrl).toAbsolutePath();

     try {

         Files.deleteIfExists(fileUploadedPath);
     } catch(IOException e) {

         CustomLogger.error(
                 LogEventCode.MULTIPART_FILE,
                 "FAIL",
                 LogEventReasonCode.MULTIPART_FILE_DELETE_FAILED,
                 LogEventReasonCode.MULTIPART_FILE_DELETE_FAILED.getMessage() + " - throw IOException",
                 null
         );
         throw new CustomException(CustomExceptionCode.MULTIPART_FILE_DELETE_FAILED);
     }
    }

    public void registerCleanUp(UUID userUuid, FilePurposeCode filePurposeCode, List<String> deleteOnCommit, List<String> deleteOnRollback) {

        if(!TransactionSynchronizationManager.isActualTransactionActive() || !TransactionSynchronizationManager.isSynchronizationActive()) {

            throw new CustomException(CustomExceptionCode.FILE_TRANSACTION_NOT_ACTIVE);
        }

        TransactionSynchronizationManager.registerSynchronization(

                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {

                        List<String> deletePathList;
                        if (status == STATUS_COMMITTED)
                            deletePathList = deleteOnCommit;
                        else if (status == STATUS_ROLLED_BACK)
                            deletePathList = deleteOnRollback;
                        else {
                            //Transaction 결과가 커밋, 롤백 둘 다 아님 => 결과 불명확하므로 삭제x
                            CustomLogger.error(
                                    LogEventCode.MULTIPART_FILE,
                                    "FAIL",
                                    LogEventReasonCode.FILE_TRANSACTION_STATUS_UNKNOWN,
                                    LogEventReasonCode.FILE_TRANSACTION_STATUS_UNKNOWN.getMessage()
                                            + " - Status: " + status
                                            + " - FilePurpose: " + filePurposeCode,
                                    userUuid.toString()
                            );

                            return;
                        }

                        for(String deletePath : deletePathList) {

                            // 중간에 실패할 경우 나머지라도 삭제하도록
                            try {
                                deleteFile(filePurposeCode, deletePath);
                            } catch (Exception e) {

                                CustomLogger.error(
                                        LogEventCode.MULTIPART_FILE,
                                        "FAIL",
                                        LogEventReasonCode.MULTIPART_FILE_DELETE_FAILED,
                                        LogEventReasonCode.MULTIPART_FILE_DELETE_FAILED.getMessage()
                                                + " - FilePurpose: " + filePurposeCode
                                                + " - FilePath: " + deletePath
                                                + " - Exception: " + e.getMessage(),
                                        e
                                );
                            }
                        }
                    }
                }
        );
    }

    private void createFileUploadDir(Path uploadPath, LogEventReasonCode logEventReasonCode) {
    try {
        Files.createDirectories(uploadPath);
    } catch (IOException e) {

        CustomLogger.error(
                LogEventCode.MULTIPART_FILE,
                "FAIL",
                logEventReasonCode,
                logEventReasonCode.getMessage() + " - IOException while creating file upload directory",
                e
        );

        throw new CustomException(CustomExceptionCode.CREATE_DIR_EXCEPTION);
    }
    }

    private void saveFile(MultipartFile multipartFile, Path fileUploadedPath, LogEventReasonCode logEventReasonCode) {

    try {
        multipartFile.transferTo(fileUploadedPath);
    } catch (IOException e) {

        CustomLogger.error(
                LogEventCode.MULTIPART_FILE,
                "FAIL",
                logEventReasonCode,
                logEventReasonCode.getMessage() + " - IOException while saving file",
                e
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

    // 경로 결정, 목적 검사
    private Path fileContentTypeCheckByPurpose(String contentType, FilePurposeCode filePurpose) {


    if (filePurpose == null ){
        CustomLogger.warn(
                LogEventCode.MULTIPART_FILE,
                "FAIL",
                LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST,
                LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST.getMessage() + " - File purpose is null",
                null
        );

        throw new CustomException(CustomExceptionCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST);
    }
    else if(filePurpose == FilePurposeCode.TERM) {

        if (!contentType.equals(FileTypeCode.PDF.getContentType())) {
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

        if(!contentType.equals(FileTypeCode.JPEG.getContentType()) && !contentType.equals(FileTypeCode.PNG.getContentType())) {
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
                LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST.getMessage() + " - " + filePurpose.name() + " is unsupported purpose: TERM, POST, PROFILE only supported",
                null
        );

        throw new CustomException(CustomExceptionCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST);
    }
    }

    // contentType null check
    private String validateContentType(MultipartFile requestedFile) {

    String contentType = requestedFile.getContentType();

    if(contentType == null) {

        CustomLogger.warn(
                LogEventCode.MULTIPART_FILE,
                "FAIL",
                LogEventReasonCode.CONTENT_TYPE_NULL,
                LogEventReasonCode.CONTENT_TYPE_NULL.getMessage(),
                null
        );

        throw new CustomException(CustomExceptionCode.INVALID_FILE_TYPE);
    }

    return contentType;
    }
}
