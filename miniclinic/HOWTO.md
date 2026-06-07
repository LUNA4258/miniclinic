# Ch09-B｜Controller、Thymeleaf 模板與 REST API 設計

## 本週目標

在 Week 1 的基礎上，把 MiniClinic 從「只有健康檢查端點」升級成「有真正頁面和 API 的診所系統」：

- 首頁（含導覽列）
- 醫師清單頁（可依科別篩選）
- 醫師詳細頁
- 線上掛號表單（本週不寫 DB，下週才真的存）
- REST API：`/api/doctors`、`/api/doctors/{id}`、`/api/departments`

---

## 這週新增了什麼

相較於 Week 1，Week 2 新增：

| 新增項目 | 說明 |
|---|---|
| `spring-boot-starter-thymeleaf` | HTML 模板引擎依賴 |
| `model/Doctor.java` | 醫師資料類別（doctorId/name/department/specialty） |
| `model/DoctorRepository.java` | `@Component` 硬編碼的五位醫師（本週沒有 DB） |
| `model/AppointmentForm.java` | 掛號表單 POJO |
| `controller/HomeController.java` | `GET /` → `home.html` |
| `controller/DoctorPageController.java` | `GET /doctors`、`GET /doctors/{id}` |
| `controller/DoctorApiController.java` | `GET /api/doctors`、`/api/doctors/{id}`、`/api/departments` |
| `controller/AppointmentController.java` | `GET/POST /appointment/new` |
| 6 個 Thymeleaf 模板 | home, doctors, doctor-detail, doctor-not-found, appointment-new, appointment-result |
| `api-tests.http` | REST Client 測試檔案 |

---

## 你會看到什麼

啟動後可訪問：

| URL | 預期結果 |
|---|---|
| `http://localhost:8080/` | 首頁，含導覽列與科別列表 |
| `http://localhost:8080/doctors` | 五位醫師的表格清單 |
| `http://localhost:8080/doctors?department=內科` | 僅顯示內科醫師（林佩君醫師） |
| `http://localhost:8080/doctors/D001` | 陳志明醫師詳細頁 |
| `http://localhost:8080/doctors/D999` | 「找不到醫師」頁面 |
| `http://localhost:8080/appointment/new` | 掛號表單 |
| `http://localhost:8080/api/health` | `{"status":"ok","service":"miniclinic"}` |
| `http://localhost:8080/api/doctors` | JSON 陣列，5 筆醫師資料 |
| `http://localhost:8080/api/doctors/D001` | 單筆醫師 JSON |
| `http://localhost:8080/api/doctors/D999` | HTTP 404 |
| `http://localhost:8080/api/departments` | JSON 陣列，5 個科別 |

---

## 環境需求

- **Java 17 JDK**（Eclipse Temurin，https://adoptium.net）
- **VS Code + Extension Pack for Java**
- **不需要另外安裝 Maven**（專案內含 Maven Wrapper）

---

## 怎麼跑

### Windows（命令提示字元）

```cmd
mvnw.cmd spring-boot:run
```

### Mac / Linux（Terminal）

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

啟動成功後，Terminal 會顯示類似：

```
Started MiniclinicApplication in X.XXX seconds
```

打開瀏覽器訪問 `http://localhost:8080/` 即可。

按 `Ctrl+C` 停止服務。

---

## 專案結構

```
miniclinic/
├── pom.xml
├── .gitignore
├── mvnw / mvnw.cmd          ← Maven Wrapper（不需安裝 Maven）
├── .mvn/wrapper/
│   └── maven-wrapper.properties
├── api-tests.http           ← REST Client 測試檔
├── HOWTO.md
└── src/
    ├── main/
    │   ├── java/tw/edu/fju/miniclinic/
    │   │   ├── MiniclinicApplication.java
    │   │   ├── controller/
    │   │   │   ├── HealthController.java     ← 沿用 Week 1
    │   │   │   ├── HomeController.java       ← 新增
    │   │   │   ├── DoctorPageController.java ← 新增
    │   │   │   ├── DoctorApiController.java  ← 新增
    │   │   │   └── AppointmentController.java← 新增
    │   │   └── model/
    │   │       ├── Doctor.java               ← 新增
    │   │       ├── DoctorRepository.java     ← 新增
    │   │       └── AppointmentForm.java      ← 新增
    │   └── resources/
    │       ├── application.properties
    │       └── templates/
    │           ├── home.html
    │           ├── doctors.html
    │           ├── doctor-detail.html
    │           ├── doctor-not-found.html
    │           ├── appointment-new.html
    │           └── appointment-result.html
    └── test/
        └── java/tw/edu/fju/miniclinic/
            └── MiniclinicApplicationTests.java
```

---

## 關鍵程式碼解說

### 1. `@Controller` 與 `@RestController` 的差別

```java
// @Controller → 回傳模板名稱（渲染 HTML 頁面）
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";  // 對應 templates/home.html
    }
}

// @RestController → 直接回傳物件（自動轉 JSON）
@RestController
public class DoctorApiController {
    @GetMapping("/api/doctors")
    public List<Doctor> getDoctors() {
        return doctorRepo.findAll();  // 自動序列化為 JSON
    }
}
```

