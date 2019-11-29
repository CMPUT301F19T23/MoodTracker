package com.example.moodtracker.recycle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * This deals with storing or loading images
 */

public class ImageLoader
{
	/**
	 * the core of lru cache
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * thread
	 */
	private ExecutorService mThreadPool;
	/**
	 * set default value of number of thread
	 */
	private int mThreadCount = 1;
	/**
	 * scheduler of the queue
	 */
	private Type mType = Type.LIFO;
	/**
	 * task queue
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * line of polling
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHander;

	/**
	 * handler running in the UI thread that
	 * sets up the image for the ImageView
	 */
	private Handler mHandler;

	/**
	 * introduce a semaphore with a value of 1
	 * to prevent the mPoolThreadHander
	 * from being initialized
	 */
	private volatile Semaphore mSemaphore = new Semaphore(1);

	/**
	 * Introduce a semaphore with a value of 1,
	 * since the thread pool also has a value of 1.
	 */
	private volatile Semaphore mPoolSemaphore;

	private static ImageLoader mInstance;

	/**
	 *The scheduler of the queue
	 */
	public enum Type
	{
		FIFO, LIFO
	}


	/**
	 * Singleton gets the instance
	 * 
	 * @return
	 */
	public static ImageLoader getInstance()
	{

		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(1, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	private ImageLoader(int threadCount, Type type)
	{
		init(threadCount, type);
	}

	private void init(int threadCount, Type type)
	{
		// loop thread
		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					// asks for semaphore sign
					mSemaphore.acquire();
				} catch (InterruptedException e)
				{
				}
				Looper.prepare();

				mPoolThreadHander = new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						mThreadPool.execute(getTask());
						try
						{
							mPoolSemaphore.acquire();
						} catch (InterruptedException e)
						{
						}
					}
				};
				// release semaphore sign
				mSemaphore.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		// Get the application to free memory
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			};
		};

		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemaphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;

	}

	/**
	 * upload image
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView)
	{
		// set tag
		imageView.setTag(path);
		// UI thread
		if (mHandler == null)
		{
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path))
					{
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null)
		{
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			mHandler.sendMessage(message);
		} else
		{
			addTask(new Runnable()
			{
				@Override
				public void run()
				{

					ImageSize imageSize = getImageViewWidth(imageView);

					int reqWidth = imageSize.width;
					int reqHeight = imageSize.height;

					Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
							reqHeight);
					addBitmapToLruCache(path, bm);
					ImgBeanHolder holder = new ImgBeanHolder();
					holder.bitmap = getBitmapFromLruCache(path);
					holder.imageView = imageView;
					holder.path = path;
					Message message = Message.obtain();
					message.obj = holder;
					// Log.e("TAG", "mHandler.sendMessage(message);");
					mHandler.sendMessage(message);
					mPoolSemaphore.release();
				}
			});
		}

	}
	
	/**
	 * add task
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable)
	{
		try
		{

			if (mPoolThreadHander == null)
				mSemaphore.acquire();
		} catch (InterruptedException e)
		{
		}
		mTasks.add(runnable);
		mPoolThreadHander.sendEmptyMessage(0x110);
	}

	/**
	 * get task
	 * 
	 * @return
	 */
	private synchronized Runnable getTask()
	{
		if (mType == Type.FIFO)
		{
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO)
		{
			return mTasks.removeLast();
		}
		return null;
	}
	
	/**
	 * singleton gets the instance pair
	 * 
	 * @return
	 */
	public static ImageLoader getInstance(int threadCount, Type type)
	{

		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}


	/**
	 * Get the appropriate compression
	 * width and based on the ImageView
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * Get an image from the LruCache
	 * and return the null if it doesn't exist
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		return mLruCache.get(key);
	}

	/**
	 * Add a picture to the LruCache
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromLruCache(key) == null)
		{
			if (bitmap != null)
				mLruCache.put(key, bitmap);
		}
	}

	/**
	 * Compute the inSampleSize to
	 * compress the diagram
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight)
	{
		// The width of the source image
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight)
		{
			// Calculate the ratio of the actual width to the target width
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * According to the calculated inSampleSize,
	 * the compressed image is obtained
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight)
	{
		// First parse set inJustDecodeBounds to true to get the picture size
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// Call the method defined above to compute the inSampleSize table
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Parse the graph again with the obtained inSampleSize value
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		
		return bitmap;
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	private class ImageSize
	{
		int width;
		int height;
	}

	/**
	 * Reflection gets the maximum width and height set by the ImageView
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;

				Log.e("TAG", value + "");
			}
		} catch (Exception e)
		{
		}
		return value;
	}

}
