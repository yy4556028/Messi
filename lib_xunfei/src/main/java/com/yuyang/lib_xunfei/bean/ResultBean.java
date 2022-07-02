package com.yuyang.lib_xunfei.bean;

import java.util.List;

/**
 * 解析 语音听写返回结果Json格式字符串 的模板类（多重嵌套Json）
 * <p/>
 * 语音识别结果Json数据格式（单条数据）：
 * {"sn":1,"ls":true,"bg":0,"ed":0,"ws":[
 * {"bg":0,"cw":[{"w":"今天","sc":0}]},
 * {"bg":0,"cw":[{"w":"的","sc":0}]},
 * {"bg":0,"cw":[{"w":"天气","sc":0}]},
 * {"bg":0,"cw":[{"w":"怎么样","sc":0}]},
 * {"bg":0,"cw":[{"w":"。","sc":0}]}
 * ]}
 * <p/>
 * ws  array :词
 * cw   array :中文分词
 * w  string :单字
 * sc  number :分数
 */
public class ResultBean {

    private int sn;//sentence number 第几句
    private boolean ls;//last sentence 是否最后一句
    private int bg;//开始
    private int ed;//结束
    private List<Word> ws;//词

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<Word> getWs() {
        return ws;
    }

    public void setWs(List<Word> ws) {
        this.ws = ws;
    }

    @Override
    public String toString() {
        String result = "";
        for (Word wsTmp : ws) {
            result += wsTmp.toString();
        }
        return result;
    }

    public class Word {
        private String bg;
        private List<Cw> cw;

        public String getBg() {
            return bg;
        }

        public void setBg(String bg) {
            this.bg = bg;
        }

        public List<Cw> getCw() {
            return cw;
        }

        public void setCw(List<Cw> cw) {
            this.cw = cw;
        }

        @Override
        public String toString() {
            String result = "";
            for (Cw cwTmp : cw) {
                result += cwTmp.toString();
            }
            return result;
        }

        public class Cw {
            private String w;//词
            private int sc;//分数

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }

            public int getSc() {
                return sc;
            }

            public void setSc(int sc) {
                this.sc = sc;
            }

            @Override
            public String toString() {
                return w;
            }
        }
    }
}