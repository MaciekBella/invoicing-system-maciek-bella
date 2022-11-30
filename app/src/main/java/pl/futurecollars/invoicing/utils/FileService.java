package pl.futurecollars.invoicing.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import lombok.Generated;

@Generated
public class FileService {

  public void appendLineToFile(Path path, String line) throws IOException {
    Files.write(path, (line + "\n").getBytes(), StandardOpenOption.APPEND);
  }

  public List<String> readAllLines(Path path) throws IOException {
    return Files.readAllLines(path);
  }

  public void writeToFile(Path path, String line) throws IOException {
    Files.write(path, List.of(line), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
  }

  public void writeLinesToFile(Path path, List<String> lines) throws IOException {
    Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
  }
}
