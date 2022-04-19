package com.qgnix.main.constant;

/**
 * 常量类
 */
public interface Constants {


    /**
     * 常量
     */
    interface Constant {


    }

    /**
     * 参数
     */
    interface Params {


    }

    /**
     * SharedPreference 使用 key
     */
    interface SPKey {


    }

    /**
     * 页面参数
     */
    interface LayoutParamsKey {


        /**
         * fragment类型key
         */
        String KEY_FRAGMENT_TYPE_1 = "fragment_type_1";

        /**
         * fragment类型key2
         */
        String KEY_FRAGMENT_TYPE_2 = "fragment_type_2";


    }

    /**
     * fragment页面类型
     */
    interface FragmentTypeKey {




        interface SportScheduleGameFragment {

            /**
             * 今天
             */
            int KEY_INDEX_TODAY = 0;

            /**
             * 足球
             */
            int KEY_INDEX_FOOTBALL = 1;

            /**
             * 网球
             */
            int KEY_INDEX_TENNIS = 2;

            /**
             * 排球
             */
            int KEY_INDEX_VOLLEYBALL = 3;

            /**
             * 板球
             */
            int KEY_INDEX_CRICKET = 4;


        }
    }


}
