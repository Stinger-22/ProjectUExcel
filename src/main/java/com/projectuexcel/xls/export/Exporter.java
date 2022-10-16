package com.projectuexcel.xls.export;

import com.projectuexcel.xls.exception.CodeNotFoundException;

public interface Exporter {
    /**
     * Export part of xls file into new file
     * @param code code of similar data looked for
     * @param path path to new file
     * @throws CodeNotFoundException is thrown when code is not found in table
     */
    void export(String code, String path) throws CodeNotFoundException;
}
