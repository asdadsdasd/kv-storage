# kv-storage

gRPC-сервис key-value хранилища на **Java 21**, **Maven** и **Tarantool 3.2**

## Обзор

Проект реализует gRPC KV-сервис со следующим API:

- `put(key, value)` — создать новую запись или перезаписать существующую
- `get(key)` — получить значение по ключу
- `delete(key)` — удалить ключ
- `range(key_since, key_to)` — потоково вернуть пары ключ-значение из заданного диапазона
- `count()` — вернуть количество записей в хранилище

В качестве хранилища данных используется **Tarantool 3.2**.

## Технологический стек

- Java 21
- Maven
- gRPC Java
- Protocol Buffers
- Tarantool 3.2
- Tarantool Java SDK 1.5.0
- Docker Compose

## Запуск проекта

1. Запустите контейнер
```powershell
docker compose up -d
```
2. Соберите проект и запустите gRPC сервер
```powershell
mvn compile exec:java "-Dexec.mainClass=org.example.Main"
```

## API

### put(key, value)
Создаёт новую запись или перезаписывает существующую.

### get(key)
Возвращает значение для указанного ключа.
Если ключ отсутствует, сервис возвращает `NOT_FOUND`.

### delete(key)
Удаляет ключ и возвращает признак того, была ли удалена запись.

### range(key_since, key_to)
Возвращает все записи во **включительном лексикографическом диапазоне** `[key_since, key_to]`.

Ответ передаётся как **server-streaming gRPC**.

### count()
Возвращает количество записей в space `KV`.