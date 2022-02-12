package com.todayworker.springboot.domain.board.es.document;

import com.todayworker.springboot.domain.board.vo.BoardVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "board") // 기존 구현 방식과의 충돌을 방지하기 위해 IndexName을 다르게 mapping
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BoardDocument {

    @Id // javax.persistence.Id가 아닌 org.springframework.data.annotation.Id 입니다.
    private String boardId;
    @Field(type = FieldType.Text)
    private String bno;
    @Field(type = FieldType.Text)
    private String categoryName;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Long)
    private Long cnt;
    @Field(type = FieldType.Text)
    private String user;
    @Field(type = FieldType.Text)
    private String regDate;

    public static BoardDocument from(BoardVO vo, String indexName) {
        return new BoardDocument(indexName + vo.getBno(),
            vo.getBno(),
            vo.getCategoriName(),
            vo.getTitle(),
            vo.getContent(),
            vo.getCnt(),
            vo.getUser(),
            vo.getRegDate()
        );
    }

    public static BoardDocument of(
        String boardId,
        String bno,
        String categoryName,
        String title,
        String content,
        Long cnt,
        String user,
        String regDate
    ) {
        return new BoardDocument(
            boardId,
            bno,
            categoryName,
            title,
            content,
            cnt,
            user,
            regDate
        );
    }
}
