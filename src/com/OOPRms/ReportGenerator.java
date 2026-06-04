package com.OOPRms;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class ReportGenerator {

    private static final String REPORTS_PATH = "reports/";
    private static final String OUTPUT_PATH  = "reports/output/";

    private static void exportToPdf(JasperPrint print, String outputPath) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));
        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        config.setAllowedPermissionsHint("PRINTING");
        exporter.setConfiguration(config);
        exporter.exportReport();
    }

    public static void generateMenuReport() {
        try {
            new File(OUTPUT_PATH).mkdirs();

            String jrxml  = REPORTS_PATH + "menu_report.jrxml";
            String jasper = REPORTS_PATH + "menu_report.jasper";	

            JasperCompileManager.compileReportToFile(jrxml, jasper);

            Connection conn = DatabaseConnection.connect();
            JasperPrint print = JasperFillManager.fillReport(
                jasper, new HashMap<>(), conn);

            String output = OUTPUT_PATH + "menu_report.pdf";
            exportToPdf(print, output);

            System.out.println("Menu report generated: " + output);

        } catch (JRException e) {
            System.out.println("Menu report error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void generateOrdersReport() {
        try {
            new File(OUTPUT_PATH).mkdirs();

            String jrxml  = REPORTS_PATH + "orders_report.jrxml";
            String jasper = REPORTS_PATH + "orders_report.jasper";

            JasperCompileManager.compileReportToFile(jrxml, jasper);

            Connection conn = DatabaseConnection.connect();
            JasperPrint print = JasperFillManager.fillReport(
                jasper, new HashMap<>(), conn);

            String output = OUTPUT_PATH + "orders_report.pdf";
            exportToPdf(print, output);

            System.out.println("Orders report generated: " + output);

        } catch (JRException e) {
            System.out.println("Orders report error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void generateBillingReport(String customerName, double grandTotal) {
        try {
            new File(OUTPUT_PATH).mkdirs();

            String jrxml  = REPORTS_PATH + "billing_report.jrxml";
            String jasper = REPORTS_PATH + "billing_report.jasper";

            JasperCompileManager.compileReportToFile(jrxml, jasper);

            Map<String, Object> params = new HashMap<>();
            params.put("CUSTOMER_NAME", customerName);
            params.put("GRAND_TOTAL",   grandTotal);

            Connection conn = DatabaseConnection.connect();
            JasperPrint print = JasperFillManager.fillReport(
                jasper, params, conn);

            String output = OUTPUT_PATH + "billing_report.pdf";
            exportToPdf(print, output);

            System.out.println("Billing report generated: " + output);

        } catch (JRException e) {
            System.out.println("Billing report error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}