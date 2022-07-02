http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0823/3353.html
http://www.jianshu.com/p/31c82862ef19
   Tip:
        自定义 ImageView 时设置占位的话，有的图片第一次加载的时候只显示占位图
                                    方案一: 不设置占位
                                    方案二：使用Glide的Transformation API自定义圆形Bitmap的转换
                                    方案三：使用下面的代码加载图片
                                    Glide.with(mContext)
                                        .load(url)
                                        .placeholder(R.drawable.loading_spinner)
                                        .into(new SimpleTarget<Bitmap>(width, height) {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                                // setImageBitmap(bitmap) on CircleImageView
                                            }
                                        };

        异常：You cannot start a load for a destroyed activity
        原因: 不要再非主线程里面使用Glide加载图片，如果真的使用了，请把context参数换成getApplicationContext



 参数                             说明
.load(String string)            string可以为一个文件路径、uri或者url
.load(Uri uri)	                uri类型
.load(File file)	            文件
.load(Integer resourceId)	    资源Id,R.drawable.xxx或者R.mipmap.xxx
.load(byte[] model)	            byte[]类型
.load(T model)	                自定义类型

Demo:
Glide.with(context)
    .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
    .into(ivImg);
自定义类型 Demo:

model：你加载的数据源
width：你加载的图片的宽度(px)
height：你加载的图片的高度(px)

Glide.with(this)
     .using(new MyGlideUrlLoader(this)) //使用我们自己的ModeLoader
     .load(new MyGlideDataModel() {     //加载我们自定义的数据源
          @Override
          public String buildUrl(int width, int height) {
              if (width >= 600) {
                  return url1;
              } else {
                  return url2;
                }
          }
      })
     .into(imageView);


     Glide.with(context)
                     .load(mediaBean.getTinygif().getPreview())
                     .asGif()// 加载到 PhotoView 会有问题
                     .asBitmap()
                     .diskCacheStrategy(DiskCacheStrategy.NONE)//NONE 跳过硬盘缓存
                     .skipMemoryCache(true)//true 跳过内存缓存
                     .placeholder(R.mipmap.ic_launcher)//加载占位符
                     .error(R.mipmap.ic_launcher)//错误占位符
                     .crossFade()//淡入显示 默认300
                     .dontAnimate()//不显示任何淡入淡出效果
                     .override (100, 100)//显示到 ImageView之前重新改变图片大小
                     .centerCrop()
                     .fitCenter()
                     .priority(Priority.HIGH)
                     .thumbnail(0.1f)
                     .into(holder.imageView);
             Glide.get(context).clearMemory();
             Glide.get(context).clearDiskCache();

