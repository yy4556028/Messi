package com.yuyang.messi.view.Picker.countrypicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryPickerPop extends PopupWindow {

    private Context context;

    /**
     * View components
     */
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private List<Country> allCountriesList;
    private List<Country> selectedCountriesList;

    public CountryPickerPop(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.country_picker_pop, null)
                , ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
                , true);
        this.context = context;

        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.colorAccent)));

        allCountriesList = CountryUtil.getAllCountries(context);
        selectedCountriesList = new ArrayList<>();
        selectedCountriesList.addAll(allCountriesList);
        initView();
        initEvent();
    }

    private void initView() {

        searchEditText = getContentView().findViewById(R.id.country_picker_search);

        recyclerView = getContentView().findViewById(R.id.country_picker_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter = new MyAdapter(context));
    }

    private void initEvent() {

        // Search for which countries matched user query
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
    }

    /**
     * Search allCountriesList contains text and put result into
     * selectedCountriesList
     *
     * @param text
     */
    private void search(String text) {
        selectedCountriesList.clear();

        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public interface CountryPickerListener{
        void onSelectCountry(String name, String code);
    }

    private CountryPickerListener countryPickerListener;

    public void setCountryPickerListener(CountryPickerListener countryPickerListener) {
        this.countryPickerListener = countryPickerListener;
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(mInflater.inflate(R.layout.country_picker_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            final Country country = selectedCountriesList.get(position);
            holder.textView.setText(country.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countryPickerListener != null) {
                        countryPickerListener.onSelectCountry(country.getName(),country.getCode());
                    }
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectedCountriesList == null ? 0 : selectedCountriesList.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public MyHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.country_picker_item_text);
            }
        }
    }
}
