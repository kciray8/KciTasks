package com.kciray.play;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class MyTransactionManager extends DataSourceTransactionManager {
    int i;

    /*protected Object doGetTransaction() throws TransactionException {
        i++;
        return "Transation #" + i;
    }*/

    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        System.out.println("doBegin");
        System.out.println(transaction);
        System.out.println(definition);

        System.out.println("Propagation: " + definition.getPropagationBehavior());
    }

    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        System.out.println("doCommit");
    }

    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        System.out.println("doRollback");
    }
    /*public TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
        System.out.println("getTransaction" + definition);
        return null;
    }

    public void commit(TransactionStatus status) throws TransactionException {
        System.out.println("commit");
    }

    public void rollback(TransactionStatus status) throws TransactionException {
        System.out.println("rollback");
    }*/



}
