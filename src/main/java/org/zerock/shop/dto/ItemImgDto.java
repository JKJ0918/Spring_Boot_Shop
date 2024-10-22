package org.zerock.shop.dto;

import org.zerock.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {
    // 상품을 저장 후 상품 이미지에 대한 데이터를 전달 할 DTO 클래스

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    // 232 추가 모델 처리를 위한 라이브러리 (DTO와 엔티티간의 변환 처리용) -> config.RootConfig에 적용
    // 상품을 등록할 때 화면으로 부터 전달 받은 DTO 객체를 엔티티로 변환하는 작업 대체(DTOtoEntity, EntityToDTO)
    // 서로다른 클래스의 값을 필드의 이름과 자료형이 같으면 getter, setter를 통해 값을 복사해서 객체로 변환 해줌)
    //    implementation 'org.modelmapper:modelmapper:3.1.0'
    private static ModelMapper modelMapper = new ModelMapper();  // 맴버 변수로 객체 추가

    public static ItemImgDto of(ItemImg itemImg) { // ItemImg를 파라미터로 받아서 자료형과 변수이름이 같을 때 ItemImgDto로 값을 복사해 반환
        // static 처리를 하여 new 없이 사용하도록 설정
        return modelMapper.map(itemImg,ItemImgDto.class);
    }


}