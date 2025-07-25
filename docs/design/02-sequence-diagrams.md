# 시퀀스 다이어그램

---
## 브랜드 & 상품
### 브랜드 조회 : GET /brands/{bandId}
```mermaid
sequenceDiagram
    participant User
    participant BrandController
    participant BrandService
    participant BrandRepository
    
    User->>BrandController: GET /brands/{id}
    BrandController->>BrandService: getBrand(brandId)
    BrandService->>+BrandRepository: findById(brandId)
    
    alt 브랜드가 존재하지 않을 경우
        BrandRepository-->>BrandService: 404 Not Found
    
    else 브랜드가 존재하는 경우
        BrandRepository-->>-BrandService: return brand
    end
    
```
### 상품 목록 조회 : GET /products
```mermaid
sequenceDiagram
    participant User
    participant ProductController
    participant ProductService
    participant ProductRepository
    participant LikeService
    
    User->>ProductController: GET /products
    ProductController->>+ProductService: getProducts(page, size, sort, brandId)
    ProductService->>+ProductRepository: findAll(page, size, sort, brandId)
    ProductRepository-->>-ProductService: return products
    ProductService->>+LikeService: findAllBy(productIds)
    LikeService-->>-ProductService: return likeCountsMap
    alt 로그인 되지 않은 유저일 경우
        ProductService-->>ProductController: return productInfos
    else 로그인 된 유저일 경우
        ProductService->>+LikeService: getIsLikes(userId, productIds)
        LikeService-->>-ProductService: return isLikesMap
        ProductService-->>-ProductController: return productInfos with isLikes
    end
    
```
### 상품 정보 조회 : GET /products/{productId}
```mermaid
sequenceDiagram
    participant User
    participant ProductController
    participant ProductService
    participant ProductRepository
    
    User->>ProductController: GET /products/{productId}
    ProductController->>+ProductService: getProduct(productId)
    ProductService->>+ProductRepository: findById(productId)
    
    alt 상품이 존재하지 않을 경우
        ProductRepository->>ProductService: 404 Not Found
    else 상품이 존재하는 경우
        ProductRepository->>-ProductService: return product
        ProductService->>+LikeService: getLikesCountBy(productId)
        LikeService-->>-ProductService: return likeCount
        alt 로그인 되지 않은 유저일 경우
            ProductService-->>ProductController: return productInfo
        else 로그인 된 유저일 경우
            ProductService->>+LikeService: isLike(userId, productId)
            LikeService-->>-ProductService: return isLike
            ProductService-->>-ProductController: return productsInfo with isLike
        end
    end
```
---
# 좋아요
### 상품 좋아요 등록 : POST /products/{productId}/likes
```mermaid
sequenceDiagram
    participant User
    participant LikeController
    participant LikeService
    participant ProductReader
    participant ProductCommand
    participant LikeRepository
    
    User->>LikeController: POST /products/{productId}/likes
    LikeController->>LikeService: addLike(userId, productId)
    LikeService->>ProductReader: get(productId)
    alt 상품이 존재하지 않을경우
        ProductReader->>LikeService: throw NotFoundException()
    else 상품이 존재할 경우
        LikeService->>LikeRepository: exist(userId, productId)
        alt 좋아요가 존재하지 않을 경우
            LikeService->>LikeRepository: save()
            LikeRepository-->>LikeService: return like
        
        else 이미 활성 상태일 경우 (멱등성)
            LikeRepository-->>LikeService: return like
    end

    end
```
### 상품 좋아요 취소 : DELETE /products/{productId}/likes
```mermaid
sequenceDiagram
    participant User
    participant LikeController
    participant LikeService
    participant ProductService
    participant ProductCommand
    participant LikeRepository
    
    User->>LikeController: DELETE /products/{productId}/likes
    LikeController->>+LikeService: removeLike(userId, productId)
    alt 로그인 되지 않은 유저 일 경우
        LikeService-->>LikeController: throw 401 Unauthorized
    else 로그인 된 유저일 경우
        LikeService->>+ProductService: get(productId)
        alt 상품이 존재하지 않을경우
            ProductService->>LikeService: throw NotFoundException()
            LikeService-->>LikeController: return 404 Not Found
        else 상품이 존재할 경우
            ProductService->>-LikeService: return product
            LikeService->>LikeRepository: existLike(userId, productId)
            alt 좋아요가 존재할 경우
                LikeService->>LikeRepository: delete(userId, productId)
                LikeRepository-->>LikeService: return 200 OK
            else 좋아요가 존재하지 않을 경우
                LikeService->>-LikeController: return 200 OK
            end
        end
    end

```
### 좋아요 상품 목록 조회 : GET /users/{userId}/likes
```mermaid
sequenceDiagram
    participant User
    participant LikeController
    participant LikeService
    participant UserService
    participant ProductService
    participant LikeRepository
    
    User->>LikeController: GET /users/{userId}/likes
    LikeController->>LikeService: getLikes(userId, page, size)
    alt 로그인 되지 않은 유저일 경우
        UserService-->>LikeService: 401 Unauthorized
    else 로그인 된 유저일 경우
        LikeService->>LikeRepository: findBy(userId, page, size)
        LikeRepository-->>LikeService: return likes with likeCount
        LikeService->>ProductService: getProducts(productIds)
        ProductService->>LikeService: return products
        LikeService-->>LikeController: return products with likeCount
    end
    
```

---
# 주문
### 주문 생성 : POST /orders
```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant OrderService
    participant OrderRepository
    participant PointService
    participant ProductService
    
    User->>OrderController: POST /orders
    OrderController->>+OrderService: createOrder(orderRequest)
    OrderService->>+PointService: usingPoint(userId, totalPrice)
    alt 포인트 잔액이 부족하면
        PointService-->>OrderService: throw InsufficientPointException()
    else 포인트 잔액이 충분하면
        OrderService->>ProductService: deductStocks(orderItems)
        alt 상품의 재고가 부족하여 차감되지 않은 경우
            ProductService-->>OrderService: 400 Bad Request (OutOfStockException)
        else 상품의 재고가 존재하여 차감된 경우
            OrderService->>+OrderRepository: save(order)
            OrderRepository-->>OrderService: return order
            OrderService-->>-OrderController: return order

        end
    end
```

### 주문 목록 조회 : GET /orders
```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant OrderService
    participant OrderRepository
    
    User->>OrderController: GET /users/{userId}/orders
    OrderController->>OrderService: getOrders(userId, page)
    OrderService->>OrderRepository: findByUserId(userId, page)
    
    OrderRepository-->>OrderService: return orders
```

### 주문 상세 조회 : GET /orders/{orderId}
```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant OrderService
    participant OrderRepository
    
    User->>OrderController: GET /orders/{orderId}
    OrderController->>OrderService: getOrder(orderId)
    OrderService->>OrderRepository: findById(orderId)
    
    alt 주문이 존재하지 않을 경우
        OrderRepository-->>OrderService: 404 Not Found()
    else 주문이 존재하는 경우
        OrderRepository-->>OrderService: return order
    end
```
