package com.yuyang.messi.ui.category;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.ui.view.picker.BottomChooseSheet;
import com.yuyang.lib_base.ui.view.picker.ChooseDialog;
import com.yuyang.lib_base.ui.view.picker.ChoosePopup;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.AssetsUtil;
import com.yuyang.messi.utils.ColorUtil;
import com.yuyang.messi.utils.DateUtil;
import com.yuyang.messi.view.Picker.AddressInitTask;
import com.yuyang.messi.view.Picker.DatePickerView;
import com.yuyang.messi.view.Picker.picker.AddressPicker;
import com.yuyang.messi.view.Picker.picker.ChineseZodiacPicker;
import com.yuyang.messi.view.Picker.picker.ColorPicker;
import com.yuyang.messi.view.Picker.picker.ConstellationPicker;
import com.yuyang.messi.view.Picker.picker.DatePicker;
import com.yuyang.messi.view.Picker.picker.DateTimePicker;
import com.yuyang.messi.view.Picker.picker.LinkagePicker;
import com.yuyang.messi.view.Picker.picker.NumberPicker;
import com.yuyang.messi.view.Picker.picker.OptionPicker;
import com.yuyang.messi.view.Picker.picker.SexPicker;
import com.yuyang.messi.view.Picker.picker.TimePicker;
import com.yuyang.messi.view.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PickerActivity extends AppBaseActivity {

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("?????????");

        initRangeSeekBar();
        initDatePicker();
        initPopupList();
    }

    private void initRangeSeekBar() {
        RangeSeekBar rangeSeekBar = findViewById(R.id.activity_picker_rangeSeekBar);

        List<CharSequence> markList = new ArrayList<>();
        markList.add("0");
        markList.add("100");
        markList.add("200");
        markList.add("400");
        markList.add("??????");

        rangeSeekBar.setTextMarks(markList);
        rangeSeekBar.setAllowSame(true);
        rangeSeekBar.setOnCursorChangeListener(new RangeSeekBar.OnCursorChangeListener() {
            @Override
            public void onLeftCursorChanged(int location, String textMark) {
                ToastUtil.showToast("location = " + location + "\r\ntextMark = " + textMark);
            }

            @Override
            public void onRightCursorChanged(int location, String textMark) {
                ToastUtil.showToast("location = " + location + "\r\ntextMark = " + textMark);
            }
        });
    }

    private void initDatePicker() {
        findViewById(R.id.activity_picker_datetimeBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickDateTime(getActivity(), (TextView) findViewById(R.id.activity_picker_datetimeBtn0), new DatePickerView.OnPickListener() {
                    @Override
                    public void onPick(Calendar calendar) {
                        ToastUtil.showToast(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.getTime()));
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_datetimeBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePicker picker = new DateTimePicker(getActivity(), DateTimePicker.HOUR_OF_DAY);
                picker.setRange(2000, 2030);
                picker.setSelectedItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                        ToastUtil.showToast(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                    }
                });
                picker.show();
            }
        });

        findViewById(R.id.activity_picker_dateBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickDate(getActivity(), (TextView) findViewById(R.id.activity_picker_dateBtn0), new DatePickerView.OnPickListener() {
                    @Override
                    public void onPick(Calendar calendar) {
                        ToastUtil.showToast(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_dateBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker picker = new DatePicker(getActivity());
                picker.setRange(1900, calendar.get(Calendar.YEAR));
                picker.setSelectedItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        ToastUtil.showToast(year + "-" + month + "-" + day);
                    }
                });
                picker.show();
            }
        });

        findViewById(R.id.activity_picker_timeBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickTime(getActivity(), (TextView) findViewById(R.id.activity_picker_timeBtn0), new DatePickerView.OnPickListener() {
                    @Override
                    public void onPick(Calendar calendar) {
                        ToastUtil.showToast(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime()));
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_timeBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????????????????
                TimePicker picker = new TimePicker(getActivity(), TimePicker.HOUR_OF_DAY);
                picker.setTopLineVisible(false);
                picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        ToastUtil.showToast(hour + ":" + minute);
                    }
                });
                picker.show();
            }
        });

        findViewById(R.id.activity_picker_yearmonthBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickYearMonth(getActivity(), (TextView) findViewById(R.id.activity_picker_yearmonthBtn0), new DatePickerView.OnYearMonthPickListener() {
                    @Override
                    public void onYearMonthPick(int year, int month) {
                        ToastUtil.showToast(year + "???" + (month + 1) + "???");
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_yearmonthBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker picker = new DatePicker(getActivity(), DatePicker.YEAR_MONTH);
                picker.setRange(1990, 2015);
                picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
                    @Override
                    public void onDatePicked(String year, String month) {
                        ToastUtil.showToast(year + "-" + month);
                    }
                });
                picker.show();
            }
        });

        findViewById(R.id.activity_picker_monthdayBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickMonthDay(getActivity(), (TextView) findViewById(R.id.activity_picker_monthdayBtn0), new DatePickerView.OnMonthDayPickListener() {
                    @Override
                    public void onMonthDayPick(int month, int day) {
                        ToastUtil.showToast((month + 1) + "???" + day + "???");
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_monthdayBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker picker = new DatePicker(getActivity(), DatePicker.MONTH_DAY);
                picker.setOnDatePickListener(new DatePicker.OnMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String month, String day) {
                        ToastUtil.showToast(month + "-" + day);
                    }
                });
                picker.show();
            }
        });

        findViewById(R.id.activity_picker_yearBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickYear(getActivity(), (TextView) findViewById(R.id.activity_picker_datetimeBtn0), new DatePickerView.OnYearPickListener() {
                    @Override
                    public void onYearPick(int year) {
                        ToastUtil.showToast(year + "???");
                    }
                });
            }
        });
        findViewById(R.id.activity_picker_monthBtn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickMonth(getActivity(), (TextView) findViewById(R.id.activity_picker_datetimeBtn0), new DatePickerView.OnMonthPickListener() {
                    @Override
                    public void onMonthPick(int month) {
                        ToastUtil.showToast(month + "???");
                    }
                });
            }
        });
    }

    private void initPopupList() {
        List<Drawable> drawableList = new ArrayList<>();
        drawableList.add(getResources().getDrawable(R.drawable.share));
        drawableList.add(getResources().getDrawable(R.drawable.download));
        drawableList.add(getResources().getDrawable(R.drawable.save));
        List<PopBean> beanList = new ArrayList<>();
        beanList.add(new PopBean("0", "??????"));
        beanList.add(new PopBean("1", "??????"));
        beanList.add(new PopBean("2", "??????"));

        ChoosePopup
                .with(this)
                .setData(drawableList, beanList)
//                .setBgDrawable(getResources().getDrawable(R.drawable.activity_main_popup_background))
                .setOnPopClickListener(new ChoosePopup.OnItemChooseListener() {
                    @Override
                    public void onChoose(int pos, PopBean popBean) {
                        ToastUtil.showToast(popBean.getName());
                    }
                })
                .bindView(findViewById(R.id.activity_picker_choosePop))
                .build();

        ChooseDialog.bind(findViewById(R.id.activity_picker_chooseDialog), drawableList, beanList, new ChooseDialog.OnItemChooseListener() {
            @Override
            public void onChoose(int pos, PopBean popBean) {
                ToastUtil.showToast(popBean.getName());
            }
        });
    }

    public void onBottomDialog(View view) {
        List<PopBean> beanList = new ArrayList<>();
        beanList.add(new PopBean("0", "??????"));
        beanList.add(new PopBean("1", "??????"));
        beanList.add(new PopBean("2", "??????"));

        BottomChooseDialog.showSingle(getActivity(),
                "?????????",
                beanList,
                new BottomChooseDialog.SingleChoiceListener() {
                    @Override
                    public void onItemClick(int index, PopBean popBean) {
                        ToastUtil.showToast(popBean.getName());
                    }
                });
    }

    public void onBottomSheetDialog(View view) {
        List<PopBean> beanList = new ArrayList<>();
        beanList.add(new PopBean("0", "??????"));
        beanList.add(new PopBean("1", "??????"));
        beanList.add(new PopBean("2", "??????"));

        BottomChooseSheet.showSingle(getActivity(),
                "?????????",
                beanList,
                new BottomChooseSheet.SingleChoiceListener() {
                    @Override
                    public void onItemClick(int index, PopBean popBean) {
                        ToastUtil.showToast(popBean.getName());
                    }
                });
    }

    public void onOptionPicker(View view) {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "?????????", "?????????", "??????????????????????????????????????????????????????????????????????????????????????????????????????"
        });
        picker.setOffset(2);
        picker.setSelectedIndex(1);
        picker.setTextSize(11);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                ToastUtil.showToast(option);
            }
        });
        picker.show();
    }

    public void onLinkagePicker(View view) {
        ArrayList<String> firstList = new ArrayList<String>();
        firstList.add("??????");
        firstList.add("??????");
        ArrayList<ArrayList<String>> secondList = new ArrayList<ArrayList<String>>();
        ArrayList<String> secondListItem = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            secondListItem.add(DateUtil.fillZero(i) + "???");
        }
        secondList.add(secondListItem);//????????????
        secondList.add(secondListItem);//????????????
        ArrayList<ArrayList<ArrayList<String>>> thirdList = new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> thirdListItem1 = new ArrayList<ArrayList<String>>();
        ArrayList<String> thirdListItem2 = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            thirdListItem2.add(DateUtil.fillZero(i) + "???");
        }
        for (int i = 0; i < 24; i++) {
            thirdListItem1.add(thirdListItem2);//??????0-23???
        }
        thirdList.add(thirdListItem1);//????????????
        thirdList.add(thirdListItem1);//????????????
        LinkagePicker picker = new LinkagePicker(this, firstList, secondList, thirdList);
        picker.setSelectedItem("??????", "9???");
        picker.setOnLinkageListener(new LinkagePicker.OnLinkageListener() {

            @Override
            public void onPicked(String first, String second, String third) {
                ToastUtil.showToast(first + "-" + second + "-" + third);
            }
        });
        picker.show();
    }

    public void onConstellationPicker(View view) {
        ConstellationPicker picker = new ConstellationPicker(this);
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setTopLineVisible(false);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setTextColor(0xFFFF0000, 0xFFCCCCCC);
        picker.setLineColor(0xFFEE0000);
        picker.setSelectedItem("??????");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                ToastUtil.showToast(option);
            }
        });
        picker.show();
    }

    public void onChineseZodiacPicker(View view) {
        ChineseZodiacPicker picker = new ChineseZodiacPicker(this);
        picker.setLineVisible(false);
        picker.setSelectedItem("???");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                ToastUtil.showToast(option);
            }
        });
        picker.show();
    }

    public void onNumberPicker(View view) {
        NumberPicker picker = new NumberPicker(this);
        picker.setOffset(2);//?????????
        picker.setRange(145, 200);//????????????
        picker.setSelectedItem(172);
        picker.setLabel("??????");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                ToastUtil.showToast(option);
            }
        });
        picker.show();
    }

    public void onSexPicker(View view) {
        SexPicker picker = new SexPicker(this);
        //picker.onlyMaleAndFemale();
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                ToastUtil.showToast(option);
            }
        });
        picker.show();
    }

    public void onAddressPicker(View view) {
        // ????????????ok
//        new AddressInitTask(this).execute("??????", "??????", "??????");

        ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
        try {
            String json = AssetsUtil.readText("city.json");
            data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
            AddressPicker picker = new AddressPicker(this, data);
            picker.setHideCounty(false);
            picker.setSelectedItem("??????", "??????", "??????");
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(String province, String city, String county) {
                    if (county == null) {
                        ToastUtil.showToast(province + city);
                    } else {
                        ToastUtil.showToast(province + city + county);
                    }
                }
            });
            picker.show();
        } catch (Exception e) {
            ToastUtil.showToast(e.toString());
        }
    }

    public void onAddress2Picker(View view) {
        try {
            ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
            String json = AssetsUtil.readText("city2.json");
            data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
            AddressPicker picker = new AddressPicker(this, data);
            picker.setHideProvince(true);
            picker.setSelectedItem("??????", "??????", "??????");
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(String province, String city, String county) {
                    ToastUtil.showToast(city + county);
                }
            });
            picker.show();
        } catch (Exception e) {
            ToastUtil.showToast(e.toString());
        }
    }

    public void onAddress3Picker(View view) {
        new AddressInitTask(this, true).execute("??????", "??????");
    }

    public void onColorPicker(View view) {
        ColorPicker picker = new ColorPicker(this);
        picker.setInitColor(0xDD00DD);
        picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
            @Override
            public void onColorPicked(int pickedColor) {
                ToastUtil.showToast(ColorUtil.toColorString(pickedColor, true));
            }
        });
        picker.show();
    }

    public void onContact(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:liyujiang_tk@yeah.net"));
        intent.putExtra(Intent.EXTRA_CC, new String[]
                {"4556028@qq.com"});
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.putExtra(Intent.EXTRA_TEXT, "?????????????????????????????????");
        startActivity(Intent.createChooser(intent, "?????????????????????"));
    }

}