package com.dds.fileexplorer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件分类加载器
 * <p>
 * Created by dds on 2017/10/26.
 * 联信摩贝
 */

public class LocalFileLoader {

    public static final int TYPE_WORD = 0; //word
    public static final int TYPE_PPT = 1;//ppt
    public static final int TYPE_EXCEL = 2;//excel
    public static final int TYPE_PDF = 3;//pdf
    public static final int TYPE_TEX = 4;//txt
    public static final int TYPE_ZIP = 5;//zip
    public static final int TYPE_APK = 6;// apk
    public static final int TYPE_CUSTOM = 7;//自定义

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private FragmentActivity activity;
    private int type = TYPE_CUSTOM;


    public LocalFileLoader(FragmentActivity activity) {
        this.activity = activity;
        this.type = TYPE_CUSTOM;
    }


    /**
     * @param activity Activity
     * @param type     {@link #type}
     */
    public LocalFileLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    /**
     * 构造方法不传type 默认加载全部
     * 构造方法传type   加载对应的文件
     *
     * @param listener {@link #load(LocalFileLoadListener, String, String[])}
     */
    public void load(LocalFileLoadListener listener) {
        load(listener, null, null);
    }

    /**
     * @param listener      {@link LocalFileLoadListener}
     * @param selection     "mine_type = ? or ?"
     * @param selectionArgs new String[]{"applicaton/msword","applicaton/msword"}
     */
    public void load(final LocalFileLoadListener listener, final String selection, final String[] selectionArgs) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader;
                if (id == TYPE_CUSTOM) {
                    cursorLoader = new CursorLoader(activity, QUERY_URI,
                            new String[]{MediaStore.Files.FileColumns.DATA}, selection + " AND " + MediaStore.MediaColumns.SIZE + ">0",
                            selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
                } else {
                    String selection = "(" + getSelection(id) + ")" + " AND " + MediaStore.MediaColumns.SIZE + ">0";
                    cursorLoader = new CursorLoader(activity, QUERY_URI,
                            new String[]{MediaStore.Files.FileColumns.DATA}, selection,
                            getSelectionArgs(id, null), MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                ArrayList<String> list = new ArrayList<>();
                while (cursor != null && cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    File file = new File(path);
                    if (file.exists() && file.isFile() && file.canRead() && file.length() > 0) {
                        list.add(path);
                    }
                }

                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                listener.loadComplete(list);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });

    }

    public void search(final LocalFileLoadListener listener, final String fuzzy) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoaderSearch;
                if (id == TYPE_CUSTOM) {
                    cursorLoaderSearch = new CursorLoader(activity, QUERY_URI,
                            new String[]{MediaStore.Files.FileColumns.DATA}, MediaStore.MediaColumns.DATA + " LIKE ?"+ " AND " + MediaStore.MediaColumns.SIZE + ">0",
                            new String[]{"%" + fuzzy + "%"}, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
                } else {
                    String selection = "(" + getSelection(id) + ") AND " + MediaStore.MediaColumns.DATA + " LIKE ?"+ " AND " + MediaStore.MediaColumns.SIZE + ">0";
                    cursorLoaderSearch = new CursorLoader(activity, QUERY_URI,
                            new String[]{MediaStore.Files.FileColumns.DATA}, selection,
                            getSelectionArgs(id, fuzzy), MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
                }
                return cursorLoaderSearch;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                ArrayList<String> list = new ArrayList<>();
                while (cursor != null && cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    File file = new File(path);
                    if (file.exists() && file.isFile() && file.canRead() && file.length() > 0) {
                        list.add(path);
                    }
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                listener.loadComplete(list);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {


            }
        });


    }


    public interface LocalFileLoadListener {
        void loadComplete(ArrayList<String> files);
    }

    private String getSelection(int type) {
        StringBuilder builder = new StringBuilder();
        if (type == TYPE_WORD) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ?");
        } else if (type == TYPE_PPT) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ").append("or ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ");
        } else if (type == TYPE_EXCEL) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("?");
        } else if (type == TYPE_TEX) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("?");
        } else if (type == TYPE_ZIP) {
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ?");
        } else if (type == TYPE_PDF) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ?");
        } else if (type == TYPE_APK) {
            builder.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" = ").append("? ").append("OR ");
            builder.append(MediaStore.Files.FileColumns.DATA).append(" LIKE ?");
        }
        return builder.toString();

    }

    private String[] getSelectionArgs(int type, String fuzzy) {
        String[] args = null;
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        if (type == TYPE_WORD) {
            args = new String[]{singleton.getMimeTypeFromExtension("doc"),
                    singleton.getMimeTypeFromExtension("docx"),
                    singleton.getMimeTypeFromExtension("dotx"),
                    "%.docx"
            };

        } else if (type == TYPE_PPT) {
            args = new String[]{singleton.getMimeTypeFromExtension("ppt"),
                    singleton.getMimeTypeFromExtension("pptx"),
                    singleton.getMimeTypeFromExtension("potx"),
                    singleton.getMimeTypeFromExtension("ppsx"),
                    "%.ppt",
                    "%.pptx"
            };
        } else if (type == TYPE_EXCEL) {
            args = new String[]{singleton.getMimeTypeFromExtension("xls"),
                    singleton.getMimeTypeFromExtension("xlsx"),
                    singleton.getMimeTypeFromExtension("xltx")};
        } else if (type == TYPE_TEX) {
            args = new String[]{singleton.getMimeTypeFromExtension("txt"),
                    singleton.getMimeTypeFromExtension("text"),
                    singleton.getMimeTypeFromExtension("css"),
                    singleton.getMimeTypeFromExtension("java"),
                    singleton.getMimeTypeFromExtension("xml"),

            };
        } else if (type == TYPE_ZIP) {
            args = new String[]{
                    "%.rar",
                    "%.zip",
                    "%.iso",
                    "%.tar"
            };
        } else if (type == TYPE_APK) {
            args = new String[]{singleton.getMimeTypeFromExtension("apk"),
                    "%.apk"};
        } else if (type == TYPE_PDF) {
            args = new String[]{singleton.getMimeTypeFromExtension("pdf"),
                    "%.pdf",
                    "%.PDF"

            };
        }
        if (fuzzy != null) {
            String[] b = new String[]{"%" + fuzzy + "%"};
            args = concat(args, b);
        }
        return args;

    }

    private static String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
