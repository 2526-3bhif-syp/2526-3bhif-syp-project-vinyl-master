package at.htl.leonding.vinylmaster.ui.image;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoverImageService {
    public static final long MAX_UPLOAD_SIZE_BYTES = 1_048_576; // 1 MB
    public static final int TARGET_SIZE = 256;
    private static final String IMAGE_DIRECTORY = "covers";
    private static final String APP_DATA_DIRECTORY = ".vinylmaster";

    public String saveCover(File sourceFile, Long vinylId) throws IOException {
        BufferedImage sourceImage = validateImageFile(sourceFile);

        BufferedImage normalizedImage = resizeToSquare(sourceImage, TARGET_SIZE);

        Path imagePath = getAbsolutePath(getRelativePath(vinylId));
        Files.createDirectories(imagePath.getParent());
        ImageIO.write(normalizedImage, "jpg", imagePath.toFile());
        return getRelativePath(vinylId);
    }

    public void deleteCover(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        Path appDataPath = getAppDataBasePath().resolve(relativePath).normalize();
        Path legacyPath = getLegacyBasePath().resolve(relativePath).normalize();

        if (Files.exists(appDataPath)) {
            Files.delete(appDataPath);
        }
        if (!appDataPath.equals(legacyPath) && Files.exists(legacyPath)) {
            Files.delete(legacyPath);
        }
    }

    public Path getAbsolutePath(String relativePath) {
        Path appDataPath = getAppDataBasePath().resolve(relativePath).normalize();
        if (Files.exists(appDataPath)) {
            return appDataPath;
        }

        // Compatibility path for old setups where covers were stored inside the repository.
        Path legacyPath = getLegacyBasePath().resolve(relativePath).normalize();
        if (Files.exists(legacyPath)) {
            return legacyPath;
        }

        return appDataPath;
    }

    public String getRelativePath(Long vinylId) {
        return IMAGE_DIRECTORY + "/vinyl-" + vinylId + ".jpg";
    }

    private Path getAppDataBasePath() {
        return Path.of(System.getProperty("user.home")).resolve(APP_DATA_DIRECTORY).normalize();
    }

    private Path getLegacyBasePath() {
        return Path.of(System.getProperty("user.dir")).normalize();
    }

    public BufferedImage validateImageFile(File sourceFile) throws IOException {
        validateSourceFile(sourceFile);
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        if (sourceImage == null) {
            throw new IOException("Selected file is not a valid image.");
        }
        return sourceImage;
    }

    private void validateSourceFile(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
            throw new IOException("Image file does not exist.");
        }
        if (sourceFile.length() > MAX_UPLOAD_SIZE_BYTES) {
            throw new IOException("Image exceeds maximum size of 1 MB.");
        }
    }

    private BufferedImage resizeToSquare(BufferedImage source, int targetSize) {
        int cropSize = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - cropSize) / 2;
        int y = (source.getHeight() - cropSize) / 2;
        BufferedImage cropped = source.getSubimage(x, y, cropSize, cropSize);

        BufferedImage output = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(cropped, 0, 0, targetSize, targetSize, null);
        graphics.dispose();
        return output;
    }
}
