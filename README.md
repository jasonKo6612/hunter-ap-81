# AP81 專案

## 專案說明

**AP81** - 104人選聯絡紀錄處理程式

主要功能：更新聯絡紀錄中人選姓名資訊
- 查詢 `contact_rec` 表中 `rid<>'0'` 且聯絡人姓名為空的記錄
- 從 `Resume` 表中取得對應人選的中文姓名或英文姓名
- 更新 `Contact_Rec` 表中的 `contact_name` 欄位

## 目錄結構
```
hunter-ap-81/
├── build.gradle          # Gradle 建構檔案
├── settings.gradle       # Gradle 設定檔案
├── gradlew               # Gradle Wrapper (Unix/Mac)
├── gradlew.bat           # Gradle Wrapper (Windows)
├── run.sh                # 執行腳本（部署用）
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── e104/          # Base package
│       │           └── AP81.java  # 主程式
│       └── resources/             # 資源檔案目錄（目前為空）
└── build/
    └── libs/                      # 建構輸出目錄
        ├── AP81.jar               # 可執行 JAR 檔案
        └── run.sh                 # 自動複製的執行腳本
```

## 技術架構

### Java 版本
- Java 8 (JDK 1.8)

### 建構工具
- Gradle 7.x
- Spring Boot 2.7.18
- Spring Dependency Management 1.0.15

### Base Package
- `com.e104`

## 依賴套件

### 外部 JAR 檔案
- **HTUtil.jar** - 104 內部工具庫
  - 路徑：`/Users/jason.ko/Work/Patch_lib/HTUtil.jar`
  - 包含：`com.ht.util.XmlGlobalHandlerNewAP` 配置處理類別
  - 用途：讀取資料庫連線配置、日誌路徑等設定

### Maven 依賴
```
mysql:mysql-connector-java:5.1.49
├── MySQL JDBC 驅動程式
├── 用於連接 MySQL 資料庫

ch.qos.logback:logback-classic:1.2.11
├── ch.qos.logback:logback-core:1.2.12 (傳遞性依賴)
├── org.slf4j:slf4j-api:1.7.36 (傳遞性依賴)
├── 日誌框架，取代傳統檔案式日誌
├── 支援自動日誌滾動、多輸出目標等功能

org.codehaus.janino:janino:3.1.9
├── Logback 條件式配置支援（可選）

javax.mail:mail:1.4.7
├── javax.activation:activation:1.1 (傳遞性依賴)
├── JavaMail API (專案中已引入但未使用)

org.jdom:jdom:1.1
├── XML 處理庫 (專案中已引入但未使用)
```

### 完整依賴樹
```
compileClasspath
├── HTUtil.jar (external)
├── mysql:mysql-connector-java:5.1.49
├── ch.qos.logback:logback-classic:1.2.11
│   ├── ch.qos.logback:logback-core:1.2.12
│   └── org.slf4j:slf4j-api:1.7.36
├── org.codehaus.janino:janino:3.1.9
├── javax.mail:mail:1.4.7
│   └── javax.activation:activation:1.1
└── org.jdom:jdom:1.1
```

## 建置專案

### 必要條件
1. **Java 8 或以上版本**
   ```bash
   java -version  # 檢查 Java 版本
   ```

2. **HTUtil.jar 外部依賴**
   - 檔案位置：`/Users/jason.ko/Work/Patch_lib/HTUtil.jar`
   - 確認檔案存在且可讀取

3. **資料庫配置**
   - 需要 XmlGlobalHandlerNewAP 讀取的全域配置檔案
   - 配置項目：
     - `dsn1.driver` - JDBC 驅動類別
     - `dsn1.database` - 資料庫連線 URL
     - `dsn1.username` - 資料庫使用者名稱
     - `dsn1.password` - 資料庫密碼
     - `apini.logpath` - 日誌檔案路徑

### 建置步驟

```bash
# 1. 清理並建置專案
./gradlew clean build

# 2. 僅編譯（不執行測試）
./gradlew compileJava

# 3. 產生可執行 JAR 檔案
./gradlew bootJar

# 4. 查看依賴關係
./gradlew dependencies

# 5. 查看專案資訊
./gradlew properties
```

**建置成功後：**
- JAR 檔案：`build/libs/AP81.jar` (~3.8MB)
- 執行腳本：`build/libs/run.sh`（自動複製）

## 執行程式

### 本地開發環境
```bash
# 方式 1：使用 Gradle 執行
./gradlew bootRun

# 方式 2：直接執行 JAR
java -jar build/libs/AP81.jar

# 方式 3：使用 JVM 參數
java -server -Xms64m -Xmx64m -XX:+UseParallelGC -jar build/libs/AP81.jar
```

### 生產環境部署
參考 `run.sh` 腳本，主要配置：
- **JAVA_HOME**：`/opt/jvm/openjdk8`
- **LIB_DIR**：`/opt/AP/Patch_lib`（HTUtil.jar 位置）
- **AP_PROG**：`/opt/AP/Patch_program`（程式部署目錄）
- **JVM 參數**：`-server -Xms64m -Xmx64m -XX:+UseParallelGC`

```bash
# 部署步驟
sudo cp build/libs/AP81.jar /opt/AP/Patch_program/81/
sudo cp build/libs/run.sh /opt/AP/Patch_program/81/
cd /opt/AP/Patch_program/81
sudo chmod +x run.sh
sudo ./run.sh
```

