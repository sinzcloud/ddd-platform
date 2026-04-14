## CQRS 到底是什么？

### 一、一句话解释

**CQRS 就是把"读"和"写"分开处理。**

就像图书馆：借书（写操作）要走复杂流程（登记、扣费、记录），而看书（读操作）直接去书架拿就行。

### 二、传统架构 vs CQRS

#### 传统架构（同一个模型）

```java
// 同一个 UserService 处理所有操作
@Service
public class UserService {
    
    // 写操作（增、删、改）
    public void createUser(User user) { ... }
    public void updateUser(Long id, User user) { ... }
    public void deleteUser(Long id) { ... }
    
    // 读操作（查）
    public User getUser(Long id) { ... }
    public List<User> listUsers() { ... }
}
```

**问题：**
- 一个类既处理读又处理写，越来越臃肿
- 复杂的查询会影响写入性能
- 难以针对读和写做不同优化

#### CQRS 架构（读写分离）

```java
// 写操作 - 命令
@Service
public class UserCommandService {
    public void createUser(CreateUserCommand command) { ... }
    public void updateUser(UpdateUserCommand command) { ... }
    public void deleteUser(DeleteUserCommand command) { ... }
}

// 读操作 - 查询
@Service
public class UserQueryService {
    public UserDTO getUser(GetUserQuery query) { ... }
    public List<UserDTO> listUsers(ListUserQuery query) { ... }
}
```

### 三、CQRS 的核心概念

```
┌─────────────────────────────────────────────────────────────────┐
│                          CQRS 架构                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   写操作（Command）              读操作（Query）                  │
│   ┌─────────────┐               ┌─────────────┐                 │
│   │  创建用户    │               │  查询用户    │                 │
│   │  更新用户    │               │  列表用户    │                 │
│   │  删除用户    │               │  获取详情    │                 │
│   └──────┬──────┘               └──────┬──────┘                 │
│          │                             │                         │
│          ↓                             ↓                         │
│   ┌─────────────┐               ┌─────────────┐                 │
│   │  命令处理器  │               │  查询处理器  │                 │
│   │ (Handler)   │               │ (Handler)   │                 │
│   └──────┬──────┘               └──────┬──────┘                 │
│          │                             │                         │
│          ↓                             ↓                         │
│   ┌─────────────┐               ┌─────────────┐                 │
│   │  写数据库    │               │  读数据库    │                 │
│   │  (MySQL)    │               │  (MySQL/缓存)│                 │
│   └─────────────┘               └─────────────┘                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 四、CQRS 中的三个关键角色

#### 1. Command（命令）- 告诉系统"做什么"

```java
// 命令：创建用户
public class CreateUserCommand {
    private String username;
    private String password;
    private String email;
}

// 命令：激活用户
public class ActivateUserCommand {
    private Long userId;
}
```

**特点：**
- 动词开头（Create、Update、Delete）
- 有副作用（会改变数据）
- 返回结果简单（通常只返回成功/失败或ID）

#### 2. Query（查询）- 问系统"是什么"

```java
// 查询：获取用户
public class GetUserQuery {
    private Long userId;
}

// 查询：分页查询用户
public class PageUserQuery {
    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
}
```

**特点：**
- 名词开头（Get、List、Page）
- 无副作用（不改变数据）
- 返回具体数据

#### 3. Bus（总线）- 负责"路由"

```java
// 命令总线：把命令交给对应的处理器
public interface CommandBus {
    <R> R send(Command command);  // 发送命令
}

// 使用
commandBus.send(new CreateUserCommand(...));
```

### 五、完整示例

#### 传统写法

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public Result<User> createUser(@RequestBody User user) {
        // 写操作
        userService.save(user);
        return Result.success(user);
    }
    
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        // 读操作
        return Result.success(userService.getById(id));
    }
}
```

#### CQRS 写法

```java
// 1. 定义命令
public class CreateUserCommand implements Command {
    private String username;
    private String password;
}

// 2. 定义命令处理器
@Component
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, Long> {
    public Long handle(CreateUserCommand command) {
        // 验证、创建用户、保存到数据库
        User user = new User(command.getUsername(), command.getPassword());
        userRepository.save(user);
        return user.getId();  // 返回用户ID
    }
}

// 3. 定义查询
public class GetUserQuery implements Query<UserDTO> {
    private Long userId;
}

// 4. 定义查询处理器
@Component
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, UserDTO> {
    public UserDTO handle(GetUserQuery query) {
        // 直接从数据库或缓存查询
        User user = userRepository.findById(query.getUserId());
        return new UserDTO(user);
    }
}

// 5. Controller 使用
@RestController
public class UserController {
    
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    
    @PostMapping("/users")
    public Result<Long> createUser(@RequestBody CreateUserCommand command) {
        // 发送命令
        Long userId = commandBus.send(command);
        return Result.success(userId);
    }
    
    @GetMapping("/users/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        // 发送查询
        GetUserQuery query = new GetUserQuery(id);
        UserDTO user = queryBus.send(query);
        return Result.success(user);
    }
}
```

### 六、什么时候用 CQRS？

#### ✅ 适合用 CQRS 的场景

| 场景 | 原因 |
|------|------|
| 读多写少（如博客、商品详情） | 可以针对读做缓存优化 |
| 复杂业务逻辑 | 命令和查询分开，代码更清晰 |
| 高并发系统 | 读写分离，互不影响 |
| 读模型和写模型差异大 | 可以分别设计 |

#### ❌ 不适合用 CQRS 的场景

| 场景 | 原因 |
|------|------|
| 简单的 CRUD | 过度设计，增加复杂度 |
| 团队小、项目简单 | 维护成本高 |
| 读和写逻辑完全一样 | 没必要分离 |

### 七、CQRS vs 传统架构

| 对比项 | 传统架构 | CQRS |
|--------|----------|------|
| 代码组织 | 一个 Service 处理所有 | Command + Query 分开 |
| 模型 | 同一个 Model | 可以不同（Command用Entity，Query用DTO） |
| 数据库 | 同一个库 | 可以读写分离 |
| 缓存 | 统一处理 | Query 层可以独立缓存 |
| 复杂度 | 低 | 中高 |
| 适用场景 | 简单业务 | 复杂业务、高并发 |

### 八、你项目中的 CQRS 实现

```java
// 命令：创建用户
public class CreateUserCommand implements Command { ... }

// 命令处理器
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, UserDTO> {
    public UserDTO handle(CreateUserCommand command) {
        // 写操作：验证、保存、触发事件
        return userApplicationService.registerUser(...);
    }
}

// 查询：获取用户
public class GetUserQuery implements Query<UserDTO> { ... }

// 查询处理器
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, UserDTO> {
    public UserDTO handle(GetUserQuery query) {
        // 读操作：直接查询返回
        return userApplicationService.getUserById(query.getUserId());
    }
}
```

### 九、核心总结

```
┌─────────────────────────────────────────────────────────────┐
│                      CQRS 核心要点                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. 命令（Command）= 写操作 = 有副作用                       │
│  2. 查询（Query）= 读操作 = 无副作用                         │
│  3. 命令总线（CommandBus）= 路由命令到处理器                 │
│  4. 查询总线（QueryBus）= 路由查询到处理器                   │
│  5. 读写分离 = 可以独立优化读和写                            │
│                                                              │
│  记住：写用 Command，读用 Query，总线来路由                  │
└─────────────────────────────────────────────────────────────┘
```

**简单理解：**
- 要改数据 → 用 Command（命令）
- 要查数据 → 用 Query（查询）
- 分开处理 → 代码更清晰、性能更好