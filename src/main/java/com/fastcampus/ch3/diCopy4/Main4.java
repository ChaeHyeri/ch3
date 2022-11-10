package com.fastcampus.ch3.diCopy4;

import com.google.common.reflect.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
class Car {
    @Resource
    Engine engine;
    @Resource Door door;

    @Override
    public String toString() {
        return "Car{" +
                "engine=" + engine +
                ", door=" + door +
                '}';
    }
}
@Component class SportsCar extends Car{}
@Component class Truck extends Car {}
@Component class Engine {}
@Component class Door{}

class AppContext {
    Map map ; // 객체 저장소

    AppContext() {
        map = new HashMap();
        doComponentScan();
        doAutoWired();
        doResource();
}

    private void doResource() {
        try {
            for(Object bean : map.values()){
                for(Field fld : bean.getClass().getDeclaredFields()){
                    if(fld.getAnnotation(Resource.class)!=null)
                        fld.set(bean, getBean(fld.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Map에 저장된 객체의 iv 중에 @AutoWired가 붙어있으면
    //Map에서 iv의 타입에 맞는 객체를 찾아서 연결
    private void doAutoWired() {
        try {
            for(Object bean : map.values()){
                for(Field fld : bean.getClass().getDeclaredFields()){
                    if(fld.getAnnotation(Autowired.class)!=null)
                        fld.set(bean, getBean(fld.getType()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doComponentScan() {
        try {
            // 패키지 내의 클래스 목록을 가져와 set에 저장
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);
            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy4");
            System.out.println("AppContext Instance Create + Set Classes");

            // @Component 클래스 확인
            for(ClassPath.ClassInfo classInfo : set) {
                Class clazz = classInfo.load();
                Component component = (Component)clazz.getAnnotation(Component.class);
                // @Component가 붙어있는 클래스의 객체를 생성하고 Map에 저장.
                if(component != null){
                    String id = StringUtils.uncapitalize(classInfo.getSimpleName());
                    map.put(id, clazz.newInstance());
                }
            }
            System.out.println("map="+map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        Object getBean(String key) {
            System.out.println("called getBean(String key)");
            return map.get(key);}

        Object getBean(Class clazz){
            System.out.println("called getBean(Class clazz)");
        for(Object obj : map.values()){
            if(clazz.isInstance(obj))
                return obj;
        }
        return null;
    }
}
    public class Main4 {
    public static void main(String[] args) throws Exception {
        AppContext ac = new AppContext();
        Car car = (Car) ac.getBean("car");     //byName
        Engine engine = (Engine) ac.getBean("engine");
        Door door = (Door)ac.getBean("door");
        Door door2 = (Door)ac.getBean(Door.class);   //byType
//
//        car.engine = engine;
//        car.door = door;

        System.out.println("car = " + car);
        System.out.println("engine = " + engine);
        System.out.println("door = " + door);
        System.out.println("door2 = " + door2);
    }
}
