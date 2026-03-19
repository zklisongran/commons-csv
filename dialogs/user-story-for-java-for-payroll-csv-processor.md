# User Story：企业员工薪资批量处理工具（payroll-csv-processor）

---
## User Story

**作为** HR 专员或财务管理员，  
**我想** 把 HR 系统导出的员工信息 CSV、奖金规则 CSV 和考勤系统导出的出勤 CSV 批量输入到薪资处理工具，由工具自动完成奖金计算、个税扣除并写出工资条 CSV 和部门汇总 CSV，  
**以便** 我无需手工在 Excel 中逐行核算，减少人为误差，同时生成可直接导入银行代发系统和财务系统的标准化 CSV 文件，提升薪资发放效率并保证计算精度。
---

## 输入 / 输出一览

| 文件 | 方向 | 关键字段 |
|------|------|---------|
| `employees.csv` | 输入 | 工号、姓名、部门、基本工资、入职日期 |
| `bonus_rules.csv` | 输入 | 部门、绩效等级、奖金比例 |
| `attendance.csv` | 输入 | 工号、出勤天数、请假天数 |
| `payroll_YYYY_MM.csv` | 输出 | 姓名、应发工资、个税、实发工资 |
| `dept_summary.csv` | 输出 | 部门、人数、工资总额 |

---

## Acceptance Criteria（BDD 风格）

### Happy Path

---

#### AC-HP-01：正常读取员工 CSV 并生成工资条

```gherkin
Given employees.csv 包含合法的表头行（工号,姓名,部门,基本工资,入职日期）
  And 文件中有 3 条员工记录，部门和工资字段均不为空
  And bonus_rules.csv 中包含与这 3 名员工部门匹配的奖金规则
  And attendance.csv 中包含与这 3 名员工工号匹配的出勤记录
When 用户执行薪资处理命令，指定上述三个文件作为输入
Then 工具成功读取全部 3 条员工记录，不抛出任何异常
  And 生成的 payroll_YYYY_MM.csv 包含 3 条数据行（不含表头）
  And 每条记录的"应发工资 = 基本工资 × 出勤比例 + 奖金"，计算结果精确到分（2 位小数）
  And 每条记录的"实发工资 = 应发工资 - 个税"，个税按分段税率计算，使用 BigDecimal 无精度丢失
```

---

#### AC-HP-02：按部门正确生成汇总 CSV

```gherkin
Given payroll 计算已成功执行（满足 AC-HP-01 前置条件）
  And 3 名员工分属"研发部"和"市场部"两个部门
When 工具完成薪资计算
Then 生成的 dept_summary.csv 包含 2 条数据行，分别对应"研发部"和"市场部"
  And 每行包含该部门的人数和工资总额（含奖金后的应发合计），数值精确到分
  And 同一部门的所有员工"实发工资"之和与 dept_summary.csv 中的工资总额一致
```

---

#### AC-HP-03：奖金规则按部门 + 绩效等级正确匹配

```gherkin
Given bonus_rules.csv 中定义：研发部 A 级奖金比例 20%，市场部 A 级奖金比例 15%
  And employees.csv 中员工甲（研发部，绩效 A，基本工资 10000）
  And employees.csv 中员工乙（市场部，绩效 A，基本工资 10000）
When 工具执行薪资计算
Then 甲的奖金 = 10000 × 20% = 2000.00，乙的奖金 = 10000 × 15% = 1500.00
  And 两人的奖金金额在 payroll CSV 中精确无误（BigDecimal 乘法，不使用 double）
```

---

#### AC-HP-04：出勤比例影响应发工资

```gherkin
Given 当月应出勤 22 天
  And attendance.csv 中员工丙实际出勤 20 天、请假 2 天
  And 员工丙基本工资 8800
When 工具执行薪资计算
Then 员工丙的出勤比例 = 20 / 22（使用 BigDecimal 除法，scale=10，四舍五入）
  And 基本工资部分 = 8800 × (20/22)，结果在 payroll CSV 中精确到分
```

---

#### AC-HP-05：输出文件编码与格式符合下游系统要求

```gherkin
Given 下游银行代发系统要求 payroll CSV 使用 UTF-8 编码、逗号分隔、首行为表头
When 工具写出 payroll_YYYY_MM.csv
Then 文件编码为 UTF-8（含 BOM 或不含 BOM，依配置而定）
  And 首行为表头：姓名,应发工资,个税,实发工资
  And 字段中含逗号或换行的内容自动添加双引号包裹（commons-csv 默认处理）
```

