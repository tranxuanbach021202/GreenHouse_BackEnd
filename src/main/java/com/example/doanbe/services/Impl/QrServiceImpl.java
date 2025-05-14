package com.example.doanbe.services.Impl;

import com.example.doanbe.document.Plot;
import com.example.doanbe.repository.LayoutArrangementRepository;
import com.example.doanbe.services.QrService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

@Service
public class QrServiceImpl implements QrService {

    @Autowired
    private LayoutArrangementRepository layoutArrangementRepository;
    @Override
    public byte[] generateQrPdf(String projectId, String urlApp) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<List<Plot>> layout = layoutArrangementRepository.findByProjectId(projectId).get().getLayout();
        generateQrPdfFromLayout("Du an 1", "DA1", layout, out, urlApp);
        return out.toByteArray();
    }


    public void generateQrPdfFromLayout(String projectName, String projectCode,
                                               List<List<Plot>> layout, OutputStream outputStream,
                                               String baseUrl) throws Exception {
        int  column = 3;

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        Paragraph title = new Paragraph("Project Name: " + projectName, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph code = new Paragraph("Project Code: " + projectCode, subFont);
        code.setAlignment(Element.ALIGN_CENTER);
        code.setSpacingAfter(20);
        document.add(code);

        // Bảng: 3 cột (QR mỗi cột), căn chỉnh giữa
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);


        for (int blockIndex = 0; blockIndex < layout.size(); blockIndex++) {
            List<Plot> block = layout.get(blockIndex);

            for (int plotIndex = 0; plotIndex < block.size(); plotIndex++) {
                Plot plot = block.get(plotIndex);

                int row = plotIndex / column;
                int col = plotIndex % column;

                String labelLine1 = String.format("Plot: %dx%d", row + 1, col + 1);
                String labelLine2 = String.format("Block: %d", blockIndex + 1);
                String labelLine3 = plot.getTreatmentCode(); // ví dụ: "CT 2"

                String url = String.format("%s?projectId=%s&block=%d&index=%d", baseUrl, projectName, blockIndex, plotIndex);

                // Tạo QR code
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                BitMatrix matrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200, hintMap);

                int matrixWidth = matrix.getWidth();
                BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, matrixWidth, matrixWidth);
                graphics.setColor(Color.BLACK);

                for (int x = 0; x < matrixWidth; x++) {
                    for (int y = 0; y < matrixWidth; y++) {
                        if (matrix.get(x, y)) {
                            graphics.fillRect(x, y, 1, 1);
                        }
                    }
                }

                ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", qrOut);
                Image qrImage = Image.getInstance(qrOut.toByteArray());

                qrImage.scaleAbsolute(99f, 99f); // ~3.5 x 3.5 cm

                // Tạo bảng 1 cột chứa QR và 3 dòng label
                PdfPTable innerTable = new PdfPTable(1);
                innerTable.setWidthPercentage(100);

                PdfPCell qrCell = new PdfPCell(qrImage);
                qrCell.setBorder(Rectangle.NO_BORDER);
                qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell label1 = new PdfPCell(new Phrase(labelLine1, subFont));
                PdfPCell label2 = new PdfPCell(new Phrase(labelLine2, subFont));
                PdfPCell label3 = new PdfPCell(new Phrase(labelLine3, subFont));
                for (PdfPCell labelCell : List.of(label1, label2, label3)) {
                    labelCell.setBorder(Rectangle.NO_BORDER);
                    labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }

                innerTable.addCell(qrCell);
                innerTable.addCell(label1);
                innerTable.addCell(label2);
                innerTable.addCell(label3);

                // Tạo khung xám bao quanh
                PdfPCell wrapperCell = new PdfPCell(innerTable);
                wrapperCell.setBorderColor(BaseColor.LIGHT_GRAY);
                wrapperCell.setBorderWidth(1f);
                wrapperCell.setPadding(5f);
                wrapperCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(wrapperCell);
            }
        }




        int remainder = layout.stream().mapToInt(List::size).sum() % 3;
        if (remainder != 0) {
            for (int i = 0; i < 3 - remainder; i++) {
                table.addCell(new PdfPCell(new Phrase("")));
            }
        }

        document.add(table);
        document.close();
    }




}
