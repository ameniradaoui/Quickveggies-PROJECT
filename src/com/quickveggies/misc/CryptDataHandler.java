package com.quickveggies.misc;
 
import java.util.Random;


import com.quickveggies.entities.User;
import com.quickveggies.impl.IUserUtils;
 
public class CryptDataHandler {
     
    public static CryptDataHandler instance=null;
    private static IUserUtils userDao ;
     
    public static CryptDataHandler getInstance(){
        if(instance==null)return new CryptDataHandler();
        return instance;
    }
     
    private CryptDataHandler(){}
     
    private char randomSymbol(){
        Random r = new Random();
        String alphabet = "GFDGDFxyzghjkfxszd";
        return alphabet.charAt(r.nextInt(alphabet.length()));
    }
     
    public String encrypt(String str){
        String result="";
        for(int ind=0; ind<str.length(); ind++){
            int numVal=(int)((str.charAt(ind))*5);
            if(numVal<10){result+=0;result+=numVal;}
            else result+=numVal;
            result+=randomSymbol();
        }
        return result;
    }
     
    public String decrypt(String str){
        String result="";
        String selection=null;
        for(int ind=0; ind<str.length()/4; ind++){
            selection=str.substring(4*ind, 4*ind+3);
            char charVal=(char)(Integer.parseInt(selection)/5);
            result+=charVal;
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
    	
        User user = userDao.getUserByName("demo");
    	System.out.println(CryptDataHandler.getInstance().decrypt(user.getPassword()));
    }
}