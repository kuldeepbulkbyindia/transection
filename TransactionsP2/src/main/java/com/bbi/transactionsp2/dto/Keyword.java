package com.bbi.transactionsp2.dto;

import lombok.Data;

import java.util.List;

@Data
public class Keyword {
    private long keywordId;
    private String keywordName;
    private boolean activeInactive;
}
