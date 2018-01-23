package com.hanvon.canvasdemo.view;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by pc on 2017/12/29.
 */

//public class Singleton {
//    private static Singleton instance = null;
//    private Singleton(){};
//
//    public static Singleton getInstance(){
//        if (instance == null){
//            synchronized (Singleton.class){
//                if (instance == null){
//                    instance = new Singleton();
//                }
//            }
//        }
//        return instance;
//    }
//
//}


//public class Singleton{
//    private static Singleton instance = null;
//    private Singleton(){};
//    public static synchronized Singleton getInstance(){
//        if (instance == null){
//            instance = new Singleton();
//        }
//        return instance;
//    }
//}

//public class Singleton{
//    private Singleton(){};
//    public static Singleton getInstance(){
//        return SingletonHolder.instance;
//    }
//    private static class SingletonHolder{
//        private static final Singleton instance = new Singleton();
//    }
//}

//public enum  Singleton{
//    INSTANCE;
//    public void dosomething(){
//
//    }
//}

public class Singleton{
    private static HashMap<String, Object> objMap = new HashMap<String, Object>();
    private Singleton(){};
    public static void registeInstance(String key, Object instance){
        if (!objMap.containsKey(key)){
            objMap.put(key, instance);
        }
    }
    public static Object getInstance(String key){
        return objMap.get(key);
    }
}


