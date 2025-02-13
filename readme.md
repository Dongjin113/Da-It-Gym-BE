![image](https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/79975172/08ab43a2-45c8-4247-9415-f7e20613cad1)



# [루틴 및 운동 일지 공유 커뮤니티, 근근근 - 배포 링크](https://daitgym.ogjg.site)
## 💭 문제 인식
### 1. 헬스장 PT의 필요성과 가격 부담
- 헬스장을 처음 찾아 운동법을 잘 모르는 이들에게 PT는 필수 코스이다.
- 체격 증대를 위한 고중량 운동기구 이용은 불가피하다.
- 운동기구마다 근육에 미치는 효과가 다르기에 몸 관리나 부상 방지를 위해 전문가 손길이 필요하다.
- 부담스러운 가격으로 선뜻 PT를 받기 어렵다.

### 2. 무자격 헬스 트레이너
- "300㎡가 넘지 않는 헬스장에 체육지도자가 1명 이상만 있다면 나머지 트레이너들은 자격증이 없어도 회원들을 지도할 수 있다."
- "300㎡가 넘는 헬스장에 체육지도자가 2명 이상만 있다면 나머지 트레이너들은 자격증이 없어도 회원들을 지도할 수 있다."
<div align=center>
   <img src="https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/79975172/83fecfb0-5070-4f39-b138-da84aa837bb8" width=500>
</div>

## 🚩 서비스 목적
- 운동 커뮤니티 구축  
  운동인의 역량을 수치화한 정보를 제공하여 자신의 운동 능력을 개선할 수 있도록 돕는다.  
  운동을 즐기고 있는 유저들 간에 운동 경험을 공유하고 피드백 함으로써 활성화된 운동 커뮤니티를 구축할 수 있다.

- 개인 맞춤 운동 일지 및 루틴 추천  
  유저가 입력한 운동 역량 정보와 기존 사용자의 데이터를 활용하여  
  유저에게 맞는 운동 일지와 과거 비슷한 역량을 지녔는데 성장한 유저의 운동 일지와 루틴을 추천한다.

  유저들은 동기부여를 얻을 수 있을 뿐만 아니라,  
  자신의 운동 계획을 효과적으로 수립하고 지속적인 성장을 이룰 수 있게 된다.

- 트레이너 검증 및 온라인 PT 채팅 상담  
  서비스 자체적으로 자격증이나 신체 정보를 통해 트레이너의 신뢰성을 확인하고    
  트레이너와 사용자 간의 온라인 PT 사전 상담을 지원한다.

  유저들은 비교적 안전하고 효율적인 트레이닝을 보장받을 수 있으며, 상담 시간을 절약할 수 있다.

## 📆 기간
2023.10.12 ~ 2023.11.28

