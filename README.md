# prayhelper-Server
> prayhelper spring repositroy <br>

<br>

# Convention

## 1. 브랜치 컨벤션

- [feature] : 기능 추가
- [fix] : 에러 수정, 버그 수정
- [docs] : README, 문서
- [refactor] : 코드 리펙토링 (기능 변경 없이 코드만 수정할 때)
- [modify] : 코드 수정 (기능의 변화가 있을 때)
- [chore] : gradle 세팅, 위의 것 이외에 거의 모든 것

### 브랜치 명 예시

`ex) feature/#issue-user-APi`

## 2. 커밋 컨벤션

- ✅ `chore` : 동작에 영향 없는 코드 or 변경 없는 변경사항(주석 추가 등)
- ✨ `feat` : 새로운 기능 구현
- ➕ `add` : Feat 이외의 부수적인 코드 추가, 라이브러리 추가, 새로운 파일 생성
- 🔨 `fix` : 버그, 오류 해결
- ⚰️ `del` : 쓸모없는 코드 삭제
- 📝 `docs` : README나 WIKI 등의 문서 수정
- ✏️ `correct` : 주로 문법의 오류나 타입의 변경, 이름 변경시
- ⏪️ `rename` : 파일 이름 변경시
- ♻️ `refactor` : 전면 수정
- 🔀 `merge`: 다른 브랜치와 병합

### 커밋 예시

`ex ) git commit -m "feat: 회원가입 기능 완료"`

## 3. 이슈 컨벤션

- [feat] : 기능 추가
- [fix] : 에러 수정, 버그 수정
- [docs] : README, 문서
- [refactor] : 코드 리펙토링 (기능 변경 없이 코드만 수정할 때)
- [modify] : 코드 수정 (기능의 변화가 있을 때)
- [chore] : gradle 세팅, 위의 것 이외에 거의 모든 것
    
    

## 4. 코드 컨벤션

### Naming

- 변수는 camelCase를 기본으로 → userEmail, userCellPhone
- ENUM이나 상수는 대문자로 네이밍
- URL, 파일명 등은 kebab-case를 사용한다. → /user-email-page ...
- 함수명은 소문자로 시작하고 동사로 네이밍 → getUserId(), isNormal()
- 패키지명은 소문자로
    - 언더스코어나 대문자 섞지 X
- 클래스명은 UpperCamelCase를 사용하고 명사로 작성 → UserEmail, Address
- 예외를 던질때는 세부적인 Custom Exception으로 던진다
- 메소드와  클래스는 쪼개서 최대한 작게 만든다.

<br>

## Archive
### [코드 리뷰](https://descriptive-pear-e89.notion.site/SW-43ffbc8c962449b28fe5e0efd9a15559?pvs=4)
### [주석 생성 전략](https://velog.io/@hangem422/clean-code-comment)
