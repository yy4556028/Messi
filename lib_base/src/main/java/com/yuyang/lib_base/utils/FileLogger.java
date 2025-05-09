package com.yuyang.lib_base.utils;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"JavaUtilDate","DefaultCharset"})
public class FileLogger {
  private static final String TAG = "FileLogger";

  private Writer mFileWriter = null;
  // Each log file every day.
  private String mCurrentDay;

  private SimpleDateFormat mDayFormat = new SimpleDateFormat("yyMMdd", Locale.US);
  private SimpleDateFormat mTimeFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.US);

  private String mLogDir;
  private String mLogFileNamePrefix;
  private String mProcessNameSuffix;

  private static final int LOG_FILE_CREATE_FAIL_MAX_LOG_COUNT = 3;
  private int mLogFileCreateFailedCount;

  public FileLogger(String logDir, String logFileNamePrefix, String processNameSuffix) {
    mLogDir = logDir;
    mLogFileNamePrefix = logFileNamePrefix;
    mProcessNameSuffix = processNameSuffix;
  }

  public String getLogDir() {
    return mLogDir;
  }

  public synchronized void close() {
    IoUtils.closeQuietly(mFileWriter);
    mFileWriter = null;
  }

  @SuppressWarnings("JavaUtilDate")
  public void logToFile(String tag, String msg, Throwable tr) {
    StringBuilder builder = new StringBuilder();
    builder.append(mTimeFormat.format(new Date()));
    builder.append(" ");
    builder.append(tag);
    builder.append("\t");
    builder.append(Process.myPid()).append(" ").append(Process.myTid()).append(" ");
    if (!TextUtils.isEmpty(msg)) {
      builder.append(msg);
    }
    if (tr != null) {
      builder.append("\n\t");
      builder.append(LogUtil_mobvoi.getStackTraceString(tr));
    }
    builder.append("\n");

    writeLog(builder.toString());
  }

  @SuppressWarnings("CatchAndPrintStackTrace")
  private synchronized void writeLog(String logLine) {
    if (null == mFileWriter) {
      if (!openFile()) {
        return;
      }
    }

    try {
      String day = getCurrentDay();
      // If is another day, then create a new log file.
      if (!day.equals(mCurrentDay)) {
        mFileWriter.flush();
        mFileWriter.close();
        mFileWriter = null;

        boolean success = openFile();
        if (!success) {
          return;
        }
      }

      mFileWriter.write(logLine);
      mFileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("CatchAndPrintStackTrace")
  private boolean openFile() {
    if (mLogDir == null) {
      return false;
    }

    File logDirFile = new File(mLogDir);
    if (!logDirFile.exists()) {
      if (!logDirFile.mkdirs()) {
        mLogFileCreateFailedCount++;
        if (mLogFileCreateFailedCount <= LOG_FILE_CREATE_FAIL_MAX_LOG_COUNT) {
          Log.w(TAG, "Cannot create dir: " + mLogDir);
        }
        return false;
      }
    }

    mCurrentDay = getCurrentDay();
    try {
      File logFile = new File(mLogDir, composeFileName(mCurrentDay));
      mFileWriter = new FileWriter(logFile, true);
      return true;
    } catch (IOException e) {
      mLogFileCreateFailedCount++;
      if (mLogFileCreateFailedCount <= LOG_FILE_CREATE_FAIL_MAX_LOG_COUNT) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private String composeFileName(String currentDay) {
    StringBuilder sb = new StringBuilder();
    sb.append(mLogFileNamePrefix).append("_log_").append(currentDay);
    if (!TextUtils.isEmpty(mProcessNameSuffix)) {
      sb.append("_").append(mProcessNameSuffix);
    }
    sb.append(".txt");
    return sb.toString();
  }

  private String getCurrentDay() {
    return mDayFormat.format(new Date());
  }
}
