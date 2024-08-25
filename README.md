# 스프링 게시판 프로젝트
- Spring Security + 멤버, 게시글, 댓글, 답글 CRUD 등을 구현한 게시판 프로젝트
- 24.07.01 프로젝트 생성 및 깃허브 리포지토리 생성 후 현재도 개발 진행 중
- 작성일인 24.08.26 기준 실질적인 개발 기간 약 2~3주 소요

# 목차
- [사용 기술](#사용-기술)
- [구현 기능 설명](#구현-기능-설명)
  - [로그인 (spring security)](#로그인-spring-security)
  - [멤버](#멤버)
  - [게시판](#게시판)
  - [댓글](#댓글)
- [구현 예정 기능 목록](#구현-예정-기능-목록)
# 사용 기술
  - # 백엔드
  - spring
  - # 프론트
  - thymeleaf
  - # 그 외
  - DB : H2 DB (향후 postgresql 사용 예정)
  - AWS : Redis
# 구현 기능 설명
  - # 로그인 (spring security)
    - # 로그인 페이지
      
    ![로그인페이지](https://github.com/user-attachments/assets/64fb717c-7b2b-431b-a749-c6f165ae915e)

    - 우측 상단의 로그인 버튼 클릭 시 이동 되는 페이지
    - OAuth2, JWT를 통한 로그인 구현
    - # security
      
    ![쿠키](https://github.com/user-attachments/assets/3d9123b0-31c5-4eba-bd42-ed87aeb0d3b4)

    ![레디스서버](https://github.com/user-attachments/assets/a3ffc830-53f2-4a9b-a7dd-6160f27fb355)

    - OAuth2로 인증하여 발급된 JWT는 클라이언트에 쿠키로 생성하여 API 호출시 권한 검사에 사용
    - 로그인 페이지, 게시판 리스트 페이지만 비로그인 상태에서도 접근 가능 그 외에는 로그인을 통한 권한 인증 필요

    ![레디스클라_키검색및값조회](https://github.com/user-attachments/assets/0b69b982-f175-49c4-83de-3640740757a4)

    ![개발자도구_리프레시토큰](https://github.com/user-attachments/assets/434f4b66-2a04-489a-af15-f7d01a79daa2)

    - refresh token은 AWS Redis를 사용해 관리함으로써 비동기적으로 access token 만료시 재발급 (구현 예정)  

  - # 멤버
    - # 멤버 DB
    ![image](https://github.com/user-attachments/assets/02afba3e-99a9-45da-84aa-388b1e55b7f5)

    - 현재 로그인은 OAuth2를 통해서만 가능하도록 구현
    - 멤버 id, 권한, OAuth2 제공자 저장
    - OAuth2를 통해 전달 받은 사용자 정보는 사용자에게 정보 제공 동의를 받은 정보만 DB에 저장 (이메일, 이름 등)

  - # 게시판
    - # 게시글 리스트
      
    ![게시판리스트페이지](https://github.com/user-attachments/assets/c174ba16-6150-4397-8178-1c7c235a0f4f)

    - 게시글 제목과 내용 및 작성, 수정 시간, 조회수, 댓글 수 등 조회
    - 헤더의 홈 버튼 클릭 시 이동 하는 게시판 리스트 페이지
    - 로그인 시 우측 상단의 로그인 버튼이 사라지고 로그인 된 사용자 이름과 로그아웃 버튼 표시
    - security 설정으로 이 페이지와 로그인 페이지만 비로그인 상태에서도 접근 가능

    ![게시판리스트페이지_본인게시글만수정삭제가능](https://github.com/user-attachments/assets/4326d39e-4dee-4fd1-8f22-a09b965b4b36)

    - 본인이 작성한 게시글만 수정 및 삭제 가능
   
    ![게시글리스트페이지_신고모달창](https://github.com/user-attachments/assets/64472dfb-2d40-4ef4-ab89-2ecbebd92367)

    - 모든 게시글은 신고 가능 하며 접수 시 관리자에게 전달 (구현 예정)
   
    ![게시글작성페이지](https://github.com/user-attachments/assets/d8381946-435f-49d7-aa1e-69e4b1c8b924)
    
    ![게시글수정페이지](https://github.com/user-attachments/assets/9aeaa1d5-bee0-40f3-b5c1-36b345dc3a8a)

    - 작성 및 수정 페이지
    - 수정 시 기존에 작성된 내용 그대로 입력된 상태
   
    - # 게시글
      
    ![게시판페이지](https://github.com/user-attachments/assets/07fe44da-f962-4ab5-a19f-db10f5badf83)

    - 게시글의 제목 및 내용 조회
    - 댓글 입력 및 작성된 댓글 조회


  - # 댓글

  ![댓글리스트](https://github.com/user-attachments/assets/84d20393-95ea-4ada-ad77-9f35bd0e297d)

  - 댓글 작성자 이름, 작성 시간, 내용 조회
  - 본인이 작성한 댓글만 수정 및 삭제 가능
  - 댓글의 답글 가능

  ![댓글_답글작성](https://github.com/user-attachments/assets/8cc756b6-98d5-451d-b31b-04d715e1bfbb)

  - 답글 작성 및 수정 버튼 클릭시 부트스트랩 모달창 표시

  ![댓글리스트_답글존재](https://github.com/user-attachments/assets/1d1e2968-ad1f-4c42-9da7-b3deb3f8ab46)

  - 답글도 댓글과 동일한 데이터 표시
  - 답글의 답글은 구현 예정

    - # 댓글 DB
    ![image](https://github.com/user-attachments/assets/f8ae1c8a-6a3b-4d03-a083-a132581dee64)

    - 댓글 작성 시 DB에 저장되는 부모 댓글 id는 본인 id로, 자식 댓글 id는 null로 설정
    - 답글의 부모 댓글 id는 답글을 작성한 댓글의 id로 설정
    - 답글이 삭제될 시 댓글의 자식 댓글 id를 다시 null로 변경
    - 답글이 존재하는 댓글이 삭제될 시 남아 있는 답글의 부모 댓글 id를 본인 id로 변경
      
# 구현 예정 기능 목록
- https://natural-study-73e.notion.site/fca325fef5b743e6aa88abc29318492e

