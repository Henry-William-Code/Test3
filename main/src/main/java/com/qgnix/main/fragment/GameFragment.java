package com.qgnix.main.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.model.Response;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.JsonBean;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.bean.TransferBean;
import com.qgnix.live.dialog.QutotaDialog;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.main.R;
import com.qgnix.main.activity.BettingSelectActivity;
import com.qgnix.main.adapter.MainGameChildAdapter;
import com.qgnix.main.adapter.MainGameMenuAdapter;
import com.qgnix.main.bean.GameChildEntry;
import com.qgnix.main.bean.GameMenuEntry;
import com.qgnix.common.bean.LoginBean;
import com.qgnix.main.bean.HomeGameBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 游戏fragment
 */
public class GameFragment extends BaseFragment {
    /**
     * 游戏菜单
     */
    private RecyclerView mRvGameMenu;
    /**
     * 游戏
     */
    private RecyclerView mRvGame;

    /**
     * 菜单适配器
     */
    private MainGameMenuAdapter mMenuAdapter;
    /**
     * 菜单数据
     */
    private final List<GameMenuEntry> mMenus = new ArrayList<>();

    /**
     * 游戏
     */
    private MainGameChildAdapter mGameChildAdapter;
    /**
     * 游戏数据
     */
    private final List<GameChildEntry> mGames = new ArrayList<>();

    /**
     * 游戏平台类型
     */
    private String mPlatType;
    /**
     * 游戏类型
     */
    private String mGameType;

    /**
     * 游戏编码
     */
    private String mGameCode;

    /**
     * 子品牌等
     */
    private String mProviderType;

    /**
     * 是否item点击
     */
    private boolean mIsItemClicked;
    /**
     * 页码
     */
    private int mPage = 1;
    /**
     * loading dialog
     */
    private Dialog dialog;

    //头部view
    private CardView rvHeader;
    //游戏类型,游戏币,转入
    private TextView tvGameCoin, tvBanlance, tvTransfer;
    //用户信息
    private UserBean userBean;
//    private TransferBean currentTransfer;

    private SmartRefreshLayout mRefresh;
//    private List<TransferBean> list = new ArrayList<>();

    private HashMap<String, TransferBean> mBalances = new HashMap();

    public static GameFragment newInstance() {
        return new GameFragment();
    }


    @Override
    public void initData( Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_game;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mRvGameMenu = view.findViewById(R.id.rv_game_menu);
        mRvGame = view.findViewById(R.id.rv_game);
        mRefresh = view.findViewById(com.qgnix.live.R.id.refresh);
        rvHeader = view.findViewById(R.id.rv_header);
        tvGameCoin = view.findViewById(R.id.tv_game_coin);
        tvBanlance = view.findViewById(R.id.tv_banlance);
        tvTransfer = view.findViewById(R.id.tv_transfer);
        tvTransfer.setOnClickListener(this);
    }

    @Override
    public void doBusiness() {


//        loadBalance();
        // 菜单
        mRvGameMenu.setLayoutManager(new LinearLayoutManager(mContext));
        mMenuAdapter = new MainGameMenuAdapter(mContext, mMenus);
        mMenuAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for (int i = 0, size = mMenus.size(); i < size; i++) {
                    if (i == position) {
                        mMenus.get(i).setSelect(true);
                    } else {
                        mMenus.get(i).setSelect(false);
                    }
                    if (position == 0 || position == 1) {
                        rvHeader.setVisibility(View.GONE);
                    } else {
                        rvHeader.setVisibility(View.VISIBLE);
                    }
                }

                mMenuAdapter.notifyDataSetChanged();
                // 获取游戏数据
                GameMenuEntry menuEntry = mMenus.get(position);
                mGameType = menuEntry.getGame_type();
                mGameCode = menuEntry.getGame_code();
                mPlatType = menuEntry.getPlat_type();
                mProviderType = menuEntry.getProvider_type();
                mPage = 1;
                getGameData(menuEntry.getPlat_type());
                if (position != 0 && position != 1) {
                    //获取游戏金额
                    getBalance();
                    loadUserData();
                }
//                getCoin();

            }
        });
        mRvGameMenu.setAdapter(mMenuAdapter);

        // 游戏
        mRvGame.setLayoutManager(new GridLayoutManager(mContext, 3));
        mGameChildAdapter = new MainGameChildAdapter(mContext, mGames);
        mGameChildAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (null == dialog) {
                    dialog = DialogUtil.loadingDialog(mContext, "Loading...");
                    dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if (null != dialog && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                return true;
                            }
                            return false;
                        }
                    });

                } else {
                    if (null != dialog && !dialog.isShowing()) {
                        dialog.show();
                    }
                }

                GameChildEntry bean = mGames.get(position);
                if ("cp".equals(mPlatType)) {
                    // 彩票
                    TicketData ticketData = new TicketData();
                    ticketData.setTitle(bean.getTitle());
                    ticketData.setImage(bean.getIcon());
                    ticketData.setId(bean.getId());
                    ticketData.setType(bean.getType());
                    Intent intent = new Intent(mContext, BettingSelectActivity.class);
                    intent.putExtra("data", new Gson().toJson(ticketData));
                    mContext.startActivity(intent);
                } else {
                    // 其他
//                    registerGame(bean.getGame_code(),bean.getGame_type(), bean.getPlat_type());
                    loginGame(bean.getGame_code(), bean.getGame_type(), bean.getPlat_type());
                }
            }
        });
        mRvGame.setAdapter(mGameChildAdapter);

        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (!"cp".equals(mPlatType)) {
                    ++mPage;
                    getData(true);
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (!"cp".equals(mPlatType)) {
                    mPage = 1;
                    getData(false);
                }

            }
        });


        // 获取菜单数据
        MainHttpUtil.getNgCategory(new CommonCallback<String>() {
            @Override
            public void callback(String info) {
                List<GameMenuEntry> menuEntries = JSON.parseArray(info, GameMenuEntry.class);
                mMenus.clear();
                if(CommonAppConfig.getInstance().getSwitchPlay()) {
                    for (GameMenuEntry gameBean : menuEntries) {
                        if ("Lottery".equals(gameBean.getTitle())) {
                            mMenus.add(gameBean);
                        } else {
                           // mMenus.add(gameBean);
                        }
                    }
                }else {
                    mMenus.addAll(menuEntries);
                }
                //mMenus.addAll(menuEntries);
                if(mMenus==null||mMenus.size()==0){
                    return;
                }
                GameMenuEntry menuEntry = mMenus.get(0);
                menuEntry.setSelect(true);
                if (null != mMenuAdapter) {
                    mMenuAdapter.notifyDataSetChanged();
                }

                //获取第一个菜单游戏数据
                mGameType = menuEntry.getGame_type();
                mGameCode = menuEntry.getGame_code();
                mProviderType = menuEntry.getProvider_type();
                getGameData(menuEntry.getPlat_type());
            }

        });

    }

