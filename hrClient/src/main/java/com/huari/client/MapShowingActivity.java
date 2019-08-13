package com.huari.client;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.huari.tools.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MapShowingActivity extends Activity {

    PopupWindow pw;
    ListView maplistview;
    ArrayAdapter<String> maplistAdatapter;
    ArrayList<String> stationlist;
    boolean show = false;
    // MapView mv;

    // BMapManager mBMapMan = null;
    // MapView mMapView = null;
    // private MyOverlay mOverlay = null;
    // private OverlayItem mCurItem = null;
    // private PopupOverlay pop = null;
    // private int prepoint=-1;
    // private GeoPoint[] geopointarray;
    // private GeoPoint[] stationsGeoPoint;
    // private int length=1024;//数组geopointarray的长度
    // private int i;//数组索引
    // private int extra=512;//当原数组满后，给数组再扩增extra个长度
    // private Symbol symbol;
    // private Geometry geometry;
    // private GraphicsOverlay graphicsOverlay,targraphicsoverlay,tgol1,tgol2;
    // private boolean popisshowing;
    // private PopupWindow popupwindow;
    // private View content,parentview;
    // private boolean pwshowing;
    // private boolean genzong=false;
    // private Button clearButton;
    // private CheckBox genzongbox;
    // 定位相关
    // LocationClient mLocClient=null;
    // LocationData locData = null;
    // //定位图层
    // MyLocationOverlay myLocationOverlay = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * overlay 位置坐标
     */
    double mLon1 = 103.546492;
    double mLat1 = 31.018786;
    double mLon2 = 104.025277;
    double mLat2 = 30.664444;
    double mLon3 = 102.53345;
    double mLat3 = 31.01304;
    double mLon4 = 104.401394;
    double mLat4 = 30.706965;

    double tlon5 = 103.035213;
    double tlat5 = 29.825891;

    // ground overlay
    // private GroundOverlay mGroundOverlay;
    // private Ground mGround;
    // private double mLon5 = 104.380338;
    // private double mLat5 = 30.92235;
    // private double mLon6 = 104.414977;
    // private double mLat6 = 30.947246;
    // MapController mMapController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mBMapMan=new BMapManager(getApplication());
        // mBMapMan.init("auLtyeWQcUaYyiGG51iaj7cn", null);
        // 注意：请在试用setContentView前初始化BMapManager对象，否则会报错

        setContentView(R.layout.activity_map_showing);
        // mv=(MapView)findViewById(R.id.bmapsView);

        maplistview = (ListView) getLayoutInflater().inflate(
                R.layout.mapselectlistview, null);
        stationlist = new ArrayList<String>();
        stationlist.add("桃花岛");
        stationlist.add("光明顶");
        stationlist.add("真武殿");
        stationlist.add("藏经阁");
        maplistAdatapter = new ArrayAdapter<String>(MapShowingActivity.this,
                R.layout.stationitem, stationlist);
        maplistview.setAdapter(maplistAdatapter);
        pw = new PopupWindow(maplistview, 200,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        SysApplication.getInstance().addActivity(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int twidth = metric.widthPixels;
        int theight = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;
        double dui = Math.sqrt(twidth * twidth + theight * theight);
        double x = dui / densityDpi;
        if (x >= 6.5) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.titlebar);
        // LinearLayout l=(LinearLayout)findViewById(R.id.unititlebar);
        // TextView t=(TextView)l.findViewById(R.id.titlename);
        // t.setText("地图显示");
        // content=(View)getLayoutInflater().inflate(R.layout.mappopuviewcontent,
        // null);
        // clearButton=(Button)content.findViewById(R.id.mapbutton);
        // genzongbox=(CheckBox)content.findViewById(R.id.mapcheckbox);
        // parentview=(View)getLayoutInflater().inflate(R.layout.activity_map_showing,
        // null);
        // popupwindow = new PopupWindow(content,
        // LinearLayout.LayoutParams.MATCH_PARENT,
        // LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // popupwindow.setOutsideTouchable(true);
        // popupwindow.setFocusable(false);
        // popupwindow.setContentView(content);
        //
        // mMapView=(MapView)findViewById(R.id.bmapsView);
        // mMapView.setBuiltInZoomControls(true);
        // //设置启用内置的缩放控件
        // mMapController=mMapView.getController();
        // // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        // GeoPoint point =new GeoPoint((int)(mLat2*1E6),(int)(mLon2*1E6));
        // ini();
        // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        // GeoPoint point1=new GeoPoint((int)(30.678*1E6), (int)(105.054*1E6));
        // mMapController.setCenter(point);//设置地图中心点
        // mMapController.setZoom(12);//设置地图zoom级别
        //
        //
        // mLocClient.registerLocationListener( myListener );
        // LocationClientOption option = new LocationClientOption();
        // option.setOpenGps(true);//打开gps
        // option.setCoorType("bd09ll"); //设置坐标类型
        // option.setScanSpan(3000);
        // mLocClient.setLocOption(option);
        // mLocClient.start();
        //
        // //定位图层初始化
        // myLocationOverlay = new MyLocationOverlay(mMapView);
        // //设置定位数据
        // myLocationOverlay.setData(locData);
        // //添加定位图层
        // mMapView.getOverlays().add(myLocationOverlay);
        // myLocationOverlay.enableCompass();
        // mLocClient.requestLocation();
        // //修改定位数据后刷新图层生效
        // mMapView.refresh();
        //
        // clearButton.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // cleargenzong();
        // }
        // });
        // genzongbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        // if(isChecked)
        // {
        // genzong=true;
        // }
        // else
        // {
        // genzong=false;
        // cleargenzong( );
        // }
        // }
        // });
        //
    }

    private void ini() {
        // mOverlay = new
        // MyOverlay(getResources().getDrawable(R.drawable.icon_mark),mMapView);
        // geopointarray=new GeoPoint[length];
        // //定位初始化
        // mLocClient = new LocationClient( getApplicationContext() );
        // locData = new LocationData();
        // symbol=new Symbol();
        // Symbol.Color lineColor =symbol.new Color();
        // lineColor.red = 65;
        // lineColor.green = 153;
        // lineColor.blue = 225;
        // lineColor.alpha = 180;
        // symbol.setLineSymbol(lineColor, 5);
        // //geometry=new Geometry();
        // graphicsOverlay=new GraphicsOverlay(mMapView);
        // targraphicsoverlay=new GraphicsOverlay(mMapView);
        // tgol1=new GraphicsOverlay(mMapView);
        // tgol2=new GraphicsOverlay(mMapView);
        // mMapView.getOverlays().add(graphicsOverlay);
        // mMapView.getOverlays().add(targraphicsoverlay);
        // mMapView.getOverlays().add(tgol1);
        // mMapView.getOverlays().add(tgol2);
        //
        //
        // GeoPoint p1 = new GeoPoint ((int)(mLat1*1E6),(int)(mLon1*1E6));
        // OverlayItem item1 = new OverlayItem(p1,"青城山站","");
        // /**
        // * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
        // */
        // item1.setMarker(getResources().getDrawable(R.drawable.icon_mark));
        //
        // GeoPoint p2 = new GeoPoint ((int)(mLat2*1E6),(int)(mLon2*1E6));
        // OverlayItem item2 = new OverlayItem(p2,"杜甫草堂站","");
        // item2.setMarker(getResources().getDrawable(R.drawable.icon_mark));
        //
        // GeoPoint p3 = new GeoPoint ((int)(mLat3*1E6),(int)(mLon3*1E6));
        // OverlayItem item3 = new OverlayItem(p3,"四姑娘山站","");
        // item3.setMarker(getResources().getDrawable(R.drawable.icon_mark));
        //
        //
        // GeoPoint tar=new GeoPoint ((int)(tlat5*1E6),(int)(tlon5*1E6));
        // OverlayItem taritem = new OverlayItem(tar,"信号源","");
        // taritem.setMarker(getResources().getDrawable(R.drawable.target));
        // if(GlobalData.stationHashMap.size()>0)
        // {
        // stationsGeoPoint=new GeoPoint[GlobalData.stationHashMap.size()];
        // for(String s:GlobalData.stationHashMap.keySet())
        // {
        // Station stationtemp=GlobalData.stationHashMap.get(s);
        // GeoPoint ptemp = new GeoPoint
        // ((int)(stationtemp.lan*1E6),(int)(stationtemp.lon*1E6));
        // OverlayItem itemtemp = new OverlayItem(ptemp,stationtemp.name,"");
        // itemtemp.setMarker(getResources().getDrawable(R.drawable.icon_mark));
        // mOverlay.addItem(itemtemp);
        // }
        // }
        //
        // mOverlay.addItem(item1);
        // mOverlay.addItem(item2);
        // mOverlay.addItem(item3);
        // mOverlay.addItem(taritem);
        //
        // Symbol sy=new Symbol();
        // Symbol.Color tarColor =symbol.new Color();
        // tarColor.red = 165;
        // tarColor.green = 13;
        // tarColor.blue = 25;
        // tarColor.alpha = 180;
        // sy.setLineSymbol(tarColor, 5);
        //
        // Geometry gt1=new Geometry();
        // GeoPoint[] g1=new GeoPoint[]{p1,tar};
        // gt1.setPolyLine(g1);
        // Graphic graphic1=new Graphic(gt1, sy);
        // targraphicsoverlay.setData(graphic1);
        //
        // Geometry gt2=new Geometry();
        // GeoPoint[] g2=new GeoPoint[]{p2,tar};
        // gt2.setPolyLine(g2);
        // Graphic graphic2=new Graphic(gt2, sy);
        // tgol1.setData(graphic2);
        //
        // Geometry gt3=new Geometry();
        // GeoPoint[] g3=new GeoPoint[]{p3,tar};
        // gt3.setPolyLine(g3);
        // Graphic graphic3=new Graphic(gt3, sy);
        // tgol2.setData(graphic3);
        //
        //
        // mMapView.getOverlays().add(mOverlay);
        //
        // mMapView.refresh();
        //
        //
        //
        // PopupClickListener popListener = new PopupClickListener(){
        // @Override
        // public void onClickedPopup(int index) {
        //
        // }
        // };
        // pop = new PopupOverlay(mMapView,popListener);
    }

    private void cleargenzong() {
        // i=0;
        // geopointarray=new GeoPoint[length];
        // // if(mMapView.getOverlays().contains(graphicsOverlay))
        // // {
        // // mMapView.getOverlays().remove(graphicsOverlay);
        // // }
        // graphicsOverlay.removeAll();
        // mMapView.refresh();

    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            // locData.latitude = location.getLatitude();
            // locData.longitude = location.getLongitude();
            // //如果不显示定位精度圈，将accuracy赋值为0即可
            // locData.accuracy = location.getRadius();
            // // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            // locData.direction = location.getDerect();
            // //更新定位数据
            // myLocationOverlay.setData(locData);
            // if(genzong==true)
            // {
            // GeoPoint geopoint=new GeoPoint((int)(locData.latitude*1E6),
            // (int)(locData.longitude*1E6));
            // if(i==length)
            // {
            // GeoPoint[] temparray=new GeoPoint[length+extra];
            // System.arraycopy(geopointarray, 0, temparray, 0, length);
            // geopointarray=temparray;
            // length=length+extra;
            // System.gc();
            // }
            // geopointarray[i]=geopoint;
            // i++;
            // GeoPoint[] temp=new GeoPoint[i];
            // System.arraycopy(geopointarray, 0,temp, 0, i);
            // Geometry geometry=new Geometry();
            // geometry.setPolyLine(temp);
            // Graphic graphic=new Graphic(geometry, symbol);
            // graphicsOverlay.setData(graphic);
            // }
            // //更新图层数据执行刷新后生效
            // mMapView.refresh();
        }

        public void onConnectHotSpotMessage(String var1, int var2) {

        }

    }

    // public class MyOverlay extends ItemizedOverlay{
    //
    // public MyOverlay(Drawable defaultMarker, MapView mapView) {
    // super(defaultMarker, mapView);
    // }
    //
    // @Override
    // public boolean onTap(int index){
    // if(index!=prepoint||(index==prepoint&&popisshowing==false))
    // {
    // OverlayItem item = getItem(index);
    // mCurItem = item ;
    // View view=getLayoutInflater().inflate(R.layout.detailstationinfo, null);
    // TextView name=(TextView)view.findViewById(R.id.stationname);
    // TextView jingdu=(TextView)view.findViewById(R.id.jingdu);
    // TextView weidu=(TextView)view.findViewById(R.id.weidu);
    // TextView moreinfo=(TextView)view.findViewById(R.id.moreinfo);
    // name.setText(mCurItem.getTitle());
    // jingdu.setText("经度："+mCurItem.getPoint().getLongitudeE6()/1E6);
    // weidu.setText("纬度："+mCurItem.getPoint().getLatitudeE6()/1E6);
    // moreinfo.setText(mCurItem.getSnippet());
    // pop.showPopup(view,item.getPoint(),32);
    // popisshowing=true;
    // prepoint=index;
    // }
    // else
    // {
    // pop.hidePop();
    // popisshowing=false;
    // prepoint=index;
    // }
    // return true;
    // }
    //
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baidumap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if(item.getItemId()==R.id.genzong)
        // {
        // if(genzong==false)
        // {
        // genzong=true;
        // item.setTitle("取消跟踪");
        // }
        // else
        // {
        // genzong=false;
        // cleargenzong();
        // item.setTitle("点击跟踪");
        // }
        // }
        // else if(item.getItemId()==R.id.qinglinggenzong)
        // {
        // cleargenzong( );
        // }
        // else if(item.getItemId()==R.id.selectstation)
        // {
        // if(show==false)
        // {
        // //pw.showAsDropDown(mMapView);
        // pw.showAtLocation(mv, Gravity.TOP|Gravity.RIGHT,
        // 0,getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop());
        // show=true;
        // }
        // else
        // {
        // pw.dismiss();
        // show=false;
        // }
        // }
        //
        return true;
    }

}
