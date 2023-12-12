package com.bbi.transactionsp2.dto;


import lombok.Data;

import java.util.List;

@Data
public class CompanyKeywordsDTO {
    private long companyId;
    private String companyName;
    private String companyLogo;
    private boolean activeInactive;
    private List<Keyword> keywords;
}
