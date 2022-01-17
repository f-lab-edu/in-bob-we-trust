<p align="center">
  <div align="center"><img src="https://user-images.githubusercontent.com/61615301/147374289-23ea1a9a-5d60-4057-affe-26444e67791f.jpeg" width="20%"/></div>
  <br>  
</p>


<p align="center"><b>"우리가 어떤 민족입니까"</b><br> <span>사장님-고객님-라이더님-배달대행사를 있는 배달의민족의 우아한 중계서비스입니다.</p>

<br>

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

### :triangular_ruler: 프로젝트 설명서 읽어보기

- [프로젝트 Use-Case 목록](https://github.com/f-lab-edu/in-bob-we-trust/wiki/Use-Case)
- [프로젝트 Validation 목록](https://github.com/f-lab-edu/in-bob-we-trust/wiki/Validation-%EB%AA%A9%EB%A1%9D)
- [프로젝트 Validation전략](https://github.com/f-lab-edu/in-bob-we-trust/wiki/Validation%EC%A0%84%EB%9E%B5)
- [프로젝트 커밋 메시지 컨벤션](https://github.com/f-lab-edu/in-bob-we-trust/wiki/commit-message-convention)
- [프로젝트 용어정리 dto vo entity dao](https://github.com/f-lab-edu/in-bob-we-trust/wiki/%EC%9A%A9%EC%96%B4%EC%A0%95%EB%A6%AC-dto-vo-entity-dao)
 
<br>

### :blue_book: 프로젝트 블로그 읽어보기
- [프로젝트 시작하기](https://vince-kim.tistory.com/23?category=973188)
- [프로젝트 주제선정하기](https://vince-kim.tistory.com/22?category=973188)
- [배달 중계서비스를 설계하기](https://vince-kim.tistory.com/24?category=973188)
- [SwaggerUI 도큐먼트 툴 적용 + 단점 보완하기](https://vince-kim.tistory.com/25?category=973188)
- [Github 프로젝트 & Intellij 전반에 걸쳐 Google Java Style Guide 를 강제하기](https://vince-kim.tistory.com/28?category=973188)
- [\[Reactive한 라이더위치 기능구현\] 요구사항 분석부터 위치정보 저장 기능 구현까지](https://vince-kim.tistory.com/29?category=973188)

<br>

### :bicyclist: 서비스 플로우

<p align="left">
  <div align="left"><img src="https://user-images.githubusercontent.com/61615301/147374405-65639f4b-08c6-4512-88c2-d5e5a1ec39ab.png" width="80%"/></div>
  <br>  
</p>

<br>

### :bicyclist: 로컬에서 프로젝트를 실행하려면?

#### Method 1. "Docker-way"

1. 로컬에 `docker`를 설치합니다. :point_right: https://docs.docker.com/get-docker/
2. 필요한 포트들이 사용중인지 확인합니다.
    - `8888` delivery-info-service 서버
    - `8090` delivery-relay-service 서버
    - `27017` delivery-info-service 메인DB
    - `27018` delivery-info-service 백업DB
    - `28017` delivery-relay-service 메인DB
3. 프로젝트 루트 디렉토리에서 다음 명령을 실행해줍니다. `docker-compose -f ./samples/docker-compose-actions.yml up -d`
4. :loudspeaker::loudspeaker: compose 파일의 다이나믹한 포트바인딩을 원하신다면 수정 후 PR을 올려주세요 큰 도움이 됩니다. :+1::+1:

#### Method 2. "Just way"

1. Intellij 를 실행해주세요!

<br>

### :bicyclist: Tips?

#### 1. 개발도중 프로젝트의 docker image를 빠르게 빌드해보고 싶다면?

- 다음 스크립트를 실행하세요. `${프로젝트root}/scripts/build-images.sh`
- (현재 Unix && Linux 에서만 가능합니다 :cry:)
- :warning: 해당 스크립트는 다음 순서로 진행됩니다.
    - 기존 이미지들을 삭제하고 (이미지 `beanskobe/delivery-info-service:latest`:
      heavy_plus_sign: `beanskobe/delivery-relay-service:latest`)
    - `gradle build`를 실행하고 (모든 테스트를 건너뒤고)
    - 이미지들을 빌드합니다.

<br>

### :bicyclist: Q/A

#### 1. 프로젝트의 <i class="fa fa-docker"></i> docker 이미지들은 어떻게 생성되고 어디에서 pull을 해오는 건가요?

- @JooHyukKim 의 DockerHub 저장소에서 pull 해옵니다.
- 이미지들은 프로젝트 `origin/main`의 push 이벤트에 의해 트리거되고 사전에 작성한 `build docker image 워크플로우`에서 빌드됩니다.
- 이미지 사용법 링크
    - :
      link: [delivery-info-service의 DockerHub 이미지 저장소 바로가기](https://hub.docker.com/repository/docker/beanskobe/delivery-info-service)
    - :
      link: [delivery-relay-service의 DockerHub 이미지 저장소 바로가기](https://hub.docker.com/repository/docker/beanskobe/delivery-relay-service)
