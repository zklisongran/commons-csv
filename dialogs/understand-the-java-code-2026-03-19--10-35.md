# Apache Commons CSV 项目理解与上手（2026-03-19--10-35）

## 你的问题 1：本项目的主要功能、特点、优势、劣势和适用场景

### 主要观点 A：本项目是一个“读写 CSV 的 Java 库”，并且支持多种 CSV 变体
答复：
- 这是 Apache Commons 体系中的 CSV 组件，定位是为 CSV 读写提供统一、简单接口。
- 它既支持解析（CSVParser / CSVRecord），也支持输出（CSVPrinter）。

来源链接（3 个，不同出处）：
1. https://commons.apache.org/proper/commons-csv/
2. https://commons.apache.org/proper/commons-csv/apidocs/
3. https://central.sonatype.com/artifact/org.apache.commons/commons-csv?smo=true

### 主要观点 B：特点是“预定义格式 + Builder 定制 + Header/JDBC 等实用能力”
答复：
- 预定义格式覆盖 DEFAULT、EXCEL、MYSQL、POSTGRESQL_CSV、RFC4180、TDF 等。
- 可以用 Builder 对分隔符、引号、空白处理、表头处理进行定制。
- 支持按表头名读取，支持将 JDBC ResultSet 输出为 CSV。

来源链接（3 个，不同出处）：
1. https://commons.apache.org/proper/commons-csv/apidocs/
2. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html
3. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVPrinter.html

### 主要观点 C：优势是“成熟、开源治理清晰、生态整合好、工程质量要求高”
答复：
- Apache 基金会项目，许可证清晰（Apache-2.0），可商用友好。
- Maven Central 有稳定发布坐标，便于在企业构建链路接入。
- 项目站点显示包含测试报告、静态检查、覆盖率等工程化报告。

来源链接（3 个，不同出处）：
1. https://www.apache.org/licenses/LICENSE-2.0
2. https://central.sonatype.com/artifact/org.apache.commons/commons-csv?smo=true
3. https://commons.apache.org/proper/commons-csv/project-reports.html

### 主要观点 D：劣势/边界主要来自 CSV 格式本身与多实现差异
答复：
- RFC 4180 明确说明 CSV 长期缺少单一“主规范”，不同实现之间存在差异。
- 这意味着你在跨系统对接时，需要明确约定分隔符、引号、换行、header 规则。
- Commons CSV 已通过多预定义格式缓解，但并不能消除所有“上游数据不规范”问题。

来源链接（3 个，不同出处）：
1. https://datatracker.ietf.org/doc/html/rfc4180
2. https://commons.apache.org/proper/commons-csv/apidocs/
3. https://commons.apache.org/proper/commons-csv/issue-tracking.html

### 主要观点 E：适用场景是“文件导入导出、报表交换、批处理集成”
答复：
- 典型场景包括：业务 CSV 文件导入、导出报表、数据库结果导出、跨系统批量交换。
- 不适合替代关系型数据库事务能力，也不适合高并发 OLTP 主链路存储。

来源链接（3 个，不同出处）：
1. https://commons.apache.org/proper/commons-csv/apidocs/
2. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVParser.html
3. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVPrinter.html

---

## 你的问题 2：如何在 Windows 11 PowerShell 7 中编译并运行（含实际调用场景）

先说结论：
- 你的仓库里已经有 quickstart 示例；我另外给你新建了一个可运行场景应用，路径为：
  - examples/windows11-demo/InventorySummaryApp.java
  - examples/windows11-demo/input/inventory.csv
  - examples/windows11-demo/output/inventory-summary.csv
- 我已在当前机器真实运行该场景并得到输出结果（见下文“结果验证”）。

### 主要观点 A：先完成环境检查（JDK + Maven）

PowerShell 7 命令：
```powershell
java -version
mvn -v
```

成功判据：
- java 命令可返回版本号。
- mvn 命令可返回 Maven 版本、Maven home、Java version。

若 mvn 不存在（我在本机遇到该情况）：
- 按 Maven 官方安装页执行（Windows 可用 choco/scoop 或手动二进制包方式）。

来源链接（3 个，不同出处）：
1. https://maven.apache.org/install.html
2. https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
3. https://maven.apache.org/download.html

### 主要观点 B：标准编译路径（推荐）是 Maven 构建

PowerShell 7 命令：
```powershell
mvn clean package
```

成功判据：
- 控制台出现 BUILD SUCCESS。
- target 目录出现构建产物（jar、classes、测试报告等）。

说明：
- README 给出的默认建议是直接运行 mvn（默认目标会跑测试和检查）；
- 官方站点也给出从源码使用 Maven 构建。

来源链接（3 个，不同出处）：
1. https://commons.apache.org/proper/commons-csv/
2. https://github.com/apache/commons-csv
3. https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

### 主要观点 C：实际调用场景运行（已在当前仓库验证）

我新增的示例说明：
- 输入：inventory.csv（库存明细）
- 处理：筛选 ACTIVE 状态，计算 active_item_count 和 active_stock_value
- 输出：inventory-summary.csv（汇总结果）

