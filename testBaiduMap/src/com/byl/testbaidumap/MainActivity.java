package com.byl.testbaidumap;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置
 * 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 *
 */
public class MainActivity extends Activity {
	
	
    private  EditText txtAddr;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	//public BMapManager mBMapManager=null;
	
	public MyApplication app;
	
	//定位图层
	locationOverlay myLocationOverlay = null;
	//弹出泡泡图层
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText = null;//泡泡view
	private View viewCache = null;
	
	//地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
	public MyLocationMapView mMapView = null;	// 地图View
	private MapController mMapController = null;
	private MKSearch mMKSearch = null;//用于信息检索服务 

	//UI相关
	OnCheckedChangeListener radioButtonListener = null;
	TextView requestLocButton ,btSerach;
	boolean isRequest = false;//是否手动触发请求定位
	boolean isFirstLoc = true;//是否首次定位
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        app = (MyApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(MyApplication.strKey,new MyApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_main);
        
       	txtAddr=(EditText)findViewById(R.id.txtAddr);//关键字输入框
       	
      //监听搜索单击事件
       	btSerach= (TextView)findViewById(R.id.btOk);
    	btSerach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMKSearch.poiSearchInCity("", txtAddr.getText().toString());  					
			}
		});
        //定位按钮
        requestLocButton = (TextView)findViewById(R.id.btget);
	    requestLocButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestLocClick();
			}
		});
	    
		//地图初始化
        mMapView = (MyLocationMapView)findViewById(R.id.bmapView);
        mMapView.setTraffic(true);//设置地图模式为交通视图(也可为卫星视图) 
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(15);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
      //信息检索初始化
    	mMKSearch = new MKSearch(); 
		mMKSearch.init(app.mBMapManager, new MKSearchListener() {
			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}

			@Override
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				if (error == MKEvent.ERROR_RESULT_NOT_FOUND) {
					Toast.makeText(MainActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				} else if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "搜索出错啦..",
							Toast.LENGTH_LONG).show();
					return;
				}
				PoiOverlay poiOverlay = new PoiOverlay(MainActivity.this,
						mMapView);
				poiOverlay.setData(res.getAllPoi());
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(poiOverlay);
				mMapView.refresh();
				for (MKPoiInfo info : res.getAllPoi()) {
					if (info.pt != null) {
						mMapView.getController().animateTo(info.pt);
						break;
					}
				}
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
			}
		});
		createPaopao();
        //定位初始化
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.disableCache(false);//禁止启用缓存定位
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
       
        //定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		//设置定位数据
	    myLocationOverlay.setData(locData);
	    //添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		//修改定位数据后刷新图层生效
		mMapView.refresh();
		
    }
    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(MainActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
    }
    
    /**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
        MyLocationMapView.pop = pop;
	}
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            locData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(locData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            //是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc){
            	//移动地图到定位点
            	//Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
            }
            //首次定位完成
            isFirstLoc = false;
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    //继承MyLocationOverlay重写dispatchTap实现点击处理
  	public class locationOverlay extends MyLocationOverlay{
  		public locationOverlay(MapView mapView) {
  			super(mapView);
  		}
  		@Override
  		protected boolean dispatchTap() {
  			//处理点击事件,弹出泡泡
  			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText(mLocClient.getLastKnownLocation().getAddrStr());
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),8);
			return true;
  		}
  		
  	}

    @Override
    protected void onPause() {
        mMapView.onPause();
        if(app.mBMapManager!=null){  
        	app.mBMapManager.stop();  
     } 
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        if(app.mBMapManager!=null){  
        	app.mBMapManager.start();  
    }
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        if(app.mBMapManager!=null){  
        	app.mBMapManager.destroy();  
        	app.mBMapManager=null;  
    }  
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
