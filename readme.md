<p align="center">
  <div align="center"><img src="https://user-images.githubusercontent.com/61615301/147374289-23ea1a9a-5d60-4057-affe-26444e67791f.jpeg" width="20%"/></div>
  <br>  
</p>


<p align="center"><b>"우리가 어떤 민족입니까"</b><br> <span>사장님-고객님-라이더님-배달대행사를 있는 배달의민족의 중계서비스입니다.</p>

<br>

## Introduction

### :bicyclist: 하루 100만 주문을 해결하는 배달의민족은 어떻게 만들어진 것일까요?

- 일반적인 클라이언트-서버의 웹서버를 넘어 여러 주체(사장님,고객님 등..)와 어떻게 통신을 주고받을까요?
- 여러 서비스들의 사이에서 어떻게 장애를 다룰까요?
- 트래픽 최저점시간 대비 100배까지 증가하는 점심, 저녁 피크타임 트래픽 스파이크를 어떻게 해결할까요?

이러한 궁금증을 해결하기 위해서 배달의민족 중계서버를 구현하게 되었습니다.

<br>

### :bicyclist: 무엇을 경험하고 싶은건가요?

- 예상치 못한 장애들을 핸들링하는 경험하기
- 여러 각도에서 테스트를 진행하고 테스트 코드를 작성하며 버그 최소화하는 경험하기
- 모든 기능이 탑재된 서비스를 개발하는 것이 아닌, 진화하는 서비스를 경험하기
- 분산환경에서 여러 서비스들과 통신하는 과정을 경험하기

<br>

### :bicyclist: 서비스 플로우

  <p align="left">
  <div align="left"><img src="https://user-images.githubusercontent.com/61615301/147374405-65639f4b-08c6-4512-88c2-d5e5a1ec39ab.png" width="80%"/></div>
  <br>  
</p>

## Docker Run Configurations

### 1. delivery-info-service

#### docker run 필수입력 변수

|이름     | 설명        |  위치 | 
|---	|---	|---			|
| spring.data.mongodb.primary.uri| 메인 몽고DB URI        |   COMMAND_LINE_ARGS_BEFORE    |   
| spring.data.mongodb.primary.database| 메인 몽고DB 데이터베이스 이름       |   COMMAND_LINE_ARGS_BEFORE    |   
|  spring.data.mongodb.secondary.uri| 백업 몽고DB URI       | COMMAND_LINE_ARGS_BEFORE    |   
|  spring.data.mongodb.secondary.database| 백업 몽고DB 데이터베이스 이름       | COMMAND_LINE_ARGS_BEFORE    | 
| spring.profiles.active    |  스프링 프로필    |  COMMAND_LINE_ARGS_AFTER    |   	

#### docker run 샘플

```shell
docker run \
-p 8888:8888 \
-e COMMAND_LINE_ARGS_BEFORE='-Dspring.data.mongodb.primary.database=<메인몽고Database> -Dspring.data.mongodb.primary.uri=<메인몽고URI> -Dspring.data.mongodb.secondary.database=<백업몽고database> -Dspring.data.mongodb.secondary.uri=<백업몽고URI>'  \
-e COMMAND_LINE_ARGS_AFTER='--spring.profiles.active=<스프링프로필>'  \
--network host beanskobe/delivery-info-service
```

#### docker run 실제 실행 스크립트

```shell
java ${COMMAND_LINE_ARGS_BEFORE} -jar ./app.jar ${COMMAND_LINE_ARGS_AFTER}
```

### 2. delivery-relay-service

#### docker run 필수 입력사항

|이름     | 설명        |  위치 | 
|---	|---	|---			|
| spring.data.mongodb.primary.uri|  몽고DB URI        |   COMMAND_LINE_ARGS_BEFORE    |   
| spring.data.mongodb.primary.database|  몽고DB 데이터베이스 이름       |   COMMAND_LINE_ARGS_BEFORE    |   
| spring.profiles.active    |  스프링 프로필    |  COMMAND_LINE_ARGS_AFTER    |   	

#### docker run 샘플

```shell
docker run \
-p 8090:8090 \
-e COMMAND_LINE_ARGS_BEFORE='-Dspring.data.mongodb.database=<몽고Database> -Dspring.data.mongodb.uri=<몽고URI>'  \
-e COMMAND_LINE_ARGS_AFTER='--spring.profiles.active=<스프링프로필>'  \
--network host beanskobe/delivery-relay-service
```

#### docker run 실제 실행 스크립트

```shell
java ${COMMAND_LINE_ARGS_BEFORE} -jar ./app.jar ${COMMAND_LINE_ARGS_AFTER}
```

