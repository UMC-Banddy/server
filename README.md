# server

## Git Convention

### Branch Strategy
- main: 배포 가능한 최종 코드만 관리합니다.
- develop: 개발 중인 기능을 통합하는 브랜치입니다.
- feat: 새로운 기능 개발 시 사용합니다. (‎⁠예: `feat/login`⁠)
- fix: 버그 수정 시 사용합니다. (‎⁠예: `fix/login-bug`)

### Issue
- 새로운 기능 추가, 버그 제보, 오류 수정, 리팩토링, 배포 작업 등을 이슈로 등록합니다.
- 이슈 템플릿을 참고하여 작성합니다.
- 이슈 제목은 제목 앞에 [타입]을 붙이고, 이슈 내용을 한 눈에 알 수 있게 작성합니다. (예: `[✨ Feat] 로그인 기능 구현`)
- 해당하는 라벨을 추가합니다.

### Pull Request (PR)
- PR 템플릿을 참고하여 작성합니다.
- PR 제목은 제목 앞에 타입:을 붙이고, PR 내용을 간결하게 작성합니다. (예: `Feat: 로그인 API 구현 (#3)`, `Fix: 로그인 실패 시 에러 수정 (#3)`)
- 관련 이슈가 있다면 연결합니다.
- 코드 리뷰를 거친 후 develop 브랜치로 머지합니다.

### Commit Message
- `[타입] 작업 내용` 형태로 작성합니다. (예: ‎⁠`feat: 로그인 API 엔드포인트 추가 (#3)`, `fix: 로그인 실패 시 에러 메시지 수정 (#3)`⁠)
- 주요 타입: `feat`(기능), `fix`(버그 수정), `docs`(문서), `refactor`(리팩토링), `test`(테스트), `chore`(기타)  


## Developers
<table>
  <tr>
    <!-- 사진 행 -->
    <td align="center">
      <a href="https://github.com/yjhss">
        <img src="https://avatars.githubusercontent.com/yjhss" width="100px" alt="홍유진" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/yeondub1121">
        <img src="https://avatars.githubusercontent.com/yeondub1121" width="100px" alt="장연주" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/J2H3233">
        <img src="https://avatars.githubusercontent.com/J2H3233" width="100px" alt="전종현" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/heexji">
        <img src="https://avatars.githubusercontent.com/heexji" width="100px" alt="김희지" />
      </a>
    </td>
  </tr>
 <tr>
    <td align="center"><b>하루/홍유진</b></td>
    <td align="center"><b>연두/장연주</b></td>
    <td align="center"><b>종현/전종현</b></td>
    <td align="center"><b>헤리/김희지</b></td>

  </tr>
  <tr>
    <td align="center">BE(Lead)</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
  </tr>

  <tr>
    <!-- 역할 행 -->
    <td><div align="center">음악</div></td>
    <td><div align="center">회원, 친구</div></td>
    <td><div align="center">모집, 채팅</div></td>
    <td><div align="center">밴드, 프로필</div></td>
</table>
