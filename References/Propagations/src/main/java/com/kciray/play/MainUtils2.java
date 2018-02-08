package com.kciray.play;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MainUtils2 {
    @Transactional(propagation = Propagation.NESTED)
    public void spawnNew(){
        System.out.println("spawnNew()");
    }
}
