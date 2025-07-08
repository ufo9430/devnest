# devnest

## 참여 인원
* 인현민 : 프로젝트 운영, 메인 페이지, 질문 조회, 검색 기능, 프론트엔드 UI 설계
* 김민제 : 질문/답변 글 CURD 구현, 이미지 업로드 기능, 마크업 에디터 연결, DB 설계
* 신드보라 : 질문 CRUD 구현
* 이채린 : 로그인/로그아웃, 회원가입, 유저 정보 변경, 관리자 권한 부여
* 송치호 : 이메일 인증 코드 발송/검증, 회원가입, 유저 정보 변경, 배포 서버 운영
* 박재엽 : 알림 및 관리자 페이지

# 상세 기능

## 메인 페이지

<img width="1437" height="690" alt="Image" src="https://github.com/user-attachments/assets/2a44c3c6-1a21-4b9f-b450-c7bde8479082" />

- 메인 페이지에서는 최신 게시글과 인기 게시글을 조회할 수 있다.
- 태그 목록을 통해 게시글에 등록된 태그 목록을 확인할 수 있고, 클릭 시 해당 태그에 관한 게시글을 검색한다.
- 총 질문 수와 답변 대기 질문 수, 해결된 질문 수, 오늘 게시된 답변 수를 조회한다

## 게시글

- 로그인 한 사용자는 자신의 게시글을 새로 작성하거나 수정, 삭제할 수 있다.
- 게시글의 내용은 마크다운 문법으로 작성할 수 있다
- 게시글 생성 시 하나 이상의 태그를 게시글에 표시할 수 있다.

## 인기 게시글

- 게시글에 접속할 때마다 조회수를 1 증가시키고, 서버는 이를 감지한다.
- 조회수가 100을 넘긴 게시글은 인기 게시글로 분류되어 최신 목록에 갱신된다.
- 갱신된 목록은 메인 페이지의 “인기 게시글” 목록에서 확인할 수 있다.

## 전체 게시판

<img width="2378" height="3020" alt="Image" src="https://github.com/user-attachments/assets/a9b9bb8d-ae15-45be-8cde-9c61e293c9d6" />

- 최신 등록 글을 기준으로 모든 글을 정렬해 표시한다.
- 한 페이지에 질문글은 7개가 페이징되고, 페이지 버튼을 눌러 이동할 수 있다.
- 해결된 질문과 전체 질문을 토글해서 필터링할 수 있다.

<img width="2378" height="2822" alt="Image" src="https://github.com/user-attachments/assets/b838b530-719d-400b-bce0-fed8fc5a00b9" />

## 질문 작성

<img width="2048" height="1115" alt="Image" src="https://github.com/user-attachments/assets/93c0bb85-ca93-47f1-8c7d-1fb9b6fb1509" />

- 로그인 한 사용자는 자신의 게시글을 새로 작성하거나 수정, 삭제 가능
- 게시글의 내용은 마크다운 문법으로 작성
- 게시글 생성 시 하나 이상의 태그를 게시글에 표시

## 답변 작성
<img width="1119" height="853" alt="Image" src="https://github.com/user-attachments/assets/8a111e1d-41c7-4de6-8fa3-8692e4d3510b" />

- 로그인 한 사용자는 게시글에 답변을 작성하거나 수정, 삭제 가능
- 답변의 내용은 마크다운 문법으로 작성

## 답변 채택
<img width="715" height="554" alt="Image" src="https://github.com/user-attachments/assets/15ac03a9-a199-4630-be20-7879c106bd45" />

- 글 작성자는 자신의 글에 달린 답변 중 마음에 드는 답변이 있을 경우 채택하기 버튼을 통해 채택 가능
- 채택 받은 답변은 아래와 같이 채택됨 표시와 함께 초록 배경으로 표시

## 키워드, 태그 검색 기능

- 사용자는 내비게이션 바 혹은 게시판의 검색 바로 키워드를 입력할 수 있다.
- 서버는 입력된 키워드가 포함된 제목의 게시글을 찾아 정확도 순으로 정렬한다.
- 정렬된 게시글 중 해결된 질문만 필터링해서 확인할 수 있다.

## 회원가입

<img width="1898" height="908" alt="Image" src="https://github.com/user-attachments/assets/3b796bca-2b51-4fc1-92dd-1603bf335346" />

- 이메일을 통해 회원가입을 진행한다.
- 이메일은 중복 확인을 통해 해당 이메일을 통해 가입한 사람이 있는지 확인한다.
- 중복 검사를 통과하면 인증 전송 버튼이 활성화된다.
- 기입한 이메일로 인증 코드가 전송된다.
    
    <img width="465" height="175" alt="Image" src="https://github.com/user-attachments/assets/b9d7f7e7-6bc6-4cd9-b3d9-cac7d76a0621" />
    
- 비밀번호는 8자 이상이어야 한다.
    
    <img width="439" height="227" alt="Image" src="https://github.com/user-attachments/assets/7860c9b7-230f-4093-8f3b-2c1ec5ed3440" />
    
- 닉네임도 중복 확인을 진행하고 중복되지 않는 닉네임일 경우 회원가입 버튼이 활성화된다.
    
    <img width="1182" height="1125" alt="Image" src="https://github.com/user-attachments/assets/16a27bb6-a02c-4c28-b4dd-94301e1e36bd" />
    
- 회원가입이 완료되면 로그인 화면으로 이동한다.

## 로그인

<img width="1902" height="906" alt="Image" src="https://github.com/user-attachments/assets/06253d1d-6879-4fef-9e0e-7af4ba0d9c6f" />

