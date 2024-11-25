# ReadME

Front 테스트하실때 `src/main/resources/application.properties` 수정해서 사용해주시면 됩니다.

<br/>

- 처음 실행 시
- your_db_name 부분 db 이름으로 바꿔주시고,
- 아래의 ${} 부분에 MySQL id pw 직접 넣어주시거나, 환경변수 설정해주시면됩니다.

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```
<br>

- 처음 실행 시 아래처럼 `create`로 설정하셔야 설정한 your_db_name db에 자동으로 테이블이 만들어집니다.
```
spring.jpa.hibernate.ddl-auto=create
```
<br>

- 두번째 실행 시 `create`를 유지하면 테이블이 새롭게 생성되어 대체되므로, `none`으로 설정해주시면 됩니다.
```
spring.jpa.hibernate.ddl-auto=none
```
