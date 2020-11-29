package com.gtechnologies.videog.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gtechnologies.videog.Library.Utility;
import com.gtechnologies.videog.Model.SubscriptionHistory;
import com.gtechnologies.videog.R;

import java.util.List;

public class SubscriptionHistoryAdapter extends RecyclerView.Adapter<SubscriptionHistoryAdapter.Todo_View_Holder> {
    Context context;
    List<SubscriptionHistory> history_list;
    Utility utility;


    public SubscriptionHistoryAdapter(List<SubscriptionHistory> to, Context c) {
        history_list = to;
        context = c;
        utility = new Utility(context);
    }

    public class Todo_View_Holder extends RecyclerView.ViewHolder {
        TextView history_pac;
        TextView history_pac_result;
        TextView history_start;
        TextView history_start_result;
        TextView history_end;
        TextView history_end_result;
        TextView history_status;
        TextView history_status_result;


        public Todo_View_Holder(View view) {
            super(view);
            history_pac = view.findViewById(R.id.history_pac);
            history_pac_result = view.findViewById(R.id.history_pac_result);
            history_start = view.findViewById(R.id.history_start);
            history_start_result = view.findViewById(R.id.history_start_result);
            history_end = view.findViewById(R.id.history_end);
            history_end_result = view.findViewById(R.id.history_end_result);
            history_status = view.findViewById(R.id.history_status);
            history_status_result = view.findViewById(R.id.history_status_result);
        }
    }

    @Override
    public SubscriptionHistoryAdapter.Todo_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribe_history_row, parent, false);

        return new SubscriptionHistoryAdapter.Todo_View_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubscriptionHistoryAdapter.Todo_View_Holder holder, int position) {
        final SubscriptionHistory bodyResponse = history_list.get(position);
        utility.setFonts(
                new View[]{
                        holder.history_status_result,
                        holder.history_status,
                        holder.history_start,
                        holder.history_start_result,
                        holder.history_pac,
                        holder.history_pac_result,
                        holder.history_end,
                        holder.history_end_result
                });
        holder.history_pac_result.setText(bodyResponse.getPlan());
        holder.history_start_result.setText(bodyResponse.getActivationDate());
        holder.history_end_result.setText(bodyResponse.getDeactivationDate());
        holder.history_status_result.setText(bodyResponse.getPresentStatus());
        holder.history_pac.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.history_pac_bn) : context.getString(R.string.history_pac_en));
        holder.history_start.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.history_start_bn) : context.getString(R.string.history_start_en));
        holder.history_end.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.history_end_bn) : context.getString(R.string.history_endc_en));
        holder.history_status.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.history_status_bn) : context.getString(R.string.history_status_en));

    }

    @Override
    public int getItemCount() {
        return history_list.size();
    }
}