- 이메일과 비밀번호를 입력하고 2차 인증하기를 누르면 이메일로 인증 코드가 전송된다.

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/ff969464-6d8d-4190-9055-60876f6f84c9" />

- 전송된 인증 코드를 해당 입력창에 입력하고 인증 버튼을 누르면, 성공했을 경우 1.5초 뒤에 해당 창이 자동으로 닫히고 2차 인증 버튼은 로그인 버튼으로 바뀌게 된다.

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/0c7bf0a5-f9c2-4b56-a6d3-08e1e89647e6" />

- 로그인 버튼을 누르면 홈 화면으로 이동하게 된다.

## 계정 찾기

<img width="1913" height="907" alt="Image" src="https://github.com/user-attachments/assets/676cecb8-a2b8-4e5a-baa2-5e37e9930068" />

- 해당 이메일로 가입한 계정이 있는지를 확인하는 기능이다.
- 입력한 이메일의 계정이 있을 경우, 가입한 닉네임과 가입일을 확인할 수 있다.
    
    <img width="670" height="773" alt="Image" src="https://github.com/user-attachments/assets/cbf83bf5-1c0f-4de1-bf45-7b40b6ea250d" />
    
- 이후, 비밀번호 찾기 페이지로 넘어가게 된다.

## 비밀번호 찾기

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/3ef107fe-2b5e-4579-a8e4-3b8e54623641" />

- 가입한 이메일을 입력하면 해당 이메일로 인증 코드가 전송된다.

<img width="1918" height="1079" alt="Image" src="https://github.com/user-attachments/assets/ee1036ea-83a5-4d83-9a65-6662a1f07dd9" />

- 인증코드를 알맞게 인증하면 임시 비밀번호가 발급된다.
- 임시 비밀번호는 해당 페이지에서도 확인이 가능하고 이메일로도 확인이 가능하다.
    
    <img width="677" height="1079" alt="Image" src="https://github.com/user-attachments/assets/8ab9e896-9707-4fc3-82a3-a929d260d43c" />
    
    <img width="481" height="240" alt="Image" src="https://github.com/user-attachments/assets/35ed5c9e-0ddb-41fd-aeea-ddb76c46e461" />
    

## 프로필 페이지

### 프로필 이미지 변경

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/a9b8620a-5569-4ece-8319-49da8934715b" />

- 프로필 이미지 편집 버튼을 누르면 위와 같은 창이 뜬다.
- 업로드 버튼을 누르고 이미지를 선택하면 미리보기를 통해 이미지를 확인할 수 있다.
- 저장 버튼을 누르면 바뀐 이미지가 저장된다.

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/2e7e69f5-be58-4ed9-b64e-d8e6b2f5bc82" />

- 바로 바뀐 이미지를 확인할 수 있다.

### 닉네임 변경

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/d9b90a31-4e45-46a9-8c81-1b8e0cc81a42" />

- 닉네임에서 중복확인 버튼을 누르면 알림창을 통해 사용 가능한 닉네임인지 중복된 닉네임인지 알려준다.
- “사용 가능한 닉네임입니다.”라는 알림이 뜬 닉네임의 경우 이후 저장 버튼을 누르면 닉네임이 변경된다.

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/9ecf019b-186b-4873-8ca0-9d506bf380c2" />

- 변경된 닉네임으로 실시간으로 변경되어 적용된다.

### 비밀번호 변경

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/5d38ff0f-afb2-4fcb-8533-3f5ac73d5373" />

- 비밀번호 변경에 앞서 현재 비밀번호를 확인한다.
- 비밀번호가 확인되면 다음 화면으로 넘어간다.

<img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/1c66f513-c506-4202-9c46-7310749115d9" />

- 새 비밀번호를 입력하고 해당 비밀번호를 재확인하는 과정을 거친 후 비밀번호를 변경할 수 있다.

### 알림 페이지

<img width="2048" height="1445" alt="Image" src="https://github.com/user-attachments/assets/9ad8956a-fe3a-44b9-b250-bd8cc3943acb" />

- **실시간 알림**: 사용자에게 중요한 메시지 전달
- **알림 타입**: 시스템 알림, 사용자 간 알림 등
- **우선순위**: 높음/보통/낮음으로 분류
- **읽음 처리**: 사용자가 확인한 알림 표시

## 관리자 페이지

### 회원관리

<img width="2048" height="1079" alt="Image" src="https://github.com/user-attachments/assets/b56399c6-da30-49bb-a441-7cbb376b034e" />

<img width="2048" height="675" alt="Image" src="https://github.com/user-attachments/assets/1fe5cda9-6106-4489-aee4-48b49ce29df8" />

- **사용자 목록 조회**: 전체 회원 정보 표시
- **사용자 통계**: 총 회원수, 활성/비활성 회원 수
- **검색 기능**: 이메일/닉네임으로 회원 검색
- **페이징**: 대량 데이터 효율적 처리

### 신고관리

<img width="2048" height="634" alt="Image" src="https://github.com/user-attachments/assets/292fb967-ab75-43ac-b6ea-2efcafee399f" />

<img width="2048" height="690" alt="Image" src="https://github.com/user-attachments/assets/13d6cf1a-a177-4b11-ab41-dd0c2bd5c355" />

<img width="1008" height="660" alt="Image" src="https://github.com/user-attachments/assets/b0fa0783-ef6f-4b43-9338-bf1704952b89" />

- **신고 목록 조회**: 사용자 신고 내역 관리
- **신고 처리**: 신고 상태 변경 및 조치
- **신고 통계**: 신고 유형별 통계 정보
