package com.umc.jatdauree.src.domain.web.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PostStoreRes { //2.그럼 요청에 응답한다 즉 요청을 보낸 목록들의 내용을 클라이언트가 적는다 그럼 이 응답에서는 우리가 아까 storeDao에서 쿼리문으로 insert시킨 목록들을 Dao의 이 구문에서 String lastInsertIdQuery = "select last_insert_id()" 가져온다
                            //insert시킨 목록들을 Dao의 이 구문에서 String lastInsertIdQuery = "select last_insert_id()" 가져온다
                            //String lastInsertIdQuery = "select last_insert_id() 이건 마지막으로 store에 추가한 마지막 storeIdx를 나타낸다 즉 이걸 반환 받는거다 그래서 쓸대없이 반환 받지도 않는거 쓰지마라.
    private long storeIdx;


}
