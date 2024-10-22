package org.zerock.shop.service;

import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.zerock.shop.dto.ItemImgDto;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;

import org.zerock.shop.dto.ItemSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// import org.zerock.shop.dto.MainItemDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;  // 아이템 서비스

    private final ItemImgService itemImgService; // 아이템 이미지 서비스

    private final ItemImgRepository itemImgRepository; // 이미지 db 연동

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.createItem();   // 등록 폼으로 입력 받은 데이터를 이용해 객체 생성
        itemRepository.save(item);              // db에 저장

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)
                itemImg.setRepimgYn("Y");        // 이미지가 첫번째 일 경우 대표이미지 Y 처리
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i)); // 상품 이미지 저장
        }

        return item.getId();                // 저장된 id를 리턴
    }

    @Transactional(readOnly = true) // 상품을 읽어오는 트랜젝션을 읽기 전용으로 설정하면 성능이 개선됨.(더티체킹(변경감지) 수행않음)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    } // 페이지 처리되는 아이템 처리용

    @Transactional(readOnly = true) // 메인 페이지용 서비스
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

}