已执行的 PowerShell 7 命令（本机可复现）：
```powershell
$cp = "examples/windows11-demo/target;target/classes;$env:USERPROFILE\.m2\repository\commons-io\commons-io\2.21.0\commons-io-2.21.0.jar;$env:USERPROFILE\.m2\repository\commons-codec\commons-codec\1.21.0\commons-codec-1.21.0.jar"
New-Item -ItemType Directory -Force -Path "examples/windows11-demo/target" | Out-Null
javac -encoding UTF-8 -cp "target/classes" -d "examples/windows11-demo/target" "examples/windows11-demo/InventorySummaryApp.java"
java -cp $cp InventorySummaryApp
```

成功判据（我在本机看到的结果）：
- 终端输出：
  - active_item_count=3
  - active_stock_value=4918.50
- 输出文件内容：
  - metric,value
  - active_item_count,3
  - active_stock_value,4918.50

来源链接（3 个，不同出处）：
1. https://commons.apache.org/proper/commons-csv/apidocs/
2. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVParser.html
3. https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVPrinter.html

### 我在当前机器遇到的真实限制（如实说明）
- 当前环境中，mvn 命令未安装成功（winget 未检索到标准 Apache Maven 包 ID）。
- 因此“mvn 全流程构建/测试”我无法在本机直接复跑到完成。
- 但我已经通过 javac/java + 本机 .m2 依赖，完成了实际业务场景运行并验证输出。

---

## 你的问题 3：如何运行本项目自动化测试

### 主要观点 A：全量单元测试入口是 Maven test（或按项目默认目标）

PowerShell 7 命令：
```powershell
mvn test
```

或执行更完整校验：
```powershell
mvn
```

成功判据：
- 控制台出现测试执行摘要（Tests run / Failures / Errors / Skipped）。
- target/surefire-reports 下生成 txt/xml 报告。

来源链接（3 个，不同出处）：
1. https://maven.apache.org/surefire/maven-surefire-plugin/
2. https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
3. https://commons.apache.org/proper/commons-csv/project-reports.html

### 主要观点 B：定向执行某个测试类可用 Surefire 的 -Dtest

PowerShell 7 命令（示例）：
```powershell
mvn -Dtest=CSVParserTest test
```

成功判据：
- 只执行目标测试类，控制台与 surefire 报告中可见对应类名。

来源链接（3 个，不同出处）：
1. https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html
2. https://maven.apache.org/surefire/maven-surefire-plugin/
3. https://maven.apache.org/plugins/index.html

### 主要观点 C：需要临时跳过测试时，明确区分 skipTests 与 maven.test.skip

PowerShell 7 命令：
```powershell
mvn install -DskipTests
mvn install -Dmaven.test.skip=true
```

说明：
- skipTests：跳过执行测试，但仍可能编译测试代码。
- maven.test.skip=true：连测试编译也跳过（影响更大）。
- 仅在你明确需要加速构建时使用，不建议常态化。

来源链接（3 个，不同出处）：
1. https://maven.apache.org/surefire/maven-surefire-plugin/examples/skipping-tests.html
2. https://maven.apache.org/surefire/maven-surefire-plugin/
3. https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html

---

## 你可以直接照抄执行的最短落地路径（Windows 11 + PowerShell 7）

```powershell
# 1) 进入项目根目录
Set-Location D:\workspace\commons-csv-master

# 2) 检查 Java/Maven
java -version
mvn -v

# 3) 推荐：Maven 全构建
mvn clean package

# 4) 编译并运行我新增的场景应用（若你已完成 Maven 构建）
$cp = "examples/windows11-demo/target;target/classes;$env:USERPROFILE\.m2\repository\commons-io\commons-io\2.21.0\commons-io-2.21.0.jar;$env:USERPROFILE\.m2\repository\commons-codec\commons-codec\1.21.0\commons-codec-1.21.0.jar"
New-Item -ItemType Directory -Force -Path "examples/windows11-demo/target" | Out-Null
javac -encoding UTF-8 -cp "target/classes" -d "examples/windows11-demo/target" "examples/windows11-demo/InventorySummaryApp.java"
java -cp $cp InventorySummaryApp

# 5) 查看结果文件
Get-Content .\examples\windows11-demo\output\inventory-summary.csv

# 6) 运行自动化测试
mvn test

# 7) 只跑一个测试类
mvn -Dtest=CSVParserTest test
```

---

## 常见失败与排查

1. 报错：mvn 无法识别
- 原因：Maven 未安装或 PATH 未生效。
- 处理：按 Maven 官方安装页安装后，重开 PowerShell，再执行 mvn -v。

2. 报错：NoClassDefFoundError（如 commons-io / commons-codec）
- 原因：你用 javac/java 直接跑示例时未把依赖 jar 放进 classpath。
- 处理：使用上文给出的 $cp 变量，确认 .m2 对应 jar 路径存在。

3. 报错：测试未执行或报告目录为空
- 原因：可能执行了跳过测试参数，或命令不是 test 阶段。
- 处理：先用 mvn test；再检查 target/surefire-reports。

---

## 本次我在仓库中的新增内容
- examples/windows11-demo/InventorySummaryApp.java
- examples/windows11-demo/input/inventory.csv
- examples/windows11-demo/output/inventory-summary.csv

如果你愿意，我下一步可以继续为你补一份“从 0 到 1 的调试版手册”（包含每一步失败截图位说明和分支排障流程）。