## 程式執行流程

1. **初始化階段**
   - 載入 XmlGlobalHandlerNewAP 配置（AP ID: 81）
   - 建立日誌檔案：`{logpath}/AP81_yyyyMMdd.log`
   - 記錄開始時間

2. **資料處理階段**
   - 查詢 `contact_rec` 表：找出 `rid<>'0'` 且 `contact_name` 為空的記錄
   - 針對每筆記錄：
     - 從 `Resume` 表查詢對應的 `CName`（中文姓名）或 `EName`（英文姓名）
     - 優先使用中文姓名，若無則使用英文姓名
     - 更新 `Contact_Rec` 表的 `contact_name` 欄位

3. **結束階段**
   - 記錄結束時間
   - 關閉資料庫連線
   - 關閉日誌檔案

## 資料庫結構

### 相關資料表

**contact_rec（聯絡紀錄表）**
- `contact_id` - 聯絡紀錄 ID（主鍵）
- `rid` - 人選編號（Resume ID）
- `contact_name` - 聯絡人姓名（本程式更新此欄位）

**Resume（履歷表）**
- `Rid` - 人選編號（主鍵）
- `CName` - 中文姓名
- `EName` - 英文姓名

## 日誌檔案

### 日誌框架
使用 **Logback** 作為日誌框架，配置檔案：`src/main/resources/logback.xml`

### 功能特色
- ✅ 自動日誌滾動（每日建立新檔案）
- ✅ 同時輸出到控制台和檔案
- ✅ 保留 30 天歷史日誌
- ✅ 日誌總大小限制 1GB
- ✅ UTF-8 編碼支援

**檔案命名規則**：`AP81_yyyyMMdd.log`

**日誌格式**：
```
檔案: yyyy/MM/dd HH:mm:ss LEVEL - message
控制台: yyyy/MM/dd HH:mm:ss [thread] LEVEL logger - message
```

**日誌內容範例**：
```
2025/11/21 16:30:15 INFO - ========== START : 2025/11/21 16:30:15 ==========
2025/11/21 16:30:15 INFO - ##現有連絡紀錄中 Rid<>0 之人選編號)SQL：...
2025/11/21 16:30:15 INFO - ##                               人選編號：12345
2025/11/21 16:30:20 INFO - ==========  END  : 2025/11/21 16:30:20 ==========
```

**日誌位置**：由 `apini.logpath` 配置決定

### 自訂日誌配置

使用外部配置檔案：
```bash
java -Dlogback.configurationFile=/path/to/custom-logback.xml -jar AP81.jar
```

啟用 Logback debug 模式：
```bash
java -Dlogback.debug=true -jar AP81.jar
```

手動指定日誌路徑：
```bash
java -DLOG_PATH=/custom/log/path -DLOG_FILE=AP81 -jar AP81.jar
```

## 開發歷史

- **2008/08/13** - Sean.Chen 建立專案
- **2008/11/12** - Sean.chen - BEP00-00001-1237 獵才派遣共用元件整合專案
- **2012/09/06** - sally.huang - db migration
- **2012/10/08** - Josie Wu - BEP00-00001-1462 MySQL轉換作業專案
- **2015/10/30** - Josie Wu - BEP00-00001-1538 機敏性欄位加密處理(AES)(1)
- **2016/09/05** - Peter.Tsai - 調整AP架構
- **2023/01/17** - Peter Tsai - HTHUNTERREQ-1237 [獵才]AP 版更
- **2025/11/21** - 整合 Logback 日誌框架，取代傳統檔案式日誌

## 注意事項

1. **外部依賴**
   - 必須確保 HTUtil.jar 檔案存在且路徑正確
   - 本地開發：`/Users/jason.ko/Work/Patch_lib/HTUtil.jar`
   - 生產環境：`/opt/AP/Patch_lib/HTUtil.jar`

2. **配置檔案**
   - XmlGlobalHandlerNewAP 需要正確的配置檔案
   - 確認 AP ID 81 的配置已正確設定

3. **資料庫權限**
   - 需要對 `contact_rec` 表的 SELECT 和 UPDATE 權限
   - 需要對 `Resume` 表的 SELECT 權限

4. **執行權限**
   - 生產環境使用 root 權限執行
   - 確保日誌目錄有寫入權限

5. **資料安全**
   - 本程式處理人選個資，需注意資料安全規範
   - 日誌檔案中會記錄人選編號

## 疑難排解

### 編譯錯誤
```bash
# 檢查 Java 版本
java -version

# 清理並重新建置
./gradlew clean build --refresh-dependencies
```

### 找不到 HTUtil.jar
- 檢查檔案路徑是否正確
- 修改 `build.gradle` 中的 `implementation files()` 路徑

### 執行時找不到類別
- 確認 Spring Boot 的 mainClass 設定正確
- 檢查 JAR 檔案是否完整建置

### 資料庫連線失敗
- 檢查 XmlGlobalHandlerNewAP 配置檔案
- 確認資料庫服務運行中
- 驗證連線參數（URL、使用者名稱、密碼）

## 相關連結

- Spring Boot 2.7.x 文件：https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/
- MySQL Connector/J：https://dev.mysql.com/downloads/connector/j/
- Gradle User Manual：https://docs.gradle.org/current/userguide/userguide.html

