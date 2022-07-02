package com.yuyang.lib_base.myglide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

import okhttp3.OkHttpClient;

@GlideModule
public class SimpleGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);

        MemorySizeCalculator sizeCalculator = new MemorySizeCalculator.Builder(context)
//                .setBitmapPoolScreens()
//                .setLowMemoryMaxSizeMultiplier()
//                .setMemoryCacheScreens()
//                .setArrayPoolSize()
//                .setMaxSizeMultiplier()
                .build();

        //用于配置Glide的内存缓存策略，默认配置是LruResourceCache
        builder.setMemoryCache(new LruResourceCache(sizeCalculator.getMemoryCacheSize()));

        //用于配置Glide的Bitmap缓存池，默认配置是LruBitmapPool
        builder.setBitmapPool(new LruBitmapPool(sizeCalculator.getBitmapPoolSize()));

        //用于配置Glide的硬盘缓存策略，默认配置是InternalCacheDiskCacheFactory
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "GlideImgCache", 150 * 1024 * 1024));
//        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "GlideImgCache", 150 * 1024 * 1024));

        //配置图片缓存格式
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888));
//        ViewTarget.setTagId(R.id.glide_tag_id);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        OkHttpClient okHttpClient = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return super.isManifestParsingEnabled();
    }
}
