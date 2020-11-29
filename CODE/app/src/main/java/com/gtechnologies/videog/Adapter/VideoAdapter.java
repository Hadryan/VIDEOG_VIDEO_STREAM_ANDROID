package com.gtechnologies.videog.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Util;
import com.gtechnologies.videog.Activity.FullscreenVideoActivity;
import com.gtechnologies.videog.Activity.MainActivity;
import com.gtechnologies.videog.Activity.Playlist;
import com.gtechnologies.videog.Fragment.Home;
import com.gtechnologies.videog.Http.BaseApiClient;
import com.gtechnologies.videog.Http.BaseApiInterface;
import com.gtechnologies.videog.Http.ContentApiClient;
import com.gtechnologies.videog.Http.ContentApiInterface;
import com.gtechnologies.videog.Interface.SubscriptionInterfacce;
import com.gtechnologies.videog.Library.ExoPlayerVideoHandler;
import com.gtechnologies.videog.Library.KeyWord;
import com.gtechnologies.videog.Library.Utility;
import com.gtechnologies.videog.Model.Banglalink;
import com.gtechnologies.videog.Model.Content;
import com.gtechnologies.videog.Model.Subscription;
import com.gtechnologies.videog.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 3/29/2018.
 */

public class VideoAdapter extends BaseAdapter implements SubscriptionInterfacce {

