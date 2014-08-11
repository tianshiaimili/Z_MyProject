/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.pccw.gzmobile.res:
//            BitmapHelper

public abstract class AbsImageCacheHelper
{

    public AbsImageCacheHelper()
    {
    }
    public abstract void putImage(String s, Bitmap bitmap, boolean flag);

    public abstract boolean containsKey(String s);

    public abstract Bitmap getImage(String s);

    public abstract Bitmap getImage(String s, int i);

    public abstract void removeImage(String s);

    public void removeImages(List keys)
    {
        if(keys != null)
        {
            String key;
            for(Iterator iterator = keys.iterator(); iterator.hasNext(); removeImage(key))
                key = (String)iterator.next();

        }
    }

    public abstract void clear();

    public abstract String getFilename(String s);

    public abstract int size();

    public Bitmap decodeScaledDownBitmap(Object toBeDecode, Resources res, int reqMinWidth, android.graphics.BitmapFactory.Options opts)
    {
        return BitmapHelper.decodeScaledDownBitmap(toBeDecode, res, reqMinWidth, opts);
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 675 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/