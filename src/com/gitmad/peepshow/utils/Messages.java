package com.gitmad.peepshow.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * TODO: Enter class description.
 */
public final class Messages {
    public static void Debug(final String message)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        Log.d(prefix, message);
    }

    public static void Error(final String message)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        Log.e(prefix, message);
    }

    public static void Error(final String message, final Throwable exception)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        Log.e(prefix, message, exception);
    }

    public static void Info(final String message)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        Log.i(prefix, message);
    }

    public static void Warn(final String message)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        Log.w(prefix, message);
    }

    public static void WTF(final String message)
    {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final String caller = elements[3].getClassName();
        final Integer line_num = elements[3].getLineNumber();
        final String prefix = String.format("%s:%d", caller, line_num);
        //zLog.wtf(prefix, message);
    }

    public static void ShowErrorDialog(final Context context, final String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
               .setCancelable(false)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) { }
               });
        builder.create().show();
    }

    public static void ShowErrorDialog(final Context context, final String title, final String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
               .setCancelable(false)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) { }
               });
        builder.create().show();
    }
}
