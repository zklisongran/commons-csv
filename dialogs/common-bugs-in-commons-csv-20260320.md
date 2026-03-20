# Commons CSV 常见 Bug 清单

## 1. 空指针异常 (NullPointerException)
**描述**: 当访问未初始化的对象引用或null值时发生
**常见场景**:
- 访问parser对象的headerMap时未检查null
- CSVRecord中的values数组为null时直接访问
- ExtendedBufferedReader.lastChar在某些边界条件下为UNDEFINED(-2)被当作字符处理

## 2. 数组越界异常 (ArrayIndexOutOfBoundsException)  
**描述**: 访问数组时索引超出有效范围
**常见场景**:
- CSVRecord.get(int index)方法中index超出values数组长度
- 在处理不完整的CSV记录时访问不存在的列
- 多字节字符处理时缓冲区索引计算错误

## 3. 资源泄露 (Resource Leak)
**描述**: 文件流、网络连接等资源未正确关闭
**常见场景**:
- CSVParser.close()未被调用导致底层Reader未关闭
- 异常情况下finally块缺失导致资源无法释放
- 嵌套try-with-resources结构不当使用

## 4. 编码问题 (Encoding Issues)
**描述**: 字符编码处理不当导致数据损坏
**常见场景**:
- ExtendedBufferedReader中多字节字符(byte tracking)计算错误
- UTF-8/UTF-16代理对(surrogate pairs)处理不完整
- BOM(Byte Order Mark)处理不正确

## 5. 并发安全问题 (Concurrency Issues)
**描述**: 多线程环境下状态不一致
**常见场景**:
- CSVPrinter中的ReentrantLock使用不当
- 共享的StringBuilder在多线程环境中被同时修改
- 静态变量或单例模式的状态竞争

## 6. 内存泄漏 (Memory Leak)
**描述**: 对象引用未及时释放导致内存持续增长
**常见场景**:
- 大型CSV文件处理时recordList未及时清理
- Token对象复用时内容未重置完全
- 缓冲区大小动态增长但无上限控制

## 7. 格式解析错误 (Parsing Errors)
**描述**: CSV格式识别和处理不符合预期
**常见场景**:
- 分隔符、引号、转义字符配置冲突
- 多字符分隔符处理逻辑缺陷
- 注释行与数据行混淆处理

## 8. 性能问题 (Performance Issues)
**描述**: 特定场景下性能急剧下降
**常见场景**:
- 大量小字符串拼接而非使用StringBuilder
- 频繁的正则表达式匹配操作
- 不必要的对象创建和垃圾回收压力

## 9. 边界条件处理不当 (Boundary Condition Issues)
**描述**: 特殊输入情况处理不完善
**常见场景**:
- 空文件或只包含空白字符的文件
- 只有一行且以分隔符结尾的CSV
- 包含特殊Unicode字符的CSV数据

## 10. 状态管理错误 (State Management Errors)
**描述**: 对象内部状态转换不正确
**常见场景**:
- CSVParser的recordNumber计数错误
- Token.type状态转换逻辑缺陷
- ExtendedBufferedReader的位置跟踪不准确

## 11. 类型转换异常 (ClassCastException)
**描述**: 不安全的类型转换操作
**常见场景**:
- Object类型参数强制转换为特定类型失败
- 泛型类型擦除导致的运行时类型错误
- 枚举值与字符串之间转换问题

## 12. 数据一致性问题 (Data Consistency Issues)
**描述**: 解析结果与原始数据不符
**常见场景**:
- 引号内数据的转义字符处理错误
- 行尾分隔符处理不一致(CRLF vs LF)
- 数值类型数据精度丢失

---
*文档生成时间: 2026-03-20*