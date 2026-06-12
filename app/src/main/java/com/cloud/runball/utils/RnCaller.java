package com.cloud.runball.utils;


//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.ReactContextBaseJavaModule;
//import com.facebook.react.bridge.ReactMethod;


/**
 * rn调用java工具类
 * 在rn中通过  NativeModules.RnCaller.open()  调用原生方法
 *
 * Boolean -> Bool
 * Integer -> Number
 * Double -> Number
 * Float -> Number
 * String -> String
 * Callback -> function
 * ReadableMap -> Object
 * ReadableArray -> Array
 */
public class RnCaller  {  //extends ReactContextBaseJavaModule
    /**
    public RnCaller(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //方法注释，会直接映射
    @ReactMethod
    public void open() {
        Toast.makeText(getReactApplicationContext(), "rn调用java工具类", Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    public void startNewActivity(String name) {
        try{
            Activity activity = getCurrentActivity();
            if (activity instanceof MainActivity) {
                Intent it=new Intent(getCurrentActivity(),Class.forName(name));
                ((MainActivity) activity).startActivity(it);
            }
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "RnCaller";
    }
    **/
}
