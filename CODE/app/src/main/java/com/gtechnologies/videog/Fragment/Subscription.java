package com.gtechnologies.videog.Fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gtechnologies.videog.Adapter.SubscriptionHistoryAdapter;
import com.gtechnologies.videog.Http.ContentApiClient;
import com.gtechnologies.videog.Http.ContentApiInterface;
import com.gtechnologies.videog.Interface.LanguageInterface;
import com.gtechnologies.videog.Interface.SubscriptionInterfacce;
import com.gtechnologies.videog.Library.KeyWord;
import com.gtechnologies.videog.Library.Utility;
import com.gtechnologies.videog.Model.Banglalink;
import com.gtechnologies.videog.Model.Plantext;
import com.gtechnologies.videog.Model.SubscriptionHistory;
import com.gtechnologies.videog.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Subscription extends Fragment implements SubscriptionInterfacce {
    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);


    LinearLayout llSubscribed;
    TextView tvMsisdnLabel, tvMsisdn, tvOperatorlabel, tvOperator, tvPrice, tvStatusLabel, tvStatus, tvPackage;
    Button btnAction;

    //Unsubscription Layout
    LinearLayout llUnsubscribed, llOption, otp_option;
    TextView tvNumberLabel, tvNumber, otp_msg;
    EditText etPhoneNumber, otp_number;
    Button btnCheck, btnSubmit, otp_submit;
    RadioGroup rgOperator;
    //RadioButton rbGp, rbRobi, rbBlink, rbGrBkash, rbOnBkash;
    RadioButton bl_daily, bl_week, bl_month;
    String mobile = "";

    List<SubscriptionHistory> historyList = new ArrayList<>();
    RecyclerView history_recycler;
    SubscriptionHistoryAdapter subscriptionHistoryAdapter;
    LinearLayout history_view;
    TextView history_title;


    Plantext plangp;
    Plantext planBanglalink;

    public Subscription() {
    }

    @SuppressLint("ValidFragment")
    public Subscription(Context context, LanguageInterface languageInterface) {
        this.context = context;
        utility = new Utility(this.context, this);
        //this.languageInterface = languageInterface;
        //bkash = utility.getBkashSubscription();
    }

    @SuppressLint("ValidFragment")
    public Subscription(Context context) {
        this.context = context;
        utility = new Utility(this.context);
        //this.languageInterface = languageInterface;
        //bkash = utility.getBkashSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);

        //Subscribed Layout
        llSubscribed = view.findViewById(R.id.ll_subscribed);
        tvMsisdnLabel = view.findViewById(R.id.tv_msisdn_label);
        tvMsisdn = view.findViewById(R.id.tv_msisdn);
        tvOperatorlabel = view.findViewById(R.id.tv_operator_label);
        tvOperator = view.findViewById(R.id.tv_operator);
        tvPrice = view.findViewById(R.id.tv_price);
        tvStatusLabel = view.findViewById(R.id.tv_status_label);
        tvStatus = view.findViewById(R.id.tv_status);
        tvPackage = view.findViewById(R.id.tv_package);
        btnAction = view.findViewById(R.id.btn_action);

        //Unsubscribed Layout
        llUnsubscribed = view.findViewById(R.id.ll_unsusbcribed);
        llOption = view.findViewById(R.id.ll_option);
        tvNumberLabel = view.findViewById(R.id.tv_number_label);
        tvNumber = view.findViewById(R.id.tv_number);
        etPhoneNumber = view.findViewById(R.id.et_phone_number);
        btnCheck = view.findViewById(R.id.btn_check);
        btnSubmit = view.findViewById(R.id.btn_submit);
        rgOperator = view.findViewById(R.id.rg_operator);
        //rbGp = (RadioButton) view.findViewById(R.id.rb_gp);
        bl_daily = view.findViewById(R.id.bl_daily);
        bl_week = view.findViewById(R.id.bl_week);
        bl_month = view.findViewById(R.id.bl_month);

        //otp view
        otp_option = view.findViewById(R.id.otp_option);
        otp_msg = view.findViewById(R.id.otp_msg);
        otp_number = view.findViewById(R.id.otp_number);
        otp_submit = view.findViewById(R.id.otp_submit);

        history_recycler = view.findViewById(R.id.recycler_history);
        history_view = view.findViewById(R.id.history_view);
        history_title = view.findViewById(R.id.hisotry_title);

        utility.setFonts(
                new View[]{
                        tvMsisdnLabel,
                        tvMsisdn,
                        tvOperatorlabel,
                        tvOperator,
                        tvPrice,
                        tvStatusLabel,
                        tvStatus,
                        tvPackage,
                        btnAction,
                        tvNumberLabel,
                        tvNumber,
                        etPhoneNumber,
                        btnCheck,
                        btnSubmit,
                        /*rbGp,*/
                        bl_daily,
                        bl_week,
                        bl_month,
                        otp_msg,
                        otp_number
                });
        initiateView();
        initial_history();

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!TextUtils.isEmpty(utility.getBkashSubscription().getMdn())) {
                        JSONObject pro_json = new JSONObject();
                        pro_json.put("mdn", utility.getBkashSubscription().getMdn());
                        unsubscribe(pro_json);
                    }
                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
            }
        });
        return view;
    }


    private void initiateView() {
        try {
            if (TextUtils.isEmpty(utility.getBkashSubscription().getMdn())) {
                getSubscriptionText();
                //unsubscribedView();
            } else {
                JSONObject pro_json = new JSONObject();
                pro_json.put("mdn", utility.getBkashSubscription().getMdn());
                String op = utility.checkOperators(utility.getBkashSubscription().getMdn());
                check_subscription(pro_json, op);
                if (planBanglalink == null) {
                    getSubscriptionText();
                }
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }


    private void unsubscribedView() {
        utility.hideAndShowView(new View[]{llSubscribed, llUnsubscribed}, llUnsubscribed);
        tvNumberLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
        btnCheck.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.check_subscription_bn) : context.getString(R.string.check_subscription_en));
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utility.hideKeyboard(view);
                String message = utility.validateMsisdn("88" + etPhoneNumber.getText().toString());
                if (message.equals("OK")) {
                    mobile = etPhoneNumber.getText().toString();
                    String op = utility.checkOperators("88" + mobile);
                    /*String[] operators = subscriptionBox.getOperators();
                    subscriptionBox.setApiCounter(0);
                    for (int i = 0; i < operators.length; i++) {
                        checkInitialSubscription("88" + etPhoneNumber.getText().toString(), operators[i]);
                    }*/
                    //utility.hideProgress();
                    //observInitialSubscriptionCheck();

                    try {
                        JSONObject pro_json = new JSONObject();
                        pro_json.put("mdn", "88" + mobile);
                        check_subscription(pro_json, op);
                    } catch (Exception e) {
                        Log.d("Error Line Number", Log.getStackTraceString(e));
                    }
                } else {
                    utility.showToast(message);
                }
            }
        });
    }

    private void showSubscribedView() {
        utility.logger("check sub" + utility.getBkashSubscription().toString());
        utility.hideAndShowView(new View[]{llUnsubscribed}, llSubscribed);
        tvStatusLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.service_status_bn) : context.getString(R.string.service_status_en));
        tvMsisdnLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.service_number_bn) : context.getString(R.string.service_number_en));
        tvOperatorlabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.package_bn) : context.getString(R.string.package_en));
        if (!TextUtils.isEmpty(utility.getBkashSubscription().getStatus())) {
            tvStatus.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.status_on_bn) : context.getString(R.string.status_on_en));
            String op = utility.checkOperators(utility.getBkashSubscription().getMdn());
            if (utility.getBkashSubscription().getStatus().equalsIgnoreCase(KeyWord.DAILY)) {
                if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_daily_pac_bn) : context.getString(R.string.gp_daily_pac_en));
                } else if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_daily_pac_bn) : context.getString(R.string.bl_daily_pac_en));
                } else {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_daily_pac_bn) : context.getString(R.string.bl_daily_pac_en));
                }
            } else if (utility.getBkashSubscription().getStatus().equalsIgnoreCase(KeyWord.WEEKLY)) {
                if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_weekly_pac_bn) : context.getString(R.string.gp_weekly_pac_en));
                } else if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_weekly_pac_bn) : context.getString(R.string.bl_weekly_pac_en));
                } else {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_weekly_pac_bn) : context.getString(R.string.bl_weekly_pac_en));
                }
                //tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.weekly_pac_bn) : context.getString(R.string.weekly_pac_en));
            } else if (utility.getBkashSubscription().getStatus().equalsIgnoreCase(KeyWord.MONTHLY)) {
                if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_monthly_pac_bn) : context.getString(R.string.gp_monthly_pac_en));
                } else if (op.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_monthly_pac_bn) : context.getString(R.string.bl_monthly_pac_en));
                } else {
                    tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_monthly_pac_bn) : context.getString(R.string.bl_monthly_pac_en));
                }
                //tvOperator.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.monthly_pac_bn) : context.getString(R.string.monthly_pac_en));
            }
        } else {
            tvStatus.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.status_off_bn) : context.getString(R.string.status_off_en));
        }
        tvMsisdn.setText(utility.getLangauge().equals("bn") ? utility.convertToBangle(utility.getBkashSubscription().getMdn()) : utility.getBkashSubscription().getMdn());
        tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.rg_monthly_bn) : context.getString(R.string.rg_monthly_en));
        tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.bk_on_message_bn).replace("\n", "") : getString(R.string.price_msg_en) + "\n" + getString(R.string.bk_on_message_en).replace("\n", ""));
        btnAction.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.button_off_bn) : context.getString(R.string.button_off_en));
    }

    private void observInitialSubscriptionCheck(final String oper) {
        llSubscribed.setVisibility(View.GONE);
        llOption.setVisibility(View.VISIBLE);
        utility.hideAndShowView(new View[]{etPhoneNumber, btnCheck}, tvNumber);
        tvNumberLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_otp_bn) : context.getString(R.string.number_msg_otp_en));
        tvNumber.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
        //rbGp.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_message_bn) : context.getString(R.string.gp_message_en));
        if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
            if (planBanglalink.getPlans().get(0) != null) {
                bl_daily.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(0).getTextplanBn()) : planBanglalink.getPlans().get(0).getTextplan());
            }
            if (planBanglalink.getPlans().get(1) != null) {
                bl_week.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(1).getTextplanBn()) : planBanglalink.getPlans().get(1).getTextplan());
            }
            if (planBanglalink.getPlans().get(2) != null) {
                bl_month.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(2).getTextplanBn()) : planBanglalink.getPlans().get(2).getTextplan());
            }
            /*bl_daily.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_rb_daily_bn) : context.getString(R.string.bl_rb_daily_en));
            bl_week.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_rb_week_bn) : context.getString(R.string.bl_rb_week_en));
            bl_month.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_rb_month_bn) : context.getString(R.string.bl_rb_month_en));*/
        } else if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
            if (plangp.getPlans().get(0) != null) {
                bl_daily.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(0).getTextplanBn()) : plangp.getPlans().get(0).getTextplan());
            }
            if (plangp.getPlans().get(1) != null) {
                bl_week.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(1).getTextplanBn()) : plangp.getPlans().get(1).getTextplan());
            }
            if (plangp.getPlans().get(2) != null) {
                bl_month.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(2).getTextplanBn()) : plangp.getPlans().get(2).getTextplan());
            }
            /*bl_daily.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_daily_message_bn) : context.getString(R.string.gp_daily_message_en));
            bl_week.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_weekly_message_bn) : context.getString(R.string.gp_weekly_message_en));
            bl_month.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_month_message_bn) : context.getString(R.string.gp_month_message_en));*/
        } else {
            if (planBanglalink.getPlans().get(0) != null) {
                bl_daily.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(0).getTextplanBn()) : planBanglalink.getPlans().get(0).getTextplan());
            }
            if (planBanglalink.getPlans().get(1) != null) {
                bl_week.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(1).getTextplanBn()) : planBanglalink.getPlans().get(1).getTextplan());
            }
            if (planBanglalink.getPlans().get(2) != null) {
                bl_month.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(2).getTextplanBn()) : planBanglalink.getPlans().get(2).getTextplan());
            }
        }
        btnSubmit.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.submit_bn) : context.getString(R.string.submit_en));
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utility.hideKeyboard(view);
                if (rgOperator.getCheckedRadioButtonId() != -1) {
                    RadioButton rb = llOption.findViewById(rgOperator.getCheckedRadioButtonId());
                    if (rb.getTag().toString().equals(context.getString(R.string.tag_daily))) {
                        if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(0).getTextplanBn()) : planBanglalink.getPlans().get(0).getTextplan(), rb.getTag().toString(), oper);
                        } else if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(0).getTextplanBn()) : plangp.getPlans().get(0).getTextplan(), rb.getTag().toString(), oper);
                        } else {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(0).getTextplanBn()) : planBanglalink.getPlans().get(0).getTextplan(), rb.getTag().toString(), oper);
                        }
                    } else if (rb.getTag().toString().equals(context.getString(R.string.tag_weekly))) {
                        if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(1).getTextplanBn()) : planBanglalink.getPlans().get(1).getTextplan(), rb.getTag().toString(), oper);
                        } else if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(1).getTextplanBn()) : plangp.getPlans().get(1).getTextplan(), rb.getTag().toString(), oper);
                        } else {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(1).getTextplanBn()) : planBanglalink.getPlans().get(1).getTextplan(), rb.getTag().toString(), oper);
                        }
                    } else if (rb.getTag().toString().equals(context.getString(R.string.tag_monthly))) {
                        if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(2).getTextplanBn()) : planBanglalink.getPlans().get(2).getTextplan(), rb.getTag().toString(), oper);
                        } else if (oper.equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(2).getTextplanBn()) : plangp.getPlans().get(2).getTextplan(), rb.getTag().toString(), oper);
                        } else {
                            showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(2).getTextplanBn()) : planBanglalink.getPlans().get(2).getTextplan(), rb.getTag().toString(), oper);
                        }
                    }
                } else {
                    utility.showToast("Please choose operator");
                }
            }
        });
    }

    private void otp_view(final String op) {
        llOption.setVisibility(View.GONE);
        otp_option.setVisibility(View.VISIBLE);
        tvNumberLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_otp_verify_bn) : context.getString(R.string.number_msg_otp_verify_en));
        otp_msg.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.otp_msg_bn) : context.getString(R.string.otp_msg_en));
        otp_submit.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.submit_bn) : context.getString(R.string.submit_en));
        otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(otp_number.getText().toString()) && !TextUtils.isEmpty(mobile)) {
                    try {
                        utility.hideKeyboard(view);
                        JSONObject pro_json = new JSONObject();
                        pro_json.put("mdn", "88" + mobile);
                        pro_json.put("pin", otp_number.getText().toString());
                        if (rgOperator.getCheckedRadioButtonId() != -1) {
                            RadioButton rb = llOption.findViewById(rgOperator.getCheckedRadioButtonId());
                            if (rb.getTag().toString().equals(context.getString(R.string.tag_daily))) {
                                pro_json.put("pkg", "1");
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_weekly))) {
                                pro_json.put("pkg", "7");
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_monthly))) {
                                pro_json.put("pkg", "30");
                            }
                            verify_pin(pro_json, op);
                        } else {
                            utility.showToast("Please choose operator");
                        }
                    } catch (Exception e) {
                        Log.d("Error Line Number", Log.getStackTraceString(e));
                    }
                } else {
                    utility.showToast(getResources().getString(R.string.otp_msg_en));
                }
            }
        });
    }

    public void showConfirmation(String message, final String tag, final String op) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout parentLayout = dialog.findViewById(R.id.parent_layout);
            TextView title = dialog.findViewById(R.id.tv_title);
            TextView number = dialog.findViewById(R.id.tv_number);
            TextView plan = dialog.findViewById(R.id.tv_plan);
            Button btnNo = dialog.findViewById(R.id.btn_no);
            Button btnYes = dialog.findViewById(R.id.btn_yes);
            ViewGroup.LayoutParams params = parentLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            parentLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, number, plan, btnNo, btnYes});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.subscription_confirmation_message_bn) : context.getString(R.string.subscription_confirmation_message_en));
            number.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
            plan.setText(message);
            btnNo.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
            btnYes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (utility.isNetworkAvailable()) {
                        try {
                            if (!TextUtils.isEmpty(mobile)) {
                                JSONObject pro_json = new JSONObject();
                                pro_json.put("mdn", "88" + mobile);
                                if (tag.equalsIgnoreCase(context.getString(R.string.tag_daily))) {
                                    pro_json.put("pkg", "1");
                                } else if (tag.equalsIgnoreCase(context.getString(R.string.tag_weekly))) {
                                    pro_json.put("pkg", "7");
                                } else if (tag.equalsIgnoreCase(context.getString(R.string.tag_monthly))) {
                                    pro_json.put("pkg", "30");
                                }
                                gen_pin(pro_json, op);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }
                    } else {
                        utility.showToast("No Internet");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }


    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void viewStatus() {

    }

    @Override
    public void numberSet() {
        utility.showToast("Paisi");
        utility.logger("Check done");
    }


    private void check_subscription(final JSONObject jsonObject, final String ope) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Log.d("Number Verification", jsonObject.toString());
            Call<ResponseBody> call = apiInterface.getblsubscriptionstatus(context.getString(R.string.authorization_key), ope, requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String check = response.body().string();
                            Log.d("Number Verification", check);
                            if (check.trim().equalsIgnoreCase("notSubscribed")) {
                                if (!TextUtils.isEmpty(mobile)) {
                                    Log.d("Number check", "First Time");
                                    observInitialSubscriptionCheck(ope);
                                } else if (!TextUtils.isEmpty(utility.getBkashSubscription().getMdn())) {
                                    Log.d("Number check", "Second Time");
                                    utility.setBkashSubscriptionclear();
                                    initiateView();
                                } else {
                                    utility.logger("not working");
                                }
                            } else if (check.trim().equalsIgnoreCase(KeyWord.DAILY) || check.trim().equalsIgnoreCase(KeyWord.WEEKLY) || check.trim().equalsIgnoreCase(KeyWord.MONTHLY)) {
                                if (!TextUtils.isEmpty(mobile)) {
                                    utility.setBkashSubscription(new Banglalink("88" + mobile, check.trim()));
                                }
                                showSubscribedView();
                            } else {
                                utility.logger("no response" + check);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }

                        check_subscribe_history(jsonObject);
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    private void gen_pin(JSONObject jsonObject, final String op) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Log.d("Code sent", jsonObject.toString() + " op" + op);
            Call<ResponseBody> call = apiInterface.genblpin(context.getString(R.string.authorization_key), op, requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String check = response.body().string();
                            Log.d("Code Sent", check);
                            if (check.trim().equalsIgnoreCase("PINSent")) {
                                utility.showToast(context.getResources().getString(R.string.otp_success_bn));
                                otp_view(op);
                            } else {
                                utility.showToast(check);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }


                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    private void verify_pin(final JSONObject jsonObject, String op) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Log.d("Code verify", jsonObject.toString());
            Call<ResponseBody> call = apiInterface.verifyblpin(context.getString(R.string.authorization_key), op, requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String check = response.body().string();
                            Log.d("Code verify", check);
                            if (check.trim().equalsIgnoreCase("UserNotVarified")) {
                                utility.showToast(check);
                            } else if (check.trim().equalsIgnoreCase(KeyWord.DAILY) || check.trim().equalsIgnoreCase(KeyWord.WEEKLY) || check.trim().equalsIgnoreCase(KeyWord.MONTHLY)) {
                                otp_option.setVisibility(View.GONE);
                                utility.setBkashSubscription(new Banglalink("88" + mobile, check.trim()));
                                showSubscribedView();
                                check_subscribe_history(jsonObject);
                            } else {
                                utility.showToast(check);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }


                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    private void unsubscribe(final JSONObject jsonObject) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress();
            String op = utility.checkOperators(utility.getBkashSubscription().getMdn());
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Log.d("unsubscribe", jsonObject.toString());
            Call<ResponseBody> call = apiInterface.getunsubscription(context.getString(R.string.authorization_key), op, requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String check = response.body().string();
                            Log.d("unsubscribe", check);
                            if (check.trim().equalsIgnoreCase("DeactivationSuccess")) {
                                utility.showToast(utility.getLangauge().equals("bn") ? context.getString(R.string.unsub_success_bn) : context.getString(R.string.unsub_success_en));
                                unsubscribedView();
                                utility.setBkashSubscriptionclear();
                                check_subscribe_history(jsonObject);
                            } else {
                                utility.showToast(check);
                            }
                        } catch (Exception e) {
                            Log.d("Error Line Number", Log.getStackTraceString(e));
                        }


                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }


    private void check_subscribe_history(JSONObject jsonObject) {
        if (utility.isNetworkAvailable()) {
            try {
                utility.showProgress();
                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                Log.d("history", jsonObject.toString());
                String op = utility.checkOperators(utility.getBkashSubscription().getMdn());
                Call<List<SubscriptionHistory>> call = apiInterface.getsubscriptionhistory(context.getString(R.string.authorization_key), op, requestBody);
                call.enqueue(new Callback<List<SubscriptionHistory>>() {
                    @Override
                    public void onResponse(Call<List<SubscriptionHistory>> call, Response<List<SubscriptionHistory>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                historyList.clear();
                                historyList.addAll(response.body());
                                utility.logger("check" + historyList.size());
                                subscriptionHistoryAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.d("Error Line Number", Log.getStackTraceString(e));
                            }


                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SubscriptionHistory>> call, Throwable t) {
                        utility.hideProgress();
                        utility.logger(t.toString());
                        utility.showToast(context.getString(R.string.http_error));
                    }
                });
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    public void initial_history() {
        history_title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.history_title_bn) : context.getString(R.string.history_title_en));
        subscriptionHistoryAdapter = new SubscriptionHistoryAdapter(historyList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        history_recycler.setLayoutManager(mLayoutManager);
        history_recycler.setItemAnimator(new DefaultItemAnimator());
        history_recycler.setAdapter(subscriptionHistoryAdapter);
    }

    //get subscription text
    private void getSubscriptionText() {
        Call<List<Plantext>> call = apiInterface.getplantext(context.getString(R.string.authorization_key));
        call.enqueue(new Callback<List<Plantext>>() {
            @Override
            public void onResponse(Call<List<Plantext>> call, Response<List<Plantext>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    utility.logger("paisi 200");
                    List<Plantext> noticeModels = response.body();
                    if (noticeModels.size() > 0) {
                        for (Plantext p : noticeModels) {
                            if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                                plangp = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                                planBanglalink = p;
                            }
                        }
                    }
                    unsubscribedView();
                } else {
                    utility.showToast("Please connect with Internet");
                }
                utility.logger("paisi 500" + response.code());
            }

            @Override
            public void onFailure(Call<List<Plantext>> call, Throwable t) {
                utility.showToast("Please connect with Internet");
            }
        });
    }
}