### 2. `@RequestParam` vs `@PathVariable`

```java
// @RequestParam：從 URL 查詢字串取值
// 訪問 /doctors?department=內科 時，department = "內科"
@GetMapping("/doctors")
public String listDoctors(@RequestParam(required = false) String department, Model model) { ... }

// @PathVariable：從 URL 路徑取值
// 訪問 /doctors/D001 時，doctorId = "D001"
@GetMapping("/doctors/{doctorId}")
public String doctorDetail(@PathVariable String doctorId, Model model) { ... }
```

### 3. Thymeleaf 的 `th:each` 迴圈與 `th:classappend`

```html
<!-- 迴圈：對每個 dept 產生一個 <a> 標籤 -->
<a th:each="dept : ${departments}"
   th:href="@{/doctors(department=${dept})}"
   th:text="${dept}"
   th:classappend="${dept == selectedDept} ? 'active'">科別</a>
```

`th:classappend` 是附加 CSS class（不是替換），讓選中的科別顯示不同樣式。

### 4. `DoctorRepository` 本週是 `@Component` 類別

```java
@Component
public class DoctorRepository {
    // 資料寫死在程式裡（沒有資料庫）
    private static final List<Doctor> DOCTORS = Arrays.asList(
        new Doctor("D001", "陳志明醫師", "家醫科", "一般內科、慢性病管理"),
        // ...
    );
    // findAll(), findById(), findByDepartment(), findAllDepartments()
}
```

**下週的重點**：Week 3 會把這個類別改成 `interface extends JpaRepository`，類別名稱完全不變——Controller 一個字都不用改！這就是「依賴注入」的威力。

---

## 用 curl / 瀏覽器試試看

```bash
# 所有醫師（JSON）
curl http://localhost:8080/api/doctors

# 依科別篩選
curl "http://localhost:8080/api/doctors?department=內科"

# 單一醫師
curl http://localhost:8080/api/doctors/D001

# 找不到的醫師（404）
curl -i http://localhost:8080/api/doctors/D999

# 所有科別
curl http://localhost:8080/api/departments
```

或在 VS Code 打開 `api-tests.http`，每個 `###` 區塊上方點「Send Request」。

---

## 常見問題

**Q：訪問 `/doctors` 出現 Whitelabel Error Page（白標錯誤頁）？**

A：通常是以下原因之一：
1. `templates/doctors.html` 檔案路徑不對（確認在 `src/main/resources/templates/`）
2. Controller 忘了 `@Controller` 或 `@GetMapping` 拼錯
3. Java 類別不在 `tw.edu.fju.miniclinic` 或其子 package 下

**Q：頁面顯示但資料是空的（表格空白）？**

A：
1. `DoctorRepository` 是否有 `@Component` 注解？
2. `Doctor` 類別是否有 getter（例如 `getName()`）？
3. Thymeleaf 的 `${doctor.name}` 是呼叫 `getName()`，大小寫要對

**Q：POST 表單提交後資料是 null？**

A：
1. `AppointmentForm` 必須有無參建構子 `public AppointmentForm() {}`
2. 所有欄位都要有 setter（例如 `setChartNo(...)`）
3. `th:field="*{chartNo}"` 裡的名稱要和 setter 一致

**Q：為什麼 `DoctorRepository` 叫 Repository 但不是 interface？**

A：這是刻意的設計。Week 2 是一個「用假資料練習 Controller」的階段，DoctorRepository 只是個普通 Spring Component。Week 3 接資料庫時，會把它改成 `interface extends JpaRepository`——**類別名完全不變，Controller 程式碼零修改**。這是「依賴注入」讓程式更有彈性的最佳示範。

---

## 本週重點回顧

- `@Controller` 回傳模板名稱（HTML 頁面），`@RestController` 回傳物件（JSON 資料）
- `@RequestParam` 取 URL 查詢字串（`?key=value`），`@PathVariable` 取 URL 路徑片段（`/path/{var}`）
- Thymeleaf 的 `th:each`、`th:text`、`th:if`、`th:href` 是最常用的四個語法
- `model.addAttribute("key", value)` 讓模板可以用 `${key}` 取得資料
- 表單綁定：`th:object` + `th:field` + `@ModelAttribute` 三者配合

---

## 接下來：Week 3（Ch09-C）

下週開始接真實資料庫！會做到：

- 引入 **SQLite** + **JPA** 依賴
- 把 `Doctor` 升級為 `@Entity`（加資料庫對應注解）
- 新增 `Patient`、`Appointment` 兩個 Entity
- **`DoctorRepository` 從 `@Component` 類別改成 `interface extends JpaRepository`**（類別名不變！）
- 透過 `data.sql` 預載 5 醫師、3 病患、3 掛號資料
- 線上掛號表單真的把資料寫進 SQLite 資料庫，重啟後還在

---

## 繳交方式

將整個 `miniclinic` 資料夾（**刪除 `target/` 子資料夾後**）壓縮為 zip 檔：

- 檔名：`學號_姓名_Ch09B.zip`
- 上傳至教學平台
