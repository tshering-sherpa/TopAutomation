package com.topautomation.core.data;

import com.topautomation.core.config.ConfigManager;
import com.topautomation.core.utils.ExcelReader;

import java.util.List;
import java.util.Map;

/** Shared helper behind every module's Excel data providers. */
public final class ExcelData {

    private ExcelData() {
    }

    /**
     * @param fileSettingKey config key holding the .xlsx path, e.g. "web.testdata.file"
     * @param sheetName      exact tab name
     * @return one Object[]{Map&lt;String,String&gt;} per row; rows with Run = N are dropped
     */
    public static Object[][] rows(String fileSettingKey, String sheetName) {
        String file = ConfigManager.get(fileSettingKey);
        if (file == null || file.isBlank()) {
            throw new IllegalStateException("Setting '" + fileSettingKey + "' is not defined in the module's config file");
        }
        List<Map<String, String>> rows = ExcelReader.read(file, sheetName);
        return rows.stream()
                .filter(row -> !"N".equalsIgnoreCase(row.getOrDefault("Run", "Y").trim()))
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }
}
