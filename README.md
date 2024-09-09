# GroupCalendar API

이 프로젝트는 Java17, Spring Boot 3.3.3 버전과 MySQL을 사용하여 만든 그룹 캘린더 API입니다.\
사용자는 캘린더를 사용자들과 공유하여 일정을 추가, 수정, 삭제할 수 있습니다.\
API 문서를 제공하기 위해 Swagger가 통합되었습니다.

# Stack

![](https://img.shields.io/badge/java_17-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot_3.3.3-✓-blue.svg)
![](https://img.shields.io/badge/mysql-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger-✓-blue.svg)

# 주요 기능

- **캘린더 일정**에 대한 CRUD 기능 제공
- **캘린더 그룹** 기능으로 다른 사용자와 일정 공유 가능
- Spring Security를 이용한 인증 및 권한 관리

# 시작하기

### 설치 및 실행 방법

1. 저장소를 클론합니다:
   ```bash
   git clone https://github.com/moolmeat/clush-back.git
   ```

2. 프로젝트 디렉토리로 이동합니다:
    ```bash
    cd calendar-api
    ```

3. application.properties 파일을 열고 MySQL 설정을 입력합니다:
    ```
    spring.datasource.url=jdbc:mysql://${MYSQL_URL}
    spring.datasource.username=${MYSQL_USERNAME}
    spring.datasource.password=${MYSQL_PASSWORD}
   
    프로젝트 평가의 위해 편의를 위해 jwt.token.secret-key를 명시해두었습니다.
    ```

4. 프로젝트를 빌드합니다:

    ```bash
    ./gradlew clean build
    ```

5. 애플리케이션을 실행합니다:

    ```bash
    java -jar build/libs/clush-back-0.0.1-SNAPSHOT.jar
    ```

# 주요 컴포넌트

## Custom Response Wrapper

프로젝트 전반에서 사용되는 API 응답의 일관성을 유지하기 위해 CustomResponse 객체를 사용합니다. 이를 통해 모든 API 응답이 일정한 구조를 따르며, 성공/실패
여부, 상태 코드, 메시지, 응답 데이터를 명확하게 전달합니다.

클라이언트와의 통신에서 일관된 응답 형식을 제공함으로써 가독성과 유지보수성을 높입니다.
모든 응답이 동일한 구조를 갖추고 있어 오류 추적 및 디버깅이 용이합니다.

## Validator

UserGroupValidator는 사용자가 특정 그룹에 속해 있는지, 그리고 해당 일정에 접근할 권한이 있는지를 검증합니다. 이러한 검증을 통해 코드 내 반복을 줄여주고,
불필요한 오류를 사전에 방지합니다.

중요한 비즈니스 로직(일정 생성, 조회 등)에서 사용자의 권한을 사전에 검증하여 보안을 강화하고, 불필요한 데이터 접근을 방지합니다.
권한 부족으로 인해 발생할 수 있는 예외 상황을 미리 처리하여 시스템의 안정성을 높입니다.

## Scheduling Recurrence (반복 일정 관리)

RecurrenceRepository와의 통합을 통해 반복 일정을 동적으로 생성하고, 사용자의 맞춤형 예외 처리 기능을 제공합니다. 특정 기간 내에서 일정을 생성하고 예외 사항(
수정, 제외)을 적용하여 보다 정교한 일정 관리가 가능합니다.

일정을 일일, 주간, 월간, 연간 단위로 반복 가능한 일정을 생성합니다.\
동적 반복 생성 기능을 통해 DB에 하나의 값만 저장되고 지정된 기간 내에서 필요한 일정만 자동으로 생성하고 반환합니다.\
반복 일정에 대한 예외 처리 기능을 제공하여, 사용자는 특정 날짜에 대해 일정을 수정하거나 제외할 수 있습니다.

## DSL Query

DSL 기반의 쿼리 작성 방식을 도입하여 복잡한 쿼리 생성을 간소화하고, 도메인 논리에 맞는 방식으로 데이터베이스와 상호작용할 수 있도록 했습니다. 이를 통해 데이터 조회와 처리가
보다 직관적이고 간결해졌으며, 데이터베이스 접근에 대한 추상화가 강화되었습니다. 또한, JPA의 명명 규칙에 따른 오류 및 오버 패칭(Over-fetching) 문제를 방지할 수
있습니다.

# API 명세

API 명세와 호출방식은 [Swagger UI](http://localhost:8080/swagger-ui.html)에서 확인 가능합니다.

# 주요 기능

### 캘린더 그룹 기능

사용자는 다른 사용자들과 캘린더를 공유할 수 있습니다.\
초대는 두 가지 방식으로 가능합니다\
1번 **고유 코드** 공유를 통해 입장\
2번 앱 내 **직접 초대** 기능을 통해 초대\
이 기능을 통해 여러 사용자들이 동일한 캘린더에서 일정을 관리하고 확인할 수 있습니다.

### 반복 일정 생성

사용자는 매일, 주간, 월간, 연간 단위로 반복 일정을 생성할 수 있으며, 주기적인 일정 관리가 가능합니다.\
또한, 특정 날짜의 일정만을 예외 처리할 수 있어 반복 일정을 수정하거나 삭제하지 않고도, 개별 일정을 관리할 수 있습니다.\
예를 들어, 매일 반복되는 일정에서 특정한 날만 제외하거나, 수정이 가능합니다.

### 월경 주기 관리 기능

생리 주기 관리 기능을 사용할 수 있습니다.\
주기의 시작일을 입력하면, 시스템은 배란일과 같은 주요 일정을 예측하고 알려줍니다. 이를 통해 사용자는
월경 주기를 효율적으로 관리하고 중요한 일정을 미리 준비할 수 있습니다.
