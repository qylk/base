package com.qylk.mp.bus.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    private static final int ONE_KB = 1024;
    private static final int ONE_MB = ONE_KB * ONE_KB;
    private static final int ONE_GB = ONE_MB * ONE_KB;
    private static final int ONE_PB = ONE_GB * ONE_KB;
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static File getHomeDir(Context context) {
        File dir;
        if (isSdCardMounted()) {
            dir = new File(getExternalFilesPath(context));
        } else {
            dir = context.getCacheDir();
        }
        dir.mkdirs();
        return dir;
    }

    public static boolean isFileExists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createFolder(String path) {
        boolean success = false;
        try {
            File folder = new File(path);
            if (folder.exists() && folder.isDirectory()) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static void createFileFolder(String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFile(String fromPath, String toPath) {
        boolean success;
        // get channels
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fis = new FileInputStream(fromPath);
            fos = new FileOutputStream(toPath);
            fcin = fis.getChannel();
            fcout = fos.getChannel();

            // do the file copy
            fcin.transferTo(0, fcin.size(), fcout);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtils.closeQuietly(fcin);
            IOUtils.closeQuietly(fcout);
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
        return success;
    }

    public static boolean moveFile(String fromPath, String toPath) {
        try {
            File fromFile = new File(fromPath);
            File toFile = new File(toPath);
            if (fromFile.exists()) {
                return fromFile.renameTo(toFile);
            } else {
                return false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(String filePath) {
        try {
            return new File(filePath).delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cleanDirectory(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }
        try {
            for (File tempFile : new File(folderPath).listFiles()) {
                if (tempFile.isDirectory()) {
                    cleanDirectory(tempFile.getPath());
                }
                tempFile.delete();
            }
            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        long availableExternalMemorySize;
        if (isSdCardMounted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableExternalMemorySize = availableBlocks * blockSize;
        } else {
            availableExternalMemorySize = -1;
        }
        return availableExternalMemorySize;
    }

    public static long getTotalExternalMemorySize() {
        long totalExternalMemorySize;
        if (isSdCardMounted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalExternalMemorySize = totalBlocks * blockSize;
        } else {
            totalExternalMemorySize = -1;
        }
        return totalExternalMemorySize;
    }

    public static String formatFileSize(double fileSize) {
        if (fileSize >= ONE_PB) {
            fileSize = fileSize / ONE_PB;
            return DECIMAL_FORMAT.format(fileSize) + "PB";
        } else if (fileSize >= ONE_GB) {
            fileSize = fileSize / ONE_GB;
            return DECIMAL_FORMAT.format(fileSize) + "GB";
        } else if (fileSize >= ONE_MB) {
            fileSize = fileSize / ONE_MB;
            return DECIMAL_FORMAT.format(fileSize) + "MB";
        } else if (fileSize >= ONE_KB) {
            fileSize = fileSize / ONE_KB;
            return DECIMAL_FORMAT.format(fileSize) + "KB";
        } else {
            return DECIMAL_FORMAT.format(fileSize) + "B";
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            OutputStream out = openOutputStream(file);
            IOUtils.closeQuietly(out);
        }
        boolean success = file.setLastModified(System.currentTimeMillis());
        if (!success) {
            throw new IOException("Unable to set the last modification time for " + file);
        }
    }

    public static boolean writeFile(File file, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
        return false;
    }

    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            o = new FileOutputStream(filePath, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.closeQuietly(o);
        }
    }

    public static String readContentFromFile(File file) {
        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileReader);
            IOUtils.closeQuietly(br);
        }
        return content;
    }

    public static String readContentFromAssetsFile(Context context, String fileName) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            res = readContentFromInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return res;
    }

    public static String readContentFromRawFile(Context context, int fileResId) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(fileResId);
            res = readContentFromInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return res;
    }

    private static String readContentFromInputStream(InputStream is) throws IOException {
        int length = is.available();
        byte[] buffer = new byte[length];
        is.read(buffer);
        return new String(buffer, Charset.forName("UTF-8"));
    }

    public static boolean writeToGzipFile(String filePath, String content) {
        File file;
        file = new File(filePath);

        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        try {
            fos = new FileOutputStream(file, false);
            gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            gos.write(content.getBytes());
            gos.finish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(gos);
        }
        return false;
    }

    public static String getExternalFilesPath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static String getExternalImagesPath(Context context) {
        return context.getExternalFilesDir("images").getAbsolutePath();
    }

    public static String getMimeType(File file) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileName = file.getName();
        if (fileName != null) {
            int index = file.getName().lastIndexOf('.');
            if (index != -1) {
                return mimeTypeMap.getMimeTypeFromExtension(fileName.toLowerCase().substring(index + 1));
            }
        }
        return null;
    }

    public static String getExternalCacheDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();
        if (file != null) {
            sb.append(file.getAbsolutePath());
        } else {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                        .append("/cache").toString();
            } else {
                sb.append(context.getCacheDir().getAbsolutePath());
            }
        }
        return sb.toString();
    }


    public static boolean unzip(String zipFile, String dir, boolean delete) {
        boolean success = false;
        ZipFile zf = null;
        try {
            BufferedInputStream bi;
            zf = new ZipFile(zipFile);
            Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                String entryName = ze2.getName();
                String path = dir + "/" + entryName;
                if (ze2.isDirectory()) {
                    File decompressDirFile = new File(path);
                    if (!decompressDirFile.exists()) {
                        decompressDirFile.mkdirs();
                    }
                } else {
                    String fileDir = path.substring(0, path.lastIndexOf("/"));
                    File fileDirFile = new File(fileDir);
                    if (!fileDirFile.exists()) {
                        fileDirFile.mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dir + "/" + entryName));
                    bi = new BufferedInputStream(zf.getInputStream(ze2));
                    byte[] readContent = new byte[1024];
                    int readCount = bi.read(readContent);
                    while (readCount != -1) {
                        bos.write(readContent, 0, readCount);
                        readCount = bi.read(readContent);
                    }
                    bi.close();
                    bos.close();
                }
            }
            zf.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (new File(dir).exists()) {
                cleanDirectory(dir);
            }
        } finally {
            IOUtils.closeQuietly(zf);
            if (delete) {
                deleteFile(zipFile);
            }
        }
        return success;
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extPos = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePos = filePath.lastIndexOf(File.separator);
        if (filePos == -1) {
            return (extPos == -1 ? filePath : filePath.substring(0, extPos));
        }
        if (extPos == -1) {
            return filePath.substring(filePos + 1);
        }
        return (filePos < extPos ? filePath.substring(filePos + 1, extPos) : filePath.substring(filePos + 1));
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? filePath : filePath.substring(filePos + 1);
    }

    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extPos = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePos = filePath.lastIndexOf(File.separator);
        if (extPos == -1) {
            return "";
        }
        return (filePos >= extPos) ? "" : filePath.substring(extPos + 1);
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static void saveObject(Context context, String fileName, Serializable object) {
        FileOutputStream fos = null;

        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);// 写入
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> T getObject(Context context, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(fileName);   //获得输入流
            ois = new ObjectInputStream(fis);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

}
