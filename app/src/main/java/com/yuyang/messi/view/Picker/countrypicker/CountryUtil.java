package com.yuyang.messi.view.Picker.countrypicker;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by albertzheng on 7/29/14.
 */
public class CountryUtil {
    private static HashMap<String, String> countryCodeHashMap;
    private static List allCountriesList = new ArrayList<Country>();

    public static String formatNumberForUS(String phoneNumber) {
        if (phoneNumber.length() == 12) {
            return String.format("+1 (%s) %s-%s", phoneNumber.substring(2, 5),
                    phoneNumber.substring(5, 8), phoneNumber.substring(8, 12));
        } else {
            return phoneNumber;
        }
    }

    public static String formatNumberForCN(String phoneNumber) {
        if (phoneNumber.length() == 14) {
            return String.format("(+86) %s %s %s", phoneNumber.substring(3, 6),
                    phoneNumber.substring(6, 10), phoneNumber.substring(10, 14));
        } else {
            return phoneNumber;
        }
    }

    public static String getCountryCode(String ISOCode) {
        HashMap<String, String> hashMap = getCountryCodeHashMap();
        String code = hashMap.get(ISOCode);
        if (code != null) {
            return "+" + code;
        } else {
            return "+";
        }
    }

    /**
     * Get all countries with code and name from res/raw/countries.json
     *
     * @return
     */
    public static List<Country> getAllCountries(Context context) {
        if (allCountriesList.isEmpty()) {
            try {
                allCountriesList = new ArrayList<Country>();

                // Read from local file
                String allCountriesString = readFileAsString(context);
                Log.d("countrypicker", "country: " + allCountriesString);
                JSONObject jsonObject = new JSONObject(allCountriesString);
                Iterator<?> keys = jsonObject.keys();

                // Add the data to all countries list
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Country country = new Country();
                    country.setCode(key);
                    country.setName(jsonObject.getString(key));
                    allCountriesList.add(country);
                }

                // Sort the all countries list based on country name
                Collections.sort(allCountriesList, new Comparator<Country>() {
                    @Override
                    public int compare(Country lhs, Country rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                // Return
                return allCountriesList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allCountriesList;
    }

    public static String getISOCode(String countryCode) {
        HashMap<String, String> hashMap = getCountryCodeHashMap();

        for (String key : hashMap.keySet()) {
            if (countryCode.replace("+", "").equals(hashMap.get(key))) {
                return key;
            }
        }

        return null;
    }

    private static HashMap<String, String> getCountryCodeHashMap() {
        if (countryCodeHashMap == null) {
            countryCodeHashMap = new HashMap<>();
            String[] countryCodePairs = MessiApp.getInstance().
                    getResources().getStringArray(R.array.CountryCodes);
            for (String countryCodePair : countryCodePairs) {
                String[] pair = countryCodePair.split(",");
                if (pair.length == 2) {
                    countryCodeHashMap.put(pair[1], pair[0]);
                }
            }
        }
        return countryCodeHashMap;
    }

    /**
     * R.string.countries is a json string which is Base64 encoded to avoid
     * special characters in XML. It's Base64 decoded here to get original json.
     *
     * @param context
     * @return
     * @throws java.io.IOException
     */
    private static String readFileAsString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String getLanguage() {
        LocaleListCompat locales = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        //        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
//        Locale locale = Resources.getSystem().getConfiguration().locale;
        Locale currLocal = locales.get(0);
        if (currLocal == null) {
            return null;
        }
        if ("zh".equalsIgnoreCase(currLocal.getLanguage())
                && !"CN".equalsIgnoreCase(currLocal.getCountry())
        ) {
            return "zh-TW";
        } else {
            return currLocal.getLanguage();
        }
    }

    public static String getCountry() {
        Locale locale = LocaleListCompat.getDefault().get(0);
//        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
//        Locale locale = Resources.getSystem().getConfiguration().locale;
        String localeCountry;
        if (locale != null) {
            localeCountry = locale.getCountry();
            if (!TextUtils.isEmpty(localeCountry)) {
                LogUtil.d("CountryUtil", "get country from locale list, country = " + localeCountry);
                return localeCountry;
            }
        }
        TelephonyManager tm = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            localeCountry = tm.getNetworkCountryIso();
            if (!TextUtils.isEmpty(localeCountry)) {
                LogUtil.d("CountryUtil", "get country from network country iso, country = " + localeCountry);
                return localeCountry.toUpperCase();
            }
        }
        localeCountry = Locale.getDefault().getCountry();
        if (TextUtils.isEmpty(Locale.getDefault().getCountry())) {
            LogUtil.d("CountryUtil", "get country all is null, country = " + localeCountry);
        } else {
            LogUtil.d("CountryUtil", "get country from locale country, country = " + localeCountry);
        }
        return localeCountry;
    }

    public static String getCurrentCountryCode() {
        TelephonyManager manager = (TelephonyManager) MessiApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String ISOCode = manager.getSimCountryIso().toUpperCase();
        if (TextUtils.isEmpty(ISOCode)) {
            ISOCode = manager.getNetworkCountryIso().toUpperCase();
        }
        if (TextUtils.isEmpty(ISOCode)) {
            ISOCode = Locale.getDefault().getCountry();
        }
        return ISOCode;
    }

    public static String addCountryCodeForPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        } else {
            String currentCountryCode = getCurrentCountryCode();
            if (currentCountryCode == null || currentCountryCode.equals("")) {
                // defaut to be US, but should not have gone to this step
                currentCountryCode = "US";
            }
            String dialingCode = getCountryCode(currentCountryCode);

            return dialingCode + phoneNumber;
        }

    }

    public static void addCountryCodeForPhoneNumbers(String phoneNumbers[]) {
        for (int i = 0; i < phoneNumbers.length; i++) {
            phoneNumbers[i] = addCountryCodeForPhoneNumber(phoneNumbers[i]);
        }
    }

}
