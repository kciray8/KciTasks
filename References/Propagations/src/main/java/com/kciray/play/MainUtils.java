package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Component
public class MainUtils {
    @Autowired
    MainUtils2 mainUtils2;

    @Transactional(propagation = Propagation.REQUIRED)
    public void check(){
        System.out.println("check");

        mandatoryTransaction();
        mainUtils2.spawnNew();

        notFromTranaction();

        notSupported();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatoryTransaction(){
        System.out.println("mandatoryTransaction");
    }


    @Transactional(propagation = Propagation.NEVER)
    public void notFromTranaction(){
        System.out.println("not");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notSupported(){
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
    }
}
