package usb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UsbManager {

    private final LogWriter logWriter = new LogWriter();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static final String[] EXTENSIONS = new String[]{"jpg", "png", "jpeg", "PNG", "JPG", "JPEG"};

    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    public void detectDevice(File[] usb, String[] driveName, boolean[] usbDetected) {
        while (true) {
            for (int i = 0; i < driveName.length; i++) {
                boolean isDetected;
                isDetected = usb[i].canRead();

                if (isDetected != usbDetected[i]) {
                    if (isDetected) {
                        long totalSpace = usb[i].getTotalSpace() / (1024 * 1024);
                        long freeSpace = usb[i].getFreeSpace() / (1024 * 1024);
                        logWriter.writeLog("USB " + driveName[i] + " detected");
                        logWriter.writeLog("Total space: " + totalSpace + " MB");
                        logWriter.writeLog("Free space: " + freeSpace + " MB");
                        logWriter.writeLog("Number of files: " + countFiles(usb[i]));
                        logWriter.writeLog("Number of folders: " + countFolder(usb[i]));
                        logWriter.writeLog("Plugin time: " + LocalDateTime.now().format(dateTimeFormatter));
                        deleteShAndBatFile(usb[i]);
                        searchAllImages(usb[i]);
                    }
                    usbDetected[i] = isDetected;
                }
            }
        }
    }

    public void runDetect() {
        String[] driveName = new String[]{"E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};
        File[] usb = new File[driveName.length];
        boolean[] usbDetected = new boolean[driveName.length];

        for (int i = 0; i < driveName.length; i++) {
            usb[i] = new File(driveName[i] + ":/");
        }

        //System.out.println("Insert USB");
        detectDevice(usb, driveName, usbDetected);
    }

    public void deleteShAndBatFile(File file) {
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (f.getName().endsWith(".sh") || f.getName().endsWith(".bat")) {
                logWriter.writeLog("Delete " + f.getName());
                f.delete();
            }
        }
    }

    public int countFiles(File filePath) {
        File file = new File(String.valueOf(filePath));
        File[] files = file.listFiles(File::isFile);

        return Objects.requireNonNull(files).length;
    }

    public int countFolder(File filePath) {
        File file = new File(String.valueOf(filePath));
        File[] files = file.listFiles(File::isDirectory);

        return Objects.requireNonNull(files).length;
    }

    public void searchAllImages(File filePath){
        int count = 0;
        long capacity = 0;
        if (filePath.isDirectory()) {
            for (File f : Objects.requireNonNull(filePath.listFiles(IMAGE_FILTER))) {
                BufferedImage img;

                try {
                    img = ImageIO.read(f);
                    String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    ImageIO.write(img, extension, new File("images\\" + f.getName()));
                    count++;
                    capacity += f.length();
//                    System.out.println("image: " + f.getName());
//                    System.out.println(" width : " + img.getWidth());
//                    System.out.println(" height: " + img.getHeight());
//                    System.out.println(" size  : " + f.length());
//                    System.out.println(" Total Capacity: " + capacity);
//                    System.out.println(" extension: " + extension);
                    if(count >= 1000 || capacity >= 100 * (1024*1024))
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logWriter.writeLog("Copy total " + count + " images, capacity: " + capacity/(1024*1024) + " MB");
        }
    }
}
