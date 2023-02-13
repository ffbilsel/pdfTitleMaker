package org;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
        List<String> files = listFilesUsingFilesList(currentPath);
        for (String fileName : files) {
            File file = new File(fileName);
            PDDocument document = PDDocument.load(file);
            PDDocumentInformation information = document.getDocumentInformation();
            String date = information.getCustomMetadataValue("date");
            date = date == null ? "" : date;
            date = date.length() >= 4 ? date.substring(0, 4) : "";
            String out = date + "_" + (information.getAuthor() == null || information.getAuthor().equals("") ? "" : information.getAuthor().substring(information.getAuthor().contains(" ") ? information.getAuthor().indexOf(" ") + 1 : 0, !information.getAuthor().contains(",") ? information.getAuthor().length() : information.getAuthor().indexOf(","))) +
                    "_" +(information.getTitle() == null || information.getTitle().equals("") ? "" : information.getTitle().replaceAll("[^a-zA-Z0-9]", "-").trim().replaceAll(" ", "_") + ".pdf");
            FileUtils.copyFile(file, new File("out/" + out));
            document.close();
        }
    }

    public static List<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file) && file.toString().contains(".pdf"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}