    Context context;
    List<Content> contents;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);

    public VideoAdapter(Context context, List<Content> contents) {
        this.context = context;
        this.contents = contents;
        utility = new Utility(this.context, this);
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.video_child_layout, null);
            }
            LinearLayout container = convertView.findViewById(R.id.video_container);
            ImageView image = convertView.findViewById(R.id.image);
            ImageView premium = convertView.findViewById(R.id.premium);
            TextView duration = convertView.findViewById(R.id.duration);
            TextView title = convertView.findViewById(R.id.title);
            TextView description = convertView.findViewById(R.id.description);
            utility.setFont(duration);
            utility.setFont(title);
            utility.setFont(description);
            HashMap<String, Integer> screenResolution = utility.getScreenRes();
            int screenWidth = screenResolution.get(KeyWord.SCREEN_WIDTH) - 20;
            int screenHeight = (screenWidth / 3) * 2;
            ViewGroup.LayoutParams params = image.getLayoutParams();
            params.width = screenWidth;
            params.height = screenHeight;
            image.setLayoutParams(params);
            if (contents.get(position).getPremium().equals("yes")) {
                premium.setVisibility(View.VISIBLE);
            } else {
                premium.setVisibility(View.GONE);
            }
            Picasso.with(context)
                    .load(context.getString(R.string.image_url) + contents.get(position).getImage())
                    .error(R.drawable.videog_light)
                    .placeholder(R.drawable.loading)
                    .into(image);
            duration.setText(utility.getLangauge().equals("bn") ?
                    utility.convertToBangle(utility.convertSecondsToHour(contents.get(position).getDuration())) : utility.convertSecondsToHour(contents.get(position).getDuration()));
            title.setText(utility.getLangauge().equals("bn") ?
                    utility.decodeBase64(contents.get(position).getTitleBn()) : contents.get(position).getTitle());
            description.setText(utility.getLangauge().equals("bn") ?
                    utility.decodeBase64(contents.get(position).getBriefBn()) : contents.get(position).getBrief());
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //utility.showToast(contents.get(position).getTitle());
                    if (contents.get(position).getPremium().equals("yes")) {
                        if (utility.getMdn().equals("00")) {
                            makeSubscriptionDialog(contents.get(position).getId(), position);
                        } else {
                            checkMasterSubscription(contents.get(position).getId(), position);
                        }
                    } else {
                        playContent(position);
                    }
                }
            });
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
        return convertView;
    }

    private void playContent(int position) {
        /*utility.setPlayingFrom(KeyWord.PLAYING_NORMAL);
        if (Util.SDK_INT <= context.getResources().getInteger(R.integer.sdk)) {
            Intent intent = new Intent(context, Playlist.class);
            //intent.putExtra("contents", new Video(contents));
            ExoPlayerVideoHandler.getInstance().setContentList(contents);
            ExoPlayerVideoHandler.getInstance().setIndex(position);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, FullscreenVideoActivity.class);
            ExoPlayerVideoHandler.getInstance().setContentList(contents);
            ExoPlayerVideoHandler.getInstance().setIndex(position);
            context.startActivity(intent);
        }*/
        try{
            if (utility.getBkashSubscription().getMdn().length() == 0) {
                //utility.makeSubscriptionDialog(true);
                callFragment(new com.gtechnologies.videog.Fragment.Subscription(context), KeyWord.SUBSCRIPTION);
            } else {
                JSONObject pro_json = new JSONObject();
                pro_json.put("mdn", utility.getBkashSubscription().getMdn());
                checkBLSubscription(pro_json,position);
            }
        }catch(Exception e){
          Log.d("Error Line Number",Log.getStackTraceString(e));
        }
    }

    public void makeSubscriptionDialog(final int trackId, final int position) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = dialog.findViewById(R.id.number_layout);
            TextView title = dialog.findViewById(R.id.subscription_title);
            final EditText phoneNumber = dialog.findViewById(R.id.phone_number);
            Button cancelBtn = dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = dialog.findViewById(R.id.rating_btn_submit);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            //utility.setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String msisdn = "8801" + phoneNumber.getText().toString();
                    String message = utility.validateMsisdn(msisdn);
                    if (message.equals("OK")) {
                        utility.writeMsisdn(msisdn);
                        checkMasterSubscription(trackId, position);
                    } else {
                        utility.showToast(message);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    private void checkMasterSubscription(final int trackId, final int position) {
        utility.showProgress(false);
        try {
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject()).toString());
            Call<Subscription> call = apiInterface.getStatus(context.getString(R.string.authorization_key), utility.getMsisdn(), "", trackId, body);
            call.enqueue(new Callback<Subscription>() {
                @Override
                public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        Subscription subscription = response.body();
                        utility.writeSubscriptionStatus(trackId, subscription);
                        if (utility.isSubscribed(trackId)) {
                            playContent(position);
                            //startDownload(keyword, position);
                        } else {
                            showPremiumDialog(trackId, position);
                        }
                    } else {
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<Subscription> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        } catch (Exception ex) {
            utility.hideProgress();
            utility.logger(ex.toString());
        }
    }

    private void showPremiumDialog(final int trackId, final int position) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = dialog.findViewById(R.id.permission_message);
        Button yes = dialog.findViewById(R.id.dialog_yes);
        Button no = dialog.findViewById(R.id.dialog_no);
        tv.setText("এই কনটেন্টটি দেখতে আপনার জিপি ব্যালান্স থেকে " + utility.getPrice(trackId) + " টাকা চার্জ প্রযোজ্য। আপনি কি আগ্রহী?");
        LinearLayout ll = dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
        no.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pushOtp(trackId, "0", position);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void pushOtp(final int trackId, String pin, final int position) {
        utility.showProgress(false);
        try {
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject()).toString());
            Call<Subscription> call = apiInterface.pushOtp(context.getString(R.string.authorization_key), utility.getMsisdn(), "", trackId, body);
            call.enqueue(new Callback<Subscription>() {
                @Override
                public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        Subscription subscription = response.body();
                        utility.writeSubscriptionStatus(trackId, subscription);
                        if (subscription.getComment().equals("generated")) {
                            validatePinDialog(trackId, position);
                        } else {
                            utility.showToast("PIN Process Failed");
                        }
                    } else {
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<Subscription> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        } catch (Exception ex) {
            utility.hideProgress();
        }
    }

    private void validatePinDialog(final int trackId, final int position) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = dialog.findViewById(R.id.number_layout);
            TextView title = dialog.findViewById(R.id.subscription_title);
            final EditText phoneNumber = dialog.findViewById(R.id.phone_number);
            Button cancelBtn = dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = dialog.findViewById(R.id.rating_btn_submit);
            TextView phoneCode = dialog.findViewById(R.id.phone_code);
            phoneCode.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            //utility.setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(context.getString(R.string.number_pin_bn));
            phoneNumber.setHint("PIN e.g. XXXX");
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = phoneNumber.getText().toString();
                    if (pin.length() > 0) {
                        dialog.dismiss();
                        chargeOtp(trackId, phoneNumber.getText().toString(), position);
                    } else {
                        utility.showToast("Pin Required");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    private void chargeOtp(final int trackId, String pin, final int position) {
        utility.showProgress(false);
        try {
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject()).toString());
            Call<Subscription> call = apiInterface.chargeOtp(context.getString(R.string.authorization_key), utility.getMsisdn(), "", trackId, Integer.parseInt(pin), body);
            call.enqueue(new Callback<Subscription>() {
                @Override
                public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        Subscription subscription = response.body();
                        utility.writeSubscriptionStatus(trackId, subscription);
                        if (subscription.getComment().equals("charged")) {
                            //showConfirmation();
                            //startDownload(keyword, position);
                            playContent(position);
                        } else {
                            utility.showToast("PIN Process Failed");
                        }
                    } else {
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<Subscription> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        } catch (Exception ex) {
            utility.hideProgress();
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

    }


    private void callFragment(android.support.v4.app.Fragment fragment, String tag) {
        try {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.containerView, fragment, tag).commit();
            //navigationView.getMenu().findItem(itemId).setChecked(true);
        } catch (Exception ex) {
            //utility.call_error(ex);
        }
    }

    private void checkBLSubscription(JSONObject jsonObject, final int position) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Log.d("Check subscription", jsonObject.toString());
            String op=utility.checkOperators(utility.getBkashSubscription().getMdn());
            Call<ResponseBody> call = apiInterface.getblsubscriptionstatus(context.getString(R.string.authorization_key),op, requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String check = response.body().string();
                            Log.d("Check subscription", check);
                            if (check.trim().equalsIgnoreCase("notSubscribed")) {
                                callFragment(new com.gtechnologies.videog.Fragment.Subscription(context), KeyWord.SUBSCRIPTION);
                            } else if (check.trim().equalsIgnoreCase("yesSubscribedDaily") || check.trim().equalsIgnoreCase("yesSubscribedweekly") || check.trim().equalsIgnoreCase("yesSubscribedmonthly")) {
                                if (utility.isNetworkAvailable()) {
                                    utility.setPlayingFrom(KeyWord.PLAYING_NORMAL);
                                    if (Util.SDK_INT <= context.getResources().getInteger(R.integer.sdk)) {
                                        Intent intent = new Intent(context, Playlist.class);
                                        //intent.putExtra("contents", new Video(contents));
                                        ExoPlayerVideoHandler.getInstance().setContentList(contents);
                                        ExoPlayerVideoHandler.getInstance().setIndex(position);
                                        context.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, FullscreenVideoActivity.class);
                                        ExoPlayerVideoHandler.getInstance().setContentList(contents);
                                        ExoPlayerVideoHandler.getInstance().setIndex(position);
                                        context.startActivity(intent);
                                    }
                                }
                            } else {
                                utility.logger("no response" + check);
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
}
