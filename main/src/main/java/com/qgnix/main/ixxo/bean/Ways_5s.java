/**
 * This is about Ways_4.java
 * 
 * @author liuray
 * @date 2015-1-17
 * @deprecated 
 */
package com.qgnix.main.ixxo.bean;

public class Ways_5s implements IWays{
    private static byte[][] ways ;
     
     public Ways_5s(){
    	 if(ways==null){
    	 ways = new byte[][]{
    	    	 {2,3,4},//0
    	    	 {2,4,5},//1
    	    	 {2,3,5},//2
    	    	 {2,4,5},//3
    	    	 {3,5},//4
    	    	 {1,3},//5
    	    	 {0,1,2,3,4},//6
    	    	 {2,3,4,5},//7
    	    	 {0,1,5},//8
    	    	 {2,3,4},//9
    	    	 {0,3,4,5},//10
    	    	 {3,4},//11
    	    	 {0,1,2,4},//12
    	    	 {0,1,5},//13
    	    	 {0},//14
    	    	 {1,3},//15
    	    	 {0,1,2,3},//16
    	    	 {0,5},//17
    	    	 {1,2,3,4},//18
    	    	 {0,1,2,3,4,5},//19
    	    	 {4,5},//20
    	    	 {2,3,4},//21 
    	    	 {2,3,4,5},//22
    	    	 {0,5},//23
    	    	 {0,2,3},//24
    	    	 {3,4,5},//25
    	    	 {1,2,3},//26
    	    	 {0,1,2,3,4,5},//27
    	    	 {0,1,4,5},//28
    	    	 {1,2,3},//29
    	    	 {0,1,2,3,4,5},//30
    	    	 {0,4,5},//31
    	    	 {3},//32
    	    	 {0,1,2,3},//33
    	    	 {0,5},//34
    	    	 {0,1,3},//35 
    	    	 {0,1,2},//36
    	    	 {3,4,5},//37
    	    	 {0,1,2},//38
    	    	 {0,1,5},//39
    	    	 {2,3,4},//40
    	    	 {0,3,4,5},//41
    	    	 {0},//42
    	    	 {0},//43
    	    	 {1,2,3,4},//44
    	    	 {0,3,4,5},//45
    	    	 {2,3,4},//46
    	    	 {1,5},//47
    	    	 {0,1,2},//48
    	    	 {0,2,5},//49
    	    	 {1,3},//50
    	    	 {0,1,3,4},//51
    	    	 {0,1,2,4},//52
    	    	 {0,3,4,5},//53
    	    	 {2,3},//54
    	    	 {4,5},//55
    	    	 {0,1},//56
    	    	 {0,1},//57
    	    	 {1,2},//58 
    	    	 {0,2,5},//59
    	    	 {0,1,5}//60 
    	     };}
     }
 
	 
	@Override
	public byte[][] getWays() {
		return ways;
	}
     
}

