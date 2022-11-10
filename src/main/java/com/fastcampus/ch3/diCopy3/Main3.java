package com.fastcampus.ch3.diCopy3;

import com.google.common.reflect.ClassPath;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component
class Car {}
@Component
class SportsCar extends Car{}
@Component
class Truck extends Car {}

class Engine {}

class AppContext {
    Map map ; // 객체 저장소

    AppContext() {
        map = new HashMap();
        doComponentScan();


}

    private void doComponentScan() {
        try {
            // 패키지 내의 클래스 목록을 가져와 set에 저장
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);
            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy3");

            // 반복문으로 클래스를 하나씩 읽어들여 @Component가 붙은 클래스 확인
            for(ClassPath.ClassInfo classInfo : set) {
                Class clazz = classInfo.load();
                Component component = (Component)clazz.getAnnotation(Component.class);

                // @Component가 붙어있는 클래스의 객체를 생성하고 Map에 저장.
                if(component != null){
                    String id = StringUtils.uncapitalize(classInfo.getSimpleName());
                    map.put(id, clazz.newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        Object getBean(String key) {return map.get(key);}
}

    public class Main3 {
    public static void main(String[] args) throws Exception {
        AppContext ac = new AppContext();
        Car car = (Car) ac.getBean("car");
        Engine engine = (Engine) ac.getBean("engine");
        System.out.println("car = " + car);
        System.out.println("engine = " + engine);
    }
}
