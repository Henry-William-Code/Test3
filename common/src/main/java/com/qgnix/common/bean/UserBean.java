package com.qgnix.common.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.annotation.JSONField;
import com.qgnix.common.R;
import com.qgnix.common.utils.WordUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by cxf on 2017/8/14.
 */

public class UserBean implements Parcelable {

    protected String id;
    protected String userNiceName;
    protected String realName;
    protected String avatar;
    protected String avatarThumb;
    protected int sex;
    protected String signature;
    protected String coin;
    protected String votes;
    protected String consumption;
    protected String votestotal;
    protected String province;
    protected String city;
    protected String birthday;
    protected int level;
    protected int levelAnchor;
    protected int lives;
    protected int follows;
    protected int fans;
    protected String fenxiao;//推广人数
    protected Vip vip;
    protected Liang liang;
    protected Car car;
    private String iszhubo;
    private int tie_card_state;
    private int invite_state;
    private String tie_card_reward;
    /**
     * 最后一次登录时间
     */
    private String last_login_time;
    /**
     * 注册时间
     */
    private String create_time;
    /**
     * 联系电话
     */
    private String mobile;

    private String country;
    /**
     * 语言
     */
    private int lan;

    /**
     * 充值等级
     */
    private ChargeLevel chargelevel;

    public ChargeLevel getChargelevel() {
        return chargelevel;
    }

    public void setChargelevel(ChargeLevel chargelevel) {
        this.chargelevel = chargelevel;
    }

    public int getLan() {
        return lan;
    }

