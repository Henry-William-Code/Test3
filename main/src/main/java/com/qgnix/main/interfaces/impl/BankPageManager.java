package com.qgnix.main.interfaces.impl;

import com.qgnix.main.interfaces.BankPageListener;

public class BankPageManager {
    private BankPageManager mInstance;

    private BankPageListener mBankPageListener;

    private BankPageManager(){

    }

    public BankPageManager getInstance(){
        synchronized (this) {
            if (mInstance == null) {
                mInstance = new BankPageManager();
            }
        }
        return mInstance;
    }

    public void setBankPageListener(BankPageListener bankPageListener){
        mBankPageListener = bankPageListener;
    }

    public BankPageListener getBankPageListener(){
        if(mBankPageListener==null){
            mBankPageListener = new EmptyBankPageListener();
        }
        return mBankPageListener;
    }


    class EmptyBankPageListener implements BankPageListener{

        @Override
        public void onCommitListener() {

        }

        @Override
        public void onSkipListener() {

        }
    }



}
