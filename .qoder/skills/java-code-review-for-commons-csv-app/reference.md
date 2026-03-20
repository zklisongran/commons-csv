# Apache Commons CSV 代码审查标准详解

## 各类Bug详细检查指南

### 1. 空指针异常 (NullPointerException)

#### 检查重点位置
- **CSVParser.java**: `getHeaderMapRaw()` 方法返回值使用
- **CSVRecord.java**: `values` 数组访问
- **ExtendedBufferedReader.java**: `lastChar` 状态处理

#### 典型问题代码模式
```java
// ❌ 危险写法 - 缺少null检查
Map<String, Integer> headerMap = parser.getHeaderMapRaw();
int columnIndex = headerMap.get("columnName"); // 可能NPE

// ✅ 安全写法 - 添加null检查
Map<String, Integer> headerMap = parser.getHeaderMapRaw();
if (headerMap != null) {
    Integer index = headerMap.get("columnName");
    if (index != null) {
        // 安全使用index
    }
}
```

#### 审查要点
- 所有Map.get()调用前检查Map是否为null
- 数组访问前验证数组非null且长度合适
- 对象方法调用前确认对象引用非null

### 2. 数组越界异常 (ArrayIndexOutOfBoundsException)

#### 关键检查点
- **CSVRecord.get(int index)**: 索引边界验证
- 循环遍历时的索引范围检查
- 多字节字符处理的缓冲区索引

#### 问题示例
```java
// ❌ 存在越界风险
public String getValue(int index) {
    return values[index]; // 没有边界检查
}

// ✅ 正确的边界检查
public String getValue(int index) {
    if (index < 0 || index >= values.length) {
        throw new ArrayIndexOutOfBoundsException(
            String.format("Index %d out of bounds [0, %d)", index, values.length));
    }
    return values[index];
}
```

### 3. 资源泄露 (Resource Leak)

#### 关键位置
- **CSVParser.close()** 方法实现
- 文件流和Reader的生命周期管理
- 异常处理路径中的资源清理

#### 最佳实践
```java
// ✅ 推荐的资源管理方式
try (CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
    // 处理CSV数据
} catch (IOException e) {
    // 异常处理
    throw new RuntimeException("Failed to parse CSV", e);
}
// 资源自动关闭，无需手动调用close()

// ❌ 避免的写法
CSVParser parser = CSVFormat.DEFAULT.parse(reader);
try {
    // 处理数据
} finally {
    parser.close(); // 可能抛出异常，导致资源泄露
}
```

### 4. 编码问题 (Encoding Issues)

#### 关注点
- **ExtendedBufferedReader**: 字符编码处理
- UTF-8/UTF-16代理对(surrogate pairs)处理
- BOM(Byte Order Mark)识别和处理

#### 常见陷阱
```java
// ❌ 编码处理不当
byte[] bytes = "测试".getBytes(); // 默认编码可能不一致

// ✅ 明确指定编码
byte[] bytes = "测试".getBytes(StandardCharsets.UTF_8);
```

### 5. 并发安全问题 (Concurrency Issues)

#### 检查要点
- **CSVPrinter**: ReentrantLock的正确使用
- 共享可变状态的同步保护
- 静态变量的线程安全处理

#### 安全模式
```java
// ✅ 线程安全的实现
public final class CSVPrinter implements Flushable, Closeable {
    private final ReentrantLock lock = new ReentrantLock();
    
    public void print(final Object value) throws IOException {
        lock.lock();
        try {
            // 临界区代码
            printRaw(value);
        } finally {
            lock.unlock(); // 确保锁释放
        }
    }
}
```

### 6. 内存泄漏 (Memory Leak)

#### 常见场景
- 大文件处理时集合对象未及时清理
- 对象复用时状态未重置
- 缓冲区无限制增长

#### 预防措施
```java
// ✅ 及时清理集合
private void processRecords() {
    List<CSVRecord> records = new ArrayList<>();
    try {
        // 处理记录
        for (CSVRecord record : parser) {
            records.add(record);
            // 处理逻辑
        }
    } finally {
        records.clear(); // 及时释放内存
    }
}
```

## 代码质量评估标准

### 命名规范
- 类名：大驼峰命名法 (CamelCase)
- 方法名：小驼峰命名法 (camelCase)
- 常量：全大写加下划线 (UPPER_CASE)
- 包名：全小写 (org.apache.commons.csv)

### 注释要求
```java
/**
 * 简洁明了的方法说明
 *
 * @param parameter 参数说明
 * @return 返回值说明
 * @throws Exception 异常说明
 * @since 版本号
 */
public ReturnType methodName(ParameterType parameter) throws Exception {
    // 实现代码
}
```

### 异常处理原则
1. **早抛出，晚捕获** - 在问题发生时立即抛出异常
2. **具体异常优于通用异常** - 使用具体的异常类型
3. **异常信息要详细** - 包含足够的上下文信息
4. **资源清理要彻底** - 确保finally块中释放资源

### 性能优化建议
1. **避免重复计算** - 缓存计算结果
2. **减少对象创建** - 重用对象和缓冲区
3. **选择合适的数据结构** - 根据使用场景选择
4. **延迟初始化** - 只在需要时创建对象

## 测试覆盖要求

### 单元测试标准
- **边界值测试**: 最小值、最大值、边界值
- **异常路径测试**: 验证异常情况的正确处理
- **并发测试**: 多线程环境下的行为验证
- **性能测试**: 关键路径的性能基准

### 集成测试要求
- **真实数据测试**: 使用实际CSV文件进行测试
- **兼容性测试**: 不同CSV格式和编码的处理
- **错误恢复测试**: 异常情况下的系统恢复能力

## 安全审查要点

### 输入验证
- 所有外部输入都要验证
- 防止注入攻击
- 限制输入长度和复杂度

### 输出编码
- 敏感信息输出时要适当编码
- 防止信息泄露
- 确保输出格式安全

### 权限控制
- 最小权限原则
- 适当的访问控制
- 安全的配置管理