package vez;

import vez.common.Result;
import vez.common.Options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class ShuffleAndCopyImages {

  private static final Random rnd = new Random();
  private static final AtomicLong amount = new AtomicLong(0L);

  public static void main(String[] args) throws IOException {

    long start = System.currentTimeMillis();

    System.out.println("Entering photo-copy application");

    // Initialize any command-line options.
    if (!Options.getInstance().parseArgs(args)) {
      return;
    }

    // Walk through
    System.out.printf("Walk through source directory: %s%n", Options.getInstance().getSource());
    List<Path> sourceFiles = getJpgFiles( Options.getInstance().getSource() );
    System.out.printf("Found %d files in: %s%n", sourceFiles.size(), Options.getInstance().getSource());

    System.out.printf("Check destination directory: %s%n", Options.getInstance().getDest());
    Files.createDirectories(Paths.get(Options.getInstance().getDest()));

    System.out.printf("Begin copy %d files to: %s%n", sourceFiles.size(), Options.getInstance().getDest() );

    List<Future<Result>> copyFileFutures;
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      copyFileFutures = sourceFiles.stream()
              .map( source ->
                      // starts a virtual thread to copy files concurrently.
                      executor.submit( () -> copyFileToFolder(source, Options.getInstance().getDest()) )
              ).toList();
    }

    System.out.printf("%d tasks are submitted. Wait to complete%n", copyFileFutures.size());

    // The Future.resultNow() calls below don't block since the try-with-resources scope
    // above won't exit until all tasks complete.
    copyFileFutures.forEach( fut -> System.out.println(fut.resultNow()) );

    long duration = System.currentTimeMillis() - start;
    System.out.println("Processed total: " + amount + ", mills: " + duration);
  }

  private static List<Path> getJpgFiles(String sourceFolder) {
    // walk directory 'source' directory and prepare list of jpg files
    try ( Stream<Path> paths = Files.walk(Paths.get(sourceFolder)) ) {
      // filtering jpeg files
      return paths
              .filter(Files::isRegularFile)
              .filter(
                      p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jpg")
              ).toList();
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }

  private static Result copyFileToFolder(Path sourceFile, String destFolderName) {

    long next;
    Path destPath = Paths.get(destFolderName);
    Path destFile;

    // find unused filename
    do {
      next = rnd.nextInt(Integer.MAX_VALUE);
      destFile = Paths.get(destPath.toString(), next + ".jpg");
    } while ( Files.exists(destFile) );

    long idx = amount.addAndGet(1L);

    // now trying to copy from sourceFile to destinationFile
    boolean isSuccess;
    try {
      System.out.printf("%d. Start '%s' to '%s'%n", idx, sourceFile, destFile);
      Files.copy(sourceFile, destFile);
      isSuccess = true;
    } catch (IOException e) {
      isSuccess = false;
    }
    System.out.printf("%d. Finish '%s''%n", idx, sourceFile);
    return new Result(String.format("%d, %s", idx, sourceFile), isSuccess);
  }

}
