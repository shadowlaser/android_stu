package com.byl.testbaidumap;



import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


public class MyApplication extends Application {
	
    private static MyApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "58byjGtcTxxOBRBCYBGCj8dt";
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static MyApplication getInstance() {
		return mInstance;
	}
	
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                //Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "���������������",Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//����ֵ��ʾkey��֤δͨ��
            if (iError != 0) {
                //��ȨKey����
//                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
//                        "���� DemoApplication.java�ļ�������ȷ����ȨKey,������������������Ƿ�������error: "+iError, Toast.LENGTH_LONG).show();
//                DemoApplication.getInstance().m_bKeyRight = false;
                MyApplication.getInstance().m_bKeyRight = true;
            }
            else{
            	MyApplication.getInstance().m_bKeyRight = true;
            	//Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "key��֤�ɹ�", Toast.LENGTH_LONG).show();
            }
        }
    }
}