    public void setLan(int lan) {
        this.lan = lan;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getTie_card_state() {
        return tie_card_state;
    }

    public void setTie_card_state(int tie_card_state) {
        this.tie_card_state = tie_card_state;
    }

    public int getInvite_state() {
        return invite_state;
    }

    public void setInvite_state(int invite_state) {
        this.invite_state = invite_state;
    }

    public String getIszhubo() {
        return iszhubo == null ? "-" : iszhubo;
    }

    public void setIszhubo(String iszhubo) {
        this.iszhubo = iszhubo;
    }

    public String getTie_card_reward() {
        return tie_card_reward == null ? "0" : tie_card_reward;
    }

    public void setTie_card_reward(String tie_card_reward) {
        this.tie_card_reward = tie_card_reward;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "user_nicename")
    public String getUserNiceName() {
        return userNiceName;
    }
    @JSONField(name = "real_name")
    public String getRealName() {
        return realName;
    }

    @JSONField(name = "real_name")
    public void setRealName(String realName) {
        this.realName = realName;
    }


    @JSONField(name = "user_nicename")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return avatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getLevel() {
        if (level == 0) {
            level = 1;
        }
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(name = "level_anchor")
    public int getLevelAnchor() {
        return levelAnchor;
    }

    @JSONField(name = "level_anchor")
    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getFenxiao() {
        return fenxiao;
    }

    public void setFenxiao(String fenxiao) {
        this.fenxiao = fenxiao;
    }

    public Vip getVip() {
        return vip;
    }

    public void setVip(Vip vip) {
        this.vip = vip;
    }

    public Liang getLiang() {
        return liang;
    }

    public void setLiang(Liang liang) {
        this.liang = liang;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * 显示靓号
     */
    public String getLiangNameTip() {
        if (this.liang != null) {
            String liangName = this.liang.getName();
            if (!TextUtils.isEmpty(liangName) && !"0".equals(liangName)) {
                return WordUtil.getString(R.string.live_liang) + ":" + liangName;
            }
        }
        return "ID:" + this.id;
    }

    /**
     * 获取靓号
     */
    public String getGoodName() {
        if (this.liang != null) {
            return this.liang.getName();
        }
        return "0";
    }

    public int getVipType() {
        if (this.vip != null) {
            return this.vip.getType();
        }
        return 0;
    }


    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.id = in.readString();
        this.userNiceName = in.readString();
        this.avatar = in.readString();
        this.avatarThumb = in.readString();
        this.sex = in.readInt();
        this.signature = in.readString();
        this.coin = in.readString();
        this.votes = in.readString();
        this.consumption = in.readString();
        this.votestotal = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.birthday = in.readString();
        this.level = in.readInt();
        this.levelAnchor = in.readInt();
        this.lives = in.readInt();
        this.follows = in.readInt();
        this.fans = in.readInt();
        this.country = in.readString();
        this.lan = in.readInt();
        this.fenxiao = in.readString();
        this.vip = in.readParcelable(Vip.class.getClassLoader());
        this.liang = in.readParcelable(Liang.class.getClassLoader());
        this.car = in.readParcelable(Car.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userNiceName);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarThumb);
        dest.writeInt(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.coin);
        dest.writeString(this.votes);
        dest.writeString(this.consumption);
        dest.writeString(this.votestotal);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.birthday);
        dest.writeInt(this.level);
        dest.writeInt(this.levelAnchor);
        dest.writeInt(this.lives);
        dest.writeInt(this.follows);
        dest.writeInt(this.fans);
        dest.writeString(this.country);
        dest.writeInt(this.lan);
        dest.writeString(this.fenxiao);
        dest.writeParcelable(this.vip, flags);
        dest.writeParcelable(this.liang, flags);
        dest.writeParcelable(this.car, flags);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }
    };


    public static class Vip implements Parcelable {
        protected int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Vip() {

        }

        public Vip(Parcel in) {
            this.type = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
        }

        public static final Creator<Vip> CREATOR = new Creator<Vip>() {
            @Override
            public Vip[] newArray(int size) {
                return new Vip[size];
            }

            @Override
            public Vip createFromParcel(Parcel in) {
                return new Vip(in);
            }
        };
    }

    public static class Liang implements Parcelable {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Liang() {

        }

        public Liang(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<Liang> CREATOR = new Creator<Liang>() {

            @Override
            public Liang createFromParcel(Parcel in) {
                return new Liang(in);
            }

            @Override
            public Liang[] newArray(int size) {
                return new Liang[size];
            }
        };

    }

    public static class Car implements Parcelable {
        protected int id;
        protected String swf;
        protected float swftime;
        protected String words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        public float getSwftime() {
            return swftime;
        }

        public void setSwftime(float swftime) {
            this.swftime = swftime;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public Car() {

        }

        public Car(Parcel in) {
            this.id = in.readInt();
            this.swf = in.readString();
            this.swftime = in.readFloat();
            this.words = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.swf);
            dest.writeFloat(this.swftime);
            dest.writeString(this.words);
        }


        public static final Creator<Car> CREATOR = new Creator<Car>() {
            @Override
            public Car[] newArray(int size) {
                return new Car[size];
            }

            @Override
            public Car createFromParcel(Parcel in) {
                return new Car(in);
            }
        };

    }

    /**
     * 充值等级
     */
    public static class ChargeLevel {
        private int levelid;

        public int getLevelid() {
            return levelid;
        }

        public void setLevelid(int levelid) {
            this.levelid = levelid;
        }
    }







    //===========================code=================================//


    private static Random random = new Random();
    private static int current =0;
    private static int random_int =0;

    public static int getRandom(int length){


        while(current==random_int){
            random_int =  random.nextInt(length);
        }
        current = random_int;
        return current;
    }
    //随机整数
    public static Integer[] randomArr(int max,int randomSize)
    {

        try {
            if(max<randomSize){
                randomSize=max;
            }
            Random random = new Random();
            // Object[] values = new Object[randomSize];
            HashSet<Integer> hashSet = new LinkedHashSet<Integer>();

            // 生成随机数字并存入HashSet
            while(hashSet.size() < randomSize){
                hashSet.add(random.nextInt(max) + 1);
            }
            Integer[] its=new Integer[randomSize];
            Iterator<Integer> it=hashSet.iterator();
            int i=0;
            while(it.hasNext()){
                its[i]=it.next();
                i++;
            }
            return its;

        } catch (Exception e) {

        }
        return new Integer[]{} ;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * pxתdp
     * @param id
     * @param context
     * @return
     */
    public static int getDimenT(int id,Context context) {
        try {
            return (int) (id * getScreenDensity(context));
        } catch (Exception e) {
            return 0;
        }
    }
    public static void checkUpdate(final Context activity,final boolean flag) {

    }

    private  static  int[] colors = { };
    //===========================================================//

}
