package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.Generated;
import pl.futurecollars.invoicing.utils.FileService;

@Generated
public class IdService {

  private final Path path;
  private final FileService fileService;
  private int nextId = 1;

  public IdService(Path path, FileService fileService) {
    this.path = path;
    this.fileService = fileService;

    try {
      List<String> lines = fileService.readAllLines(path);
      if (lines.isEmpty()) {
        fileService.writeToFile(path, "1");
      } else {
        nextId = Integer.parseInt(lines.get(0));
      }
    } catch (IOException ex) {
      throw new RuntimeException("Failed", ex);
    }

  }

  public int getIdAndIncrement() {
    try {
      fileService.writeToFile(path, String.valueOf(nextId + 1));
      return nextId++;
    } catch (IOException ex) {
      throw new RuntimeException("Failed", ex);
    }
  }

}
