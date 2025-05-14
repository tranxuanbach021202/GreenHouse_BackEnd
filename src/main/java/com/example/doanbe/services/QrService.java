package com.example.doanbe.services;

import com.example.doanbe.document.PlotInfo;

public interface QrService {
    byte[] generateQrPdf(String projectId, String urlApp) throws Exception;
}