---

### Sad Path

---

#### AC-SP-01：员工记录中部门字段为空时，工具给出明确错误提示而非 NullPointerException

```gherkin
Given employees.csv 中某条记录的"部门"字段为空字符串或缺失
When 工具尝试用部门名称查找对应的奖金规则（调用 department.equals(ruleDept)）
Then 工具不抛出 NullPointerException
  And 工具在日志 / 控制台输出明确错误消息，包含出错的工号和行号
  And 该条记录被跳过或标记为"数据异常"，其余合法记录仍正常处理并写出
```

---

#### AC-SP-02：CSV 未配置 header 时，按列名访问触发可读错误而非 IllegalStateException 崩溃

```gherkin
Given employees.csv 在解析时未启用 withHeader()（即以无 header 模式读取）
When 代码尝试通过 record.get("基本工资") 访问字段
Then 工具捕获 IllegalStateException 并输出"CSV 解析配置错误：未启用列名映射，请检查 CSVFormat 配置"
  And 工具退出并返回非零错误码，不生成不完整的输出文件
```

---

#### AC-SP-03：重复列名仅保留最后一列，工具检测并警告

```gherkin
Given employees.csv 的表头行中存在两列均命名为"备注"
When 工具使用 commons-csv withHeader() 解析该文件
Then 工具检测到重复列名，在日志输出警告："检测到重复列名 [备注]，将只保留最后一列的值，请检查源数据"
  And 工具继续处理，但在计算结果中不依赖"备注"列
  And 如果业务逻辑强依赖该列，工具输出错误并终止
```

---

#### AC-SP-04：工资字段包含非数值字符时，工具报告数据格式错误

```gherkin
Given employees.csv 中某员工的"基本工资"字段值为 "8,800"（含千位分隔符）或 "N/A"
When 工具尝试将该字段解析为 BigDecimal
Then 工具捕获 NumberFormatException，输出"第 N 行基本工资字段格式非法：[8,800]，跳过该记录"
  And 该记录不参与计算，其余记录正常处理
  And 最终输出的错误摘要包含所有跳过的行号和原始值
```

---

#### AC-SP-05：输入文件不存在或路径错误时，工具给出明确提示

```gherkin
Given 用户执行命令时指定的 employees.csv 路径不存在
When 工具尝试打开该文件
Then 工具输出"输入文件未找到：[路径]，请确认文件路径后重试"
  And 工具不生成任何输出文件
  And 工具返回非零错误码
```

---

#### AC-SP-06：bonus_rules.csv 中找不到员工部门的匹配规则

```gherkin
Given employees.csv 中某员工部门为"法务部"
  And bonus_rules.csv 中没有"法务部"对应的奖金规则
When 工具执行奖金匹配
Then 工具输出警告："员工 [工号/姓名] 所在部门 [法务部] 未找到奖金规则，奖金按 0 计算"
  And 该员工的奖金默认为 0.00，薪资计算继续正常进行
  And 输出 CSV 中该员工的奖金列标注为 0.00
```

---

#### AC-SP-07：整数除法截断导致个税计算错误——工具必须使用 BigDecimal

```gherkin
Given 某员工应发工资为 10500 元，适用税率 20%
  And 代码存在风险写法：int tax = income * 20 / 100（整数截断）
When 工具使用 BigDecimal 规范实现执行计算
Then 个税 = 10500 × 0.20 = 2100.00，无截断或精度丢失
  And payroll CSV 中个税字段值为 "2100.00"，而非因整数截断导致的错误值
```

---

## 备注（Scope & Out of Scope）

| 在范围内 | 不在范围内 |
|---------|-----------|
| 读取 / 写出本地 CSV 文件（commons-csv） | 直连 HR 系统数据库 |
| 奖金规则：部门 + 绩效等级 + 比例 | 多月滚动对比报表 |
| 个税分段税率（简化版，讲师自定义） | 社保、公积金等复杂扣款项 |
| 部门汇总 CSV | 可视化图表 / Web 界面 |
| BigDecimal 精确计算 | 多货币支持 |
| 数据校验与错误报告 | 电子签名 / 加密 |
