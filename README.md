#  API - README

## 1. 檔案架構

```
project_root/
│── src/main/java/com/example/sunset/
│   ├── controller/
│   │   ├── ExportController.java       # 負責 Excel 匯出的 API
│   │   ├── SunriseSunsetController.java # 負責日出日落查詢與儲存的 API
│   ├── dto/
│   │   ├── SunriseSunsetRequest.java   # API 請求的 DTO
│   │   ├── SunriseSunsetResponse.java  # API 回應的 DTO
│   ├── entity/
│   │   ├── SunriseSunset.java          # 資料庫對應的 Entity
│   ├── repository/
│   │   ├── SunriseSunsetRepository.java # JPA Repository
│   ├── service/
│   │   ├── SunriseSunsetService.java   # 核心服務邏輯
│   ├── DemoApplication.java            # Spring Boot 啟動類
│── src/main/resources/
│   ├── application.properties          # Spring Boot 配置文件
│── pom.xml                             # Maven 配置文件
│── sunrise_sunset_data.xlsx            # 輸出文件
```

---

## 2. 如何測試 (使用 Postman)

### 2.1 取得日出/日落時間並存入資料庫
**方法**: `POST`
**URL**: `http://localhost:8081/your_api`
**Headers**:
```
Content-Type: application/json
```
**Body** (JSON):
```json
{
   "startDate": "2024/11/28",
   "dates": 5,
   "lat": 24.2887533,
   "lng": 120.4997223
}
```
**預期回應**:
- 服務會從 `https://api.sunrise-sunset.org` 取得日出日落時間
- 若資料庫內已有相同 (日期、lat、lng) 的資料，則更新
- 若無資料，則新增資料

---

### 2.2 匯出 Excel
**方法**: `POST`
**URL**: `http://localhost:8081/your_export_api`
**Headers**:
```
Content-Type: application/json
```
**Body** (JSON):
```json
{
   "startDate": "2024/11/28",
   "dates": 5,
   "lat": 24.2887533,
   "lng": 120.4997223
}
```
**預期回應**:
- 服務會從資料庫查詢符合條件的資料
- 依日期排序
- 匯出 Excel 檔案，檔案內容格式如附檔:sunrise_sunset_data.xlsx

---

## 3. 測試結果

### 3.1 取得日出/日落時間並存入資料庫
**操作**:
- 使用 `POST http://localhost:8081/your_api`
- 輸入經緯度、日期範圍
- 觀察資料庫是否存入資料

**結果**:
- MySQL `sunrise_sunset` table 內成功存入數據
- 相同日期、lat、lng 會進行更新

### 3.2 匯出 Excel
**操作**:
- 使用 `POST http://localhost:8081/your_export_api`
- 下載的 Excel 需包含日期、日出、日落時間

**結果**:
- 下載的 Excel 正確顯示日出、日落時間，格式符合預期

---

## 4. 遇到的難點與解決方法

### 4.1 API 資料未成功存入資料庫
**問題**:
- 初期發現 `your_api` 執行後，資料庫未存入資料

**解決方法**:
- 透過 `logger.info` 確認 `repository.save(entity);` 是否被執行
- 確保 `application.properties` 正確配置 `spring.datasource.url`
- 檢查 JPA 設定，確保 `@Entity` 類別正確對應 `@Table`

### 4.2 匯出 Excel 時時間格式錯誤
**問題**:
- Excel 顯示的日出、日落時間為 UNIX 時間戳記 (毫秒數)

**解決方法**:
- 使用 `Instant.ofEpochMilli(record.getSunrise()).atZone(ZoneId.of("Asia/Taipei")).toLocalTime()` 轉換為 `HH:mm:ss`
- 轉換日期格式為 `yyyy/MM/dd`，符合需求

### 4.3 `POST http://localhost:8081/your_export_api` 無法成功回應
**問題**:
- 初期測試時，發現 `ExportController` 無法執行

**解決方法**:
- 確保 `ExportController` 使用 `@RestController`
- 在 `DemoApplication` 的 `@SpringBootApplication` 內含括 `com.example.sunset` 以確保 Bean 掃描