## 🏃 팀 구성
### Frontend
- [김준서](https://github.com/narcoker)
- [한승재](https://github.com/stat1202)
- [조재균](https://github.com/stat1202)
- [한세라](https://github.com/hansera)

### Backend
- [안병규](https://github.com/bstaran)
- [이정준](https://github.com/dunowljj)
- [이동진](https://github.com/Dongjin113)
- [고예진](https://github.com/YEJINGO)


## ⚡ 개발 프로세스
![image](https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/79975172/516df55d-2b73-450c-bbfc-45bf62ed0a20)


## ⚙️ 아키텍쳐
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/2ee683b7-6c11-4923-ba4f-8012400cb724)

## 👀 ERD
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/c476c0aa-39b5-4611-906f-f27d5511eff8)

## 😀 API
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/d2726f9a-bb23-4520-ba4b-ebb0b5fb0c48)


### 맡은 부분
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/d96e9979-d19f-47e8-85a4-1230defc62bb)

### 고민사항
#### 1. 키워드를 통한 검색
- 사용자가 운동에 대한 키워드를 설정할 수 있습니다.
- 키워드를 통해 운동일지를 검색하는데, 이렇게 되면 운동 부위와 운동 분할 테이블에 데이터가 많아질 수 있습니다.
- 예를 들어, 2분할, 등, 어깨, 하체 등의 내용을 데이터베이스에 저장한다면 사용자 정보와 키워드로 총 4개의 데이터가 저장될 것입니다.
- 중복되는 내용을 제거하기 위해 사용자가 키워드를 지정해서 요청을 보내면, 키워드를 묶어서 분류하면 좋지 않을까 고민했습니다.
  - 예시: 사용자 + 2분할, 사용자 + 등, 사용자 + 어깨, 사용자 + 하체를 총 4개의 데이터로 저장하는 대신,
  - 사용자 + (등, 어깨, 하체)를 묶은 묶음 키워드로 1개의 데이터로 저장할 수 있도록 합니다.
- 이 방식을 사용하면 존재하는 키워드로 생성될 수 있는 모든 경우의 수가 전부 생성된다면 더 이상 데이터가 생성되지 않을 것으로 예상됩니다.

![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/8200c93b-d478-49f5-9c49-8ade29dbb056)
- 문제점: 서비스 로직이 복잡해질 것 같다. 데이터베이스 비용을 줄이려다 더욱 높은 서비스 비용이 발생할 것 같다
- 결론 : 운동별로 한개의 부위만 지정해서 사용할 것이라서 키워드를 묶어서 사용할 필요가 없어졌습니다. 
- 그래서 운동과 운동부위별로 일대일 맵핑으로 사용하게 되었습니다

#### 2. DB의 테이블을 어디까지 정규화 해야 하는가?
- 문제점 : 데이터를 정규화를 진행 했더니 원하는 데이터를 얻기 위해서 많은 join이 발생합니다. 이렇게 된다면 join에 대한 비용이 커지게되므로 성능적인 문제가 발생하지 않을까라는 고민을 했습니다.
- 결론 : 데이터양과, usecase에 따라 달라지는데 join에 대해 들어가는 비용이 그렇게 크지 않다는 멘토링 답변을 받았습니다. 
- 그러나 join과 1:N 관계가 많은 부분이 존재했고 하루에 많은 운동을 하는 사용자가 존재한다면 데이터 또한 많아질 것을 고려하여 Join이 많이 발생하는 부분은 Redis를 통해 사용하였습니다.

## Clean Code를 위한 고려사항
### 1. HelperClass의 사용
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/34e3e104-7a1e-4c7e-94ba-351883080a1c)

- feed와 journal의 사용 데이터는 같으나 도메인을 분리함으로써 서비스 간의 참조가 발생하여 순환 참조가 일어났습니다.
- 따라서 공통 로직을 분리하기 위해 Helper Class를 사용하였습니다.
- Helper 클래스를 사용하여 도메인 검색 로직 등 서로 공유 가능한 로직을 분리하고, 서비스 로직에는 서비스와 관련된 로직만 작성하도록 했습니다.
- 공통으로 사용할 수 있는 로직들만 Helper Class로 분리하여 코드를 정리했습니다.

### 2. Java Doc을 통한 메서드에 대한 설명 정리
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/88d712de-aaf5-46b2-b9de-72ae4f894d42)
- 사용 이유: 이전 프로젝트에서는 정의된 메서드를 잘못 이해하고 사용하는 경우가 있었습니다. 그래서 JavaDoc을 활용하여 간단한 설명을 작성하게 되었습니다.

## 🔎 UI 및 기능

### 운동일지

#### 운동 일지 목록 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/a707fb5b-eda7-446c-b429-b8a0ce7dbc51

#### 운동 일지 상세 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/2f2585d6-028a-459c-98a8-9a4bcb587348

#### 운동 계획 작성 및 시작
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/4a48fdfc-a0ec-46fa-8d26-f052fb8bd89e

- 운동일지를 작성할 때 사용자별로 해당 날짜에 한개의 일지만 생성할 수 있다.
- 서비스 로직에서 이미 일지가 존재한다면 생성 하지 못하도록 막아 뒀지만 한번에 여러번의 요청을 보낸다면 중복생성이 되는 버그가 발생했다.
- 고민사항: Unique 제약조건을 걸어서 사용자의 중복생성을 막을것인가? Lock을 걸어서 막을것인가?
    - Lock을 걸기에는 성능적이나 다른 문제들이 생길수 있을 것 같기에 Unique 제약조건을 통해 중복생성을 막도록했다.

#### 운동 일지 작성
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/cf8757f5-6814-4589-a72f-21569b2fd638

### 유저

### 근근근을 사용하고 있는 카카오톡 친구
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/1ea6affe-c707-4a62-98ac-88e57fe49550

### 팔로우 팔로잉
![팔로우](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/c99a5203-8ece-4ebb-8216-533d00ece01e)


### S3 Bucket 이미지 업로드하기
1. MultipartFile 과 AWS Multipart 업로드
   이미지의 업로드가 잦을것을 고려하여 AWS Multipart 방식으로 S3 버킷으로 업로드를 구현해보고 싶었지만
   기간내 구현을 위해 비교적 간단한 MultipartFile을 선택하기로 했다.


### TestCode와 Spring Rest Doc 추가
#### 1. Controller 통합 테스트 추가
MockMvc를 사용하여 테스트를 작성하고 Spring Rest Doc으로 문서화
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/844bcd85-5716-4983-a331-3933e75dadec)

#### 2. Service 단위 테스트 추가
![image](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/fe5dd38c-ec7b-446e-9f78-9d5b2551f139)




## 트러블 슈팅
### 1. 도메인 분리
이전 프로젝트에서는 도메인별로 패키지를 나눠서 각 도메인별로 엔티티를 생성해서 사용을 했지만  
이번에 프로젝트에서는 엔티티들을 domain 패키지를 만들어서 한 곳에서 관리하기로 해서 엔티티를 만드는 과정중에  
도메인 패키지 내에서 관리를 하더라도 도메인 패키지내에서 패키지로 분류를 해서 사용을 하는게 엔티티를 관리하는데 수월하다고 생각을 하고  
팀원 분들은 한번에 보기 위해 도메인 패키지로 한번에 묶어서 관리를 하기 때문에 패키지로 따로 추가 분류를 안하고  
사용하는 것이 관리에 더 용이하는 생각이었다.

결론 : 도메인 패키지안에서 패키지별로 도메인을 분리하는 것이 확장성과 유지보수성이 더 좋을것으로 생각해서 분리하기로 결정

## 고민사항
1. 데이터를 보내줄때 운동일지에 운동목록, 운동기록들이 있어서 이 모든 데이터를 join을 통해 한번에 조회해오는것이 성능적으로 좋을까?
2. 운동목록을 조회하고, 조회해온 운동목록을 통해 하위의 운동기록들을 재조회해오는 것이 좋을까?

join을 통해 데이터를 한번에 가져온다면 쿼리문은 적게 나갈지 모르나 중복데이터가 많이 생길것으로 예상
연속해서 조회를 하기 때문에 query문 자체가 많이 발생할것으로 예상

아쉬운점 : 기간내에 구현이 급하다보니 test코드를 작성 후 성능테스트를 통해 결과를 낼려고했으나 test코드를 작성하지 못하여 원하는대로 성능테스트를 진행하지 못함
추후진행할 예정

## 코드를 구성하며 진행할려했던 성능테스트와 궁금사항
### 1. 데이터를 한번에 가져와 영속성 컨텍스트에 데이터를 추가 해 놓는다면 하면 추가적인 쿼리문이 발생하지 않지 않을까?
실험내용: queryDsl의 on절을 통해 데이터를 한번에 영속성컨텍스트에 가져와서 이후 가져온 데이터 내에서 조회문을 실행했으나  
어떤 부분은 query문이 발생하고 어떤부분은 query문이 실행되지 않았다

결과 : 고민해봐도 이유를 잘 모르겠어서 멘토링을 받아본 결과 on절을 통해 데이터를 가져오면 영속성 컨텍스트에 추가 되지 않는다는 답변을 받음  
의문증이 완전히 해소되지 않아 이후 실험해볼 것: on절을 통해 데이터를 가져오면 정말 영속성 컨텍스트에 추가되지 않는지 실험해 볼 것  
성능적으로도 유의미한 차이가 있는지 테스트코드를 작성하고 실험해 볼 것

## 성능 개선 및 테스트
### Redis를 이용한 성능개선
### 운동일지 상세보기 조회
운동일지를 완료한다면 운동일지는 과거의 기록 이기때문에 변경될 여지가 없다.  
그래서 첫 조회가 일어난다면 Redis에 등록이 되고 이후부터는 Redis를 통해 일지를 조회해오도록 변경하였다.
1. 운동일지 1건, 운동목록 총 3건, 운동기록 총 15건의 기록을 가져올때의 Mysql과 Redis의 성능 테스트 (1000번의 요청)
   ![Redis와 Mysql의 성능테스트 적은양 1000건 단순조회](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/9cdb851f-a1bb-45f7-8e89-8c8cde01f371)
     - Mysql에서 Redis에서 조회해 옴 으로써 평균적으로 11.11%의 성능이 향상되었다.
2. 운동일지 1건, 운동목록 총 12건, 운동기록 총 89건의 기록을 가져올때의 Mysql과 Redis의 성능 테스트 (10000번의 요청)
   ![10000건 조회 많은양](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/80c96e8f-f067-4a7e-8afb-4417be97d933)
    - Mysql에서 Redis에서 조회해 옴 으로써 평균적으로 26.67%의 성능이 향상되었다.
#### 결론: Join을 해서 가져오는 데이터의 양이 늘어날수록 Mysql과 Redis의 성능차이가 점점 커지게 된다.


### 운동일지 복사해서 가져오기
피드에 공유된 운동 일지를 복사해서 가져오는 기능은 이미 피드에 공유된 일지는 변경이 일어날 일이 없기 때문에 첫 조회 때 Redis에 등록이 되고 그 이후에 Redis에서 데이터를 가져와 복사되도록 구현했다.
1. 운동일지 1건, 운동목록 총 3건, 운동기록 총 15건의 기록을 가져올때의 Mysql과 Redis의 성능 테스트 (약 1796번 요청)
   ![1796회 적은양 복사](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/c4ae2dba-306f-4fe4-ae79-ccea37c88421)
   - Mysql에서 Redis에서 조회해 옴 으로써 평균적으로 14.29%의 성능이 향상되었다.
2. 운동일지 1건, 운동목록 총 12건, 운동기록 총 89건의 기록을 가져올때의 Mysql과 Redis의 성능 테스트 (10000번 요청)
   ![10000건복사 많은양](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/24d9fdb0-243b-43a8-99de-b613652862e7)
   - Mysql에서 Redis에서 조회해 옴 으로써 평균적으로 8.05%의 성능이 향상되었다.

### Redis와 Mysql 비교
![성능기록](https://github.com/Dongjin113/Da-It-Gym-BE/assets/104759062/1222280c-b20a-49fc-ae71-ccbec8e03e19)
Redis에서는 첫 실행 한 번 오래걸린 후 전반적으로 개선된 성능을 보여주었고  
Mysql에서는 전반적으로 엇비슷한 실행시간을 보여주었다

## 전체 시연 영상
### 회원가입, 로그인
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/79975172/c728ec7b-0139-4ef7-b236-8ca7093c95a6

### 루틴 목록 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/79af2b21-2a6c-4df4-8126-1bfcecaa30d4

### 루틴 상세 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/d00cfbf5-a570-4bc4-9930-a3ad6f4e9356

### 루틴 작성 하기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/557063b8-e2c8-487c-9856-983b861c55e5

### 알림
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/598d9f7c-3f77-4a47-b658-8109a5bbc094

### 운동 일지 목록 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/a707fb5b-eda7-446c-b429-b8a0ce7dbc51

### 운동 일지 상세 보기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/2f2585d6-028a-459c-98a8-9a4bcb587348

### 운동 계획 작성 및 시작
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/4a48fdfc-a0ec-46fa-8d26-f052fb8bd89e

### 운동 일지 작성
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/cf8757f5-6814-4589-a72f-21569b2fd638

### 마이 페이지 - 보관함/인바디
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/35f687b2-55e4-4b10-ad24-fd5d517e8139

### 마이 페이지 - 프로필 편집
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/460188d0-f376-435e-9cca-3943d3766e39

### 관리자 페이지 - 트레이너 승인
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/8162de51-2b74-4abb-b5b5-454f98e8f3ce

### 유저 찾기
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/dff067d4-7639-4755-9762-59f4d27e0360

### 근근근을 사용하고 있는 카카오톡 친구
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/1ea6affe-c707-4a62-98ac-88e57fe49550

### 채팅
https://github.com/Goorm-OGJG/Da-It-Gym-FE/assets/62943439/14ee20ac-bfa7-4e94-869d-d7173139d881
