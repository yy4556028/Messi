package com.yuyang.messi.utils;


import com.yuyang.messi.bean.AddrPicker.CityBean;
import com.yuyang.messi.bean.AddrPicker.DistrictBean;
import com.yuyang.messi.bean.AddrPicker.ProvinceBean;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XmlParserHandler extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<ProvinceBean> provinceList = new ArrayList<>();

    ProvinceBean provinceBean;
    CityBean cityBean;
    DistrictBean districtBean;

    public List<ProvinceBean> getProvinceList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        //开始解析文档
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        //文档解析结束
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //开始解析节点
        if (qName.equals("province")) {
            provinceBean = new ProvinceBean();
            provinceBean.setName(attributes.getValue(0));
            provinceBean.setCityList(new ArrayList<CityBean>());
        } else if (qName.equals("city")) {
            cityBean = new CityBean();
            cityBean.setName(attributes.getValue(0));
            cityBean.setDistrictList(new ArrayList<DistrictBean>());
        } else if (qName.equals("district")) {
            districtBean = new DistrictBean();
            districtBean.setName(attributes.getValue(0));
            districtBean.setZipcode(attributes.getValue(1));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //保存节点内容
        super.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //结束解析节点
        if (qName.equals("district")) {
            cityBean.getDistrictList().add(districtBean);
        } else if (qName.equals("city")) {
            provinceBean.getCityList().add(cityBean);
        } else if (qName.equals("province")) {
            provinceList.add(provinceBean);
        }
    }
}
