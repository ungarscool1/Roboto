package com.github.ungarscool1.Roboto.enums;

public enum VoteEnum {
    DESCRIPTION(0),
    MULTI(1),
    OPTIONNUMBERS(2),
    OPTION(3),
    FILL(4),
    ENDED(5),
    MULTIPLE_ENDED(6)
    ;

    public final int label;

    private VoteEnum(int label) {
        this.label = label;
    }

    public static VoteEnum findByValue(int value){
        for(VoteEnum v : values()){
            if( v.label == value){
                return v;
            }
        }
        return null;
    }
}
