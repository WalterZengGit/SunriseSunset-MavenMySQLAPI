# SunriseSunset-MavenMySQLAPI

## 1. Project Structure

```
project_root/
│── src/main/java/com/example/sunset/
│   ├── controller/
│   │   ├── ExportController.java       # API for exporting Excel
│   │   ├── SunriseSunsetController.java # API for retrieving and storing sunrise/sunset data
│   ├── dto/
│   │   ├── SunriseSunsetRequest.java   # DTO for API request
│   │   ├── SunriseSunsetResponse.java  # DTO for API response
│   ├── entity/
│   │   ├── SunriseSunset.java          # Entity mapping for database
│   ├── repository/
│   │   ├── SunriseSunsetRepository.java # JPA Repository
│   ├── service/
│   │   ├── SunriseSunsetService.java   # Core service logic
│   ├── DemoApplication.java            # Spring Boot main class
│── src/main/resources/
│   ├── application.properties          # Spring Boot configuration file
│   ├── sunrise_sunset_data.xlsx          # example file
│── pom.xml                              # Maven configuration file
```

---

## 2. How to Test (Using Postman)

### 2.1 Retrieve and Store Sunrise/Sunset Data
**Method**: `POST`
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
**Expected Outcome**:
- Retrieves sunrise and sunset data from `https://api.sunrise-sunset.org`
- If a record with the same (date, lat, lng) exists, it updates the existing record
- If no record exists, it inserts a new one

---

### 2.2 Export to Excel
**Method**: `POST`
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
**Expected Outcome**:
- Queries the database for records matching the criteria
- Sorts results by date
- Exports an Excel file As: file resources/sunrise_sunset_data.xlsx

---

## 3. Testing Results

### 3.1 Retrieve and Store Sunrise/Sunset Data
**Steps**:
- Use `POST http://localhost:8081/your_api`
- Enter latitude, longitude, start date, and days
- Verify if data is stored in the database

**Outcome**:
- The MySQL `sunrise_sunset` table successfully stores the retrieved data
- If the same (date, lat, lng) record exists, it is updated

### 3.2 Export to Excel
**Steps**:
- Use `POST http://localhost:8081/your_export_api`
- The downloaded Excel file should contain the correct date, sunrise, and sunset times

**Outcome**:
- The exported Excel file displays the correct sunrise/sunset times in the expected format

---

## 4. Challenges and Solutions

### 4.1 Data Not Persisting in Database
**Issue**:
- Initially, data was not being stored after calling `your_api`

**Solution**:
- Used `logger.info` to verify if `repository.save(entity);` was being executed
- Ensured `application.properties` was correctly configured for `spring.datasource.url`
- Verified that the `@Entity` class was correctly mapped to `@Table`

### 4.2 Incorrect Time Format in Exported Excel
**Issue**:
- Excel displayed sunrise/sunset times as UNIX timestamps (milliseconds)

**Solution**:
- Used `Instant.ofEpochMilli(record.getSunrise()).atZone(ZoneId.of("Asia/Taipei")).toLocalTime()` to convert to `HH:mm:ss`
- Formatted dates as `yyyy/MM/dd` to match the required format

### 4.3 `POST http://localhost:8081/your_export_api` Not Returning Data
**Issue**:
- Initially, the `ExportController` did not execute properly

**Solution**:
- Ensured `ExportController` was correctly annotated with `@RestController`
- Verified `DemoApplication` used `@SpringBootApplication` and included `com.example.sunset` to enable proper Bean scanning