//    private void getCoin() {
//        if (null != list && list.size() > 0) {
//            for (int i = 0; i < list.size(); i++) {
//                TransferBean transferBean = list.get(i);
//                if (transferBean.getPlat_type().equals(mPlatType)) {
//                    currentTransfer = transferBean;
//                    tvGameCoin.setText(mPlatType.toUpperCase() + " game coin: " + transferBean.getCoin());
//                    break;
//                } else {
//                    tvGameCoin.setText(mPlatType.toUpperCase() + " game coin: 0");
//                }
//            }
//        }
//    }

    /**
     * 获取用户信息
     */
    private void loadUserData() {
//        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
//            @Override
//            public void callback(UserBean bean) {
//                userBean = bean;
//                if (null != tvBanlance) {
//                    tvBanlance.setText("Cherry balance: " + userBean.getCoin() + " (COIN)");
//                }
//            }
//        });
        if (userBean == null) {
            CommonHttpUtil.getUserBrief(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    if (bean != null) {
                        userBean = bean;
                        if (null != tvBanlance) {
                            tvBanlance.setText("Cherry balance: " + userBean.getCoin() + " (COIN)");
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        new QutotaDialog(mContext, mBalances.get(mPlatType), 1, new QutotaDialog.DialogListener() {
            @Override
            public void dialogCallback(int code) {
                if (code == 1) {
//                    loadBalance();
                    mBalances.remove(mPlatType);
                    getBalance();
                    userBean = null;
                    loadUserData();
                }
            }
        }).show();

    }

    // 获取类别
//    private void loadBalance() {
//        final Dialog dialog = DialogUtil.loadingDialog(mContext);
//        dialog.show();
//        LiveHttpUtil.nGAllBalance(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                dialog.dismiss();
//                if (code != 0 || info.length == 0) {
//                    ToastUtil.show(msg);
//                    return;
//                }
//                List<TransferBean> data = JSON.parseArray(info[0], TransferBean.class);
//                list.clear();
//                list.addAll(data);
//                getCoin();
//            }
//
//            @Override
//            public void onError(Response<JsonBean> response) {
//                dialog.dismiss();
//            }
//        });
//    }

    /**
     * 获取游戏数据
     *
     * @param platType 游戏平台类型
     */
    private void getGameData(String platType) {
        mPlatType = platType;
        if ("cp".equals(platType)) {
            mRefresh.setEnableRefresh(false);
            mRefresh.setEnableLoadMore(false);
            String adasData = CacheData.getAdasData();
            if (!TextUtils.isEmpty(adasData)) {
                L.e("===本地获取数据===");
                handelData(adasData);
                return;
            }
            // 彩票
            MainHttpUtil.getTicketAdas(platType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code != 0 || info.length == 0) {
                        ToastUtil.show(msg);
                        return;
                    }
                    // 保存数据到本地
                    CacheData.setAdasData(info[0]);

                    handelData(info[0]);
                }
            });
        } else {
            mRefresh.setEnableRefresh(true);
            mRefresh.setEnableLoadMore(true);
            getData(false);
        }
    }

    /**
     * 其他游戏
     *
     * @param isRefresh
     */
    public void getData(final boolean isRefresh) {
        // 其他
        MainHttpUtil.getNgGameCode(mPlatType, mGameType, mGameCode, mProviderType, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || null == info || info.length == 0) {
                    if (!isRefresh) {
                        mGames.clear();
                    }
                    mGameChildAdapter.notifyDataSetChanged();
                    ToastUtil.show(msg);
                    return;
                }
                if (info[0] == null || "[]".equals(info[0])) {
                    if (!isRefresh) {
                        mGames.clear();
                    }
                    mGameChildAdapter.notifyDataSetChanged();
                    return;
                }
                JsonObject jsonObject = new JsonParser().parse(info[0]).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                List<GameChildEntry> list = JSON.parseArray(jsonArray.toString(), GameChildEntry.class);
                if (null == list || list.isEmpty()) {
                    mRefresh.setEnableLoadMore(false);
                }
                if (!isRefresh) {
                    mGames.clear();
                }
                mGames.addAll(list);
                mGameChildAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isRefresh) {
                    mRefresh.finishLoadMore();
                } else {
                    mRefresh.finishRefresh();
                }
            }
        });
    }

    /**
     * 处理彩票游戏
     *
     * @param data
     */
    private void handelData(String data) {
        List<GameChildEntry> list = JSON.parseArray(data, GameChildEntry.class);
        for (GameChildEntry gameChildEntry : list) {
            gameChildEntry.setImage(gameChildEntry.getIcon());
            gameChildEntry.setName(gameChildEntry.getTitle());
        }
        mGames.clear();
        mGames.addAll(list);
        if (null != mGameChildAdapter) {
            mGameChildAdapter.notifyDataSetChanged();
        }

    }


    /**
     * 注册游戏
     *
     * @param gameCode
     */
    private void registerGame(final String gameCode, final String gameType, final String platType) {
        MainHttpUtil.ngRegister(gameCode, gameType, platType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    if (null != dialog && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ToastUtil.show(msg);
                    return;
                }
                loginGame(gameCode, gameType, platType);
            }

            @Override
            public void onError() {
                super.onError();
            }
        });
    }

    /**
     * 登录游戏
     *
     * @param gameCode
     */
    private void loginGame(String gameCode, String gameType, final String platType) {
        CommonHttpUtil.ngLogin(platType, gameType, gameCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                LoginBean loginBean = new Gson().fromJson(info[0], LoginBean.class);
//                Intent intent = new Intent(mContext, BrowserActivity.class);
//                intent.putExtra(Constants.PARAM_URL, loginBean.getData());
//                intent.putExtra(Constants.PARAM_TITLE, "title");
//                intent.putExtra(Constants.PARAM_MODE, 0);
//                intent.putExtra(Constants.PARAM_PLAT, platType);
//                intent.putExtra(Constants.PARAM_BALANCE, userBean.getCoin());
//                intent.putExtra(Constants.PARAM_TRANSFER, "recommend".equals(platType) ? null : mBalances.get(platType));
//                intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
////                mContext.startActivity(intent);
//                startActivityForResult(intent, 100);
                RouteUtil.toGame(GameFragment.this, loginBean.getData(), platType, userBean.getCoin(), "recommend".equals(platType) ? null : mBalances.get(platType).getCoin());
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


    private void getBalance() {
        TransferBean transferBean = mBalances.get(mPlatType);
        if (transferBean != null) {
            tvGameCoin.setText(mPlatType.toUpperCase() + " game coin: " + transferBean.getCoin());
            return;
        }
        final Dialog dialog = DialogUtil.loadingDialog(mContext);
        dialog.show();
        MainHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                TransferBean [{"coin":"10","currency":"INR","plat_type":"bng","title":"Booongo & Playson"}]
                TransferBean transferBean = JSON.parseArray(info[0], TransferBean.class).get(0);
//                String coin = new JsonParser().parse(info[0]).getAsJsonArray().get(0).getAsJsonObject().get("coin").toString();
                mBalances.put(mPlatType, transferBean);
                if (null != tvBanlance) {
                    tvGameCoin.setText(mPlatType.toUpperCase() + " game coin: " + transferBean.getCoin());
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        }, mPlatType);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
//            loadBalance();
            mBalances.remove(mPlatType);
            getBalance();
            userBean = null;
            loadUserData();
        }
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.NG_CATEGORY);
        MainHttpUtil.cancel(MainHttpConsts.NG_GAME_CODE);
        MainHttpUtil.cancel(MainHttpConsts.HOME_GET_ADAS);
        MainHttpUtil.cancel(MainHttpConsts.NG_REGISTER);
        MainHttpUtil.cancel(CommonHttpConsts.NG_LOGIN);
        super.onDestroy();
    }
}