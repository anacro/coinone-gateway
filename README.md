= coinone-gameway

TCP 소켓을 사용하여 코인원 API를 호출할 수 있는 게이트웨이 서버

== 프로그램의 목적

* TCP 소켓을 사용하여 코인원 API 호출
* HTTP API 서버의 부하를 줄이는 캐시 레이어
* 이벤트 publish(notify)

== 프로그램의 설명

vertx와 스프링부트 사용해 개발

=== 기본

* Application : 프로그샘 진입점
* AppConfiguration : spring 환경설정

=== 요청파이프라인

coinone.handler.impl

패킷 수신 => web api 호출 => 결과 전달 까지의 요청 프로세스를 처리

AppConfiguration::pipeline 확인
 
=== 패킷

coinone.message.packet

요청,응답,구독 패킷을 생성하고 파싱

사용 예는 RequestPacketHandler 와 Client의 encode, onMessage 확인

=== 캐시

coinone.session

샘플이기 때문에 단순히 메모리 사용

사용 예는 CacheHandler 확인

=== 구독
TickerPublisher : Ticker API를 호출하여 변경이 있으면 현재 접속된 사용자에게 publish

AppConfiguration::tickerRunner 설정

사용 예는 ServerTest의 42 라인

=== 테스트

ServerTest::testBALANCE 실행
