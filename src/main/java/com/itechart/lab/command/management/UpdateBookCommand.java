package com.itechart.lab.command.management;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.dto.BookInfo;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.service.BorrowService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

@Log4j2
public class UpdateBookCommand implements Command {
    private static final UpdateBookCommand instance = new UpdateBookCommand();

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2;
    private static final String COVER_FOLDER_NAME = "img\\cover";

    private static final String MSG_FAILED_SAVE_BOOK_COVER
            = "Failed to save book cover image";
    private static final String MSG_MULTIPART_CONTENT_TYPE_REQUIRED
            = "Multipart content type required";
    private static final String MSG_FAILED_FIND_CREATE_COVERS_DIR
            = "Failed to find or create book covers directory";

    private UpdateBookCommand() {
    }

    public static UpdateBookCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            log.error(MSG_MULTIPART_CONTENT_TYPE_REQUIRED);
            return CommandResponse.ERROR_400;
        }

        BookInfo editedBookInfo = assembleBookInfo(request);
        if (editedBookInfo == null) {
            return CommandResponse.ERROR_500;
        }
        try {
            BookService.getInstance().save(editedBookInfo);
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
        editedBookInfo.getBorrows().forEach(b -> {
            try {
                BorrowService.getInstance().save(b, editedBookInfo.getId());
            } catch (ServiceException e) {
                log.error(e.getMessage());
            }
        });

        return new CommandResponse() {
            @Override
            public String location() {
                return "http://localhost:8080/bookman/controller?command=book_page&bookId="
                        + editedBookInfo.getId();
            }

            @Override
            public boolean redirect() {
                return true;
            }
        };
    }

    private BookInfo assembleBookInfo(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        DiskFileItemFactory factory = new DiskFileItemFactory(MAX_FILE_SIZE, repository);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);

        List<FileItem> items;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e) {
            log.error(e.getMessage());
            return null;
        }

        BookInfo bookInfo = new BookInfo();
        for (FileItem formPart : items) {
            if (formPart.isFormField()) {
                bookInfo.setFormField(formPart.getFieldName(), formPart.getString());
            } else {
                String fileName = formPart.getName();
                if (fileName == null || fileName.isEmpty()) {
                    continue;
                }
                fileName = generateString() + fileName.substring(fileName.lastIndexOf('.'));
                try {
                    String coverDirPath = request.getServletContext().getRealPath(COVER_FOLDER_NAME);
                    File coverDir = new File(coverDirPath);
                    if (!coverDir.exists()) {
                        if (!coverDir.mkdir()) {
                            throw new Exception(MSG_FAILED_FIND_CREATE_COVERS_DIR);
                        }
                    }
                    String filePath = coverDirPath + "\\" + fileName;
                    File uploadedFile = new File(filePath);
                    if (uploadedFile.createNewFile()) {
                        String absoluteFilePath = uploadedFile.getAbsolutePath();
                        filePath = absoluteFilePath
                                .substring(absoluteFilePath.indexOf(COVER_FOLDER_NAME))
                                .replaceAll("\\\\", "/");
                        formPart.write(uploadedFile);

                        //todo remove in real application
                        File cpFile = new File("D:\\OneDrive\\Documents\\iTechArt Lab\\book-library\\src\\main\\webapp\\img\\cover\\" + fileName);
                        if (cpFile.createNewFile()) {
                            Files.copy(
                                    Paths.get(absoluteFilePath), cpFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING
                            );
                        }

                        bookInfo.setFormField(formPart.getFieldName(), filePath);
                    }
                } catch (Exception e) {
                    log.warn(MSG_FAILED_SAVE_BOOK_COVER);
                }
            }
        }
        return bookInfo;
    }

    public String generateString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 25;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
