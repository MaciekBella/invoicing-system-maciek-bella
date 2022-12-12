package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfigurator {

  private static final String DATABASE_LOCATION = "db";
  private static final String ID_FILE_NAME = "id.txt";
  private static final String INVOICES_FILE_NAME = "invoices.txt";

  @Bean
  public IdService idService(FileService fileService) throws IOException {
    Path idFilePath = Files.createTempFile(DATABASE_LOCATION, ID_FILE_NAME);
    return new IdService(idFilePath, fileService);
  }

  @Bean
  public DataBase fileBasedDatabase(IdService idProvider, FileService fileService, JsonService jsonService) throws IOException {
    Path databaseFilePath = Files.createTempFile(DATABASE_LOCATION, INVOICES_FILE_NAME);
    return new FileBasedDatabase(databaseFilePath, idProvider, fileService, jsonService);
  }
}
