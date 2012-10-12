
package com.zxt.download2;

import android.content.Context;

public class Layout {
    public static int listItem(Context context) {
        return Res.getInstance(context).getLayout("download_list_item");
    }

    public static int list(Context context) {
        return Res.getInstance(context).getLayout("download_list");
    }

    public static int notify(Context context) {
        return Res.getInstance(context).getLayout("download_notify");
    }
}
