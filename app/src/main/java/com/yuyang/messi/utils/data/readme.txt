https://github.com/ZhaoKaiQiang/AndroidDifficultAnalysis/blob/master/06.%E7%BC%93%E5%AD%98%E6%96%87%E4%BB%B6%E5%8F%AF%E4%BB%A5%E6%94%BE%E5%9C%A8%E5%93%AA%E9%87%8C%EF%BC%9F%E5%AE%83%E4%BB%AC%E5%90%84%E8%87%AA%E7%9A%84%E7%89%B9%E7%82%B9%E6%98%AF%E4%BB%80%E4%B9%88%EF%BC%9F.md#私有存储
缓存文件可以放在哪里？它们各自的特点是什么？

在Android手机里面，缓存的位置分为两类，一类是Internal Storage，即内部存储，另外一类是External Storage，即外部存储。

比较老的手机，有一个手机内部存储，还有一个SD卡存储，就是分别对应这两种存储位置，因为以前的SD卡是可以扩展的，即可拆卸的，所以可以用是否可拆卸作为内外存储的分类标准。

但是现在最新的设备，比如小米、锤子、华为等，都取消了可拆卸的SD卡，直接与机身焊接在一起，分为16G、32G版本，所以现在内外存储的分类不再以是否可拆卸作为标准，而是以下面的几方面作为新的标准：

内部存储

总是可用的
这里的文件默认是只能被你的app所访问的。
当用户卸载你的app的时候，系统会把internal里面的相关文件都清除干净。
Internal是在你想确保不被用户与其他app所访问的最佳存储区域。
外部存储

并不总是可用的，因为用户可以选择把这部分作为USB存储模式，这样就不可以访问了。
是大家都可以访问的，因此保存到这里的文件是失去访问控制权限的。
当用户卸载你的app时，系统仅仅会删除external根目录（getExternalFilesDir()）下的相关文件。
External是在你不需要严格的访问权限并且你希望这些文件能够被其他app所共享或者是允许用户通过电脑访问时的最佳存储区域。
文件位置

内部存储

getFileDir() 通过此方法可以获取到你的APP内部存储的文件，路径为/data/data/pacgage_name/files
getCacheDir() 通过此方法可以获取到你的APP内部存储的文件，路径为/data/data/package_name/cache
openFileOutput() 通过此方法，我们可以获取到一个输出流，输出流的保存路径是/data/data/package_name/files ，和getFileDir()的路径一致
外部存储

私有存储

Context.getExternalCacheDir()
Context.getExternalFilesDir()
创建的私有文件的地址是/sdcard/Android/date/package_name下面，Android文件夹是隐藏文件夹，用户无法操作。

如果我们想缓存图片等比较耗空间的文件，推荐放在getExternalCacheDir()所在的文件下面，这个文件和getCacheDir()很像，都可以放缓存文件，在APP被卸载的时候，都会被系统删除，而且缓存的内容对其他APP是相对私有的。

公共存储

你的APP产生的文件不需要隐藏，即对用户是可见的，那么你可以把文件放在外部的公共存储文件下面。这个方法不是Context的方法，而是Environment的两个方法，第一个方法获取到的其实是外部存储的根目录，而第二个方法获取到得则是外部存储的公共目录。其实在访问权限上是没有区别的，不同点是getExternalStoragePublicDirectory()在运行的时候，会需要你带有一个特定的参数来指定这些public的文件类型，以便于与其他public文件进行分类。

Environment.getExternalStorageDirectory()
Environment.getExternalStoragePublicDirectory()
表现

内部存储

你的app的internal storage 目录是以你的app的包名作为标识存放在Android文件系统的特定目录下[data/data/com.example.xx]。 从技术上讲，如果你设置文件为可读的，那么其他app就可以读取你的internal文件。然而，其他app需要知道你的包名与文件名。若是你没有设置为可读或者可写，其他app是没有办法读写的。因此只要你使用MODE_PRIVATE ，那么这些文件就不可能被其他app所访问。

另外记住一点，内部存储在你的APP卸载的时候，会一块被删除，因此，我们可以在cache目录里面放置我们的图片缓存，而且cache与files的差别在于，如果手机的内部存储空间不够了，会自行选择cache目录进行删除，因此，不要把重要的文件放在cache文件里面，可以放置在files里面，因为这个文件只有在APP被卸载的时候才会被删除。还有要注意的一点是，如果应用程序是更新操作，内部存储不会被删除，区别于被用户手动卸载。

外部存储

不管你是使用 getExternalStoragePublicDirectory() 来存储可以共享的文件，还是使用 getExternalFilesDir() 来储存那些对于你的app来说是私有的文件，有一点很重要，那就是你要使用那些类似DIRECTORY_PICTURES 的API的常量。那些目录类型参数可以确保那些文件被系统正确的对待。例如，那些以DIRECTORY_RINGTONES 类型保存的文件就会被系统的media scanner认为是ringtone而不是音乐。

清除数据、清除缓存的区别

清除数据主要是清除用户配置，比如SharedPreferences、数据库等等，这些数据都是在程序运行过程中保存的用户配置信息，清除数据后，下次进入程序就和第一次进入程序时一样
缓存是程序运行时的临时存储空间，它可以存放从网络下载的临时图片，从用户的角度出发清除缓存对用户并没有太大的影响，但是清除缓存后用户再次使用该APP时，由于本地缓存已经被清理，所有的数据需要重新从网络上获取。为了在清除缓存的时候能够正常清除与应用相关的缓存，请将缓存文件存放在getCacheDir()或者 getExternalCacheDir()路径下。