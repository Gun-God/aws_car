package com.aws.carno.Enum;
/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/21 11:05
 * @description
 */
public enum CarTypeEnum {
    CARTYPE_1(20,16,"二轴车"),
    CARTYPE_2(21,1,"12"),
    CARTYPE_3(22,1,"12"),
    CARTYPE_4(23,1,"12"),
    CARTYPE_5(24,1,"12"),
    CARTYPE_6(25,1,"12"),
    CARTYPE_7(26,1,"12"),
    CARTYPE_8(1,1,"12"),
    CARTYPE_9(1,1,"12"),
    CARTYPE_10(1,1,"12"),
    CARTYPE_11(1,1,"12"),
    CARTYPE_12(1,1,"12"),
    CARTYPE_13(1,1,"12"),
    CARTYPE_14(1,1,"12"),
    CARTYPE_15(1,1,"12"),
    CARTYPE_16(1,1,"12"),
    CARTYPE_17(1,1,"12"),
    CARTYPE_18(1,1,"12"),
    CARTYPE_19(1,1,"12"),
    CARTYPE_20(1,1,"12"),
    CARTYPE_21(1,1,"12"),
    CARTYPE_22(1,1,"12"),
    CARTYPE_23(1,1,"12"),
    CARTYPE_24(1,1,"12"),
    CARTYPE_25(1,1,"12"),
    CARTYPE_26(1,1,"12"),
    CARTYPE_27(1,1,"12"),
    CARTYPE_28(1,1,"12"),
    CARTYPE_29(1,1,"12"),
    CARTYPE_30(1,1,"12"),
    CARTYPE_31(1,1,"12"),
    CARTYPE_32(1,1,"12"),
    CARTYPE_33(1,1,"12"),
    CARTYPE_34(1,1,"12"),
    CARTYPE_35(1,1,"12"),
    CARTYPE_36(1,1,"12"),
    CARTYPE_37(1,1,"12"),
    CARTYPE_38(1,1,"12"),
    CARTYPE_39(1,1,"12"),
    CARTYPE_40(1,1,"12"),
    CARTYPE_41(1,1,"12"),
    CARTYPE_42(1,1,"12"),
    CARTYPE_43(1,1,"12"),
    CARTYPE_44(1,1,"12"),
    CARTYPE_45(1,1,"12"),
    CARTYPE_46(1,1,"12"),
    CARTYPE_47(1,1,"12"),
    CARTYPE_48(1,1,"12"),
    CARTYPE_49(1,1,"12"),
    CARTYPE_50(1,1,"12"),
    CARTYPE_51(1,1,"12"),
    CARTYPE_52(1,1,"12"),
    CARTYPE_53(1,1,"12"),
    CARTYPE_54(1,1,"12"),
    CARTYPE_55(1,1,"12"),
    CARTYPE_56(1,1,"12"),
    CARTYPE_57(1,1,"12"),
    CARTYPE_58(1,1,"12"),
    CARTYPE_59(1,1,"12"),
    CARTYPE_60(1,1,"12"),
    CARTYPE_61(1,1,"12"),
    CARTYPE_62(1,1,"12"),
    CARTYPE_63(1,1,"12"),
    CARTYPE_64(1,1,"12"),
    CARTYPE_65(1,1,"12"),
    CARTYPE_66(1,1,"12"),
    CARTYPE_67(1,1,"12"),
    CARTYPE_68(1,1,"12"),
    CARTYPE_69(1,1,"12"),
    CARTYPE_70(1,1,"12"),
    CARTYPE_71(1,1,"12");

    int vehId;
    int vehTypeId;
    String vehTypeName;

    CarTypeEnum(int vehId, int vehTypeId, String vehTypeName) {
        this.vehId=vehId;
        this.vehTypeId=vehTypeId;
        this.vehTypeName=vehTypeName;
    }
    // 根据车辆id获取车型id
    public static int getVehTypeId(int vehId) {
        for (CarTypeEnum c : CarTypeEnum.values()) {
            if (c.getVehId() == vehId) {
                return c.getVehTypeId();
            }
        }
        return 0;
    }

    public int getVehId() {
        return vehId;
    }

    public void setVehId(int vehId) {
        this.vehId = vehId;
    }

    public int getVehTypeId() {
        return vehTypeId;
    }

    public void setVehTypeId(int vehTypeId) {
        this.vehTypeId = vehTypeId;
    }

    public String getVehTypeName() {
        return vehTypeName;
    }

    public void setVehTypeName(String vehTypeName) {
        this.vehTypeName = vehTypeName;
    }
}
