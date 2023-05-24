package vez;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Stream;

public class WalkFileTreeExample {

  private static final String SRC_DIR = "C:\\Users\\vez\\Pictures\\GooglePhotos\\2023";
  private static final String DEST_DIR = "D:\\photos";
  private static final Random rnd = new Random();
  private static long amount = 0L;

  public static void main(String[] args) {

    amount = 0L;
    Path destDir = Paths.get(DEST_DIR);
    try (Stream<Path> paths = Files.walk(Paths.get(SRC_DIR))) {
      paths.filter(Files::isRegularFile)
              .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jpg"))
              .forEach(filePath -> copyFileToFolder(filePath, destDir));
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Processed: " + amount);
  }

  private static void copyFileToFolder(Path filePath, Path destDir) {
    long next;
    Path destPath;
    do {
      next = rnd.nextInt(Integer.MAX_VALUE);
      destPath = Paths.get(destDir.toString(), String.valueOf(next) + ".jpg");
    } while ( Files.exists(destPath) );

    try {
      Files.copy(filePath, destPath);
      amount++;
      System.out.println("Copied:\t" + amount + ";\t process:\t" + destPath.getFileName() + "\t from: " + filePath );
    } catch (IOException e) {
      System.out.println("Error with file:\t" + filePath + ".\t " + e.getMessage());
    }
  }

}
