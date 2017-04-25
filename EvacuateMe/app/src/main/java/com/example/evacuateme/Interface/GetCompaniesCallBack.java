package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.Companies;

import java.util.List;

/**
 * Created by Андрей Кравченко on 20-Apr-17.
 */

public interface GetCompaniesCallBack {
    public void completed(boolean result, List<Companies> list_companies);
}
