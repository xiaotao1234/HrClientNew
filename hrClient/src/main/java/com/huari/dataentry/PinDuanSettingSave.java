package com.huari.dataentry;

public class PinDuanSettingSave {
    boolean showBig = true;
    boolean showaSmall = true;
    boolean showAverage = true;
    boolean formVisible = true;
    boolean orientation = true;//代表目前为横向

    public void setShowBig(boolean showBig) {
        this.showBig = showBig;
    }

    public void setShowaSmall(boolean showaSmall) {
        this.showaSmall = showaSmall;
    }

    public void setShowAverage(boolean showAverage) {
        this.showAverage = showAverage;
    }

    public void setFormVisible(boolean formVisible) {
        this.formVisible = formVisible;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public boolean isShowBig() {
        return showBig;
    }

    public boolean isShowaSmall() {
        return showaSmall;
    }

    public boolean isShowAverage() {
        return showAverage;
    }

    public boolean isFormVisible() {
        return formVisible;
    }

    public boolean isOrientation() {
        return orientation;
    }

}
